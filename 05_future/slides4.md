---
author: Rohan
date: 2021-11-22
title: Execution
---

```
 _____                     _   _             
| ____|_  _____  ___ _   _| |_(_) ___  _ __  
|  _| \ \/ / _ \/ __| | | | __| |/ _ \| '_ \ 
| |___ >  <  __/ (__| |_| | |_| | (_) | | | |
|_____/_/\_\___|\___|\__,_|\__|_|\___/|_| |_|
```

---

# Quick Recap!

---

# Future[A]

A handle to a computation running somewhere else

---

# Future[A]

A handle to a computation running somewhere else

Will eventually produce an `A` (or fail)

---

# Tools

- `.map`


- `.flatMap`


- `.recover`


- `.recoverWith`


- `Future.successful`


- `Future.sequence/traverse`

---

# Today

> A handle to a computation running _somewhere else_

Somewhere else?

Where!?

---

# Where?

```scala
Future {
  ...
}.map {
  ...
}.flatMap {
  ...
}.recover {
  ...
}
```

Where is each `...` running?

---

# Today

Looking under the hood a bit more

---

# Why

> Looking under the hood a bit more

But isn't the point of a library like `Future` that you don't have to care?

---

# Why

> But isn't the point of a library like `Future` that you don't have to care?

Mostly yes, but sometimes it matters where something is running

(particularly blocking logic)

---

# Introducing...

```
 _____                     _   _              ____            _            _   
| ____|_  _____  ___ _   _| |_(_) ___  _ __  / ___|___  _ __ | |_ _____  _| |_ 
|  _| \ \/ / _ \/ __| | | | __| |/ _ \| '_ \| |   / _ \| '_ \| __/ _ \ \/ / __|
| |___ >  <  __/ (__| |_| | |_| | (_) | | | | |__| (_) | | | | ||  __/>  <| |_ 
|_____/_/\_\___|\___|\__,_|\__|_|\___/|_| |_|\____\___/|_| |_|\__\___/_/\_\\__|
                                                                               
```

`scala.concurrent.ExecutionContext`

---

# Rough definition

> An `ExecutionContext` represents resources to execute jobs on

---

# Abstraction

`ExecutionContext` is an abstraction

You give it a job represented by `Runnable` and it executes it somehow:

```scala
trait ExecutionContext {
  def execute(runnable: Runnable): Unit
}
```

---

# Abstraction

_How_ it executes it depends on the implementation

```scala
trait ExecutionContext {
  def execute(runnable: Runnable): Unit
}
```

---

# Abstraction

> _How_ it executes it depends on the implementation

Examples:

- create a new thread and run it on that


- run it on a "thread pool"

---

# Relationship to Future?

---

# Relationship to Future?

Every `...` here is running on an execution context

```scala
Future {
  ...
}.map {
  ...
}.flatMap {
  ...
}.recover {
  ...
}
```

ie. the `Future` library translates each bit of code into a `Runnable`

and submits it to some execution context

---

# Desugaring

```scala
Future {
  "hi!"
}.map { s =>
  s.toUpperCase
}
```

What's really going on here?

To intellij!

---

# Summary

```scala
// Defined somewhere
implicit ec: ExecutionContext

// Sugary
Future {
  "hi!"
}.map { s =>
  s.toUpperCase
}

// Desugared
Future.apply("hi!")(ec)
  .map(s => s.toUpperCase)(ec)
```

---

# Observations

Whenever we start a Future or define a continuation,

there is always an execution context being passed through,

it's just that it's almost always an implicit parameter

---

# The "standard" one

To the repl!

---

# Summary

We have stumbled on a new feature of scala: implicit parameters

---

# Implicits aren't compulsory

To intellij!

---

# Future is a good use case for implicits

> Implicits aren't compulsory

Which looks nicer:

```scala
implicit val ec: ExecutionContext = ...

Future {
  ...
}.map {
  ...
}.flatMap {
  ...
}
```

or

```scala
val ec: ExecutionContext = ...

Future {
  ...
}(ec).map {
  ...
}(ec).flatMap {
  ...
}(ec)
```

---

# Don't forget for comprehensions

To intellij!

---

# Summary

The `Future` library uses execution contexts to run code

---

# Runnable

> The `Future` library uses execution contexts to run code

```scala
trait ExecutionContext {
  def execute(runnable: Runnable): Unit
}


Future {
  ...   // Translates thunk into a Runnable and submits to ec
}(ec).map {
  ...   // Translates continuation likewise
}(ec).flatMap {
  ...
}(ec).recover {
  ...
}(ec)
```

---

# Implicit parameters

An advanced language feature for scala

Good for cases where it gets tedious to keep explicitly passing a certain value in

Another common example is a database connection object

---

# Implicit parameters

You can always pass explicitly if you prefer

---

# Desugaring

Useful tool to undo some of the scala magic

e.g. tells you where an implicit is being supplied from

---

```
 ___                 _                           _   
|_ _|_ __ ___  _ __ | | ___ _ __ ___   ___ _ __ | |_ 
 | || '_ ` _ \| '_ \| |/ _ \ '_ ` _ \ / _ \ '_ \| __|
 | || | | | | | |_) | |  __/ | | | | |  __/ | | | |_ 
|___|_| |_| |_| .__/|_|\___|_| |_| |_|\___|_| |_|\__|
              |_|                                    
             
  __ _ _ __  
 / _` | '_ \ 
| (_| | | | |
 \__,_|_| |_|
             
 _____                     _   _              ____            _            _   
| ____|_  _____  ___ _   _| |_(_) ___  _ __  / ___|___  _ __ | |_ _____  _| |_ 
|  _| \ \/ / _ \/ __| | | | __| |/ _ \| '_ \| |   / _ \| '_ \| __/ _ \ \/ / __|
| |___ >  <  __/ (__| |_| | |_| | (_) | | | | |__| (_) | | | | ||  __/>  <| |_ 
|_____/_/\_\___|\___|\__,_|\__|_|\___/|_| |_|\____\___/|_| |_|\__\___/_/\_\\__|
                                                                               
```

---

# Building intuition

Let's define our own execution context to get a better sense of how it works

---

# How will it work?

When a job is submitted, spin up a new thread and run it on that

---

# Threads?

If you don't have a strong intuition for threads,

we'll cover them in a bit more detail next time 

---

# In essence...

Your code runs on a thread

(The basic unit of computation)

---

# Multi-threaded?

If you want to do 2 things at once,

you'll need 2 threads

---

# Quick analogy

> When a job is submitted, spin up a new thread and run it on that

Analogy: hiring a new contractor to do a job, then firing them once it's done 

---

# Let's build it!

To the repl!

---

# Summary

An implementation of `ExecutionContext` that spins up a new thread for each job its given:

```scala
object ThreadExecutionContext extends ExecutionContext {
  def execute(runnable: Runnable): Unit = {
    val thread = new Thread(runnable)
    thread.start
  }

  def reportFailure(cause: Throwable): Unit = {
    println(s"Failure: $cause")
  }
}
```

---

```
 ___                 _                           _   
|_ _|_ __ ___  _ __ | | ___ _ __ ___   ___ _ __ | |_ 
 | || '_ ` _ \| '_ \| |/ _ \ '_ ` _ \ / _ \ '_ \| __|
 | || | | | | | |_) | |  __/ | | | | |  __/ | | | |_ 
|___|_| |_| |_| .__/|_|\___|_| |_| |_|\___|_| |_|\__|
              |_|                                    
                   _   _               
  __ _ _ __   ___ | |_| |__   ___ _ __ 
 / _` | '_ \ / _ \| __| '_ \ / _ \ '__|
| (_| | | | | (_) | |_| | | |  __/ |   
 \__,_|_| |_|\___/ \__|_| |_|\___|_|   
                                       
 _____                     _   _              ____            _            _   
| ____|_  _____  ___ _   _| |_(_) ___  _ __  / ___|___  _ __ | |_ _____  _| |_ 
|  _| \ \/ / _ \/ __| | | | __| |/ _ \| '_ \| |   / _ \| '_ \| __/ _ \ \/ / __|
| |___ >  <  __/ (__| |_| | |_| | (_) | | | | |__| (_) | | | | ||  __/>  <| |_ 
|_____/_/\_\___|\___|\__,_|\__|_|\___/|_| |_|\____\___/|_| |_|\__\___/_/\_\\__|
                                                                               
```

---

# Introducing a "thread pool"

---

# What is a thread pool?

A reserved set of threads

---

# Analogy

A dedicated group of full time employees pulling work off a queue

(e.g. a jira board!)

---

# Crucial difference

## First example

Spin up a new thread for each job

Like hiring a new worker for each job

## Second example

Threads reserved and waiting to work

Jobs put into a queue

Parallelism limited by size of the threadpool

---

# Possible delay

> Jobs put into a queue
>
> Parallelism limited by size of the threadpool

```
submit
job
|
------------------>
     worker
     starts on it
     |
     ------------------>
```

---

# Creating a thread pool

Use the `java.util.concurrent` library

To intellij!

---

# Building our execution context

To the repl!

---

# Summary

```scala
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object EightThreadExecutionContext extends ExecutionContext {
  private val threadPool = Executors.newFixedThreadPool(8)

  def execute(runnable: Runnable): Unit = {
    threadPool.submit(runnable)
  }

  def reportFailure(cause: Throwable): Unit = {
    println(s"Failure: $cause")
  }
}
```

---

# And some ammonite tricks...

```scala
// Import a script into the repl
@ import $file.[FILE STEM]

// Import a script into the repl _and_ import all the definitions inside it
@ import $file.[FILE STEM], [FILE STEM]._
```

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

# ExecutionContext

An abstraction representing how to run jobs

---

# ExecutionContext and Future

Under the hood, `Future` converts jobs into `Runnable`

then submits them to the `ExecutionContext` you provided

---

# Implicit ExecutionContext

You must always supply an `ExecutionContext`

(it's usually being supplied implicitly)

---

# Example execution contexts

- create a thread for every job


- use a dedicated thread pool

(other examples we could come up with)

---

# Leaky abstraction

Can't switch off our brains completely

Whilst `ExecutionContext` is an abstraction,

we still need a basic awareness of how it works

(more on this next time)

---

# Next time

Lower level execution concepts

Cores, blocking, threads...

---

```
  ___                  _   _                 
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___ 
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \ ?
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
                                             
```
