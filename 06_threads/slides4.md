---
author: Rohan
date: 2021-12-01
title: Locks
---

```
 _               _        
| |    ___   ___| | _____ 
| |   / _ \ / __| |/ / __|
| |__| (_) | (__|   <\__ \
|_____\___/ \___|_|\_\___/
                          
```

---

# Recap

- threads can get blocked


- can cause thread starvation

ie. work building up, CPU not fully utilised

---

# Today

Locks

Another cause of blocking

Watch out!

---

# Realistically

> Another cause of blocking
> 
> Watch out!

You won't use locks much

Covering this more for completeness

---

# Agenda

- photocopier analogy introduces concepts


- motivate locks: understand the "check then act" bug


- show how to use locks to make code atomic


- understand that locks cause blocking

---

```
  ___                     _   
 / _ \ _ __   ___    __ _| |_ 
| | | | '_ \ / _ \  / _` | __|
| |_| | | | |  __/ | (_| | |_ 
 \___/|_| |_|\___|  \__,_|\__|
                              
         _   _                
  __ _  | |_(_)_ __ ___   ___ 
 / _` | | __| | '_ ` _ \ / _ \
| (_| | | |_| | | | | | |  __/
 \__,_|  \__|_|_| |_| |_|\___|
                              
```

The photocopier analogy

---

# One at a time

Some things should only be accessed one a time

Examples?

```
 ___ ___ ___ 
|__ \__ \__ \
  / / / / / /
 |_| |_| |_| 
 (_) (_) (_) 
             
```

---

# Examples

- toilet


- photocopier

---

# Photocopier example

Robots are programmed to photocopy paper:

- lift lid of copier


- place paper face down


- close lid of copier


- push copy button


- wait for copy to appear


- take copy from tray


- take original

---

# Scenario

Imagine two robots overlapping on the same photocopier

---

# Photocopier example

> Imagine two robots on the same photocopier

One possible scenario

```
time | Robot A                | Robot B
-----|------------------------|----------------------
 0   | lift lid of copier     |
 1   | place paper face down  |
 2   | close lid of copier    |
 3   | push copy button       |
 4   |     <waiting>          | lift lid of copier      <---- copying with lid raised!
 5   |     <waiting>          | place paper face down
 6   | take copy from tray    | close lid of copier
 7   | take original          | push copy button        <---- takes B's original, copies A's again!
 8   |                        |     <waiting>
 9   |                        | take copy from tray
10   |                        | take original           <---- takes A's original

```

A robot sitcom

---

# Analogy

Robots are like threads

No common sense

Don't know or care if someone else is using it

Will just blindly follow instructions

---

# Programming

> Will just blindly follow instructions

In our mind we picture having exclusive access to it

---

# One at a time

> In our mind we picture having exclusive access to it

Generally if you are applying complex mutations,

form a queue

---

# Blocked

> form a queue

Analogy: the robots are standing in line doing nothing

CPU: blocking and thread starvation

---

# Recapping

Our analogy introduced concepts:

- complex mutations


- concurrency bugs


- the need for exclusive access


- blocking

---

```
  ____ _               _    
 / ___| |__   ___  ___| | __
| |   | '_ \ / _ \/ __| |/ /
| |___| | | |  __/ (__|   < 
 \____|_| |_|\___|\___|_|\_\
                            
 _   _                
| |_| |__   ___ _ __  
| __| '_ \ / _ \ '_ \ 
| |_| | | |  __/ | | |
 \__|_| |_|\___|_| |_|
                      
            _   
  __ _  ___| |_ 
 / _` |/ __| __|
| (_| | (__| |_ 
 \__,_|\___|\__|
                
```

Common kind of concurrency bug

---

# Consider this code

```scala
var total = 0

val future = Future.traverse(1 to 10_000) { _ =>
  Future {
    total += 1
  }
}

Await.result(future, 30.seconds)
```

---

# Final total?

```scala
var total = 0

val future = Future.traverse(1 to 10_000) { _ =>
  Future {
    total += 1
  }
}

Await.result(future, 30.seconds)

println(s"After 10,000 iterations, total is: $total")
```

What total will it print...

---

# What will it print?

```scala
var total = 0

val future = Future.traverse(1 to 10_000) { _ =>
  Future {
    total += 1
  }
}

Await.result(future, 30.seconds)

println(s"After 10,000 iterations, total is: $total")
```

10K right?

- starts at 0


- 10K Futures


- each causes it to increase by 1

---

# Vote time

> What will it print?

Who thinks:

- 10K


- anything but 10K

---

# Vote time

> What will it print?

Who thinks:

- 10K


- anything but 10K

By now you should know it's a trick and won't be 10K

Otherwise what's the point of this?

---

# Run it

To the repl!

---

# Results

Always a bit short of 10K

---

# Why?

> Always a bit short of 10K

```
 ___ ___ ___ 
|__ \__ \__ \
  / / / / / /
 |_| |_| |_| 
 (_) (_) (_) 
             
```

---

# Modify it

```diff
 import scala.concurrent.Future
 import scala.concurrent.ExecutionContext.Implicits.global
 import scala.concurrent.Await
 import scala.concurrent.duration._
+import java.util.concurrent.atomic.AtomicInteger

...

-var total = 0
+val total = new AtomicInteger(0)

...

  Future {
-   total += 1
+   total.incrementAndGet()
  }
```

---

# See how this one goes!

To the repl!

---

# Results

Printed 10K

---

# "Atomic"?

The problem with the first example:

```scala
var total = 0

...

for (...) {
  Future {
    total += 1 // <---- not atomic
  }
}
```

---

# Not atomic

```scala
total += 1

// is really
total = total + 1

// equivalent to:
val currentTotal = total // Check
val newValue = currentTotal + 1
total = newValue // Act
```

---

# Check then act

```scala
val currentTotal = total         // Check  <--
                                 //           | Time gap
                                 //           |
val newValue = currentTotal + 1  //           | What if someone else
                                 //           |  changed total?
total = newValue                 // Act    <--
```

Between "check" and "act", the world can change...

---

# Example

Two threads executing around the same time

```
total   |   Thread-1                         |  Thread-2
--------------------------------------------------------------------------
0       |   val currentTotal = total         |
        |       ^^^ 0                        |
        |                                    |
        |   val newValue = currentTotal + 1  |  val currentTotal = total
        |       ^^^ 1                        |      ^^^ 0
        |                                    |
        |                                    |
1       |   total = newValue                 |  val newValue = currentTotal + 1
        |                                    |      ^^^ 1
        |                                    |
1       |                                    |  total = newValue
```

---

# The result

It's as if one message got lost

```
total   |   Thread-1                         |  Thread-2
--------------------------------------------------------------------------
0       |   val currentTotal = total         |
        |       ^^^ 0                        |
        |                                    |
        |   val newValue = currentTotal + 1  |  val currentTotal = total
        |       ^^^ 1                        |      ^^^ 0
        |                                    |
        |                                    |
1       |   total = newValue                 |  val newValue = currentTotal + 1
        |                                    |      ^^^ 1
        |                                    |
1       |                                    |  total = newValue
```

Final state of total: 1

(should have been 2)

---

# Root cause?

"Check then act"

Thread-2's `currentTotal` became stale

```
total   |   Thread-1                         |  Thread-2
--------------------------------------------------------------------------
0       |   val currentTotal = total         |
        |       ^^^ 0                        |
        |                                    |
        |   val newValue = currentTotal + 1  |  val currentTotal = total
        |       ^^^ 1                        |      ^^^ 0
        |                                    |
        |                                    |
1       |   total = newValue                 |  val newValue = currentTotal + 1
        |                                    |                 ^^^ stale!!!
        |                                    |
1       |                                    |  total = newValue
```

---

# Back to our script

Random updates are getting lost

```scala
total += 1
```

Two or more threads are updating based on stale values

Final value is less than 10K because updates are effectively lost

---

# Concurrency bug

Non-deterministic

Hard to reproduce

Requires high load to expose it

Won't show up in unit tests

But can be very costly

---

```
 _____ _      _             
|  ___(_)_  _(_)_ __   __ _ 
| |_  | \ \/ / | '_ \ / _` |
|  _| | |>  <| | | | | (_| |
|_|   |_/_/\_\_|_| |_|\__, |
                      |___/ 
 _ _   
(_) |_ 
| | __|
| | |_ 
|_|\__|
       
                           _                
  ___  _   _ _ __ ___  ___| |_   _____  ___ 
 / _ \| | | | '__/ __|/ _ \ \ \ / / _ \/ __|
| (_) | |_| | |  \__ \  __/ |\ V /  __/\__ \
 \___/ \__,_|_|  |___/\___|_| \_/ \___||___/
                                            
```

Introducing locks with `synchronized` keyword

---

# synchronized

Use `synchronized` to obtain a lock:

```diff
 var total = 0
 
+val lock = new Object // could be anything
 
 for (...) {
   Future {
+    lock.synchronized { // Wait your turn!
       total += 1
+    } // okay I'm done
   }
 }
```

---

# Simulation

```scala
var total = 0

val lock = new Object // could be anything

for (...) {
  Future {
    lock.synchronized { // Wait your turn!
      total += 1
    } // okay I'm done
  }
}
```

```
total   |   lock    |    Thread-1            |  Thread-2
--------------------------------------------------------------------------
0       |   free    |                        |
        |           |                        |
        | occupied  |  lock.synchronized {   |
        |           |      <take lock>       |
        |           |                        |
1       | occupied  |    total += 1          |  lock.synchronized {
        |           |                        |      <lock occupied...>
        |   free    |  }  <release lock>     |      <lock occupied...>
        | occupied  |                        |      <take lock!>
1       |           |                        |
2       |           |                        |      total += 1
        |           |                        |
2       |   free    |                        |  }  <release lock>
```

Final state = 2

---

# Fix it!

To the code!

---

# Observations

```scala
var total = 0

val lock = new Object

for (...) {
  Future {
    lock.synchronized {
      total += 1
    }
  }
}
```

- logically correct


- lock creates a bottleneck


- threads waiting on the lock are blocked

---

# Thread starvation

> threads waiting on the lock are blocked

Waiting on a lock is another form of blocking

Threads are stuck doing nothing

Potential thread starvation, under utilised CPU

---

```
 ____                       
|  _ \  ___ _ __ ___   ___  
| | | |/ _ \ '_ ` _ \ / _ \ 
| |_| |  __/ | | | | | (_) |
|____/ \___|_| |_| |_|\___/ 
                            
```

Locks cause blocking

Show using our requests demo

---

# Replicating a bottleneck

```diff
+val lock = new Object

 Future {
   doHardWork(20_000_000)
+  lock.synchronized {
     Thread.sleep(5000)
+  }
   doHardWork(20_000_000)
 }
```

The sleep represents a long running operation that only

allows "one at a time" access

e.g. appending to a shared file

---

# Code it up

To the editor!

---

# Ec?

Keep it simple with a fixed size thread pool

```
> run pooled 10
```

(lower number of requests so we don't have to wait so long)

---

# What we expect (At the start)

- first thread will get the lock


- will hold it for 5 seconds


- other threads will go into the "monitor" state

---

# What we expect (Overall)

Only one thread sleeping at a time, ie. no parallel purple bars

Lots of light blue bars

Will take at least 50 seconds (10 sleeps of 5 seconds)

(light blue = monitor, purple = sleeping)

---

# Run it

To the repl!

---

# Observation

Thread 2 doesn't necessarily get the lock after thread 1 releases it

Less fattening (interesting)

Took a long time to do 10 requests

---

# Lesson

Locks can cause extreme blocking

Beware children

---

# Aside

`AtomicInteger` and its friends _don't_ use locks internally

Use a "check and swap/set" approach

(Not for today)

---

```
 ____                _ _             
|  _ \ ___  __ _  __| (_)_ __   __ _ 
| |_) / _ \/ _` |/ _` | | '_ \ / _` |
|  _ <  __/ (_| | (_| | | | | | (_| |
|_| \_\___|\__,_|\__,_|_|_| |_|\__, |
                               |___/ 
                 _        _     _      
 _ __ ___  _   _| |_ __ _| |__ | | ___ 
| '_ ` _ \| | | | __/ _` | '_ \| |/ _ \
| | | | | | |_| | || (_| | |_) | |  __/
|_| |_| |_|\__,_|\__\__,_|_.__/|_|\___|
                                       
     _       _        
  __| | __ _| |_ __ _ 
 / _` |/ _` | __/ _` |
| (_| | (_| | || (_| |
 \__,_|\__,_|\__\__,_|
                      
```

---

# Reading?

> If we're reading mutable data,
>
> do we still need to lock it?
>
> We're not modifying anything

---

# Reading

> do we still need to lock it?

Sometimes yes

---

# Example

In a multi-threaded context

```scala
def doStuff(list: java.util.List[String]): Unit = {
  if (list.nonEmpty)
    println(list.at(0))
}
```

---

# Check then act

What if the list gets cleared between the check then act?

```scala
def doStuff(list: java.util.List[String]): Unit = {
  if (list.nonEmpty) // Check
    println(list.at(0)) // Act
}
```

The check is _trying_ to make the list access safe

Will randomly throw exceptions

---

# Not atomic

```scala
def doStuff(list: java.util.List[String]): Unit = {
  if (list.nonEmpty) // Check
    println(list.at(0)) // Act
}
```

Again "check then act" is not atomic

We're potentially seeing two different versions of the list

---

# Atomic read?

Even if your read is atomic,

you might end up seeing the resource in an undefined state

Analogy: robot reading the paper level in a photocopy whilst another robot refills it

---

# Recap

_Sometimes_ even reading can require locking

That creates even more lock contention

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

# Locks 

Used for modelling "one at a time" logic

---

# Contention

Locks lead to threads being blocked

Can cause thread starvation

---

# Mutation

Be careful if you have to mutate shared state

Very easy to introduce concurrency bugs

---

# Many tools

> Be careful if you have to mutate shared state

Many alternatives to locks that we can use

(Cover another day)

---

# FP

Later we'll see that immutable data avoids a lot of these issues

A plus for functional programming

---

```
  ___                  _   _                 
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___ 
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
                                             
```
