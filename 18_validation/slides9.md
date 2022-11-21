---
author: Rohan
date: 2022-11-24
title: Safer Sequence
---

```
 ____         __
/ ___|  __ _ / _| ___ _ __
\___ \ / _` | |_ / _ \ '__|
 ___) | (_| |  _|  __/ |
|____/ \__,_|_|  \___|_|

 ____
/ ___|  ___  __ _ _   _  ___ _ __   ___ ___
\___ \ / _ \/ _` | | | |/ _ \ '_ \ / __/ _ \
 ___) |  __/ (_| | |_| |  __/ | | | (_|  __/
|____/ \___|\__, |\__,_|\___|_| |_|\___\___|
               |_|
```

---

# Today

Another use case for literal singleton types

---

# Problem we'll solve

Make a `Seq` that is safe

---

# Problem we'll solve

Make a `Seq` that is safe

ie. methods are complete

---

# Incomplete examples

To the repl!

---

# Recap

Unsafe operations:

- head


- tail


- last


- zip


- take


- drop

---

# How to make it safe?

---

# How to make it safe?

If the compiler knows the length of a sequence,

it can refuse method calls that aren't safe

---

# What it would look like

```scala
val nseq1 = NSeq("hi", "yo")
nseq1.head // compiles

val nseq2 = NSeq.empty[String]
nseq2.head // won't compile
```

---

# What it would look like

```scala
val nseq1 = NSeq("hi", "yo", "man")
val nseq2 = NSeq("me", "mo", "man")
nseq1.zip(nseq2) // compiles

val nseq3 = NSeq("two", "elements")
val nseq4 = NSeq("three", "elements", "ha!")
nseq3.zip(nseq4) // won't compile
```

---

# What it would look like

```scala
val nseq = NSeq("x", "y", "z")

nseq.take[2] // compiles
nseq.take[4] // won't compile
```

---

# How to make it safe?

> If the compiler knows the length of a sequence,
>
> it can refuse method calls that aren't safe

How do you make the compiler "know" something?

What does the compiler use to reason about things?

---

# How to make it safe?

> How do you make the compiler "know" something?
>
> What does the compiler use to reason about things?

Types

---

# Length as a type

Our sequence will make its length into a type

```scala
val nseq: NSeq[3, String] = NSeq("x", "y", "z")
```

---

# DRY

We don't want to reinvent the wheel and reimplement all the logic of sequences

---

# Wrapper

> We don't want to reinvent the wheel and roll our own sequence

```

                  
                  Seq[A]


```

---

# Wrapper

> We don't want to reinvent the wheel and roll our own sequence

```
                ----------
               |          |  
               |  Seq[A]  |
               |          |
                ----------
```

Put a wrapper layer around it

---

# Type safe Wrapper

> We don't want to reinvent the wheel and roll our own sequence

```
                ----------
               |          |  
               |  Seq[A] <---- head
               |          |
                ----------
```

The outer layer lets method calls through to the inner sequence,

if it deems them safe

---

# Let's go!

---

# Recap

```scala
import $ivy.`eu.timepit::singleton-ops:0.5.0`
import singleton.ops.{+, -, Require, >, <=, >=}

class NSeq[N, A] private(val seq: Seq[A]) {
  def reverse: NSeq[N, A] = new NSeq(seq.reverse)

  def map[B](f: A => B): NSeq[N, B] = new NSeq(seq.map(f))

  def ::(newHead: A): NSeq[N + 1, A] = new NSeq(newHead :: seq.toList)

  def ++[M](other: NSeq[M, A]): NSeq[N + M, A] = new NSeq(seq ++ other.seq)

  def head(implicit req: Require[N > 0]): A = seq.head

  def last(implicit req: Require[N > 0]): A = seq.last

  def tail(implicit req: Require[N > 0]): NSeq[N-1, A] = new NSeq(seq.tail)

  def zip[B](other: NSeq[N, B]): NSeq[N, (A, B)] = new NSeq(seq.zip(other.seq))

  def take[M <: Int](implicit m: ValueOf[M], req1: Require[M <= N], req2: Require[M >= 0]): NSeq[M, A] = new NSeq(seq.take(m.value))

  def drop[M <: Int](implicit m: ValueOf[M], req1: Require[M <= N], req2: Require[M >= 0]): NSeq[N-M, A] = new NSeq(seq.drop(m.value))

  def ===(other: NSeq[N, A]): Boolean = seq == other.seq

  def filter(predicate: A => Boolean): Seq[A] = seq.filter(predicate)

  override def toString: String = s"NSeq${seq.mkString("(", ", ", ")")}"
}

object NSeq {
  def empty[A]: NSeq[0, A] = new NSeq(Seq.empty)
  def apply[A](a0: A): NSeq[1, A] = new NSeq(Seq(a0))
  def apply[A](a0: A, a1: A): NSeq[2, A] = new NSeq(Seq(a0, a1))
  def apply[A](a0: A, a1: A, a2: A): NSeq[3, A] = new NSeq(Seq(a0, a1, a2))
  def fromSeq[N, A](seq: Seq[A])(implicit n: ValueOf[N]): Option[NSeq[N, A]] = if (seq.size == n.value) Some(new NSeq(seq)) else None
}
```

---

# Recap

Our goal was to create a "safe" sequence

ie. all methods on it are complete, and no subtle traps (e.g. zip)

---

# Recap

We achieved this by capturing the length of the sequence as a type

---

# Recap

> We achieved this by capturing the length of the sequence as a type

That allows the compiler to know whether an operation is safe or not

---

# Literal Singletons

We used singleton int's as types

---

# Tidying up

Sometimes we need to reshape data

To the repl!

---

# Recap

```scala
def gimme3(nseq: NSeq[3, String]): Unit = {
  println(nseq)
}

gimme3("a" :: nseq2)
/*
Type mismatch
Found:    NSeq[2 + 1, String]
Required: NSeq[3, String]
*/
```

---

# Solution 1

Make `gimme3` more flexible:

```scala
def gimme3[N](nseq: NSeq[N, String])(implicit req: Require[N == 3]): Unit = {
  println(nseq)
}

gimme3("a" :: nseq2) // compiles
```

---

# Solution 2

Add reshape method:

```scala
class NSeq[N, A] private(val seq: Seq[A]) {

  ...

  def reshape[M](implicit req: Require[M == N]): NSeq[M, A] = new NSeq(seq)

  ...
}

gimme3(("a" :: nseq2).reshape[3]) // compiles
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

# Type level programming

Another example of "type level" programming

ie. pushing more information/logic into the type system

---

# Type level programming

Our `NSeq` was very safe

The compiler can analyse the length of our sequence and decide if an operation is safe

---

# Practical?

This example isn't going to be practical in real programming

---

# Why?

If it's not practical, why cover it?

---

# Why?

> If it's not practical, why cover it?

Some of the tricks are useful

Push you a bit more into "type level" programming and safety

---

# Why?

> If it's not practical, why cover it?

And it's just interesting and fun!

(for me anyway)

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
