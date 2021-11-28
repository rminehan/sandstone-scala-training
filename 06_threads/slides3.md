---
author: Rohan
date: 2021-11-29
title: Fork join pool
---

```
 _____          _
|  ___|__  _ __| | __
| |_ / _ \| '__| |/ /
|  _| (_) | |  |   <
|_|  \___/|_|  |_|\_\

     _       _
    | | ___ (_)_ __
 _  | |/ _ \| | '_ \
| |_| | (_) | | | | |
 \___/ \___/|_|_| |_|

 ____             _
|  _ \ ___   ___ | |
| |_) / _ \ / _ \| |
|  __/ (_) | (_) | |
|_|   \___/ \___/|_|

```

---

# Today

Look at the fork join pool

Like a hybrid of the two approaches we've seen

---

# Recap

Two kinds execution contexts

## "Free"

One thread per job

```scala
def execute(runnable: Runnable): Unit = {
  val thread = new Thread(runnable)
  thread.start()
}
```

## Fixed size thread pool

Reuse the same threads over and over

```scala
private val threadPool = Executors.newFixedThreadPool(8)

def execute(runnable: Runnable): Unit = {
  threadPool.submit(runnable)
}
```

---

# Strengths and weaknesses

## "Free"

✘ Competition for resources

✘ Wasted CPU

✘ Higher memory usage

✓ Avoids thread starvation

## Fixed size thread pool

✓ Avoids CPU waste

✓ Lower memory usage

✘ Prone to thread starvation with blocking

✘ Contention for next job

---

# Hybrid approach

Thread pool with some capacity to grow

---

# Hybrid approach

> Thread pool with some capacity to grow

## Thread pool

Still have a reserved set of threads

## Capacity to grow

During thread starvation it can spawn more threads

---

# Best of both worlds

Has the efficiencies of a thread pool

But not as fragile regarding blocking

---

```
    _             _     _ _            _
   / \   _ __ ___| |__ (_) |_ ___  ___| |_ _   _ _ __ ___
  / _ \ | '__/ __| '_ \| | __/ _ \/ __| __| | | | '__/ _ \
 / ___ \| | | (__| | | | | ||  __/ (__| |_| |_| | | |  __/
/_/   \_\_|  \___|_| |_|_|\__\___|\___|\__|\__,_|_|  \___|

```

The "Fork Join pool"

---

# Architecture

Suppose 2 requests per second, request takes 1+5+1=7 seconds

```
                     1   2   3   4   5   6   7   8   9   10
 dequeues                            |
                                     |
[       ]  Thread-1: ----<      sleeping     >----
[       ]  Thread-2:   ----<      sleeping     >----
[       ]  Thread-3:     ----<      sleeping     >----
[       ]  Thread-4:       ----<      sleeping     >----
[       ]  Thread-5:         ----<      sleeping     >----
[       ]  Thread-6:           ----<      sleeping     >----
[       ]  Thread-7:             ----<      sleeping     >----
[       ]  Thread-8:               ----<      sleeping     >----
                                     |
num cores utilised:                  1
num threads used:                    8
```

Request 9 arrives at 5 second mark and threads are starved

(dequeue = d-e-queue = Double Ended Queue)

---

# Freeze at 5s

A difference: threads signal they're blocked

```
                     1   2   3   4   5   6   7   8   9   10
 dequeues                            |
                                     |
[       ]  Thread-1: ----<           |   "I'm blocked"
[       ]  Thread-2:   ----<         |   "I'm blocked"
[       ]  Thread-3:     ----<       |   "I'm blocked"
[       ]  Thread-4:       ----<     |   "I'm blocked"
[       ]  Thread-5:         ----<   |   "I'm blocked"
[       ]  Thread-6:           ----< |   "I'm blocked"
[       ]  Thread-7:             ----<   "I'm blocked"
[       ]  Thread-8:               ---
                                     |
num cores utilised:                  1
```

---

# Manager's perspective

```
                     1   2   3   4   5   6   7   8   9   10
 dequeues                            |
                                     |
[       ]  Thread-1: ----<           |   "I'm blocked"
[       ]  Thread-2:   ----<  sleep  |   "I'm blocked"
[       ]  Thread-3:     ----<       |   "I'm blocked"
[       ]  Thread-4:       ----<     |   "I'm blocked"
[       ]  Thread-5:         ----<   |   "I'm blocked"
[       ]  Thread-6:           ----< |   "I'm blocked"
[       ]  Thread-7:             ----<   "I'm blocked"
[       ]  Thread-8:               ---
                                     |
num cores utilised:                  1
```

Manager can see:

- new work arriving (request 9)


- no threads free



- **BUT** 7 threads are blocked

> Aha, it's safe to spin up a new thread

---

# Understanding the mentality

> Aha, it's safe to spin up a new thread

```
                     1   2   3   4   5   6   7   8   9   10
 dequeues                            |
                                     |
[       ]  Thread-1: ----<           |   "I'm blocked"
[       ]  Thread-2:   ----<         |   "I'm blocked"
[       ]  Thread-3:     ----<       |   "I'm blocked"
[       ]  Thread-4:       ----<     |   "I'm blocked"
[       ]  Thread-5:         ----<   |   "I'm blocked"
[       ]  Thread-6:           ----< |   "I'm blocked"
[       ]  Thread-7:             ----<   "I'm blocked"
[       ]  Thread-8:               ---
                                     |
num cores utilised:                  1
```

Usually you want to avoid making threads:

- increases context switching


- spreads resources thinner

But here we really only have 1 thread needing time slices

---

# Comparing

## Before

Manager sees from the outside

Can only see whether a thread is doing something or not

Doesn't understand the impact on the CPU

## After

Manager has more insight into the impact on the CPU

Has enough knowledge to safely add a thread without causing contention

---

# The result

> Aha, it's safe to spin up a new thread

```
                     1   2   3   4   5   6   7   8   9   10
 dequeues                            |
                                     |
[       ]  Thread-1: ----<           |   "I'm blocked"
[       ]  Thread-2:   ----<         |   "I'm blocked"
[       ]  Thread-3:     ----<       |   "I'm blocked"
[       ]  Thread-4:       ----<     |   "I'm blocked"
[       ]  Thread-5:         ----<   |   "I'm blocked"
[       ]  Thread-6:           ----< |   "I'm blocked"
[       ]  Thread-7:             ----<   "I'm blocked"
[       ]  Thread-8:               ---
[       ]  Thread-9:                 ----   <-------- new thread created
                                     |
num cores utilised:                  2
```

---

# Recap so far

The manager knows whether a worker is blocked based on a signal

Can make more threads to soak up remaining cpu resources

---

# Recap so far

A little more intelligent

---

# dequeues

```
[       ]  thread-1:
[       ]  thread-2:
[       ]  thread-3:
[       ]  thread-4:
[  ???  ]  thread-5:
[       ]  thread-6:
[       ]  thread-7:
[       ]  thread-8:
[       ]  thread-9:
```

Previously: one big queue for everyone

Now: every thread has their own queue

How are they used?

---

# Queueing

When new work arrives, the manager puts it into one of the queues

Example where everyone is busy

```
[ 9, 12  ]  thread-1: (busy)
[ 10, 16 ]  thread-2: (busy)
[ 20     ]  thread-3: (busy)
[ 18, 11 ]  thread-4: (busy)
...
```

---

# Why have multiple queues?

```
[ 9, 12  ]  thread-1: (busy)
[ 10, 16 ]  thread-2: (busy)
[ 20     ]  thread-3: (busy)
[ 18, 11 ]  thread-4: (busy)
...
```

Why not have one big queue?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Why have multiple queues?

```
[ 9, 12  ]  thread-1: (busy)
[ 10, 16 ]  thread-2: (busy)
[ 20     ]  thread-3: (busy)
[ 18, 11 ]  thread-4: (busy)
...
```

Removes lock contention when fetching the next message

Analogous to kafka partitions

---

# Why double ended?

Firstly what is a double ended queue?

---

# Double ended

> Firstly what is a double ended queue?

You can pop from both ends

```
[ 0, 1, 2, 3, 4, 5 ]

(pop from front)
[ 1, 2, 3, 4, 5 ]

(pop from back)
[ 1, 2, 3, 4 ]
```

---

# Popping

```
[ 0, 1, 2, 3, 4, 5 ]
  ^              ^
```

Why do we want to be able to pop from both ends?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Hint

```
 dequeues

[       ]  thread-1:  (parked)
[ 8, 12 ]  thread-2:  (busy)
```

---

# Help out

```
[       ]  thread-1:  (parked)
[ 8, 12 ]  thread-2:  (busy)
```

Thread-2 is backed up with work

Thread-1 is free

Help out your friend!

---

# "Work Stealing"

```
[       ]  thread-1:  (parked)
[ 8, 12 ]  thread-2:  (busy)
```

Innevitably some jobs take longer than others

Queues can become imbalanced

Parked threads can steal work from their friends

---

# Back to our question

> Why do we want to be able to pop from both ends?

A thread accesses its own queue at one end ("front door")

Other threads can access the queue at the other end ("back door")

```
   ----------
  |         |
  |         |
 \ /        |
[ ..... ]  thread
     / \
      |
      ---- other threads
```

The thread won't contend with its friends

---

# Example

```
[ 0, 1, 2 ]   thread-1 (just finished)
[         ]   thread-2 (just finished)
```

- thread-1 takes message 0


- thread-2 takes message 2

---

# Non-deterministic

Fork join pools _aren't_ deterministic regarding order

ie.

> If job X is submitted before job Y,
>
> job Y might execute before job X

---

# Example

Two threads both busy but empty queue:

```
[    ] Thread-1: (busy)
[    ] Thread-2: (busy)
```

Message X arrives and goes to Thread-1:

```
[ X  ] Thread-1: (busy)
[    ] Thread-2: (busy)
```

Messay Y arrives and goes to Thread-2:

```
[ X  ] Thread-1: (busy)
[ Y  ] Thread-2: (busy)
```

Thread-2 finishes first and starts on message Y:

```
[ X  ] Thread-1: (busy)
[    ] Thread-2: (busy with Y)
```

Thread-1 finishes and starts on message X:

```
[    ] Thread-1: (busy with X)
[    ] Thread-2: (busy with Y)
```

---

# The point

Message X was queued first, but message Y was processed first 

---

# More extreme example

```
[ 0, 1, 2, 3, 4, 5, 6 ] Thread-1: (busy)
[                     ] Thread-2: (free)
```

Thread-2 steals job 6 (newest job)

Job 6 is done before job 0 (queue jumper)

---

# Recap

A fork join pool:

- can increase it's threads to utilise CPU


- threads get their own queues


- threads can steal work from each other


- isn't deterministic in terms of processing order

---

```
 _     _            _    _
| |__ | | ___   ___| | _(_)_ __   __ _
| '_ \| |/ _ \ / __| |/ / | '_ \ / _` |
| |_) | | (_) | (__|   <| | | | | (_| |
|_.__/|_|\___/ \___|_|\_\_|_| |_|\__, |
                                 |___/
```

---

# What does it look like?

We have this conceptual picture:

```
                     1   2   3   4   5   6   7   8   9   10
 dequeues                            |
                                     |
[       ]  Thread-1: ----<           |   "I'm blocked"
[       ]  Thread-2:   ----<         |   "I'm blocked"
[       ]  Thread-3:     ----<       |   "I'm blocked"
[       ]  Thread-4:       ----<     |   "I'm blocked"
[       ]  Thread-5:         ----<   |   "I'm blocked"
[       ]  Thread-6:           ----< |   "I'm blocked"
[       ]  Thread-7:             ----<   "I'm blocked"
[       ]  Thread-8:               ---
[       ]  Thread-9:                 ----   <-------- new thread created
                                     |
num cores utilised:                  2
```

How to code the signalling?

---

# blocking

## Before

```scala
Future {
  workHard(20_000_000)
  Thread.sleep(5000)
  workHard(20_000_000)
}
```

## After

```scala
import scala.concurrent.blocking
Future {
  workHard(20_000_000)
  blocking { // "I'm blocked"
    Thread.sleep(5000)
  } // "Not blocked anymore"
  workHard(20_000_000)
}
```

---

# Not magic

```scala
import scala.concurrent.blocking
Future {
  workHard(20_000_000)
  blocking { // "I'm blocked"
    Thread.sleep(5000)
  } // "Not blocked anymore"
  workHard(20_000_000)
}
```

You have to explicitly signal it with `blocking`

---

# Doesn't always work

The execution context needs to support this feature (not all do)

```scala
  /** Used to designate a piece of code which potentially blocks, allowing the current [[BlockContext]] to adjust
   *  the runtime's behavior.
   *  Properly marking blocking code may improve performance or avoid deadlocks.
                                     ^^^
   */
  def blocking[T](body: =>T): T = 
```

> (thread) I'm blocked!
>
> (ec) And?

---

# Example: Fixed sized thread pool

> (thread) I'm blocked!
>
> (ec) And?

```
          1   2   3   4   5   6   7   8   9   10
                          |
                          |
Thread-1: ----<           |   "I'm blocked"
Thread-2:   ----<         |   "I'm blocked"
Thread-3:     ----<       |   "I'm blocked"
Thread-4:       ----<     |   "I'm blocked"
Thread-5:         ----<   |   "I'm blocked"
Thread-6:           ----< |   "I'm blocked"
Thread-7:             ----<   "I'm blocked"
Thread-8:               ---
                          |
num cores utilised:       1
```

"Fixed size"

ie. it can't add new threads

---

# Recap

`blocking` _may_ help you

Requires an execution context that is able to use that information

---

```
 ____                       
|  _ \  ___ _ __ ___   ___  
| | | |/ _ \ '_ ` _ \ / _ \ 
| |_| |  __/ | | | | | (_) |
|____/ \___|_| |_| |_|\___/ 
                            
 _____ _                
|_   _(_)_ __ ___   ___ 
  | | | | '_ ` _ \ / _ \
  | | | | | | | | |  __/
  |_| |_|_| |_| |_|\___|
                        
```

---

# Demo's

- show `blocking` doesn't help fixed size thread pools


- show fork join pools aren't magic (ie. need `blocking` keyword)


- show fork join pool works better with `blocking`

---

# Going to speed it up too

2 requests per second

Makes it easier to show off `blocking`

---

# Demo 1

---

# Demo 1

> show `blocking` doesn't help fixed size thread pools

```diff
+import scala.concurrent.blocking

 for (...) {
-  Thread.sleep(1000)
+  Thread.sleep(500)

   Future {
     workHard(20_000_000) 
-    if (sleep) Thread.sleep(5000)
+    blocking { if (sleep) Thread.sleep(5000) }
     workHard(20_000_000) 
   }
 }
```

---

# Expectations:

- no threads created


- same green-purple-green zig zag pattern

Demo it!

---

# Conclusion

Adding `blocking` doesn't help if you use a fixed size thread pool as your ec

---

# Demo 2

---

# Demo 2

> show fork join pools aren't magic
>
> (ie. need `blocking` keyword)

Back out our blocking:

```diff
 Future {
   workHard(20_000_000) 
-  blocking { if (sleep) Thread.sleep(5000) }
+  if (sleep) Thread.sleep(5000)
   workHard(20_000_000) 
 }
```

and add another ec to our list:

```diff
 object EcExtract {
   def unapply(ecDescription: String): Option[ExecutionContext] = ecDescription match {
     case "free" => Some(ThreadExecutionContext)
     case "pooled" => Some(EightThreadExecutionContext)
+    case "fork-join" => Some(scala.concurrent.ExecutionContext.global)
     case _ => None
   }
 }
```

---

# Quick detour

`scala.concurrent.ExecutionContext.global`

Built with this:

```scala
def createDefaultExecutorService(reporter: Throwable => Unit): ExecutorService = {
  ...
  val numThreads = getInt(
    "scala.concurrent.context.numThreads",
    default = Runtime.getRuntime.availableProcessors
  )

  // The hard limit on the number of _active_ threads that the thread factory will produce
  val maxNoOfThreads = getInt(
    "scala.concurrent.context.maxThreads",
    default = Runtime.getRuntime.availableProcessors
  )

  // How many extra to make when blocking detected
  val maxExtraThreads = getInt(
    "scala.concurrent.context.maxExtraThreads",
    default = 256
  )

  ...

  val threadFactory = new ExecutionContextImpl.DefaultThreadFactory(
    daemonic = true,
    prefix = "scala-execution-context-global",
    ...
  )

  new ForkJoinPool(desiredParallelism, threadFactory, ...)
}
```

---

# Main points

- uses `Runtime.getRuntime.availableProcessors` or config to configure the pool


- runs as background threads


- has a hard limit on the number of extra threads that are made


- uses a `ForkJoinPool`

---

# Back to our experiment

---

# Expectations

- ec will create 8 threads for us


- won't create any more as we aren't using `blocking`


(ie. will behave like a fixed size thread pool)

Demo time!

---

# Demo 3

---

# Demo 3

> show fork join pool works better with `blocking`

Put our blocking back in

```diff
 Future {
   workHard(20_000_000) 
-  if (sleep) Thread.sleep(5000)
+  blocking { if (sleep) Thread.sleep(5000) }
   workHard(20_000_000) 
 }
```

---

# Expectations

New threads will be made when we have:

- no threads avaialable

- but some are blocked

```
                     1   2   3   4   5   6   7   8   9   10
 dequeues                            |
                                     |
[       ]  Thread-1: ----<           |   "I'm blocked"
[       ]  Thread-2:   ----<         |   "I'm blocked"
[       ]  Thread-3:     ----<       |   "I'm blocked"
[       ]  Thread-4:       ----<     |   "I'm blocked"
[       ]  Thread-5:         ----<   |   "I'm blocked"
[       ]  Thread-6:           ----< |   "I'm blocked"
[       ]  Thread-7:             ----<   "I'm blocked"
[       ]  Thread-8:               ---
[       ]  Thread-9:                 ----   <-------- new thread created
                                     |
num cores utilised:                  2
```

Demo it!

---

```
 _____          _        _       _      ___ 
|  ___|__  _ __| | __   (_) ___ (_)_ __|__ \
| |_ / _ \| '__| |/ /   | |/ _ \| | '_ \ / /
|  _| (_) | |  |   <    | | (_) | | | | |_| 
|_|  \___/|_|  |_|\_\  _/ |\___/|_|_| |_(_) 
                      |__/                  
```

> This fork join pool is cool,
>
> but what does it have to do with forks?

(I'll be surprised if we got this far without anyone asking this)

---

# My _guess_

It fits well with recursive divide and conquer style algorithms

Example: Add all the numbers in a tree

```
                    5
                   / \ 
                  3   6
                 / \ / \
                .........
```

---

# At each node

If it's a parent, fork off workers for its kids, then join the results (blocks)

If it's a leaf, return your value

```
                    5
                   / \ 
                  3   6
                 / \ / \
                .........
```

We don't want to spin up millions of threads though

---

# Relevance

> This fork join pool is cool,
>
> but what does it have to do with forks?

We'll never use it for fork-join style work

What matters to us:

- avoids thread starvation


- uses work stealing

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

# Fork join pool

A nice hybrid

Performs well, and can utilise `blocking`

---

# global ec

Implemented using a fork join pool

---

# Common

Used in frameworks like play and akka

---

```
  ___                  _   _                 
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___ 
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
                                             
```
