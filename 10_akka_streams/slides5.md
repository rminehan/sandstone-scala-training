---
author: Rohan
date: 2022-01-31
title: Akka Streams 5
---

```
    _    _    _         
   / \  | | _| | ____ _ 
  / _ \ | |/ / |/ / _` |
 / ___ \|   <|   < (_| |
/_/   \_\_|\_\_|\_\__,_|
                        
 ____  _                                
/ ___|| |_ _ __ ___  __ _ _ __ ___  ___ 
\___ \| __| '__/ _ \/ _` | '_ ` _ \/ __|
 ___) | |_| | |  __/ (_| | | | | | \__ \
|____/ \__|_|  \___|\__,_|_| |_| |_|___/
                                        
```

Part 5

---

# Today

Async stuff

---

# Example

Translate user id's to users

```scala
val userIds: Source[UserId, NotUsed] = ...

// Go to database or some api
def getUser(userId: UserId): Future[User]
```

---

# Warm up question

Suppose we map `getUsers` over our source:

```scala
val userIds: Source[UserId, NotUsed] = ...

// Go to database or some api
def getUser(userId: UserId): Future[User]

val users = userIds.map(getUser)
```

What is the type of `users`?

`Source[???, NotUsed]`

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Future[User]

```scala
val userIds: Source[UserId, NotUsed] = ...

// Go to database or some api
def getUser(userId: UserId): Future[User]

val users = userIds.map(getUser)
```

> What is the type of `users`?

`Source[Future[User], NotUsed]`

```
                               map(getUser)
|===== UserId     +     UserId ============ Future[User]    =    |================= Future[User]
```

---

# Type is a bit wrong

We have `Future[User]` flowing through our stream...

Would prefer `User`

```
                               map(getUser)
|===== UserId     +     UserId ============ Future[User]    =    |================= Future[User]
```

---

# Demo 7

Let's try it out

---

# Demo 7 recap

No back pressure

Would be overwhelming

---

# Demo 7 recap

```
                               map(getUser)
|===== UserId     +     UserId ============ Future[User]
  FAST                             SLOW?
```

If back pressue is working, we'd expect the source to slow down

---

# Demo 7 Recap

Our sleep represents our "hard work"

```scala
def getUser(userId: UserId): Future[User] = {
  Future {
    Thread.sleep(1000)

    ...
  }
}
```

But what thread is the sleep running on?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Threads

```scala
def getUser(userId: UserId): Future[User] = {
  Future {
    Thread.sleep(1000)

    ...
  }(global)
}
```

> What thread is the sleep running on?

Runs in the global execution context

It represents the slow hard work

---

# Who's doing what?

```scala
def getUser(userId: UserId): Future[User] = {
  // Stream thread
  Future {
    // Global ec thread
    Thread.sleep(1000)

    ...
  }(global)
}
```

The stream is just starting the Future

The global ec is fetching the user

---

# Starting Future's 

> The stream is just starting the Future

Starting a Future is very fast

Running the Future is usually slow

---

# Analogy

Lee making jira tickets

---

# Analogy

Lee has an endlist list of tasks to be converted into tickets

Creating a ticket is cheap compared to _doing_ the ticket

---

# Back pressure?

Jira board doesn't push back

---

# Overwhelming

> Creating a ticket is cheap compared to _doing_ the ticket

Our board will rapidly fill with tickets

We can't do them fast enough

---

# Back to our initial example

```scala
def getUser(userId: UserId): Future[User] = {
  Future {
    Thread.sleep(1000) // Doesn't slow the stream down

    ...
  }
}
```

```
                               map(getUser)
|===== UserId     +     UserId ============ Future[User]    +    =================|
 Source                            Flow                                 Sink
 FAST                              FAST
                                    | work
                                    | work
                                    | work
                                   \|/

                                global ec SLOW
```

The stream is done with the message once it _starts_ the `Future`

Our global ec is flooded with work (faster than can be completed)

Need to make the global ec push back on the source (even though it's outside the stream)

---

# Why I mention this

Seen this kind of mistake before

---

# Hack in a fix

We want the source to slow down and not overwhelm our system

Demo time!

---

# Recap

Blocked the stream thread

```scala
def getUserBlocking(userId: UserId): User = {
  Await.result(getUser(userId), 3.seconds)
}

ids.map(getUserBlocking).to(printUser).run()
```

---

# Recap - fixed our problems

`getUserBlocking` is slow now from the stream's perspective

Triggers back pressure

```
                                map(getUserBlocking)
|===== UserId --------> UserId ===================== User
  FAST                                SLOW
    <-----------------------------------
                 slow down!
                                        | work
                                        | work
                                        | work
                                       \|/

                                    global ec SLOW
```

Also outputs `User` now which is easier to work with

---

# Problem

`getUserBlocking` solved our issue

But what is the problem with `getUserBlocking`?

```scala
def getUserBlocking(userId: UserId): User = {
  Await.result(getUser(userId), 3.seconds)
}
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Blocking

`getUserBlocking` is blocking

Generally blocking is bad

---

# Recap

Blocked threads are stuck on your task doing nothing,

like sitting waiting for a response to an email

---

# Under utilised

Blocked threads could be doing something else

Leads to issues like thread starvation (low CPU usage and low throughput)

```
Queue  |
       |
       |    Thread-1: job1 (BLOCKED)
       |    Thread-2: job2 (BLOCKED)
       |    Thread-3: job3 (working)
 job13 |    Thread-4: job4 (working)
 job12 |    Thread-5: job5 (BLOCKED)
 job11 |    Thread-6: job6 (BLOCKED)
 job10 |    Thread-7: job7 (BLOCKED)
 job9  |    Thread-8: job8 (BLOCKED)
```

Maybe our actor system is shared with other streams

---

# What we want

- back pressure


- but not blocking

---

# Solution

`mapAsync`

---

# map vs mapAsync

## map

```
f: A => B

    map(f)
  A ====== B
```

## mapAsync

Like `map`, but expects a `Future`

```
g: A => Future[B]

    map(g)
  A ====== Future[B]

   mapAsync(g)
  A ====== B
```

It emits `B`'s, not `Future[B]`'s

---

# Try it out

Back to our demo!

---

# Recap

```scala
ids  // Source[UserId]
  .mapAsync(parallelism = 4)(getUser) // Flow[UserId, User]
  .to(printUser) // Sink[User]
  .run()
```

```
getUser: UserId => Future[User]

       map(getUser)
  UserId ====== Future[User]

   mapAsync(getUser)
  UserId ====== User
```

---

# How does `mapAsync` work?

Hmmm...

---

# My guess

Analogous to this:

```scala
// inside mapAsync

val userId = pull()

val userFut = getUser(userId)

userFut.onComplete { user =>
  push(user)
}
```

---

# Parallelism?

How many futures it will have running at once

---

# Randomise work time

Make the time for each user lookup random

Back to our demo!

---

# Demo recap

With `mapAsync`:

Fast workers have to wait for slow ones before being released into the stream

---

# Inefficient

Preserves order, but inefficient

```
Captain Kirk took 5000
Captain Picard took 1000     <--- waits 4 seconds before releasing
Captain Janeway took 4000    <--- waits 1 second before releasing
Captain Kirk took 5000
Captain Picard took 1000
Captain Janeway took 2000
```

---

# Order doesn't matter

For this particular example we don't need to preserve order

(elsewhere you might care)

---

# `mapAsyncUnordered`

```diff
- ids.mapAsync(parallelism = 3)(getUser).to(printUserAndTime).run()
+ ids.mapAsyncUnordered(parallelism = 3)(getUser).to(printUserAndTime).run()
```

Allows free workers to process new elements once they're done

```
Captain Janeway took 0
Captain Picard took 1000
Captain Kirk took 2000
```

---

# Enough for today

---

```
 ____                      
|  _ \ ___  ___ __ _ _ __  
| |_) / _ \/ __/ _` | '_ \ 
|  _ <  __/ (_| (_| | |_) |
|_| \_\___|\___\__,_| .__/ 
                    |_|    
```

---

# Working with Futures

In real code you'll need to integrate Future logic into your streams

---

# map doesn't cut it

Type signature is wrong/awkward

(will output Futures into the stream)

---

# map doesn't cut it

The future itself is usually slow

But the stream will only see the time to _start_ the future (not run it)

Won't generate the appropriate back pressure

---

# mapAsync

A way of making the stream wait without blocking

Keeps the `Future` out of the stream

---

# mapAsyncUnordered

`mapAsync` will maintain the original order

Can be inefficient

If order doesn't matter, use `mapAsyncUnordered`

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\__,_|\___||___/\__|_|\___/|_| |_|___/

```
