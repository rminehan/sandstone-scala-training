---
author: Rohan
date: 2023-05-12
title: DIY Seq
---

```
 ____ _____   __
|  _ \_ _\ \ / /
| | | | | \ V /
| |_| | |  | |
|____/___| |_|

 ____
/ ___|  ___  __ _
\___ \ / _ \/ _` |
 ___) |  __/ (_| |
|____/ \___|\__, |
               |_|
```

---

# Today

Build our own `Seq`!

---

# Why?

Just to peak under the hood

---

# Why?

Just to peak under the hood

Not something you'd usually do in the wild

---

# Let's go!

Let's make our own custom `Seq[String]`

To the repl!

---

# Summary

We hand crafted a sequence with 3 elements

```scala
object MySeq extends Seq[String] {
  def apply(i: Int): String = i match {
    case 0 => "hello"
    case 1 => "world"
    case 2 => "yo"
  }
  val length: Int = 3
  def iterator: Iterator[String] = new Iterator[String] {
    var currentIndex = 0
    def hasNext: Boolean = currentIndex < 3
    def next(): String = {
      val element = apply(currentIndex)
      currentIndex += 1
      element
    }
  }
}
```

---

# Seq combinators

```scala
@ MySeq.map(_.length)
// Seq[Int] = List(5, 5, 2)

@ MySeq.filter(_.startsWith("h"))
// Seq[String] = List("hello")

@ for (s <- MySeq) {
    println(s)
  }
// hello
// world
// yo
```

---

# Range time

---

# Range time

Let's build our own Range

(for fun)

To the repl!

---

# Summary

```scala
case class MyRange(start: Int, length: Int, step: Int) extends Seq[Int] { range =>
  def apply(i: Int): Int = start + (i * step)

  def iterator: Iterator[Int] = new Iterator[Int] {
    var currentIndex = 0
    def hasNext: Boolean = currentIndex < range.length
    def next(): Int = {
      val element = apply(currentIndex)
      currentIndex += 1
      element
    }
  }

  def expand(factor: Int): MyRange = MyRange(start * factor, length, step * factor)
}

val singleDigits = MyRange(start = 0, length = 10, step = 1)

singleDigits.map(_ * 3) // O(n) time and space
// List(0, 3, 6, 9, 12, 15, 18, 21, 24, 27)

singleDigits.expand(3) // O(1) time and space
// Seq(0, 3, 6, 9, 12, 15, 18, 21, 24, 27)
```

---

# Better iterator

```scala
  def apply(i: Int): Int = start + i * step

  def next(): Int = {
    val element = apply(currentIndex) // <---- keeps calculating from the start
    currentIndex += 1
    element
  }
```

```
next = previous + step
```

Back to the repl!

---

# Summary

```scala
case class MyRange(start: Int, length: Int, step: Int) extends Seq[Int] { range =>
  // Random access
  def apply(i: Int): Int = start + (i * step)

  // Sequential access
  def iterator: Iterator[Int] = new Iterator[Int] {
    var currentIndex = 0
    var current = start
    def hasNext: Boolean = currentIndex < range.length
    def next(): Int = {
      val element = current
      currentIndex += 1
      current += step
      element
    }
  }
}
```

This implementation leans into the iterator pattern more

by taking advantage of the accumulated state

---

# Even numbers

---

# Even numbers

Element i is the i'th even number (starting from 0)

---

# Even numbers

Element i is the i'th even number (starting from 0)

```
Index  0   1   2   3   4   5 ...

Even   0   2   4   6   8  10 ...
```

---

# Well defined?

Is this conceptually a sequence?

```
Index  0   1   2   3   4   5 ...

Even   0   2   4   6   8  10 ...
```

Hint: what was the essence of a sequence from Pranali's talk?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Well defined?

Is this conceptually a sequence?

```
Index  0   1   2   3   4   5 ...

Even   0   2   4   6   8  10 ...
```

> Hint: what was the essence of a sequence from Pranali's talk?

Deterministic concept of the i'th element

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Well defined?

Is this conceptually a sequence?

```
Index  0   1   2   3   4   5 ...

Even   0   2   4   6   8  10 ...
```

Yah

Element i is just i*2

---

# Hmmm

What's my next question going to be?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Length

How long is the even sequence?

```
Index  0   1   2   3   4   5 ...

Even   0   2   4   6   8  10 ...

 ___
|__ \
  / /
 |_|
 (_)
```

---

# Length

How long is the even sequence?

```
Index  0   1   2   3   4   5 ...

Even   0   2   4   6   8  10 ...
```

It doesn't really have a conceptual upper bound

True mathematical integers can always be doubled

---

# Implementation time

To the repl!

---

# Summary

```scala
object Evens extends Seq[BigInt] { events =>
  def apply(i: Int): BigInt = BigInt(i) * BigInt(2)

  val length: Int = 20

  def iterator: Iterator[BigInt] = new Iterator[BigInt] {
    val iteratorMax = events.length * 2
    var current = BigInt(0)

    def hasNext: Boolean = current < iteratorMax

    def next(): BigInt = {
      val element = current
      current += BigInt(2)
      element
    }
  }
}
```

---

# Space

---

# Space

What is the space complexity of the sequences we made?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Last question

> What is the space complexity of the sequences we made?

O(1) for all

---

# Subconscious developer brain

> A sequence with n elements takes O(n) space

---

# Subconscious developer brain

> A sequence with n elements takes O(n) space
>
> Therefore a sequence with infinite elements must use infinite space

---

# Subconscious developer brain

> A sequence with n elements takes O(n) space
>
> Therefore a sequence with infinite elements must use infinite space
>
> But machines don't have infinite space, therefore they're impossible to create!

---

# Subconscious developer brain

> A sequence with n elements takes O(n) space
>
> Therefore a sequence with infinite elements must use infinite space
>
> But machines don't have infinite space, therefore they're impossible to create!

Then your brain melts

---

# Underlying misconception

> A sequence with n elements takes O(n) space

No!

Sequences are far more abstract than that

---

# Function sequence

---

# Function sequence

Even numbers was really just a function: `i => i * 2`

Let's generalise this

To the repl!

---

# Recap

```scala
def funcSeq[A](len: Int)(f: Int => A): Seq[A] = new Seq[A] {
  def apply(i: Int): A = f(i)

  val length: Int = len

  def iterator: Iterator[A] = new Iterator[A] {
    var currentIndex = 0
    def hasNext: Boolean = currentIndex < len
    def next(): A = {
      val element = apply(currentIndex)
      currentIndex += 1
      element
    }
  }
}

val evens = funcSeq(len = 20)(_ * 2)
// Seq(0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38)

val multiplesOf3 = funcSeq(len = 20)(_ * 3)
// Seq(0, 3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36, 39, 42, 45, 48, 51, 54, 57)
```

---

# Reflecting on this

Sequence:

> Deterministic concept of the i'th element

---

# Reflecting on this

Sequence:

> Deterministic concept of the i'th element

ie.

> It's a deterministic function from Int => A

(with a length)

---

# Simplifying this

Sequences are like deterministic functions that receive integers

Something to ponder...

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

# DIY

To create a scala `Seq`:

- implement `Seq[A]`


- fill in the missing bits (apply, length, iterator)

---

# Size vs Memory

A sequence's size does not have to correlate with the memory used

This correlation will mentally trip you up

---

# Sequences as functions

Looking at sequences from another perspective,

they are just deterministic functions from `Int` to your type

---

# Next time

Partial functions and sequences

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
