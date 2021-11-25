---
author: Rohan
date: 2021-11-26
title: Threads in the JVM (2)
---

```
 _____ _                        _
|_   _| |__  _ __ ___  __ _  __| |___
  | | | '_ \| '__/ _ \/ _` |/ _` / __|
  | | | | | | | |  __/ (_| | (_| \__ \
  |_| |_| |_|_|  \___|\__,_|\__,_|___/

 _         _   _
(_)_ __   | |_| |__   ___
| | '_ \  | __| '_ \ / _ \
| | | | | | |_| | | |  __/
|_|_| |_|  \__|_| |_|\___|

     ___     ____  __
    | \ \   / /  \/  |
 _  | |\ \ / /| |\/| | (part 2)
| |_| | \ V / | |  | |
 \___/   \_/  |_|  |_|

```

---

# Recap

---

# Pretend Server

Simulated 50 requests arriving 1 second apart

---

# "Free"

"Free" because there is no cap on the number of threads

---

# "Fattening Ladder"

Later threads were taking up to 8 times longer

to do the same work as earlier threads

---

# Why?

Probably increased competition for resources

---

# Fragile and unpredictable

A burst of traffic can cause issues

---

# Fragile and unpredictable

> A burst of traffic can cause issues

Like real life traffic systems that can quickly gridlock

---

# Today

Switch out the execution context for one that uses a thread pool

See how it goes

---

# Reminder of our new ec

To the code!

---

```
 _____ _                        _
|_   _| |__  _ __ ___  __ _  __| |
  | | | '_ \| '__/ _ \/ _` |/ _` |
  | | | | | | | |  __/ (_| | (_| |
  |_| |_| |_|_|  \___|\__,_|\__,_|

 ____             _
|  _ \ ___   ___ | |
| |_) / _ \ / _ \| |
|  __/ (_) | (_) | |
|_|   \___/ \___/|_|

 ____
|  _ \ ___  ___ __ _ _ __
| |_) / _ \/ __/ _` | '_ \
|  _ <  __/ (_| (_| | |_) |
|_| \_\___|\___\__,_| .__/
                    |_|
```

---

# Reminder of how a thread pool works...

Has a queue and workers that process the queue

---

# Everyone busy

```
 new messages
     \/
|          |   Thread-1    <message_6>
|          |   Thread-2    <message_7>
|.....     |   Thread-3    <message_1>
|message_11|   Thread-4    <message_5>
|message_10|   Thread-5    <message_2>
|message_9 |   Thread-6    <message_3>
   ---->       Thread-7    <message_8>
   QUEUE       Thread-8    <message_4>
```

---

# Not enough work

```
 new messages
     \/
|          |   Thread-1    (busy)
|          |   Thread-2    (busy)
|          |   Thread-3    (parked)
|          |   Thread-4    (parked)
|          |   Thread-5    (parked)
|          |   Thread-6    (busy)
   ---->       Thread-7    (parked)
   QUEUE       Thread-8    (busy)
```

---

# What we don't want

```
 new messages
     \/
|          |   Thread-1    <message_6>
|          |   Thread-2    (doing nothing)
|.....     |   Thread-3    <message_1>
|message_11|   Thread-4    (doing nothing)
|message_10|   Thread-5    <message_2>
|message_9 |   Thread-6    <message_3>
   ---->       Thread-7    (doing nothing)
   QUEUE       Thread-8    <message_4>
```

There is work to do,

but workers are doing nothing

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
  | | | | | | | | |  __/ !
  |_| |_|_| |_| |_|\___|

```

---

# Switch the ec

```diff
-run free 50
+run pooled 50
```

To sbt!

---

# Observations

- a little "fattening"


- lag between schedule and start time


- colour pattern: green/purple - red


- slower than "free" mode (was blocked)

---

```
 _                
| |    __ _  __ _ 
| |   / _` |/ _` |
| |__| (_| | (_| |
|_____\__,_|\__, |
            |___/ 
```

---

# Lag

> lag between schedule and start time

```
Time (s)            1  2  3  4  5  6  7  8  9  10 

Scheduling thread:  |  |  |  |  |  |  |  |

Worker thread 1:    -------------------
Worker thread 2:       ------------------
Worker thread 3:          ------------------
Worker thread 4:             ------------------
Worker thread 5:                ------------------
Worker thread 6:                   ------------------
Worker thread 7:                      ------------------
Worker thread 8:                         ------------------
```

Nice straight line

Each job taking under 8 seconds, is okay

---

# Lag

Suppose a few jobs take a bit longer

and no one is free when the 9th job is submitted

```
Time (s)            1  2  3  4  5  6  7  8  9  10 

Scheduling thread:  |  |  |  |  |  |  |  |  |  |

Worker thread 1:    ---------------------------
Worker thread 2:       -------------------------
Worker thread 3:          ----------------------
Worker thread 4:             ------ not free ----
Worker thread 5:                ------------------
Worker thread 6:     free          ------------------
Worker thread 7:                      ------------------
Worker thread 8:                         ------------------
```

---

# Lag

Suppose a few jobs take a bit longer

and no one is free when the 9th job is submitted

```
Time (s)            1  2  3  4  5  6  7  8  9  10 

Scheduling thread:  |  |  |  |  |  |  |  |  |  |  \/ starts here

Worker thread 1:    --------------------------- -------job 9------
Worker thread 2:       -------------------------
Worker thread 3:          ----------------------
Worker thread 4:             ------ not free ----
Worker thread 5:                ------------------
Worker thread 6:     free          ------------------
Worker thread 7:                      ------------------
Worker thread 8:                         ------------------
```

Job 9 is scheduled, but goes in the queue

It will get started a bit later

---

# Accumulated lag

If there are 8 workers

and they taking more than 8 seconds/job on average,

lag will begin to accumulate

(which we see)

---

# Recap

Jobs won't necessarily start at the time they're scheduled

```scala
// Scheduled 9 seconds in
Future {
  // Starts 10 seconds in
  ...
  // Finishes 19 seconds in
}.map {
  // Starts 21 seconds in
  ...
}
```

---

```
 _   _           _                   _   _ _ _              _ 
| | | |_ __   __| | ___ _ __   _   _| |_(_) (_)___  ___  __| |
| | | | '_ \ / _` |/ _ \ '__| | | | | __| | | / __|/ _ \/ _` |
| |_| | | | | (_| |  __/ |    | |_| | |_| | | \__ \  __/ (_| |
 \___/|_| |_|\__,_|\___|_|     \__,_|\__|_|_|_|___/\___|\__,_|
                                                              
```

---

# Observation

> slower than "free" mode (was blocked)

Took a lot longer

---

# Sleeping threads

```
    Time (s)        1  2  3  4  5  6  7  8  9  10



Worker thread 1:    ----<sleeping>-----
Worker thread 2:       ----<sleeping>----
Worker thread 3:          ----<sleeping>----
Worker thread 4:             ----<sleeping>----
Worker thread 5:                ----<sleeping>----
Worker thread 6:                   ----<sleeping>----
Worker thread 7:                      ----<sleeping>----
Worker thread 8:                         ----<sleeping>----
```

---

# Count cores at different times

```
    Time (s)        1  2  3  4  5  6  7  8  9  10
                    |        |           |
                    |        |           |
                    |        |           |
Worker thread 1:    ----<sleeping>-----  |  ----<sleeping>-----
Worker thread 2:    |  ----<sleeping>----|     ----<sleeping>-----
Worker thread 3:    |     ----<sleeping>----      ...
Worker thread 4:    |        ----<sleeping>----
Worker thread 5:    |        |  ----<sleeping>----
Worker thread 6:    |        |     ----<sleeping>----
Worker thread 7:    |        |        ----<sleeping>----
Worker thread 8:    |        |           ----<sleeping>----
                    |        |           |
#num cores in use   1        2           3
```

Not using resources efficiently

Okay if requests take less than 8 seconds

---

# What if...

> Okay if requests take less than 8 seconds

Requests come in a bit faster

A request takes a bit longer for some reason

---

# Double request speed

1 request every half second

```
    Time (s)        1 2 3 4 5 6 7 8 9 10
                            | <--- request submitted
                            |
Worker thread 1:    --<sleeping>--
Worker thread 2:     --<sleeping>--
Worker thread 3:      --<sleeping>--
Worker thread 4:       --<sleeping>--
Worker thread 5:        --<sleeping>--
Worker thread 6:         --<sleeping>--
Worker thread 7:          --<sleeping>--
Worker thread 8:           --<sleeping>--
```

Request 9 is submitted at 5 seconds in

---

# No worker available

1 request every half second

```
    Time (s)        1 2 3 4 5 6 7 8 9 10
                            |
                            |
Worker thread 1:    --<sleeping>--
Worker thread 2:     --<sleeping>--
Worker thread 3:      --<sleeping>--
Worker thread 4:       --<sleeping>--
Worker thread 5:        --<sleeping>--
Worker thread 6:         --<sleeping>--
Worker thread 7:          --<sleeping>--
Worker thread 8:           --<sleeping>--
                            |
#num cores in use           1
```

7 cores free, but no workers to use them

---

# Queue and wait

```
 new messages      Time (s)        1 2 3 4 5 6 7 8 9 10  
     \/                                    |     |
|          |                               |     |
|          |   Worker thread 1:    --<sleeping>--|-<sleeping>--
|.....     |   Worker thread 2:     --<sleeping>--|-<sleeping>--
|message_11|   Worker thread 3:      --<sleeping>--|-<sleeping>--
|message_10|   Worker thread 4:       --<sleeping>--|...
|message_9 |   Worker thread 5:        --<sleeping>--
   ---->       Worker thread 6:         --<sleeping>--
   QUEUE       Worker thread 7:          --<sleeping>--
               Worker thread 8:           --<sleeping>--
```

Request 9 is submitted at 5 seconds in

Runs at about 8 seconds in

---

# Waste!

> Request 9 is submitted at 5 seconds in
>
> Runs at about 8 seconds in

3 seconds where:

- CPU wasn't fully utilised (around 12%)


- job was waiting to be done

---

# Why?

Workers were asleep

Cores were free, but threads weren't

---

# Last time

Didn't have this issue

> Cores were free, but threads weren't

There's loads of threads waiting to take a time slice

---

# Recap

If you mix blocking code with fixed size thread pools,

it can lead to under utilised resources

Slower to respond to requests

(But no problem if each request gets its own thread)

---

```
 ____           _                   _                  
|  _ \ ___  ___| |_ _ __ _   _  ___| |_ _   _ _ __ ___ 
| |_) / _ \/ __| __| '__| | | |/ __| __| | | | '__/ _ \
|  _ <  __/\__ \ |_| |  | |_| | (__| |_| |_| | | |  __/
|_| \_\___||___/\__|_|   \__,_|\___|\__|\__,_|_|  \___|
                                                       
 _   _                          _      
| |_| |__   ___    ___ ___   __| | ___ 
| __| '_ \ / _ \  / __/ _ \ / _` |/ _ \
| |_| | | |  __/ | (_| (_) | (_| |  __/
 \__|_| |_|\___|  \___\___/ \__,_|\___|
                                       
```

---

# Currently

Our workers are "stuck" in that sleep

```scala
Future {
  workHard(20_000_000)
  Thread.sleep(5000) // <---- blocking our worker
  workHard(20_000_000)
}
```

Break the work up

---

# Break the work up

Before

```
    Time (s)        1 2 3 4 5 6 7 8 9 10

Worker thread 1:    --<sleeping>--
Worker thread 2:     --<sleeping>--
Worker thread 3:      --<sleeping>--
Worker thread 4:       --<sleeping>--
Worker thread 5:        --<sleeping>--
Worker thread 6:         --<sleeping>--
Worker thread 7:          --<sleeping>--
Worker thread 8:           --<sleeping>--
```

After

```
    Time (s)        1 2 3 4 5 6 7 8 9 10

Worker thread 1:    --          --
Worker thread 2:     --          --
Worker thread 3:      --          --
Worker thread 4:       --          --
Worker thread 5:        --          --
Worker thread 6:         --          --
Worker thread 7:          --          --
Worker thread 8:           --          --
```

The two CPU intensive parts of each request are converted into separate jobs

Once a worker completes a `--`, it is free for another job

---

# Counting

Before

```
    Time (s)        1 2 3 4 5 6 7 8 9 10
                            |
Worker thread 1:    --<sleeping>--
Worker thread 2:     --<sleeping>--
Worker thread 3:      --<sleeping>--
Worker thread 4:       --<sleeping>--
Worker thread 5:        --<sleeping>--
Worker thread 6:         --<sleeping>--
Worker thread 7:          --<sleeping>--
Worker thread 8:           --<sleeping>--
                            |
#num cores in use           1
#num threads in use         8
```

After

```
    Time (s)        1 2 3 4 5 6 7 8 9 10
                            |
Worker thread 1:    --      |   --
Worker thread 2:     --     |    --
Worker thread 3:      --    |     --
Worker thread 4:       --   |      --
Worker thread 5:        --  |       --
Worker thread 6:         -- |        --
Worker thread 7:          --|         --
Worker thread 8:           --          --
                            |
#num cores in use           1  equal!
#num threads in use         1
```

---

# Request 9 arrives

---

```
    Time (s)        1 2 3 4 5 6 7 8 9 10

Worker thread 1:    --      --  --    <--------
Worker thread 2:     --     |    --
Worker thread 3:      --    |     --
Worker thread 4:       --   |      --
Worker thread 5:        --  |       --
Worker thread 6:         -- |        --
Worker thread 7:          --|         --
Worker thread 8:           --          --
```

Possible scenario:

Thread 1 processes the first chunk of request 9

then later processes the second chunk of request 1

(but any thread could have taken either)

---

# How to break it up?

This is a contrived problem

Work for a bit, sleep for a fixed time, then work for a bit

The sleep was originally put in to represent IO like a database call

---

# Many solutions

Use a scheduler (what you'd usually do)

Use `blocking` feature

Use a different execution context for the sleep

---

# Easiest for demo purposes

> Use a different execution context for the sleep

## Old

```scala
Future {
  workHard(20_000_000)
  Thread.sleep(5000) // <---- blocking our worker
  workHard(20_000_000)
}
```

## New

```scala
implicit val ec = EightThreadExecutionContext

Future {
  workHard(20_000_000)
}.map { _ =>
  Thread.sleep(5000)
}(ThreadExecutionContext).map { _ =>
  workHard(20_000_000)
}
```

---

# Desugar

```scala
implicit val ec = EightThreadExecutionContext

Future {
  workHard(20_000_000)
}.map { _ =>
  Thread.sleep(5000)
}(ThreadExecutionContext).map { _ =>
  workHard(20_000_000)
}
```

```scala
Future {
  workHard(20_000_000)
}(EightThreadExecutionContext).map { _ =>
  Thread.sleep(5000)
}(ThreadExecutionContext).map { _ =>
  workHard(20_000_000)
}(EightThreadExecutionContext)
```

---

# Visualising

```scala
Future {
  workHard(20_000_000)
}(EightThreadExecutionContext).map { _ =>
  Thread.sleep(5000)
}(ThreadExecutionContext).map { _ =>
  workHard(20_000_000)
}(EightThreadExecutionContext)
```


```
    Time (s)        1   2   3   4   5   6   7   8   9   10

THREAD POOL
Worker thread 1:    ----
Worker thread x:       |                    -----
                       |callback            |
THREAD EC              |                    |callback
Temp thread            ----------------------
                  (thread born)        (thread dies)
                          <--- 5s --->
```

A continuous story across different threads

---

# Reiterating

This is a silly solution for an unusual problem

In reality we'd use a scheduler but that adds too much complexity for a demo

---

# Code it up

Then run it!

See how long it takes

(should finish faster and use more CPU)

---

# Results

Takes around 55 seconds

CPU is higher

---

```
 ____          _                
|  _ \ ___  __| |_   _  ___ ___ 
| |_) / _ \/ _` | | | |/ __/ _ \
|  _ <  __/ (_| | |_| | (_|  __/
|_| \_\___|\__,_|\__,_|\___\___|
                                
 _____ _                        _     
|_   _| |__  _ __ ___  __ _  __| |___ 
  | | | '_ \| '__/ _ \/ _` |/ _` / __|
  | | | | | | | |  __/ (_| | (_| \__ \
  |_| |_| |_|_|  \___|\__,_|\__,_|___/
                                      
```

Just for fun

---

# Lower threads

See what happens when we lower the number of workers:

```diff
 object EightThreadExecutionContext extends ExecutionContext {
-  private val threadPool = Executors.newFixedThreadPool(8)
+  private val threadPool = Executors.newFixedThreadPool(6)
 
   def execute(runnable: Runnable): Unit = {
     threadPool.submit(runnable)
   }
 
```

To the shell!

---

# Results

Doesn't make much of a difference...

Hmmm...

---

```
 _____     _   _             _             
|  ___|_ _| |_| |_ ___ _ __ (_)_ __   __ _ 
| |_ / _` | __| __/ _ \ '_ \| | '_ \ / _` |
|  _| (_| | |_| ||  __/ | | | | | | | (_| |
|_|  \__,_|\__|\__\___|_| |_|_|_| |_|\__, |
                                     |___/ 
```

---

# Observation

> a little "fattening"

ie. CPU intensive work on later requests is a little slower

Not as pronounced as last time, but still there

And seems to cause lag

---

# Why?

Not sure

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

# Comparing

How does "one thread per request" compare with a thread pool?

---

# Memory

But both are O(n) where n = size of burst

Constant is smaller for thread pool

Thread pool can endure bigger bursts

---

# CPU

"Free" approach has overhead:

- thread creation


- context switching

---

# Blocking

"Free" approach works fine

When a thread gets blocked it yields resources

Another thread will take those resources

---

# Blocking

"Pooled" approach doesn't work well

When a thread gets blocked it yields resources

But no other thread is there to take those resources

Can end up with low CPU even when there is work to do

---

# Predictability

"Free" approach becomes unpredictable during bursts

Many threads competing for time slices

Can gridlock

---

# Predictability

"Pooled" approach is very predictable

No new threads created

Each thread has its own core, shouldn't be competition

---

# Overall...

---

# Pooled approach

Predictable

Efficient

Scales well (e.g. spikes)

BUT: falls apart if there is blocking code

---

# Free approach

Simple

Efficient at low load

Doesn't scale well

Don't have to worry about blocking

---

```
  ___                  _   _                 
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___ 
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
                                             
```
