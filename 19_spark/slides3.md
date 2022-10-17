---
author: Rohan
date: 2022-10-14
title: Spark Gotchas
---

```
  ____       _       _
 / ___| ___ | |_ ___| |__   __ _ ___
| |  _ / _ \| __/ __| '_ \ / _` / __|
| |_| | (_) | || (__| | | | (_| \__ \
 \____|\___/ \__\___|_| |_|\__,_|___/
```

Common ways to shoot yourself in the foot with spark

---

# Spark vs Vanilla Scala

They look the same,

but they're different

Plenty of ways to shoot yourself

---

# Today

Go through common gotchas I've seen people hit

---

# Common theme

Wearing your "software engineering" hat, not your "data enginerring" hat

---

# Transformations vs Actions

---

# Transformations vs Actions

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

cleaned.map { name =>
  println(name)
  name
}
```

---

# Recall

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

cleaned.map { name =>
  println(name)
  name
}
```

We are building tranformations here

You need an "action" like `.size` or `.collect` to cause a DAG to be built and deployed

---

# Summary

RDD's have a "lazy" feel to them

They need an action to trigger execution

---

# Pulling RDD's into memory

---

# .collect

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

val cleanedArray: Array[String] = cleaned.collect
```

(Different `collect` to the one from the standard library)

---

# Gotcha

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

val cleanedArray: Array[String] = cleaned.collect
```

This brings the entire RDD into the memory of the driver program

```
-- Laptop ---               ------------ Spark Cluster ----------------

                              Node 1     Node 2    Node 3 ...

  Driver        ---->         Executor  Executor  Executor  Executor ...
  Program                       JVM       JVM       JVM       JVM
  JVM
```

---

# Scale

> This brings the entire RDD into the memory of the driver program

If it can fit in memory, then maybe you don't need spark

---

# Common pattern

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

val cleanedArray: Array[String] = cleaned.take(20).collect
//                                        ^^^^^^^^
```

---

# Data Engineering Hat

Spark makes it easy to blur the lines between the driver program and cluster

Developes might use software engineering patterns that don't make sense at scale

---

# Summary

Be mindful of _where_ computations/memory are

Large scale computations are meant for the cluster

Driver program is usually meant for O(1) space complexity

---

# Printing

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

cleaned.foreach(println)
```

Any thoughts?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Problems

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

cleaned.foreach(println)
```

- scale - what if there's 40 million names?


- where is the printing being done? How will it interleave?

---

# Shift in mindset

We can't print entire RDD's

Just samples

---

# Helpful tools

```scala
show(n)

printSchema()
```

To the script!

---

# Printing and Actions

Be mindful that printing might trigger unnecessary processing

Might not make sense in production

---

# Shuffles

---

# Recap

Actually covered this a bit already

---

# Fast example

```scala
sparkContext
  .textFile("names.txt")
  .filter(!_.startsWith("Boban"))
  .map(_.toUpperCase)
```

New partitions are created

```
Partition 1 || Mapped
------------||------
Boban       ||
Bobanita    ||
Susu        || SUSU
...         || ...
Lulu        || LULU
Bobanthon   ||
Clement     || CLEMENT
```

Nice because the computation is "local"

---

# Slow example

```scala
val names = sparkContext.textFile("names.txt")

val namePairs = names.cartesian(names)
```

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

Entire partitions of data being shuffled across the network

---

# More common example

Grouping/Bucketing

---

# Analytics

Common to group data when you're doing analytics:

- find the average income by age brackets


- find the total revenue by month

---

# Repl example

Play around with regular scala to build some intuition

---

# Summary

```scala
@ names.groupBy(_.head)
Map(
  'T' -> List("Tom", "Tiger"),
  'J' -> List("James", "Jamscii", "Jimmy"),
  'F' -> List("Feroz"),
  'B' -> List("Boban", "Bill", "Barry"),
  'P' -> List("Pranali"),
  'H' -> List("Harry")
)

@ names.groupBy(_.head).mapValues(_.size).toMap
Map('T' -> 2, 'J' -> 3, 'F' -> 1, 'B' -> 3, 'P' -> 1, 'H' -> 1)
```

---

# Spark

Let's replicate this

To the script!

---

# Spark

```scala
val names = sc.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

val grouped = cleaned.groupBy(_.head.toString)

val totals = grouped.mapValues(_.size)

val df = totals.toDF()

df.printSchema
df.show()
```

```
root
 |-- _1: string (nullable = true)
 |-- _2: integer (nullable = false)

+---+---+
| _1| _2|
+---+---+
|  R|  1|
|  B|  1|
|  P|  2|
|  J|  3|
|  F|  1|
|  V|  1|
|  Y|  2|
+---+---+
```

---

# Problem...

But there's a scale issue...

Let's understand what's really happening step by step

---

# Grouping

```scala
val grouped = cleaned.groupBy(_.head)
```

```
names.txt  |||       cleaned 1   ||  cleaned 2   ||  cleaned 3
--------   |||       ------------||--------------||-------------
Boban      |||       Boban       ||  Bobanish    ||  Rohan
Bobanita   |||       Bobanita    ||  Jonathan    ||  Lulu
...        |||       ...         ||  ...         ||  ...
Clement    |||       Clement     ||  Bobanta     ||  Bobanithy
Bobanish   |||
Jonathan   |||
...        |||       grouped 1                     ||   grouped 2
Bobanta    |||       ------------------------------||-----------------------
Rohan      |||       ('C', ["Clement"])            ||   ('L', ["Lulu", "Linh"])
Lulu       |||       ('E', ["Enxhell"])            ||   ('J', ["Jonathan", "James"])
...        |||       ('R', ["Rohan", "Ritchie"])   ||   ('Z', ["Zij"])
Bobanithy  |||       ('V', ["Vish", "Valentine"])  ||
```

Causes a big shuffle

Shuffles == Sad

---

# Sizing

```scala
val grouped = cleaned.groupBy(_.head)

val totals = grouped.mapValues(_.size)
```

```
names.txt  |||       cleaned 1   ||  cleaned 2   ||  cleaned 3
--------   |||       ------------||--------------||-------------
Boban      |||       Boban       ||  Bobanish    ||  Rohan
Bobanita   |||       Bobanita    ||  Jonathan    ||  Lulu
...        |||       ...         ||  ...         ||  ...
Clement    |||       Clement     ||  Bobanta     ||  Bobanithy
Bobanish   |||
Jonathan   |||
...        |||       grouped 1                     ||   grouped 2
Bobanta    |||       ------------------------------||-----------------------
Rohan      |||       ('C', ["Clement", ...])       ||   ('L', ["Lulu", "Linh", ...])
Lulu       |||       ('E', ["Enxhell", ...])       ||   ('J', ["Jonathan", "James", ...])
...        |||       ('R', ["Rohan", "Ron", ..])   ||   ('Z', ["Zij", ...])
Bobanithy  |||       ('V', ["Vish", "Valentine"])  ||   ('C', ["Charles", "Chucky", ...])
                      ...



                    totals 1   ||  totals 2
                   ------------||----------
                   ('C', 234)  || ('L', 139)
                   ('E', 300)  || ('J', 423)
                   ('R', 123)  || ('Z', 443)
                   ...
```

---

# Analogy

You want to know how many people in the world have names starting with 'J'

---

# Analogy

> You want to know how many people in the world have names starting with 'J'

You talk with each world government

You arrange to have them all flown to Sydney

Then you count all the people

---

# A more efficient way?

---

# A more efficient way

```diff
 You talk with each world government

-You arrange to have them all flown to Sydney
+You arrange to have each government count their J citizens and send you the number

-Then you count them
+Then you add those subtotals
```

---

# reduceByKey

Grouping and reducing in one step

To the demo!

---

# Summary

```scala
val names = sc.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

val paired = cleaned.map(name => (name.head, 1))

val totals = paired.reduceByKey(_ + _)
```

Replaced `groupBy(..).mapValues(...)` with `map(...).reduceByKey`

Gave us the same answer

---

# Who cares?

## Before

```scala
val grouped = cleaned.groupBy(_.head)

val totals = grouped.mapValues(_.size)
```

## After

```scala
val paired = cleaned.map(name => (name.head, 1))

val totals = paired.reduceByKey(_ + _)
```

## Evaluating

They give the same answer

And the second looks weird and unfamiliar...

Why not just use the first one?

---

# Second example under the hood

---

# Pairing

```scala
val paired = cleaned.map(name => (name.head, 1))
```

```
names.txt  |||       cleaned 1    || cleaned 2    ||  cleaned 3
--------   |||       -------------||--------------||-------------
Boban      |||       Boban        || Bobanish     ||  Rohan
Bobanita   |||       Bobanita     || Jonathan     ||  Lulu
...        |||       ...          || ...          ||  ...
Clement    |||       Clement      || Bobanta      ||  Bobanithy
Bobanish   |||
Jonathan   |||
...        |||       paired 1     || paired 2     ||  paired 3
Bobanta    |||       -------------||--------------||-------------
Rohan      |||       (B, 1)       || (B, 1)       ||  (R, 1)
Lulu       |||       (B, 1)       || (J, 1)       ||  (L, 1)
...        |||       ...          || ...          ||  ...
Bobanithy  |||       (C, 1)       || (B, 1)       ||  (B, 1)
```

Pairing doesn't require any shuffles

---

# Reducing locally

```scala
val paired = cleaned.map(name => (name.head, 1))

val totals = paired.reduceByKey(_ + _)
```

```
names.txt  |||       cleaned 1    || cleaned 2    ||  cleaned 3
--------   |||       -------------||--------------||-------------
Boban      |||       Boban        || Bobanish     ||  Rohan
Bobanita   |||       Bobanita     || Jonathan     ||  Lulu
...        |||       ...          || ...          ||  ...
Clement    |||       Clement      || Bobanta      ||  Bobanithy
Bobanish   |||
Jonathan   |||
...        |||       paired 1     || paired 2     ||  paired 3
Bobanta    |||       -------------||--------------||-------------
Rohan      |||       (B, 1)       || (B, 1)       ||  (R, 1)
Lulu       |||       (B, 1)       || (J, 1)       ||  (L, 1)
...        |||       ...          || ...          ||  ...
Bobanithy  |||       (C, 1)       || (B, 1)       ||  (B, 1)


                     reduce 1     || reduce 2     ||  reduce 3
                     -------------||--------------||-------------
                     (A, 100)     || (A, 130)     ||  (A, 430)
                     (B, 200)     || (B, 150)     ||  (B, 250)
                     (C, 150)     || (C, 200)     ||  (C, 130)
                     (D, 420)     || (D, 320)     ||  (D, 220)
                     ...          || ...          ||  ...
```

The partition-wise reduce doesn't require any shuffles

---

# Reducing globally

```scala
val paired = cleaned.map(name => (name.head, 1))

val totals = paired.reduceByKey(_ + _)
```

```
names.txt  |||       cleaned 1    || cleaned 2    ||  cleaned 3
--------   |||       -------------||--------------||-------------
Boban      |||       Boban        || Bobanish     ||  Rohan
Bobanita   |||       Bobanita     || Jonathan     ||  Lulu
...        |||       ...          || ...          ||  ...
Clement    |||       Clement      || Bobanta      ||  Bobanithy
Bobanish   |||
Jonathan   |||
...        |||       paired 1     || paired 2     ||  paired 3
Bobanta    |||       -------------||--------------||-------------
Rohan      |||       (B, 1)       || (B, 1)       ||  (R, 1)
Lulu       |||       (B, 1)       || (J, 1)       ||  (L, 1)
...        |||       ...          || ...          ||  ...
Bobanithy  |||       (C, 1)       || (B, 1)       ||  (B, 1)


                     reduce 1     || reduce 2     ||  reduce 3
                     -------------||--------------||-------------
                     (A, 100)     || (A, 130)     ||  (A, 430)       |
                     (B, 200)     || (B, 150)     ||  (B, 250)       | 26 rows
                     (C, 150)     || (C, 200)     ||  (C, 130)       |
                     (D, 420)     || (D, 320)     ||  (D, 220)       |
                     ...          || ...          ||  ...


                                  final
                                  -----
                                  (A, 660)
                                  (B, 600)
                                  (C, 480)
                                  ...
```

This requires shuffling of data from across partitions

But the volume of data is already aggregated into tiny dictionaries

---

# Back to our analogy

```diff
 You talk with each world government

-You arrange to have them all flown to Sydney
+You arrange to have each government count their J citizens and send you the number

-Then you count them
+Then you add those subtotals
```

Word governments = Executors/Partitions

> You arrange to have them all flown to Sydney

Big data shuffle

```
('J', ["James", "Jamscii", "Jimmy", ...])
```

> You arrange to have each government count their J citizens and send you the number

```
reduce 1   || reduce 2    || reduce 3
-----------||-------------||-------------------------
('J', 300) || ('J', 400)  || ('J', 450)
```

---

# Summary

The first method shuffles data, then aggregates it

The second method aggregates data, then shuffles it

Both give the same result, but at scale, the second is much better

---

# Reminder

With big data, it's not good enough that your script:

- compiles


- is functionally correct


- runs quickly on your test data

---

# Reminder

With big data, it's not good enough that your script:

- compiles


- is functionally correct


- runs quickly on your test data

It also needs to scale

---

# RDD vs DataFrame API

My example used the older RDD api

Most likely you will be using the DataFrame API

---

# RDD vs DataFrame API

My example used the older RDD api

Most likely you will be using the DataFrame API

The equivalent code using DataFrames would probably optimise the shuffles

The example is still a helpful insight and warning

---

# Partition Skew

---

# Conceptually

This is where your RDD isn't balanced across your partitions.

```
Partition 1: ===================================================
Partition 2: ==
Partition 3: =
Partition 4: =
```

---

# Scala Example

To the repl!

---

# Summary

```scala
@ val names = Seq(...) // lots of 'J' names

@ names.groupBy(_.head)
Map(
  'T' -> List("Tom", "Tiger"),
  'J' -> List("Jon", "Jon", "James", "Jiminy", "James", "Jamscii", "Jimmy"),
  'F' -> List("Feroz"),
  'B' -> List("Boban", "Bill", "Barry"),
  'P' -> List("Pranali"),
  'H' -> List("Harry")
)
```

Whoever has to process 'J' will become a bottleneck

---

# Deja vu

We've discussed this before...

---

# One-dollar-ionairre

When have we discussed partition skew before?

```
(A) Discussing Pranali's glasses       (C) MIS requests

(B) Kafka                              (D) Joseph's haircut
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# B!

When have we discussed partition skew before?

```
(A) Discussing Pranali's glasses       (C) MIS requests

(B) Kafka                              (D) Joseph's haircut
    ^^^^^
```

---

# Kafka

Kafka has partitions

There is a hashing process to determine which partition a message goes to

Only one worker can process a partition at a time

## Healthy

```
Partition 1: =============
Partition 2: ==============
Partition 3: ============
Partition 4: =============
```

## Skewed

```
Partition 1: ===========================================
Partition 2: ==
Partition 3: =
Partition 4: ==
```

---

# null/None

Be careful of default values like `null` or `None`

To the repl!

---

# Summary

```scala
@ people.groupBy(_.age).mapValues(_.map(_.name)).toMap
Map(
  Some(26) -> List("Boban"),
  Some(64) -> List("Lee", "Simon"),
  None     -> List("Tom", "Pranali", "Feroz", "Ben", "William", "Suzie", "Marge", "Tiffany", "Sally")
)
```

Often data is heavily skewed towards default values

`null/None` are very common examples of this

---

# Universal Concept

Skew pops up everywhere (spark, kafka, databases)

It's easy to write code that is logically correct and runs fine on dummy data,

but **chokes at scale**

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

# Many gotchas!

---

# Laziness

RDD's are "lazy" and won't execute until some action triggers them

---

# Pulling RDD's into memory

Beware of things like `rdd.collect`

---

# Printing

Beware of code like this:

```scala
rdd.foreach(println)
```

In a big data context, you should be sampling data and printing O(1)

Spark has nice print methods like `.show`

---

# Shuffles

Shuffles make your job slow

Sometimes they're unavoidable,

but often there are tricks you can use to reduce the volume of data shuffled

---

# Partition Skew

Skew makes your parallelism meaningless by creating bottlenecks

Be careful when grouping data by key, doing joins etc...

Default values like `null` are big sources of skew

---

# Overall

Change your thinking when working with big data

Be aware that your data is distributed across nodes in a cluster being executed in parallel

---

# Know what you're doing

Copy pasting off stack overflow doesn't cut it

Blindly using auto-complete to get the answer you need doesn't cut it

---

# Sandstone?

Do we do big data?

---

# Sandstone?

> Do we do big data?

No

We don't even use a cluster

So most of these gotchas don't affect us

(Might be useful for Feroz though)

---

# Next time

Spark at Sandstone

How are we using it?

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
