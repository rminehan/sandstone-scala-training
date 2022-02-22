---
author: Rohan
date: 2022-01-20
title: Akka Streams 2
---

```
    _    _    _               _
   / \  | | _| | ____ _   ___| |_ _ __ ___  __ _ _ __ ___  ___
  / _ \ | |/ / |/ / _` | / __| __| '__/ _ \/ _` | '_ ` _ \/ __|
 / ___ \|   <|   < (_| | \__ \ |_| | |  __/ (_| | | | | | \__ \
/_/   \_\_|\_\_|\_\__,_| |___/\__|_|  \___|\__,_|_| |_| |_|___/

```

Part 2

---

# Recap

---

# Kafka

Kafka code uses akka streams

---

# Rivers

Analogous to streams

---

# Production line

Got a sense for the internal complexity of a stream

---

# Today

Introduction to akka streams

(one particular implementation of streams)

---

# But first

> Akka!? Does this mean we have to use actors?

No

Akka streams are powered by an actor system as an execution context

You don't use actors directly

---

```
 _
| |    ___  __ _  ___
| |   / _ \/ _` |/ _ \
| |__|  __/ (_| | (_) |
|_____\___|\__, |\___/
           |___/
 ____  _            _
| __ )| | ___   ___| | _____
|  _ \| |/ _ \ / __| |/ / __|
| |_) | | (_) | (__|   <\__ \
|____/|_|\___/ \___|_|\_\___/

```

The algebra of streams

---

# Lego blocks

Common shapes:

```
 Source      open rhs
 |=====

 Flow        open lhs and rhs
 ====

 Sink        open lhs
 ===|
```

In my diagrams, data flows left to right

"left" = in

"right" = out

---

# The algebra of streams

We can connect open ends together

---

# Source + Flow

```
Source     +     Flow    =        ???
|=====           ====         |==========
```

Q: What shape do we get?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Source + Flow

```
Source     +     Flow    =      Source
|=====           ====         |==========
```

> Q: What shape do we get?

A: Source!

---

# Flow + Sink

```
Flow   +   Sink     =        ???
====       ====|
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Flow + Sink

```
Flow   +   Sink     =        Sink
====       ====|          ==========|
```

Sink!

---

# Flow + Flow

```
Flow   +   Flow    =        ???
====       ====
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# The algebra of streams

```
Flow   +   Flow    =        Flow
====       ====         ===========
```

Flow!

---

# Source + Sink

```
Source   +   Sink     =          ???
|=====       ====|
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Source + Sink

```
Source   +   Sink     =     RunnableGraph
|=====       ====|          |===========|
```

RunnableGraph! (new one)

---

# RunnableGraph

```
Source   +   Sink     =     RunnableGraph
|=====       ====|          |==========|
```

No open ends, ready to run

"RunnableGraph"... odd name... will make more sense later

---

# Summary of rules

```
Source  +    Flow    =      Source
|=====       ====         |==========


Flow    +    Sink    =       Sink
====         ====|         ==========|


Flow    +    Flow    =        Flow
====         ====          ===========


Source  +    Sink    =     RunnableGraph
|=====       ====|         |===========|
```

---

# Example

Reducing it down

```
Source1    +      Flow1    +    Flow2     +    Flow3    +     Sink1
|======           =====         =====          =====          ====|
```

---

# Example

Reducing it down

```
Source1    +      Flow1    +    Flow2     +    Flow3    +     Sink1
|======           =====         =====          =====          ====|

     \            /

        Source2            +    Flow2     +    Flow3    +     Sink1
       |==========              =====          =====          ====|

                                                    \        /

        Source2            +    Flow2     +            Sink2
       |==========              =====                =========|

                  \            /

                    Source3               +            Sink2
                |===============                     =========|

                               \                    /

                                   RunnableGraph
                            |=======================|
```

---

# Does order matter?

Simple example where we can combine two ways

```
                                Source1      +      Flow      +      Sink1
                                |======             ====             ====|

                                        two ways to resolve

(Source1    +    Flow)    +    Sink1                     Source1    +    (Flow    +    Sink1)
(|======         ====)         ====|                     |======         (====         ====|)

        Source2           +    Sink1                     Source1    +           Sink2
        |======                ====|                     |======                ====|

                RunnableGraph                                  RunnableGraph
                |===========|                                  |===========|

```

Do we end up with a functionally equivalent stream?

---

# Order doesn't matter

> Do we end up with a functionally equivalent stream?

Yep

---

# Aside: Math buzzword quiz

What maths word do we use for this?

```
a + (b + c) = (a + b) + c
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Aside: Math buzzword quiz

> What maths word do we use for this?

```
a + (b + c) = (a + b) + c
```

Associative

---

# Associativity = Lazy with brackets

We can get lazy and drop the brackets as they don't matter

```
a + (b + c) = (a + b) + c

        a + b + c
```

e.g. `1 + 2 + 3` is unambiguous

---

# Not everything is associative

Couldn't do that for `^`

```
          2 ^ 2 ^ 3 ?

Option 1: 2 ^ (2 ^ 3) = 2 ^ 8 = 256

Option 2: (2 ^ 2) ^ 3 = 4 ^ 3 = 64
```

---

# Running it

To be runnable, a graph can't have any open sections

```
-------------------------
Graph            Runnable
-------------------------
Source           nope
|=====
-------------------------
Sink             nope
===|
-------------------------
Flow             nope
====
-------------------------
RunnableGraph    yep
|===========|
-------------------------
```

---

# Demo 2

Build a stream showing off these pieces

---

# Demo 2 Recap

- most definitions come from `akka.stream.scaladsl._`


- `via`: `Source/Flow + Flows`


- `to`: `Source/Flow + Sink`


- combined individual components into a graph two different ways


- the runnable graph didn't do anything on its own

---

# Description vs Execution

> the runnable graph didn't do anything on its own

A runnable graph is just a description

---

# Demo 2 Continued

> A runnable graph is just a description

Call `.run()` to bring it to life

(requires implicit execution resources)

To the demo!

---

# Demo 2 Recap

- used an actor system for the execution resources


- a graph is just a description/recipe/blue print


- we ran the same stream twice (like using the same recipe to cook 2 meals)

---

```
  ____                _     _             _
 / ___|___  _ __ ___ | |__ (_)_ __   __ _| |_ ___  _ __ ___
| |   / _ \| '_ ` _ \| '_ \| | '_ \ / _` | __/ _ \| '__/ __|
| |__| (_) | | | | | | |_) | | | | | (_| | || (_) | |  \__ \
 \____\___/|_| |_| |_|_.__/|_|_| |_|\__,_|\__\___/|_|  |___/

```

---

# Recap

So far we've got this mental model:

- `Source`


- (via) `Flow`


- (to) `Sink`

---

# Recall our first demo

```scala
Source
  .repeat(1) // source
  .take(20)
  .map(_ * 2)
  .scan(0)(_ + _)
  .filter(_ % 10 != 0)
  .to(Sink.foreach(println))  // sink
  .run()
```

We have all these combinators like `map` and `filter`

How do they fit into our mental model?

---

# Example: map

Look at the definition:

```scala
// Flow.scala
def map[T](f: Out => T): Repr[T] = via(Map(f))
```

Remember `via` is for flows,

so it seems it's a wrapper around a `Map` flow

---

# Map

```scala
// Flow.scala
def map[T](f: Out => T): Repr[T] = via(Map(f))

// Ops.scala
case class Map[In, Out](f: In => Out) extends GraphStage[FlowShape[In, Out]] { ... }
```

It's basically a flow

---

# Summary

These combinators are just friendly wrappers

```scala
source.map(_ * 2)
```

vs

```scala
source.via(Flow.fromFunction[Int, Int](_ * 2))
```

They make streams feel like more like sequences

---

```
__        __                     _
\ \      / / __ __ _ _ __  _ __ (_)_ __   __ _
 \ \ /\ / / '__/ _` | '_ \| '_ \| | '_ \ / _` |
  \ V  V /| | | (_| | |_) | |_) | | | | | (_| |
   \_/\_/ |_|  \__,_| .__/| .__/|_|_| |_|\__, |
                    |_|   |_|            |___/
 _   _
| | | |_ __
| | | | '_ \
| |_| | |_) |
 \___/| .__/
      |_|
```

---

# Lego blocks

- Source


- Flow


- Sink


- RunnableGraph

Can combine them with each other

---

# Description vs Execution

A `RunnableGraph` is just a description of a stream

Use `run` to bring it to life

Usually an actor system provides the resources

---

# Combinators

`map`, `filter`, `scan` etc...

Wrappers which make streams easier to write

---

# Resources

Akka docs are generally really good

[Akka cookbook](https://akka-ja-2411-translated.netlify.app/scala/stream/stream-cookbook.html)
is pretty good

---

# Next time

More complex topologies

---

# Lying Rohan

So far I've been simplifying some explanations

Real code you see might look a little different

---

# Homework Reminder

See `homework_solution.sc`

Can peek at it

Will go through it next time

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
