---
author: Rohan
date: 2022-10-06
title: RDD's and Gotchas
---

```
     ____  ____  ____  _
    |  _ \|  _ \|  _ \( )___
    | |_) | | | | | | |// __|
    |  _ <| |_| | |_| | \__ \
    |_| \_\____/|____/  |___/
```

---

# Recap

Spark is a tool for "data engineering"

ie. processing big data

---

# Distributed computing

Spark lets us spread data over a cluster

---

# Our script

To the editor!

---

# Today

What is an RDD?

---

```
 ____  ____  ____  _
|  _ \|  _ \|  _ \( )___
| |_) | | | | | | |// __|
|  _ <| |_| | |_| | \__ \
|_| \_\____/|____/  |___/
```

---

# RDD

Resilient

Distributed

Dataset

---

# RDD

Resilient

Distributed - start here

Dataset

---

# Distributed

```
-- Laptop ---               ------------ Spark Cluster ----------------

                              Node 1     Node 2    Node 3 ...

  Driver        ---->         Executor  Executor  Executor  Executor ...
  Program                       JVM       JVM       JVM       JVM
  JVM
```

Driver program is sending work to the cluster

The "real" work happens on the executors

```scala
val session = ...

val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

val jNames = cleaned.filter(_.startsWith("J"))

val jNameCount = jNames.count()
```

---

# Partitions

```
names.txt  |||       Partition 1 ||  Partition 2 ||  Partition 3
--------   |||       ------------||--------------||-------------
Boban      |||       Boban       ||  Bobanish    ||  Rohan
Bobanita   |||       Bobanita    ||  Jonathan    ||  Lulu
...        |||       ...         ||  ...         ||  ...
Clement    |||       Clement     ||  Bobanta     ||  Bobanithy
Bobanish   |||
Jonathan   |||
...        |||
Bobanta    |||
Rohan      |||
Lulu       |||
...        |||
Bobanithy  |||
```

RDD is broken into "partitions" that are spread across the executors

---

# New partitions

```scala
sparkContext
  .textFile("names.txt")
  .filter(!_.startsWith("Boban"))
  .map(_.toUpperCase)
```

New partitions are created

```
Partition 1 || Filtered  || Mapped
------------||-----------||------
Boban       ||           ||
Bobanita    ||           ||
Susu        || Susu      || SUSU
...         || ...       || ...
Lulu        || Lulu      || LULU
Bobanthon   ||           ||
Clement     || Clement   || CLEMENT
```

---

# No IO

```scala
sparkContext
  .textFile("names.txt")
  .filter(!_.startsWith("Boban"))
  .map(_.toUpperCase)
```

The executor can create the new partitions without needing to communicate with other executors

```
Partition 1 || Filtered  || Mapped
------------||-----------||------
Boban       ||           ||
Bobanita    ||           ||
Susu        || Susu      || SUSU
...         || ...       || ...
Lulu        || Lulu      || LULU
Bobanthon   ||           ||
Clement     || Clement   || CLEMENT
```

---

# Cartesian example

```scala
val names = sparkContext.textFile("names.txt")

val namePairs = names.cartesian(names)
```

Demo in repl

---

# Cartesian example

```scala
val names = sparkContext.textFile("names.txt")

val namePairs = names.cartesian(names)
```

All the pairs

```
           | Boban | Tom | Susu | Feroz | Pranali ...
--------------------------------------------------
Boban      |       |     |      |       |
--------------------------------------------------
Tom        |       |     |      |       |
--------------------------------------------------
Susu       |       |     |      |       |
--------------------------------------------------
Feroz      |       |     |      |       |
--------------------------------------------------
Pranali    |       |     |      |       |
--------------------------------------------------
...
```

---

# Lots of IO

Every partition has to be paired with every other

---

# Zoom in on 2 partitions

```
   node 1          node 2
Partition 1 ||  Partition 2 ||
------------||--------------||
Boban       ||  Bobanish    ||
Bobanita    ||  Jonathan    ||
...         ||  ...         ||
Clement     ||  Bobanta     ||


      node x
 Partition y
-----------------
(Boban, Bobanish)
(Boban, Jonathan)
...
(Bobanita, Bobanish)
(Bobanita, Jonathan)
...
(Clement, Bobanish)
(Clement, Jonathan)
...
```

Because the input partitions are on different nodes,

you are guaranteed to have shuffling of data for at least 1 partition

---

# Zooming out

Every partition is being paired with every other

Huge amount of IO making the new partitions

---

# Back to RDD's

More on these kinds of gotchas later

---

# RDD

Resilient - up to here

Distributed

Dataset

---

# Failure happens

Spark is running heavy jobs on worker nodes and it's easy for stuff to break

---

# Scenario

You're running a 2 hour job, it fails at 1:45

---

# Scenario

> You're running a 2 hour job, it fails at 1:45

Ideally we wouldn't have to rerun the whole job from scratch

---

# Internally

Spark breaks your jobs into stages and you effectively get "save points"

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

val jNames = cleaned.filter(_.startsWith("J")) // failure! rerun from here

val jNameCount = jNames.count()
```

Gives you "resiliency"

---

# How does it work?

---

# How does it work?

I lied a bit last week to simplify things

Do some demo's to understand what spark's doing

---

# Example

```diff
 val names = session.read.textFile("names.txt")

 val cleaned = names.map(_.trim).filter(_.nonEmpty)

-val jNames = cleaned.filter(_.startsWith("J"))
+val jNames = cleaned.filter(_.startsWith("J")).map { cleaned =>
+  println(cleaned) // sneaky side effect
+  cleaned
+}

-val jNameCount = jNames.count()
-println(s"There are $jNameCount names starting with 'J'")
+// val jNameCount = jNames.count()
+// println(s"There are $jNameCount names starting with 'J'")
```

---

# What happened?

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty).map { cleaned =>
  println(cleaned) // This didn't print... ???
  cleaned
}

val jNames = cleaned.filter(_.startsWith("J"))

// val jNameCount = jNames.count()

// println(s"There are $jNameCount names starting with 'J'")
```

Our side effect didn't execute

---

# Put back the action

```diff
 val names = session.read.textFile("names.txt")

 val cleaned = names.map(_.trim).filter(_.nonEmpty)

 val jNames = cleaned.filter(_.startsWith("J")).map { cleaned =>
   println(cleaned) // sneaky side effect
   cleaned
 }

-// val jNameCount = jNames.count()
+val jNameCount = jNames.count()

 // println(s"There are $jNameCount names starting with 'J'")
```

---

# What happened?

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty).map { cleaned =>
  println(cleaned) // It printed!
  cleaned
}

val jNames = cleaned.filter(_.startsWith("J"))

val jNameCount = jNames.count()
```

---

# Add bNames

```diff
 val names = session.read.textFile("names.txt")

 val cleaned = names.map(_.trim).filter(_.nonEmpty)

 val jNames = cleaned.filter(_.startsWith("J"))

+val bNames = cleaned.filter(_.startsWith("B"))
```

---

# Add printing to each

```diff
 val names = session.read.textFile("names.txt")

 val cleaned = names.map(_.trim).filter(_.nonEmpty)

 val jNames = cleaned.filter(_.startsWith("J")).map { jName =>
+  println(jName)
+  jName
+}

 val bNames = cleaned.filter(_.startsWith("B")).map { bName =>
+  println(bName)
+  bName
+}
```

---

# See what happens!

---

# What happened?

Just printed the jNames...

```scala
val jNames = cleaned.filter(_.startsWith("J")).map { jName =>
  println(jName)
  jName
}

val bNames = cleaned.filter(_.startsWith("B")).map { bName =>
  println(bName)
  bName
}

val jNameCount = jNames.count()
```

---

# One-dollar-ionairre

Why did spark print the jNames but not the bNames?

```
(A) Spark is dumb           (C) Spark internally represents RDD computations
                                as DAG's and executes them when there's an action e.g. count()


(B) Spark is jascist        (D) All of the above
```

---

# C!

Why did spark print the jNames but not the bNames?

```
(A) Spark is dumb           (C) Spark internally represents RDD computations
                                as DAG's and executes them when there's an action e.g. count()
                                ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

(B) Spark is jascist        (D) All of the above
```

---

# DAG's

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

val jNames = cleaned.filter(_.startsWith("J"))

val bNames = cleaned.filter(_.startsWith("B"))

val jNameCount = jNames.count()
```

```
names            textFile(names.txt)

                        |

cleaned           .map(_.trim).filter(_.nonEmpty)

                   /                             \
jNames       .filter(_.startsWith("J"))    .filter(_.startsWith("B"))
bNames
                  |

jNameCount    .count()
```

There is no "action" associated with `bNames`, so evalutation is never triggered

---

# Recap

RDD's are "lazy"

You can define them, but they aren't computed until an "action" (e.g. count) is executed which needs that RDD

---

# Recap

RDD definitions are more like recipes

They get combined into a graph and sent to the cluster to execute

---

# Combining transformations

---

# Repl example

Let's replicate this on the repl

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

val jNames = cleaned.filter(_.startsWith("J"))
```

---

# Summary

```scala
val names = Seq("Boban", " Tom", "", "Bill ", "Harry", "Feroz", " James", "Jamscii ")

// Many intermediate collections and additional passes
names.map(_.trim).filter(_.nonEmpty).filter(_.startsWith("J"))

names.map(_.trim).filter(name => name.nonEmpty && name.startsWith("J"))
// Seq("James", "Jamscii")

names.collect { case name if name.trim.nonEmpty && name.trim.startsWith("J") => name.trim }
// Seq("James", "Jamscii")
```

The collect is ugly, but it avoids intermediate collections

---

# My lies

```scala
sparkContext
  .textFile("names.txt")
  .filter(!_.startsWith("Boban"))
  .map(_.toUpperCase)
```

```
Partition 1 || Filtered  || Mapped
------------||-----------||------
Boban       ||           ||
Bobanita    ||           ||
Susu        || Susu      || SUSU
...         || ...       || ...
Lulu        || Lulu      || LULU
Bobanthon   ||           ||
Clement     || Clement   || CLEMENT
```

Spark does this for you under the hood

It wouldn't make the intermediate filtered partition

---

# Recap

Spark will combine steps as much as possible to avoid the creation of intermediate partitions

---

# Recap

Spark will combine steps as much as possible to avoid the creation of intermediate partitions

---

# Recap

Spark isn't executing your job "one step at a time"

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

val jNames = cleaned.filter(_.startsWith("J"))

jNames.count()
```

```
names            textFile(names.txt)

                        |

cleaned     .map(_.trim).filter(_.nonEmpty)

                        |

jNames       .filter(_.startsWith("J"))

                        |

jNameCount           .count()
```

Spark evaluates all the steps and builds it into an execution plan

It will combine steps and do all kinds of ugly optimisations under the hood to make it faster

---

# That pause...

Probably spark analysing the transformations and building an execution plan

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

# Today

RDD's are a fundamental spark concept

Today we got a deeper sense of what an RDD really is

---

# Developers perspective

An Seq-like abstraction representing a step of your job

Resilient

Distributed

"Lazy"

Immutable

---

# Under the hood

Your RDD definitions get compiled into an execution plan

Many steps might get combined into a single stage

Spark does all this under the hood hiding the details from you

---

# Next time

Common gotchas

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
