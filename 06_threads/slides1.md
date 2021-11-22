---
author: Rohan
date: 2021-11-24
title: Threads in the JVM
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
 _  | |\ \ / /| |\/| |
| |_| | \ V / | |  | |
 \___/   \_/  |_|  |_|

```

---

# Last time...

---

# `ExecutionContext`

What `Future` uses to run async code

ie. execution resources

---

# Abstraction

We made two implementations:

- one thread per job


- fixed size thread pool

---

# Next two sessions

Looking more closely at threads,

particularly in the JVM

---

# How

Use a tool called "VisualVM" to visualise what is happening inside a running JVM

---

# JVM Quick Recap

JVM = Java Virtual Machine

When you start a java/kotlin/scala program, it runs in a JVM

---

# Diagnostics

The JVM exposes internal information about its state

Diagnostic tools can attach to a JVM and visualise its internals

---

```
__     ___                 ___     ____  __
\ \   / (_)___ _   _  __ _| \ \   / /  \/  |
 \ \ / /| / __| | | |/ _` | |\ \ / /| |\/| |
  \ V / | \__ \ |_| | (_| | | \ V / | |  | |
   \_/  |_|___/\__,_|\__,_|_|  \_/  |_|  |_|

```

---

# Quick demo!

---

```
 ____           _                 _
|  _ \ _ __ ___| |_ ___ _ __   __| |
| |_) | '__/ _ \ __/ _ \ '_ \ / _` |
|  __/| | |  __/ ||  __/ | | | (_| |
|_|   |_|  \___|\__\___|_| |_|\__,_|

 ____
/ ___|  ___ _ ____   _____ _ __
\___ \ / _ \ '__\ \ / / _ \ '__|
 ___) |  __/ |   \ V /  __/ |
|____/ \___|_|    \_/ \___|_|

```

---

# Comparing two approaches

- creating a new thread for each request


- using a thread pool

---

# Pretend scenario

- 1 request arrives per second


- 1 request takes ~6 seconds to process

---

# Emulating requests

```scala
implicit val ec: ExecutionContext = ...

val numRequests = ...

for (i <- 1 to numRequests) {
  Thread.sleep(1000) // 1 second between requests

  Future {
    workHard(...)
    Thread.sleep(5000)
    workHard(...)
  }
}
```

Look at threads and see how long it will take

---

```
 _____ _
|_   _| |__   ___
  | | | '_ \ / _ \
  | | | | | |  __/
  |_| |_| |_|\___|

  ____          _
 / ___|___   __| | ___
| |   / _ \ / _` |/ _ \
| |__| (_) | (_| |  __/
 \____\___/ \__,_|\___|

```

---

# Quick run through

Simplification

```scala
implicit val ec: ExecutionContext = ...

val numRequests = ...

for (i <- 1 to numRequests) {
  Thread.sleep(1000) // 1 second between requests

  Future {
    workHard(...)
    Thread.sleep(5000)
    workHard(...)
  }
}
```

Real code is a bit more complex

(but that doesn't matter for this)

---

# The code

To the editor!

---

# Today

```
 _ _ _____              _ _
( | )  ___| __ ___  ___( | )
 V V| |_ | '__/ _ \/ _ \V V
    |  _|| | |  __/  __/
    |_|  |_|  \___|\___|

                     _
 _ __ ___   ___   __| | ___
| '_ ` _ \ / _ \ / _` |/ _ \
| | | | | | (_) | (_| |  __/
|_| |_| |_|\___/ \__,_|\___|

```

"Free" meaning we don't limit the number of threads

ie. one thread per request

---

# Before running it

Can't hammer my system too hard during a presentation

(Don't want teams to crash)

---

# Run it!

```bash
$ cd 06_threads/thread-demo
$ sbt
# Select "All threads" in VisualVM
> run free 50
```

- "free" = one thread per request


- 50 requests

---

# Results?

~60 seconds to process 50 messages

"Fattening Ladder"

---

# Fattening Ladder

What is causing this?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Fattening Ladder

> What is causing this?

My guess: increasing competition for resources

---

```
 ____  _                _
/ ___|| |__   __ _ _ __(_)_ __   __ _
\___ \| '_ \ / _` | '__| | '_ \ / _` |
 ___) | | | | (_| | |  | | | | | (_| |
|____/|_| |_|\__,_|_|  |_|_| |_|\__, |
                                |___/
                _
               (_)___
               | / __|
               | \__ \
               |_|___/

  ____           _
 / ___|__ _ _ __(_)_ __   __ _
| |   / _` | '__| | '_ \ / _` |
| |__| (_| | |  | | | | | (_| |
 \____\__,_|_|  |_|_| |_|\__, |
                         |___/
```

---

# Understanding cores

My laptop has 4 cores (hyperthreaded)

ie. it can theoretically do 8 things simultaneously

---

# Don't want

```
Thread-1       1111111111111111111111111111111...
Thread-2       2222222222222222222222222222222...
Thread-3       3333333333333333333333333333333...
Thread-4       4444444444444444444444444444444...
Thread-5       5555555555555555555555555555555...
Thread-6       6666666666666666666666666666666...
Thread-7       7777777777777777777777777777777...
Thread-8       8888888888888888888888888888888...
Thread-9
Thread-10
Thread-11
Thread-12
```

Not sharing, not caring

---

# Sharing

Threads have to share those cores

---

# Time slices

The scheduler gives each thread a slice of time on a core

```
Thread-1       1111111111111          4444444
Thread-2       22222222
Thread-3                    111111111111
Thread-4       33333333333333333
Thread-5           888888888888888
Thread-6       444444444444444444444  66666666
Thread-7               2222222222222222
Thread-8       55555555555555
Thread-9                        33333333333
Thread-10      666666666666666666
Thread-11      777777777777777777777
Thread-12      8888          555555555555
```

tries to share it around

---

# If you had just 8 threads

```
Thread-1       1111111111111111111111111111111...
Thread-2       2222222222222222222222222222222...
Thread-3       3333333333333333333333333333333...
Thread-4       4444444444444444444444444444444...
Thread-5       5555555555555555555555555555555...
Thread-6       6666666666666666666666666666666...
Thread-7       7777777777777777777777777777777...
Thread-8       8888888888888888888888888888888...
```

Every thread gets their own core, no need to share

---

# If you had 16 threads

```
Thread-1       1111111111111
Thread-2                    222222222222222222...
Thread-3                    111111111111111111...
Thread-4       4444444444444                  ...
Thread-5                    444444444444444444...
...
Thread-15              33333333333333         ...
Thread-16      55555555555555555              ...
```

Same amount of resources,

but twice the demand

Each thread gets half the resources per second

---

# More threads

Spreading the resources thinner and thinner

---

# Back to the fattening Ladder

Later requests have more and more threads to contend with

Each thread gets less time slices each second

---

# Modify the experiment

```diff
 Future {
   // Simulate doing some CPU intensive work with a sleep between
   workHard(20_000_000)
   if (sleep) Thread.sleep(5000)
-  workHard(20_000_000)
 }
```

Just work then sleep

See what happens

---

# Results

Less widening

---

# Comparing

## First experiment

That extra work caused enough overlap of requests

to slow the system down

Compounding effect

## Second experiment

Each thread was able to get its work done

before the next thread started

---

# Traffic Jam Analogy

Road systems hit a similar point

Once they get congested, cars entering > cars leaving

Then the congestion compounds

Only clears up when the cars entering drops down (e.g. peak hour ends)

---

# Frail unpredictable system

There is a tipping point where the throughput will suddenly drop

System is unpredictable when a burst occurs

---

# Unpredictable

> System is unpredictable when a burst occurs

We'll see a thread pool based one is much more predictable

---

```
 _____ _                        _
|_   _| |__  _ __ ___  __ _  __| |
  | | | '_ \| '__/ _ \/ _` |/ _` |
  | | | | | | | |  __/ (_| | (_| |
  |_| |_| |_|_|  \___|\__,_|\__,_|

  ___                 _                    _
 / _ \__   _____ _ __| |__   ___  __ _  __| |
| | | \ \ / / _ \ '__| '_ \ / _ \/ _` |/ _` |
| |_| |\ V /  __/ |  | | | |  __/ (_| | (_| |
 \___/  \_/ \___|_|  |_| |_|\___|\__,_|\__,_|

```

---

# Thread Overhead

- context switching


- startup


- memory

---

# First up

Context switching

(the main one)

---

# Context switching

Activity vs Productivity

(easy to conflate)

---

# Analogy

Every member of the DiVA team has 5 tasks to do

---

# Analogy

Every member of the DiVA team has 5 tasks to do

Every 5 minutes Simon rings a bell and we have to switch task

---

# Switching task

Analogy:

- remembering where you were up to


- closing down apps and windows


- opening up new apps and windows

---

# Efficiency

> Every 5 minutes Simon rings a bell and we have to switch task

Suppose it takes 30 seconds to context switch

30 seconds / 5 minutes = 10%

---

# That 30 seconds

You are busy and active,

but you are not productive

ie. you are not making progress on a meaningful task

---

# Back to computing...

The CPU might be at 100%,

but a lot of that "activity" isn't necessarily productivity

---

# Context switching overhead

Switching threads is work

```
                                      \/ unpack
Thread-1                              4444444


...                                \/ pack up
Thread-6       444444444444444444444
```

Some of your core's time is lost to this overhead

---

# Back to our analogy

> Every 5 minutes Simon rings a bell and we change task
>
> It takes 30 seconds to context switch

Developer = core

Jira Tasks = threads

Simon = scheduler

5 minutes = time slice

30 seconds = context switching overhead

Efficiency = 90%

---

# How to avoid context switching?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# How to avoid context switching?

Don't have more threads than cores

```
Thread-1       1111111111111111111111111111111...
Thread-2       2222222222222222222222222222222...
Thread-3       3333333333333333333333333333333...
Thread-4       4444444444444444444444444444444...
Thread-5       5555555555555555555555555555555...
Thread-6       6666666666666666666666666666666...
```

No need to share, no need to care

---

# Next overhead

Thread creation time

---

# Creating a Thread

Takes time as well

Not a huge amount but might matter in some contexts

(Doesn't matter for us)

---

# Next overhead

Threads use memory

---

# Threads use memory

A thread costs about 1MB

(e.g. for callstack and other overhead)

---

# Be careful

1 request = 1 thread = 1 MB

If you have really high load or a spike, you could run out of memory and crash

(Probably not a problem for us)

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

# Looked under the hood

Got a sense for threads in the JVM

---

# VisualVM

A nice tool for getting diagnostics out of a JVM

---

# One thread per request

Gave us a "fattened ladder" shape during bursts

---

# Thread overhead

- context switching


- startup time


- memory

---

# If you want to try it yourself

```bash
$ cd 06_threads/thread-demo
$ sbt
# Select "All Threads"
> run ...
```

(Don't roast your computer though)

---

# Next time

Run the experiment again using a thread pool

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
