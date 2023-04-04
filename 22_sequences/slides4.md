---
author: Rohan
date: 2023-05-29
title: Infinite Sequences
---

```
 ___        __ _       _ _
|_ _|_ __  / _(_)_ __ (_) |_ ___
 | || '_ \| |_| | '_ \| | __/ _ \
 | || | | |  _| | | | | | ||  __/
|___|_| |_|_| |_|_| |_|_|\__\___|

 ____
/ ___|  ___  __ _ _   _  ___ _ __   ___ ___  ___
\___ \ / _ \/ _` | | | |/ _ \ '_ \ / __/ _ \/ __|
 ___) |  __/ (_| | |_| |  __/ | | | (_|  __/\__ \
|____/ \___|\__, |\__,_|\___|_| |_|\___\___||___/
               |_|
```

---

# Infinite Sequences

Sequences don't have to be finite

---

# Infinite Sequences

Sequences don't have to be finite

Today's session will stretch our brains and give us a new perspective on sequences

---

```
 ____
|  _ \ ___  ___ __ _ _ __
| |_) / _ \/ __/ _` | '_ \
|  _ <  __/ (_| (_| | |_) |
|_| \_\___|\___\__,_| .__/
                    |_|
```

---

# Confusion

If you find it hard to accept that sequences are infinite,

---

# Confusion

If you find it hard to accept that sequences are infinite,

it might be because you think of sequences as being "in memory"

---

# Array example

```scala
val array = Array(0, 1, 2, 3)
```

```
Memory:
     | 0 | 1 | 2 | 3 |
```

---

# Array example

```scala
val array = Array(0, 1, 2, 3)
```

```
Memory:
     | 0 | 1 | 2 | 3 |
```

If you visualise sequences like this,

then how can you have an "infinite" sequence within finite memory?

---

# Recap

What is the essence of a sequence?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Recap

> What is the essence of a sequence?

Deterministic ordering

The i'th element is well defined

---

# Recap

> What is the essence of a sequence?

Deterministic ordering

The i'th element is well defined

There's no requirement that the sequence is "in memory" or finite

Sequences are more abstract than that

---

# Examples

## Range

```scala
val range = 0 until 2_000_000_000
```

2 billion elements

O(1) space - start, end and step

Element i is computed on the fly, not looked up from some data structure

## Function sequences

Values computed on the fly

e.g. evens

---

# Function sequences

Recall our `funcSeq` method

To the repl!

---

Make one that's:

```
 ___        __ _       _ _
|_ _|_ __  / _(_)_ __ (_) |_ ___
 | || '_ \| |_| | '_ \| | __/ _ \
 | || | | |  _| | | | | | ||  __/
|___|_| |_|_| |_|_| |_|_|\__\___|
```

---

# Squares

Imagine implementing our own sequence of squares:

```
Index:  0   1   2   3   4  ...

Value:  0   1   4   9   16 ...
```

ie. element `i` is `i^2`

---

# Make it infinite!

To the repl!

---

# Summary

```scala
def infFuncSeq[A](f: Int => A): Seq[A] = new Seq[A] {
  def apply(i: Int): A = f(i)

  def length: Int = throw new Exception("I won't be bound!")

  def iterator: Iterator[A] = new Iterator[A] {
    var currentIndex = 0
    def hasNext: Boolean = true
    def next(): A = {
      val element = apply(currentIndex)
      currentIndex += 1
      element
    }
  }
}

val squares = infFuncSeq(i => BigInt(i) * BigInt(i))

// Infinite loops
squares.map(_ + 1)
squares.filter(_ > 100)
squares.foreach(println)

// This is okay
squares.take(5)
```

Infinite and uses O(1) space

---

# length

```scala
  def length: Int = ...
```

Some sequences don't conceptually have a length,

so there is no way they can return something correct there

---

# length

```scala
  def length: Int = ...
```

Some sequences don't conceptually have a length,

so there is no way they can return something correct there

Not a complete function

---

# Pragmatic Compromise

This is more correct modelling:

```scala
  def length: Option[Int] = ...
```

---

# Pragmatic Compromise

This is more correct modelling:

```scala
  def length: Option[Int] = ...
```

But when 99.9% of sequences you work with are finite,

it would be annoying

---

```
 ____                          _
|  _ \ ___  ___ _   _ _ __ ___(_)_   _____
| |_) / _ \/ __| | | | '__/ __| \ \ / / _ \
|  _ <  __/ (__| |_| | |  \__ \ |\ V /  __/
|_| \_\___|\___|\__,_|_|  |___/_| \_/ \___|

 ____
/ ___|  ___  __ _ _   _  ___ _ __   ___ ___  ___
\___ \ / _ \/ _` | | | |/ _ \ '_ \ / __/ _ \/ __|
 ___) |  __/ (_| | |_| |  __/ | | | (_|  __/\__ \
|____/ \___|\__, |\__,_|\___|_| |_|\___\___||___/
               |_|
```

---

# Recursive sequences?

Element i is computed from the previous elements somehow

---

# Simple example

```
squareChain

0  ----->  2

1  ----->  4

2  ----->  16

3  ----->  256

4  ----->  65536

...
```

---

# Generalising

```
squareChain

0  ----->  2

i  ----->  squareChain(i - 1)^2
```

---

# Time complexity?

```
squareChain

0  ----->  2

1  ----->  4

2  ----->  16

3  ----->  256

4  ----->  65536

...

n  ----->  ???
```

How long to find element n?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Time complexity?

```
squareChain

0  ----->  2      /|\
                   |
1  ----->  4       |
                   |
2  ----->  16      |
                  /|\
3  ----->  256     |
                   |
4  ----->  65536   |
                   |
...               /|\
                   |
n  ----->  ???     |
```

> How long to find element n?

O(n) at least

(Squaring also gets more expensive as the numbers get larger)

---

# Implement it

To the repl!

---

# Summary

```scala
val squareChain: Seq[BigInt] = new Seq[BigInt] {
  // Imperative style
  def apply(i: Int): BigInt = {
    var acc = BigInt(2)
    for (j <- 1 until i)
      acc = acc * acc
    acc
  }

  // Functional style
  def apply(i: Int): BigInt = {
    (1 until i).foldLeft(BigInt(2)) {
      case (acc, _) => acc * acc
    }
  }

  def length: Int = throw new Exception("No length defined, sequence is infinite")

  def iterator: Iterator[BigInt] = new Iterator[BigInt] {
    var current = BigInt(2)

    def hasNext: Boolean = true

    def next(): BigInt = {
      val element = current
      current = current * current
      element
    }
  }
}
```

---

# Speed improvements

---

# Slow example

Code like this is a bit slow:

```scala
squareChain(100)

squareChain(80)
```

---

# Slow example

Code like this is a bit slow:

```scala
squareChain(100) // incidentally computes squareChain(80)

squareChain(80)  // recomputes
```

How to avoid this waste?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Slow example

Code like this is a bit slow:

```scala
squareChain(100) // incidentally computes squareChain(80)

squareChain(80)  // recomputes
```

> How to avoid this waste?

Memoisation!

---

# Stale?

Do we have to worry about stored values getting stale?

Cache invalidation?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Stale?

> Do we have to worry about stored values getting stale?
>
> Cache invalidation?

Sequences are deterministic (by definition)

Cache invalidation is for non-deterministic functions

---

# Example

## Non-deterministic

```scala
// June 2022
favouriteEditor("varun") // sublime

// June 2023
favouriteEditor("varun") // neovim
```

## Deterministic

```scala
// All day, every day
squareChain(3) // 256
```

The output is entirely determined by the input

---

# Data structure

What is a good data structure for the cache?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Data structure

> What is a good data structure for the cache?

`ArrayBuffer` (assuming single threaded access)

Contiguous data (no "gaps")

Needs to grow efficiently

O(1) lookup

```scala
[0] 2
[1] 4
[2] 16
[3] 256
...
```

---

# Code it up

To the repl!

---

# Summary

Jeepers, that was a lot of work...

```scala
val squareChain: Seq[BigInt] = new Seq[BigInt] {
  // NOTE: not thread-safe
  def apply(i: Int): BigInt = {
    cache.lift(i) match {
      case Some(value) => value
      case None =>
        // Dangerous - will SO if the last cached value is a lot lower than the current
        // Would be better to square upwards from the last cached value
        val value = i match {
          case 0 => BigInt(2)
          case _ =>
            val previous = apply(i - 1)
            previous * previous
        }
        cache.append(value)
        value
    }
  }

  def length: Int = throw new Exception("No length defined, sequence is infinite")

  private val cache = scala.collection.mutable.ArrayBuffer.empty[BigInt]

  def iterator: Iterator[BigInt] = new Iterator[BigInt] {
    var current = BigInt(2)

    def hasNext: Boolean = true

    def next(): BigInt = {
      val element = current
      current = current * current
      element
    }
  }
}
```

---

# LazyList

We will implement this with a lazy list

Will be much cleaner

---

# LazyList

Quick introduction first!

---

# Summary

```scala
// Normal factory method
val lazyList = LazyList(1, 2, 3)

// Slightly different prepend
0 #:: lazyList // LazyList(0, 1, 2, 3)
```

They have the same kind of recursive structure as `List`

---

# Summary

So far `List` and `LazyList` seem identical

However, one is lazy...

---

# Summary

So far `List` and `LazyList` seem identical

However, one is lazy...

Which one do you think is lazy?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Implement squareChain

With `List`?

---

# Implement squareChain

With `List`?

Problem is that `List` starts from the end

To the repl!

---

# Summary

`List` is built by starting and right and prepending to the left

```
1 :: 2 :: 3 :: Nil
```

By construction, it must be finite

But infinite sequences can't have a "right"

---

# LazyList

Like a `List`, but doesn't need a right

Use it to implement `squareChain`

To the repl!

---

# Summary

```scala
def buildSquareChain(current: BigInt): LazyList[BigInt] = current #:: buildSquareChain(current * current)

val squareChain = buildSquareChain(BigInt(2))

// Unravelling the recursion
val squareChain = BigInt(2) #:: buildSquareChain(BigInt(2) * BigInt(2))

val squareChain = BigInt(2) #:: BigInt(4) #:: buildSquareChain(BigInt(16))

val squareChain = BigInt(2) #:: BigInt(4) #:: BigInt(16) #:: BigInt(256) #:: BigInt(65536) #:: ...
```

---

# My next question?

What is my next question going to be?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Infinite recursion?

When does it stop?

Why didn't the repl get stuck in a loop or SO?

```scala
def buildSquareChain(current: BigInt): LazyList[BigInt] = current #:: buildSquareChain(current * current)
//                                                                    ^^^^^^^^^^^^^^^^
//                                                        Recursion without a base case...

val squareChain = buildSquareChain(BigInt(2))
```

---

# Laziness

There's some sneaky laziness happening under the hood

Similar to how loggers are lazy

More on this another time

---

# Memoisation!

To the repl!

---

# Summary

```scala
// Add a side effect to make clear when it's being evaluated
def buildSquareChain(current: BigInt): LazyList[BigInt] = {
  println(s"Calling for $current")
  current #:: buildSquareChain(current * current)
}

object Demo {
  val squareChain = buildSquareChain(BigInt(2))
}

Demo.squareChain.head
// Calling for 2
// 2

// Try again - no side effect - it's memoised
Demo.squareChain.head
// 2

Demo.squareChain(1)
// Calling for 4
// 4

Demo.squareChain(1)
// 4

Demo.squareChain.take(5)
// Calling for 16
// Calling for 256
// Calling for 65536
// LazyList(2, 4, 16, 256, 65536)
```

The elements are evaluated on demand and evaluated

Conceptually the sequence has infinite size, but only uses finite memory

---

# Comparing

## My ugly implementation

```scala
val squareChain: Seq[BigInt] = new Seq[BigInt] {
  // NOTE: not thread-safe
  def apply(i: Int): BigInt = {
    cache.lift(i) match {
      case Some(value) => value
      case None =>
        // Dangerous - will SO if the last cached value is a lot lower than the current
        // Would be better to square upwards from the last cached value
        val value = i match {
          case 0 => BigInt(2)
          case _ =>
            val previous = apply(i - 1)
            previous * previous
        }
        cache.append(value)
        value
    }
  }

  def length: Int = throw new Exception("No length defined, sequence is infinite")

  private val cache = scala.collection.mutable.ArrayBuffer.empty[BigInt]

  def iterator: Iterator[BigInt] = new Iterator[BigInt] {
    var current = BigInt(2)

    def hasNext: Boolean = true

    def next(): BigInt = {
      val element = current
      current = current * current
      element
    }
  }
}
```

## LazyList implementation

```scala
def buildSquareChain(current: BigInt): LazyList[BigInt] = current #:: buildSquareChain(current * current)

val squareChain = buildSquareChain(BigInt(2))
```

## Comparing

Which looks nicer?

---

# Summary

LazyList's are great for recursive sequences

Clean to express

Built in memoisation

---

# Example from the wild

Sequence of dates

---

# Example from the wild

Sequence of dates

```scala
LocalDate(2023, 5, 12)
LocalDate(2023, 5, 13)
LocalDate(2023, 5, 14)
LocalDate(2023, 5, 15)
LocalDate(2023, 5, 16)
...
```

Conceptually infinite

Useful for getting slices, e.g. "the next 5 days"

To the repl!

---

# Summary

```scala
import java.time.LocalDate

def datesFrom(start: LocalDate): LazyList[LocalDate] = start #:: datesFrom(start.plusDays(1))

val today = LocalDate.of(2023, 5, 12)

val dates = datesFrom(today)

val next5Days = dates.take(5)
// LazyList(2023-05-12, 2023-05-13, 2023-05-14, 2023-05-15, 2023-05-16)
```

---

# Wrapping up

LazyList's are great for recursive sequences being accessed from the front

Elegant syntax

Memoisation

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

# Concepts

Sequences are a deterministically ordered collection

That definition doesn't require them to be finite

---

# Space

Infinite sequences can be represented with finite space

---

# LazyList

Great tool for recursively defined sequences

---

# Next time

Parallel sequences

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
