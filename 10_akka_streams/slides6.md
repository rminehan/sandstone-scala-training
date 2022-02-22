---
author: Rohan
date: 2022-02-03
title: Akka Streams 6 - case studies
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

Part 6

---

# Today

A Sandstone case study

Look at a usages of akka streams in our code

---

# Why case study?

Brings what we've learnt together with something real

- async


- back pressure and overflow


- materialisation

---

# Sessions

Might need to split it into two sessions

Depends on the questions

---

# Case study

`mlservice`

(scala one)

---

# Background context

Images get OCR'd and that text is sent to `mlservice` for classification

(similar to the demo the other day)

---

# Recap

See requests in postman

---

# Within mlservice

Requests get pushed into a stream

Focus for today (not the actual ML logic)

---

# Routes

Two kinds of jobs:

```
POST        /job/sync           controllers.MLController.runSync
POST        /job/async          controllers.MLController.runAsync
```

They are similar, so we'll just focus on `/job/sync`

---

# Controller

To the code!

---

# Controller recap

Request is deserialised into

```scala
case class MLJobRequest(jobType: MLJobType, taskId: Option[BSONObjectID], data: JsValue)
```

---

# Sync vs Async

```scala
val exec = if (async) jobExecutionService.runAsync _ else jobExecutionService.runSync _

exec
  .apply(request)
  .map {
    // convert to 200 response
  }
  .recover {
    // convert to error response
  }
```

Follow the trail into `jobExecutionService`

---

# JobExecutionService

To the editor!

---

# JobExecutionService summary

Big class!

This is the only public stuff that matters:

```scala
class JobExecutionService {
  def runSync(jobRequest: MLJobRequest): Future[MLJobResponse] = {
    syncJobQueue.processRequest(jobRequest)
  }

  def runAsync(jobRequest: MLJobRequest): Future[MLJobResponse] = {
    asyncJobQueue.processRequest(jobRequest)
  }
}
```

Within the class there are two queues

---

# Queue summary

Specialised classes configured with length and parallelism

Each queue is constructed with logic for how to process requests,

e.g. `SyncJobQueue` receives a function `MLJobRequest => Future[MLJobResponse]`

---

# Recap so far

- requests go to either `/sync` or `/async` endpoints


- controller deserialises them and sends them to `SyncJobQueue` or `AsyncJobQueue` respectively


- these queues are configured with length and parallelism and processing logic

---

# Peeking inside the queue classes

To the code!

---

# Inside the queue

There is an akka stream

---

# Topology

```
        Source.queue               +                  mapAsync(process)         +         Sink.ignore
      |============= QueuedTask         QueuedTask  ==================== Unit       Unit =============|

```

Sink is just there to create a `ClosedShape`

---

# Materialisation

```
        Source.queue               +                  mapAsync(process)         +         Sink.ignore
      |============= QueuedTask         QueuedTask  ==================== Unit       Unit =============|

Mat:  SourceQueueWithComplete[QueuedTask]                doesn't matter                   doesn't matter
```

---

# Keep.left!

```
        Source.queue               +                  mapAsync(process)         +         Sink.ignore
      |============= QueuedTask         QueuedTask  ==================== Unit       Unit =============|

Mat: (SourceQueueWithComplete[QueuedTask]                doesn't matter)                  doesn't matter
     -------------------------------------- Keep.left ------------------

                        SourceQueueWithComplete[QueuedTask]                               doesn't matter
                        -------------------------------------------- Keep.left -------------------------

                                                    SourceQueueWithComplete[QueuedTask]
```

Final graph materialises `SourceQueueWithComplete[QueuedTask]`

---

# Source.queue

Haven't seen this before

To the scala docs!

---

# queue summary

- creates a `Source[QueuedTask, SourceQueueWithComplete[QueuedTask]]`


- it actually materialises something interesting! (`SourceQueueWithComplete[QueuedTask]`)


- you can manually push elements into this `Source` using the materialised thing


- there is a buffer size and an overflow strategy

---

# queue demo

demo8!

---

# queue demo recap

```scala
val graph = Source
  .queue[Int](bufferSize = 5, overflowStrategy = OverflowStrategy.dropNew)
  .mapAsync(parallelism = 4)(...)
  .to(Sink.foreach(println))

val sourceQueueWithComplete: SourceQueueWithComplete[Int] = graph.run()

sourceQueueWithComplete.offer(0)
sourceQueueWithComplete.offer(1)
sourceQueueWithComplete.offer(2)
```

Our source materialises a thing we can offer elements with

---

# queue demo recap

```scala
val graph = Source
  .queue[Int](bufferSize = 5, overflowStrategy = OverflowStrategy.dropNew)
  .mapAsync(parallelism = 4)(i => Future {
    Thread.sleep(2000)
    i
  }) // on average, 1 job every second
  .to(Sink.foreach(println))

val sourceQueueWithComplete: SourceQueueWithComplete[Int] = graph.run()

for(i <- 0 until 50) {
  Thread.sleep(250) // on average, 1 job every quarter second
  sourceQueueWithComplete.offer(i)
}
```

We're producing work at twice the speed the stream can process it

Buffer eventually overflows and work is dropped

---

# Back pressure detour

Sometimes you can slow down the source

Sometimes you can't

---

# Control

> Sometimes you can slow down the source

If it's something we control,

e.g. homework exercise processing a file

No need to drop work

---

# Less control

> Sometimes you can't

e.g. requests driven by another service

That is outside the control of the stream

---

# No perfect solution

> e.g. requests driven by another service

If the stream can't keep up, we have a real problem

- can't slow it down


- can't buffer forever

---

# Inevitable loss of service

> If the stream can't keep up, we have a real problem

One way or another we can't meet the demand

---

# Options

No win situation, choose the least evil option

- crash and burn


- degrade service (e.g. dropping some jobs)

---

# Options

No win situation, choose the least evil option

- crash and burn


- degrade service (e.g. dropping some jobs)

Degrading is the least evil

---

# Akka streams

Let's us control this with `OverflowStrategy`

---

# Recap

`mlservice` case study

---

# Recap (high level)

- requests go to either `/sync`


- controller deserialises them into `MLJobRequest`


- pushed to `SyncJobQueue`

---

# Recap (SyncJobQueue)

- inside is an akka stream


- stream uses `Source.queue`


- that materialises a handle into the stream which lets us push in elements

---

# Recap (buffer overflow)

- our stream is configured with a buffer length


- if too many messages flow in, an overflow strategy is used


- in our case it drops the message causing us to return an error

---

# QueuedTask?

```
        Source.queue               +                  mapAsync(process)         +         Sink.ignore
      |============= QueuedTask         QueuedTask  ==================== Unit       Unit =============|

```

```scala
case class QueuedTask(request: MLJobRequest, result: Promise[MLJobResponse])
```

The thing we put into the stream

Why not just use `MLJobRequest`? What is this `Promise`?

---

# Detour: promises

---

# Future and Promise

Two sides of the same coin

---

# Scenario

A king sends a knight on a sacred quest to find an `Int` in a foreign land

---

# Scenario

> A king sends a knight on a sacred quest to find an `Int` in a foreign land

The knight will take a long time

The king doesn't want to be blocked waiting around for the `Int`

---

# Future

> The king doesn't want to be blocked waiting around for the `Int`

The king wants a `Future[Int]` to track the knight's progress

ie. something which eventually yields an `Int`

---

# Notification

> The king wants a `Future[Int]` to keep track of the knight's progress

Once the knight finds the `Int`,

the king wants to be notified immediately

(even though the knight is in a distant land)

---

# Future and Promise

> Once the knight finds the `Int`,
>
> the king wants to be notified immediately

The wizard has a magical promise/future pair that are connected

When the knight pushes a value into the promise,

the future completes with that value immediately

---

# Future and Promise

The king gets the `Future[Int]`

The knight gets the `Promise[Int]`

They are connected behind the scenes

---

# Future

> The king gets the `Future[Int]`

He can register callbacks for when the value arrives:

```scala
// King's logic
future.foreach { int =>
  println(s"Attention everyone: My trusty knight found me: $int")
}
```

---

# Promise

> The knight gets the `Promise[Int]`

When he finds the `Int`, he pushes the value into the promise

```scala
promise.success(42)
```

---

# Promise

> The knight gets the `Promise[Int]`

When he finds the `Int`, he pushes the value into the promise

```scala
promise.success(42)
```

or if something goes wrong, he can complete the promise with a failure

```scala
promise.failure(new HammyException("Yikes, I pulled my hammy fighting a dragon, quest failed"))
```

---

# Two sides of the same coin

The moment the knight pushes a value into the promise,

the king's future completes

```scala
// knight's thread
promise.success(42)

future.foreach { int =>
  println(s"Attention everyone: My trusty knight found me: $int") // callback triggers
}
```

---

# Promise demo

knights_quest.sc

---

# Recap

When you create a `Promise`, there is a `Future` on it that observers can access

```scala
val promise = Promise[Int]()

val kingsFuture = promise.future
```

---

# Division of responsibility

The `Promise` is used by some worker to signal when something is done (knight)

The `Future` is intended for anyone wanting to observe the promise (king)

Two sides of the same coin

---

# Abstraction

The `Future` abstracts over all the details

Lets observers register callbacks

---

# Usefulness

When are promises useful?

---

# Usefulness

> When are promises useful?

Usually when we have to handroll our own futures to track something happening elsewhere

---

# Why "promise"?

Represents a promise to eventually produce something,

as if the knight promises the king to find him a value

Promises aren't fulfilled straight away

---

# Back to our question

> What is `QueuedTask` about?

```scala
case class QueuedTask(request: MLJobRequest, result: Promise[MLJobResponse])
```

We are pairing each request with a promise before sending it into the stream

The stream will complete the promise

That lets us track what is going on from outside the stream via a Future

---

# Back to our question

```
        Source.queue               +                  mapAsync(process)         +         Sink.ignore
      |============= QueuedTask         QueuedTask  ==================== Unit       Unit =============|
        /|\
         |
         |
   val result = Promise[MLJobResponse]()

   val future = result.future
         |
         |
        \|/
   return to caller
   of processRequest
```

```scala
case class QueuedTask(request: MLJobRequest, result: Promise[MLJobResponse])
```

> We are pairing each request with a promise before sending it into the stream
>
> The stream will complete the promise
>
> That lets us track what is going on from outside the stream via a Future

---

# Back to our question

```
        Source.queue               +                  mapAsync(process)         +         Sink.ignore
      |============= QueuedTask         QueuedTask  ==================== Unit       Unit =============|
        /|\                                           result.success(...)
         |                                            result.failure(...)
         |                                                  |
   val result = Promise[MLJobResponse]()                    |
                                                            | Future completes
   val future = result.future        <----------------------
         |
         |
        \|/
   return to caller
   of processRequest
```

```scala
case class QueuedTask(request: MLJobRequest, result: Promise[MLJobResponse])
```

Somewhere inside the stream the promise will succeed or fail

Observers outside the stream will see that as a completed successful/failed future

---

# The essence of this design

```
        Source.queue               +                  mapAsync(process)         +         Sink.ignore
      |============= QueuedTask         QueuedTask  ==================== Unit       Unit =============|
        /|\                                           result.success(...)
         |                                            result.failure(...)
         |                                                  |
   val result = Promise[MLJobResponse]()                    |
                                                            | Future completes
   val future = result.future        <----------------------
         |
         |
        \|/
   return to caller
   of processRequest
```

```scala
case class QueuedTask(request: MLJobRequest, result: Promise[MLJobResponse])
```

It gives observers outside the stream access to what's going on within the stream

(normally streams are very closed off)

---

# Unit

Now it makes sense why `mapAsync` is producing a `Unit`

```
        Source.queue               +                  mapAsync(process)         +         Sink.ignore
      |============= QueuedTask         QueuedTask  ==================== Unit       Unit =============|
        /|\                                           result.success(...) ^^
         |                                            result.failure(...) ^^
         |                                                  |
   val result = Promise[MLJobResponse]()                    |
                                                            | Future completes
   val future = result.future        <----------------------
         |
         |
        \|/
   return to caller
   of processRequest
```

---

# To the code!

With this context hopefully the code makes more sense

---

# Recap

For `/sync`, when processing a request

- create a promise


- pair the promise with the request (`QueuedTask`)


- try to queue that


- if you can't even queue it, just fail


- otherwise the stream will use the `processs` logic it was configured with to process the request


- return the future associated with that promise


- later the stream will complete the promise

---

# That's enough for today

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

# Case study

Saw a real stream used in `mlservice` to process jobs

---

# queue

Saw `Stream.queue` gives a way to manually push elements into a stream

---

# Overflow

When you can't slow the stream down, you can get overflow

Akka streams let you specify how to handle that

---

# Promise

A mechanism to create your own future

Useful when you are tracking the progress of something happening elsewhere

---

# That's it for akka streams!

Look at kafka next

Then back to functional programming

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\__,_|\___||___/\__|_|\___/|_| |_|___/

```
