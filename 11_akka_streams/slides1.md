---
author: Rohan
title: Intro to akka streams
date: 2022-01-17
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
 ___) | |_| | |  __/ (_| | | | | | \__ \ !
|____/ \__|_|  \___|\__,_|_| |_| |_|___/

```

---

# Akka streams

Spend a few sessions on akka streams

Then kafka code will make more sense

---

# Why learn about akka streams

- mentioned in feedback


- Denis mentioned training was needed


- used in heaps of our services with kafka

---

# What we'll cover

- quick demo


- the "stream" concepts


- factory analogy


- lego blocks (sources, sinks etc...)


- akka streams api


- advanced topology


- materialisation concepts


- back pressure (if there's time)

---

# Kafka

Then we'll understand how kafka consumers and producers are represented as streams

---

# After kafka

Back to FP brainwashing

- functional data structures


- category theory


- folding

---

# Goal for this series

You'll be able to follow the high level logic of our streaming code,

(even if you couldn't write it yourself confidentally)

---

# Disclaimer

I'm not at expert on this stuff!

Not a deep dive

---

```
  ___        _      _
 / _ \ _   _(_) ___| | __
| | | | | | | |/ __| |/ /
| |_| | |_| | | (__|   <
 \__\_\\__,_|_|\___|_|\_\

 ____
|  _ \  ___ _ __ ___   ___
| | | |/ _ \ '_ ` _ \ / _ \
| |_| |  __/ | | | | | (_) |
|____/ \___|_| |_| |_|\___/

```

To the editor!

---

# Recap of demo

- needed an actor system to power the stream


- streams can be infinite


- streams have an api _similar_ to `Seq`

---

```
  ____                           _
 / ___|___  _ __   ___ ___ _ __ | |_ ___
| |   / _ \| '_ \ / __/ _ \ '_ \| __/ __|
| |__| (_) | | | | (_|  __/ |_) | |_\__ \
 \____\___/|_| |_|\___\___| .__/ \__|___/
                          |_|
```

What is a stream really?

---

# Concepts

"Stream" is a good name

Our programmatic streams are similar to streams/rivers in nature

---

# What is a river?

You're standing with a friend on the bank of a flowing river.

Your friend asks you:

> What is a river?

What would you say?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Answers?

You point to the river and say: "It's that"

"This body of water in front of me"

---

# Water molecules

Is it the water molecules in front of you?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Water molecules

> Is it the water molecules in front of you?

In an hours time, all those water molecules will have been replaced.

Yet we'd still call it the same stream.

---

# Keep in mind

Rivers also flood and dry up

Rivers get blocked (those beavers...)

Water level will change

---

# Keep in mind

Rivers also flood and dry up

Rivers get blocked (those beavers...)

Water level will change

But we still call it the same river...

---

# Abstract

We all _know_ what a river is, but it's hard to define precisely

---

# A simple definition

Rivers tend to have:

- a start point (for example where the rain collects)


- a topological flow ("topological" implies some flexibility)


- an end point (the sea)

---

# Example

> Duck River starts at the top of Mount Victoria.
>
> It flows down the between Mount Victoria and Mount Herschel.
>
> From there it flows through the Shanon valley.
>
> Finally it empties into the Adrian Sea.

This captures the general topology without being overly prescriptive.

---

# Recapping

A river/stream has these concepts:

- flow of water


- temporal - you can't cross the same stream twice


- topology/shape


- water level can change (floods, droughts)


- long lived (or even infinite)

---

# Why are we talking about rivers?

Because a lot of this intuition applies to programmatic streams

---

# Analogy

> Because a lot of this intuition applies to programmatic streams.

A river/stream has these concepts:

- flow of water (flow of requests, messages, data)


- temporal - you can't cross the same stream twice (different requests at different times)


- topology/shape (your pipeline of transformations)


- water level can change (floods, droughts) (high load, low load)


- long lived (or even infinite) (serving customer requests)

---

Pause for:

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```

Coming up: Factory analogy

---

We're releasing our own banking action figure:

```
 ____                  _     _
/ ___|  __ _ _ __   __| |___| |_ ___  _ __   ___
\___ \ / _` | '_ \ / _` / __| __/ _ \| '_ \ / _ \
 ___) | (_| | | | | (_| \__ \ || (_) | | | |  __/
|____/ \__,_|_| |_|\__,_|___/\__\___/|_| |_|\___|

 __  __
|  \/  | __ _ _ __
| |\/| |/ _` | '_ \
| |  | | (_| | | | |
|_|  |_|\__,_|_| |_|

```

Sandstone Man defeats inefficient borrowing villains

---

# Factory line

We'll need a factory line to assemble our action figures

---

# Factory line

Each of us sit at a different position on a conveyor belt

```
 --->     --->     --->     --->     --->  ....  --->   Done!
-----------------------------------------------
 Feroz    Pranali  Yuhan
 torso    arms     legs     ....
```

---

# Conveyor belt observations

```
 --->     --->     --->     --->     --->  ....  --->   Done!
-----------------------------------------------
 Feroz    Pranali  Yuhan
 torso    arms     legs     ....
```

Suppose Feroz puts one torso on each second

What problem do you see?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Coupled

```
 --->     --->     --->     --->     --->  ....  --->   Done!
-----------------------------------------------
 Feroz    Pranali  Yuhan
 torso    arms     legs     ....
```

> Suppose Feroz puts one torso on each second
>
> What problem do you see?

They're coupled by the conveyor belt

Each worker has to "keep up"

Brittle: Can't absorb shocks, e.g. a long sneeze, a bathroom break

---

# Long stages

Some stages just take longer

```
 --->     --->     --->     --->     --->  ....  --->   Done!
-----------------------------------------------
 Feroz    Pranali  Yuhan             James
 torso    arms     legs     ....     spandex
                                     superhero
                                     underpants
```

James won't be able to keep up

---

# Problems

System can't absorb shocks well

Let's improve it...

---

# How to improve it?

Replace conveyor belt with buckets:

```
  Feroz  -->  BUCKET  -->  Pranali  -->  BUCKET -->  Yuhan  --> ...
                 1                          2
```

Bucket 1: Feroz's outbox and Pranali's inbox's

Bucket 2: Pranali's outbox and Yuhan's inbox

etc...

---

# Decoupling

```
  Feroz  -->  BUCKET  -->  Pranali  -->  BUCKET -->  Yuhan  --> ...
                 1                          2
```

The buckets act like buffers

Decouples workers

---

# Scaling up

```
   Stage 1                      Stage 2                   ...        Stage 8

 1 x worker -->  BUCKET  -->  1 x worker -->  BUCKET -->  ...  --> 3 x workers --> BUCKET --> ...
                    1                            2                                    8
```

What about ordering?

---

# Reordering

Later elements might process faster than earlier ones

```
  Stage 1                              Stage 2

  Worker   --->    BUCKET    --->     Worker 1 - A     --->     BUCKET
          A, B, C     1     A, B, C   Worker 2 - B    C, A, B      2
                                      Worker 3 - C
```

You get small localized reorderings here and there.

---

# Context specific

For Sandstone Man, it's okay if ordering isn't preserved

For a trading system it would matter

`mapAsync` vs `mapAsyncUnordered`

---

# Efficient resource usage

Sometimes workers have nothing to do

---

# Efficient resource usage

> Sometimes workers have nothing to do

They can go and help somewhere else

---

# Disclaimer

Sandstone Man is just an analogy to illustrate concepts like:

- internal stages


- buffering


- reallocating resources


- back pressure

Real akka streams might work differently internally

---

# Enough for today

Just a gentle introduction to the mental model

---

# Homework

Create a script that uses an akka stream to uppercase a text file
and remove lines over 10 characters

```
input.txt                   output.txt
this is                     THIS IS
a                           A
lowercase file              FILE
file
```

---

# Finite memory

> Create a script that uses an akka stream to uppercase a text file
>
> and remove lines over 10 characters

Must be able to process a file of any size with a fixed amount of memory

e.g. could process a 50GB input file on a developer machine

---

```
  ___                  _   _                ___
 / _ \ _   _  ___  ___| |_(_) ___  _ __  __|__ \
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|/ /
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \_|
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___(_)
```

---

