---
author: Rohan
date: 2023-07-10
title: Advanced Sequences
---

```
    _       _                               _
   / \   __| |_   ____ _ _ __   ___ ___  __| |
  / _ \ / _` \ \ / / _` | '_ \ / __/ _ \/ _` |
 / ___ \ (_| |\ V / (_| | | | | (_|  __/ (_| |
/_/   \_\__,_| \_/ \__,_|_| |_|\___\___|\__,_|

 ____
/ ___|  ___  __ _ _   _  ___ _ __   ___ ___  ___
\___ \ / _ \/ _` | | | |/ _ \ '_ \ / __/ _ \/ __|
 ___) |  __/ (_| | |_| |  __/ | | | (_|  __/\__ \
|____/ \___|\__, |\__,_|\___|_| |_|\___\___||___/
               |_|
```

---

# Today

Mopping up

- indexed sequences


- linear sequences


- parallel sequences

---

# Diagram

To the browser!

---

# Recap

`IndexedSeq` - abstraction for efficient `apply` and `length`

`LinearSeq` - abstraction with efficient `head` and `tail` operations

[Docs](https://docs.scala-lang.org/overviews/collections-2.13/seqs.html)

[Diagram](https://docs.scala-lang.org/overviews/collections-2.13/overview.html)

---

# Built in iterators

`IndexedSeq` has a built in iterator

To the repl!

---

# Summary

```scala
def indexedFuncSeq[A](len: Int)(f: Int => A): IndexedSeq[A] = new IndexedSeq[A] {
  def apply(i: Int): A = f(i) // TODO - add range checking

  val length: Int = len
}
```

Iterator can be generated off the `apply` method (assuming it's fast)

---

# LinearSeq

Operations derived off the iterator

To the repl!

---

# Summary

```scala
val linearSeq = new LinearSeq[String] {
  override def iterator: Iterator[String] = Iterator("yo", "hi", "man")
}
```

All behaviour derived off the iterator

---

# Seq discussion

Recall discussion from first session:

> Should we use `Seq` everywhere?

---

# Some talking points

## Abstraction vs Performance

```scala
def gimmeSeq(seq: Seq[String]): Unit = {
  ...
  seq.head // how fast?
  ...
}
```

Data isn't an abstraction

Abstractions don't capture performance

## Decision fatigue

Using `Seq` everywhere makes life simple

But we could use say `Vector` everywhere


## Interoperating with libraries

`Seq` makes life easier here

---

# Abstractions?

> Data isn't an abstraction

But `Seq` is an abstraction

It abstracts over _how_ the data is fetched

---

# Performance

> Abstractions don't capture performance

This is what `IndexedSeq` and `LinearSeq` are trying to capture

---

# Performance

> Abstractions don't capture performance

This is what `IndexedSeq` and `LinearSeq` are trying to capture

You could use these in performance critical situations

```scala
def gimmeSeq(seq: LinearSeq[String]): Unit = {
  ...
  seq.head // how fast? O(1)
  ...
}
```

Flexibility and performance

---

```
 ____                 _ _      _
|  _ \ __ _ _ __ __ _| | | ___| |
| |_) / _` | '__/ _` | | |/ _ \ |
|  __/ (_| | | | (_| | | |  __/ |
|_|   \__,_|_|  \__,_|_|_|\___|_|

 ____
/ ___|  ___  __ _ _   _  ___ _ __   ___ ___  ___
\___ \ / _ \/ _` | | | |/ _ \ '_ \ / __/ _ \/ __|
 ___) |  __/ (_| | |_| |  __/ | | | (_|  __/\__ \
|____/ \___|\__, |\__,_|\___|_| |_|\___\___||___/
               |_|
```

---

# Demo time

To the repl!

---

# Summary

## Hand rolled

```scala
Future.traverse((0 until 20).grouped(3).toSeq) { subRange => Future(subRange.map(_ + 1)) }.map(_.flatten)
```

(And await the Future)

## Using parallel collection

```scala
import $ivy.`org.scala-lang.modules::scala-parallel-collections:1.0.3`

import scala.collection.parallel.CollectionConverters._

(0 until 20).par.map(_ + 1)
```

Easy to understand

Doesn't introduce Future's

---

# Repl notes

NOTE: repl bug triggers deadlock so wrap it in a little object

See [issue](https://github.com/com-lihaoyi/Ammonite/issues/556)

```scala
object Demo {
  def demo = (0 until 20).par.map(_ + 1)
}
Demo.demo
```

---

# Caution

Be careful with side effects as the order they're executed is not deterministic

```scala
object Demo {
  def demo = (0 until 20).par.map(_ + 1).foreach(println)
}
Demo.demo
/*
6
3
8
1
13
2
16
11
17
14
10
4
9
7
5
15
18
12
19
20
*/
```

Output comes out in random order due to parallelisation

---

# What's it about?

[Docs](https://docs.scala-lang.org/overviews/parallel-collections/overview.html)

> Parallel collections were included in the Scala standard library in an effort to facilitate parallel programming
>
> by sparing users from low-level parallelization details,

---

# What's it about?

[Docs](https://docs.scala-lang.org/overviews/parallel-collections/overview.html)

> Parallel collections were included in the Scala standard library in an effort to facilitate parallel programming
>
> by sparing users from low-level parallelization details,
>
> meanwhile providing them with a familiar and simple high-level abstraction.

---

# What's it about?

[Docs](https://docs.scala-lang.org/overviews/parallel-collections/overview.html)

> Parallel collections were included in the Scala standard library in an effort to facilitate parallel programming
>
> by sparing users from low-level parallelization details,
>
> meanwhile providing them with a familiar and simple high-level abstraction.
>
> The hope was, and still is, that implicit parallelism behind a collections abstraction will bring reliable parallel execution one step closer to the workflow of mainstream developers.

---

# When to use it?

---

# When to use it?

Parallelism has an overhead

---

# When to use it?

Parallelism has an overhead

>  As a general heuristic,
>
>  speed-ups tend to be noticeable when the size of the collection is large,
>
>  typically several thousand elements

---

# Thinking globally

You're not the only one using cores

e.g. the play framework / akka

---

# Good use cases

Ad-hoc scripts

---

# Collection types

---

# Collection types

There is `ParArray` and `ParVector`...

---

# Collection types

There is `ParArray` and `ParVector`...

but no `ParList`

Why?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Collection types

There is `ParArray` and `ParVector`...

but no `ParList`

Why?

> Collections that are inherently sequential
>
> (in the sense that the elements must be accessed one after the other),
>
> like lists, queues, and streams,
>
> are converted to their parallel counterparts

---

# Imagine parallelisation

```
            0  1  2  3  4  5  6  7  8  9  10  11  ...
                          |                  |
map(_ * 10)               |                  |
                          |                  |
            0  10 20 30 40 50 60 70 80 90 100 110 ...

              Thread 1             Thread 2       Thread 3
```

Much easier when you can hop around

ie. fast random access

---

# Conversion

> Collections that are inherently sequential ..., like lists,
>
> are converted to their parallel counterparts
>
> by copying the elements into a similar parallel collection.

Overhead

---

# Conversion

> Collections that are inherently sequential ..., like lists,
>
> are converted to their parallel counterparts
>
> by copying the elements into a similar parallel collection.

Overhead

No overhead for `Array` and `Vector` - they're just wrapped

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

# IndexedSeq

An abstraction for sequences with fast `apply` and `length`

`Array`, `Vector`, `Range`, `String`

---

# LinearSeq

An abstraction for sequences with fast `head` and `tail`

`List`, `LazyList`

---

# Parallel Sequences

Designed to make quick and dirty parallelism easy

Use with care

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
