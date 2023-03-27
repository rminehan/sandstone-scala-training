---
author: Rohan
date: 2023-03-22
title: Concurrent Maps
---

```
  ____                                           _
 / ___|___  _ __   ___ _   _ _ __ _ __ ___ _ __ | |_
| |   / _ \| '_ \ / __| | | | '__| '__/ _ \ '_ \| __|
| |__| (_) | | | | (__| |_| | |  | | |  __/ | | | |_
 \____\___/|_| |_|\___|\__,_|_|  |_|  \___|_| |_|\__|

 __  __
|  \/  | __ _ _ __  ___
| |\/| |/ _` | '_ \/ __|
| |  | | (_| | |_) \__ \
|_|  |_|\__,_| .__/|___/
             |_|
```

---

# Background

Diva has a few instances of caching in memory using maps

---

# Background

Diva has a few instances of caching in memory using maps

Getting a deeper understanding was raised a while back

---

# Agenda

- understand diva context


- understand the need for concurrent maps


- discuss different concurrent maps

---

```
 ____  _
|  _ \(_)_   ____ _
| | | | \ \ / / _` |
| |_| | |\ V / (_| |
|____/|_| \_/ \__,_|

  ____            _            _
 / ___|___  _ __ | |_ _____  _| |_
| |   / _ \| '_ \| __/ _ \ \/ / __|
| |__| (_) | | | | ||  __/>  <| |_
 \____\___/|_| |_|\__\___/_/\_\\__|
```

---

# Usages

- docservicemodel: `DocumentModelService.scala` line 23


- awsparameterservice: `AwsSettingsService.scala` line 25


- docrequirementsservice: `ClientBroadcastService.scala` line 20


- docservicemodel: `ImageService.scala` line 62 (too complex for today, see DIVA-4861)

---

# docservicemodel

To the code!

---

# docservicemodel summary

Caching wrapper around:

```
divaadminservice
GET  /admin-internal/docmodels/type/$docType/client/$clientId
```

ie. fetching the doc model for a particular client and doc type

Cache keys of the form "CLIENT_ID.DOCUMENT_TYPE"

Used by docanalyser and docrequirementsservice

(Similar practice to this in `ImageService.scala`)

---

# awsparameterservice

To the code!

---

# awsparameterservice summary

Used for caching database connection info

Avoids having to refetch the password from SSM

(Is probably unnecessary and causes issues)

---

# docrequirementsservice

To the code!

---

# docrequirementsservice summary

Caches the current websocket connections to that pod by UUID

```
Browsers:   ---  ---  ---     ---  ---          ---           ---  ---  ---
           |   ||   ||   |   |   ||   |        |   |         |   ||   ||   |
            ---  ---  ---     ---  ---          ---           ---  ---  ---


Websocket:   \   |   /         |   |             |             \   |   /


Pods:         ------          ------          ------            ------     ...
             |      |        |      |        |      |          |      |
              ------          ------          ------            ------
```

If a browser kills the socket, the entry gets removed from the cache

If a BE pod dies, the websocket will die and the browser won't get updates to the progress bar (unless it reconnects to a new pod)

---

# Summary

We use concurrent maps in at least 4 places

Major use case is caching

---

# Summary

General caching pattern is a long running cache in the memory of the pod:

- create an empty map when pod starts


- (request arrives) check if the data is in the map, and return if it is


- otherwise generate a result the slow way and put into the map


- invalidate the cache in some situations

---

```
  ____                                           _
 / ___|___  _ __   ___ _   _ _ __ _ __ ___ _ __ | |_
| |   / _ \| '_ \ / __| | | | '__| '__/ _ \ '_ \| __|
| |__| (_) | | | | (__| |_| | |  | | |  __/ | | | |_
 \____\___/|_| |_|\___|\__,_|_|  |_|  \___|_| |_|\__|

 __  __
|  \/  | __ _ _ __
| |\/| |/ _` | '_ \
| |  | | (_| | |_) | ?
|_|  |_|\__,_| .__/
             |_|
```

Why do we use concurrent maps?

Why not use regular maps?

---

# mutable.Map demo

To the repl!

---

# Experiment

Build a map:

```scala
cache(0) = 0
cache(1) = 1
cache(2) = 2
cache(3) = 3
...
cache(9999) = 9999
```

10K entries

---

# Parallel

Build a map:

```scala
cache(0) = 0
cache(1) = 1
cache(2) = 2
cache(3) = 3
...
cache(9999) = 9999
```

10K entries

Do it in parallel - 10 threads, each doing 1000 each

---

# Parallel

Do it in parallel - 10 threads, each doing 1000 each

- thread 0: 0-999
- thread 1: 1000-1999
- thread 2: 2000-2999
- thread 3: 3000-3999
...
- thread 9: 9000-9999

---

# Visually

None of the threads are stepping on each others toes

```
0      |
..     | thread 0
999    |
1000     |
...      | thread 1
1999     |
...
```

Each value is set once and never changed

---

# Visually

None of the threads are stepping on each others toes

```
0      |
..     | thread 0
999    |
1000     |
...      | thread 1
1999     |
...
```

Each value is set once and never changed

So what could _possibly_ go wrong!

---

# Survey

During the experiment:

```
(A) Something will go wrong       (B) Nothing will go wrong,
                                      everything is okay
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Experiment time!

To the repl!

---

# Summary

```scala
val cache = scala.collection.mutable.Map.empty[Int, Int]

def populateRange(start: Int, length: Int = 1000): Unit = {
  for (i <- start until start + length) cache(i) = i
}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

for (start <- 0 until 10000 by 1000) {
  Future(populateRange(start))
}

cache.size // Not 10K
```

Something broke inside our cache

---

# Survey

What is wrong?

```
(A) Maths is broken         (B) The cache is broken
    My educations is
    a lie
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Problem

Internally, the cache is not "thread safe"

---

# Problem

Internally, the cache is not "thread safe"

It's designed for use by a single thread only

---

# Illustration

To the repl!

---

# Summary

```scala
var length = 0

def insert(key: Int, value: Int): Unit = {
  // Update hash buckets
  // ...
  length += 1
}

// Single threaded
for (i <- 0 until 10000) insert(i, i)
length // 10000

// Try again in parallel
length = 0
for (start <- 0 until 10000 by 1000) { // 0, 1000, 2000, ..., 9000
  Future(for (i <- start until start + 1000) insert(i, i))
}

length // Something less than 10K! Whoopsie!
```

---

# Summary

The mutable.Map is not "thread safe"

The logic that modifies its internal state implicitly assumes only one thread is working on it at a time

---

# Note

Bugs like these are non-deterministic and hard to reproduce

They are probabilistic and depend on environmental factors like load

---

# Note

Bugs like these are non-deterministic and hard to reproduce

They are probabilistic and depend on environmental factors like load

Concurrency bugs are very annoying!

Try not to make them!

---

# Dangerous things

- blocking code


- locks


- non-standard execution context


- mutating state

Slow down, be careful

---

# Aside

This is an issue specific to mutable objects

Immutable objects can't be mutated so are intrinsically thread safe

Idiomatic scala code will try to avoid mutation based patterns because of all the baggage that comes with it

---

# Back to diva

> Why does diva code use concurrent maps for its caches?

---

# Back to diva

> Why does diva code use concurrent maps for its caches?

The caches get mutated over time

Mutations will come from multiple threads in parallel

Hence a thread safe implementation of map is needed

---

# Threadsafe by default?

> Why isn't the mutable map from the standard library threadsafe?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Threadsafe by default?

> Why isn't the mutable map from the standard library threadsafe?

Possible reasons:

- trying to keep it simple


- there's a peformance cost to making it threadsafe

In single threaded situations you don't want to pay that cost

They expect you to know to use a concurrent map if you're writing multi-threaded code

---

Fix our problem with a:

```
  ____                                           _
 / ___|___  _ __   ___ _   _ _ __ _ __ ___ _ __ | |_
| |   / _ \| '_ \ / __| | | | '__| '__/ _ \ '_ \| __|
| |__| (_) | | | | (__| |_| | |  | | |  __/ | | | |_
 \____\___/|_| |_|\___|\__,_|_|  |_|  \___|_| |_|\__|

 __  __
|  \/  | __ _ _ __
| |\/| |/ _` | '_ \
| |  | | (_| | |_) |
|_|  |_|\__,_| .__/
             |_|
```

---

# Demo

Let's redo our demo with a concurrent map

To the repl!

---

# Summary

```scala
val cache2 = new java.util.concurrent.ConcurrentHashMap[Int, Int]()

def populateRange2(start: Int, length: Int = 1000): Unit = {
  for (i <- start until start + length) cache2.put(i, i)
}

for (start <- 0 until 10000 by 1000) {
  Future(populateRange2(start))
}

cache2.size // 10K, hoorah!
```

---

```
 ____
/ ___|  ___
\___ \ / _ \
 ___) | (_) |
|____/ \___/


 _ __ ___   __ _ _ __  _   _
| '_ ` _ \ / _` | '_ \| | | |
| | | | | | (_| | | | | |_| |
|_| |_| |_|\__,_|_| |_|\__, |
                       |___/
                                                _
  ___ ___  _ __   ___ _   _ _ __ _ __ ___ _ __ | |_
 / __/ _ \| '_ \ / __| | | | '__| '__/ _ \ '_ \| __|
| (_| (_) | | | | (__| |_| | |  | | |  __/ | | | |_
 \___\___/|_| |_|\___|\__,_|_|  |_|  \___|_| |_|\__|


 _ __ ___   __ _ _ __  ___
| '_ ` _ \ / _` | '_ \/ __|
| | | | | | (_| | |_) \__ \
|_| |_| |_|\__,_| .__/|___/ ...
                |_|
```

---

# Why are there so many?

- (java) synchronized map


- (java) ConcurrentHashMap


- (scala) trie map

---

# Why are there so many?

- (java) synchronized map


- (java) ConcurrentHashMap


- (scala) trie map

Some is a scala/java thing

Main difference is performance characteristics

---

# Quick rule of thumb

Don't use the _synchronized_ map from java - it's locking mechanism makes it a bit slow

Use the _concurrent_ hashmap from java, or the trie map from scala

---

```
 ____
/ ___| _   _ _ __ ___  _ __ ___   __ _ _ __ _   _
\___ \| | | | '_ ` _ \| '_ ` _ \ / _` | '__| | | |
 ___) | |_| | | | | | | | | | | | (_| | |  | |_| |
|____/ \__,_|_| |_| |_|_| |_| |_|\__,_|_|   \__, |
                                            |___/
```

---

# Concurrent Maps

Threadsafe map implementations

Useful for caching

---

# Diva

Concurrent maps used in about 4 places

---

# Careful

Updating mutable state within a concurrent context is dangerous

Old school mechanisms to manage that often block and are dangerous for performance

If you see code using a concurrent map, be more careful when updating it

---

# One more question

(A late addition)

---

# Question:

What is ARR?

How do you feel about service based revenue vs product based revenue?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
