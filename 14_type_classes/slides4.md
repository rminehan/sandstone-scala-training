---
author: Rohan
date: 2022-04-19
title: Monoids
---

```
 __  __                   _     _
|  \/  | ___  _ __   ___ (_) __| |___
| |\/| |/ _ \| '_ \ / _ \| |/ _` / __|
| |  | | (_) | | | | (_) | | (_| \__ \
|_|  |_|\___/|_| |_|\___/|_|\__,_|___/
```

---

# Hard core FP

Another funky category theory concept

---

# Monoids, monads?

Names sound similar

There's no 'i' in monad,

but there is one in monoid...

---

# Monoids, monads?

Names sound similar (unfortunately)

They're different beasts

---

# Monads recap

Something with a `flatMap` concept, e.g.

- `Seq`


- `Option`


- `Future`

---

# Today: Monoids

Something "combine-able"

---

# Adjectives

A trick to remember things:

- functor: something map-able


- monad: something flatMap-able


- monoid: something combine-able

---

# Monoid - sneaky Rohan

You actually already have an intuition for monoids...

I just didn't use the terminology

---

```
 __  __                   _     _
|  \/  | ___  _ __   ___ (_) __| |
| |\/| |/ _ \| '_ \ / _ \| |/ _` |
| |  | | (_) | | | | (_) | | (_| |
|_|  |_|\___/|_| |_|\___/|_|\__,_|
```

---

# How to think about it

Suppose you wanted to fold a `Seq[A]` down to an `A`

What information would you need?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# How to think about it

> Suppose you wanted to fold a `Seq[A]` down to an `A`

`A` is a completely unknown type

> What information would you need?

- combine logic


- seed value (what if the sequence is empty?)

---

# Converting to types

> seed value (what if the sequence is empty?)

Value of type `A`

> combine logic

```scala
(A, A) => A
```

---

# Deja vu

These are just the parameters we put into an early fold method:

```scala
def fold[A](seq: Seq[A], seed: A)(combine: (A, A) => A): A = {
  var acc = seed

  for (number <- numbers)
    acc = combine(acc, number)

  acc
}

fold(Seq("a", "b", "c"), "")((acc, next) => acc + next)
// "abc"
```

---

# Rearranging

The seed and combine logic might be useful in other contexts, e.g.

- folding a matrix


- combining two individual `A`'s

Let's create a little abstraction to capture that...

---

# `Monoid[A]`

```scala
trait Monoid[A] {
  val identity: A

  def combine(left: A, right: A): A
}
```

This captures what we need to fold `A`'s together

---

# Rewrite fold

Have it take a monoid rather than individual arguments

Use it to sum and multiply over some sequences

To the repl!

---

# Summary

```scala
def fold[A](seq: Seq[A])(ev: Monoid[A]): A = {
  var acc = ev.identity

  for (a <- seq)
    acc = ev.combine(acc, a)

  acc
}

object IntAddition extends Monoid[Int] {
  val identity: Int = 0
  def combine(left: Int, right: Int): Int = left + right
}
fold(Seq(1, 2, 3))(IntAddition) // 6

object IntMultiplication extends Monoid[Int] {
  val identity: Int = 1
  def combine(left: Int, right: Int): Int = left * right
}
fold(Seq(2, 3, 4))(IntAddition) // 24
```

---

```
 _         _   _
| |    ___| |_( )___
| |   / _ \ __|// __|
| |__|  __/ |_  \__ \
|_____\___|\__| |___/

 _
| |__   ___
| '_ \ / _ \
| |_) |  __/
|_.__/ \___|

                    _ _     _
 ___  ___ _ __  ___(_) |__ | | ___
/ __|/ _ \ '_ \/ __| | '_ \| |/ _ \
\__ \  __/ | | \__ \ | |_) | |  __/
|___/\___|_| |_|___/_|_.__/|_|\___|

```

---

# Crazy monoids

This compiles as a monoid:

```scala
object CrazyMonoid extends Monoid[Int] {
  val identity = 42

  def combine(left: Int, right: Int): Int = (left * left) + (right * right * right)
}
```

---

# "Sensible" monoids

A true monoid doesn't allow any old riff raff,

there are some sensible properties you must fulfil

---

# Sensible identity

> combining with identity element doesn't change anything

---

# Sensible identity

> combining with identity element doesn't change anything

```scala
// for all a: A,
monoid.combine(a, monoid.identity) == a
monoid.combine(monoid.identity, a) == a
```

---

# Check our examples

```scala
object IntAddition extends Monoid[Int] {
  val identity: Int = 0
  def combine(left: Int, right: Int): Int = left + right
}
```

Adding 0 to something doesn't change it

```scala
object IntMultiplication extends Monoid[Int] {
  val identity: Int = 1
  def combine(left: Int, right: Int): Int = left * right
}
```

Multiplying 1 by something doesn't change it

```scala
object CrazyMonoid extends Monoid[Int] {
  val identity = 42

  def combine(left: Int, right: Int): Int = (left * left) + (right * right * right)
}
```

Combining with 42 will definitely change it

---

# Sensible combine

Associative

Hmmm, what is that again...?

---

# Who wants to be a one-dollar-ionairre!

For 60c,

Which of these expresses that `+` is associative?

```
(A)   a + b = b + a                  (B)   a * (b + c) = ab + ac

(C)  a + (b + c) = (a + b) + c       (D)   a + 0 = a
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# C

> Which of these expresses that `+` is associative?

```
(A)   a + b = b + a                  (B)   a * (b + c) = ab + ac
      commutativity                        distributivity

(C)  a + (b + c) = (a + b) + c       (D)   a + 0 = a
     associativity                         0 is the additive identity
     ^^^^^^^^^^^^^^^^^^^^^^^^^
```

---

# Practically

```
a + (b + c) = (a + b) + c
```

We can be lazy and drop brackets

ie. can write it as `a + b + c` without ambiguity

---

# Back to combine

It must be associative:

```scala
combine(a, combine(b, c)) == combine(combine(a, b), c)
```

---

# Why are they nice?

Why care about these properties...

---

# Example

`foldLeft` vs `foldRight`

---

# foldLeft vs foldRight

Add the numbers 1 to 4 from the left and right

---

# foldLeft vs foldRight

Add the numbers 1 to 4 from the left and right

## foldLeft

```scala
List(1, 2, 3, 4).foldLeft(0)((acc, next) => acc + next)
```

```
    0
    0 + 1
   (0 + 1) + 2
  ((0 + 1) + 2) + 3
 (((0 + 1) + 2) + 3) + 4
```

Left clustered brackets

---

# foldLeft vs foldRight

Add the numbers 1 to 4 from the left and right

## foldRight

```scala
List(1, 2, 3, 4).foldRight(0)((next, acc) => next + acc)
```

```
                   0
               4 + 0
          3 + (4 + 0)
     2 + (3 + (4 + 0))
1 + (2 + (3 + (4 + 0)))
```

Right clustered brackets

---

# Comparing

## foldLeft

```
    0
    0 + 1
   (0 + 1) + 2
  ((0 + 1) + 2) + 3
 (((0 + 1) + 2) + 3) + 4
```

## foldRight

```
                   0
               4 + 0
          3 + (4 + 0)
     2 + (3 + (4 + 0))
1 + (2 + (3 + (4 + 0)))
```

Different shapes

---

# Final computations

foldLeft

```
 (((0 + 1) + 2) + 3) + 4
```

foldRight

```
1 + (2 + (3 + (4 + 0)))
```

Very different computations

Are they the same?

---

# Final computations

foldLeft

```
 (((0 + 1) + 2) + 3) + 4
```

foldRight

```
1 + (2 + (3 + (4 + 0)))
```

> Are they the same?

Pretend you know nothing about integers and addition,

all you have is the monoid properties

---

# Apply associativity law

foldLeft

```
 (((0 + 1) + 2) + 3) + 4

    0 + 1  + 2  + 3  + 4        remove unnecessary brackets
```

foldRight

```
1 + (2 + (3 + (4 + 0)))

1 +  2 +  3 +  4 + 0            remove unnecessary brackets
```

Not quite there yet...

---

# Apply identity law

```
 (((0 + 1) + 2) + 3) + 4

    0 + 1  + 2  + 3  + 4        remove unnecessary brackets

        1  + 2  + 3  + 4        0 + 1 = 1
```

foldRight

```
1 + (2 + (3 + (4 + 0)))

1 +  2 +  3 +  4 + 0            remove unnecessary brackets

1 +  2 +  3 +  4                4 + 0 = 4
```

They're the same! (ignoring whitespace)

---

# Summary

## What we showed:

foldLeft and foldRight produce the same things

## What we needed to show it:

Just two laws:

- combining with identity does nothing


- combining is associative

We didn't use anything else specific to `Int` addition

---

# So

> We didn't use anything else specific to `Int` addition

`foldLeft` and `foldRight` will give you the same value for any monoid

Implementers are free to pick a direction which gives the best performance

---

# Why are they nice?

Example: parallelism

---

# Parallelised fold

You have 10 threads and you want to sum 500,000 numbers

---

# High level strategy

> You have 10 threads and you want to sum 500,000 numbers

Break into 10 sections of 50K each and give each to a worker

Each worker produces a result

Parent combines the 10 results into a final result

---

# Correct?

This is a very different computation to a traditional fold

How do you know it's correct?

---

# Bracketing structure

The original computation as a foldLeft

```
(((((((((((((((.....(0 + 1) + 2) + 3) + 4) + .... + 500,000)
```

Breaking into chunks:

```
(.......) + (.......) + ....
   50K         50K
```

The bracket structures are different, but that's fine because its associative

(just don't change the order)

---

# Seed

Each worker can bootstrap its fold with the identity element

(helpful if there is an empty chunk)

```
(0 + .......) + (0 + .......) + ....
     50K             50K
```

This is fine as it doesn't affect the overall computation

---

# Correct!

It will be a different computation to the original,

but will still be correct

---

# Generalising

We used integer addition for these examples,

but we only exploited that addition is associative and has an identity element

---

# What this means?

> We used integer addition for these examples,
>
> but we only exploited that addition is associative and has an identity element

If your monoid follows those laws,

someone implementing a fold has the freedom to:

- fold from the left **or** right

(right folding might be more performant)

- parallelise

ie. you give implementers more potential optimisations

---

```
    _                _   _
   / \   _ __   ___ | |_| |__   ___ _ __
  / _ \ | '_ \ / _ \| __| '_ \ / _ \ '__|
 / ___ \| | | | (_) | |_| | | |  __/ |
/_/   \_\_| |_|\___/ \__|_| |_|\___|_|

                                _
  _____  ____ _ _ __ ___  _ __ | | ___
 / _ \ \/ / _` | '_ ` _ \| '_ \| |/ _ \
|  __/>  < (_| | | | | | | |_) | |  __/
 \___/_/\_\__,_|_| |_| |_| .__/|_|\___|
                         |_|
```

---

# Stats example

```scala
// How many matches for a particular client and doc type for some period
case class Stats(fullMatches: Int, partialMatches: Int, noMatches: Int)
```

(totally hypothetical)

---

# Combining them

For customer Boban, we have stats objects for Monday and Tuesday:

```scala
val mondayStats  = Stats(fullMatches = 3, partialMatches = 4, noMatches = 2)
val tuesdayStats = Stats(fullMatches = 4, partialMatches = 3, noMatches = 4)
```

Want to aggregate them into a single object covering both days:

```scala
val combinedStats = Stats(
  fullMatches = mondayStats.fullMatches + tuesdayStats.fullMatches,
  partialMatches = mondayStats.partialMatches + tuesdayStats.partialMatches,
  noMatches = mondayStats.noMatches + tuesdayStats.noMatches
)
```

---

# Missing stats

Sometimes we need to aggregate stats across multiple days,

but we're missing entries for all those days

---

# Missing stats

> Sometimes we need to aggregate stats across multiple days,
>
> but we're missing entries for all those days

Like folding an empty sequence...

---

# Default value

> Like folding an empty sequence...

We need a sensible default value sometimes

How about a stats object with 0's for all fields...

```scala
val defaultStats = Stats(fullMatches = 0, partialMatches = 0, noMatches = 0)
```

---

# Deja vu...

We've come up with a sensible:

- combine logic


- default value

What have we realised about `Stats`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Deja vu...

We've come up with a sensible:

- combine logic


- default value

> What have we realised about `Stats`?

It's a monoid! ie. it's combinable

Let's codify that... To the editor!

---

# Summary

```scala
object StatsAddition extends Monoid[Stats] {
  val identity: Stats = Stats(fullMatches = 0, partialMatches = 0, noMatches = 0)

  def combine(left: Stats, right: Stats): Stats = Stats(
    fullMatches = left.fullMatches + right.fullMatches,
    partialMatches = left.partialMatches + right.partialMatches,
    noMatches = left.noMatches + right.noMatches
  )
}

val mondayStats  = Stats(fullMatches = 3, partialMatches = 4, noMatches = 2)
val tuesdayStats = Stats(fullMatches = 4, partialMatches = 3, noMatches = 4)

val bothDays = StatsAddition.combine(mondayStats, tuesdayStats)
```

---

# That's enough for today...

---

# Homework

"Prove" that sequences are monoids under concatenation

---

# Hints

Concatenating two `Seq[A]`'s gives you another `Seq[A]`

Concatenating an empty sequence does nothing

---

# Hints

"Proving" something is a monoid means creating a monoid instance

```scala
implicit class SeqConcatenation[A] extends Monoid[Seq[A]] {
  ...
}
```

You're "proving" it to the compiler in that will let you use it with `|+|`

---

# Solution

```scala
class SeqConcatenation[A] extends Monoid[Seq[A]] {
  val identity: Seq[A] = Seq.empty[A]
  def combine(left: Seq[A], right: Seq[A]): Seq[A] = left ++ right
}
```

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

# Monoid

An abstraction that represents being able to combine things

```scala
trait Monoid[A] {
  val identity: A

  def combine(left: A, right: A): A
}
```

---

# Examples

Integers are combine-able under addition (with identity 0)

Integers are combine-able under multiplication (with identity 1)

Strings are combine-able under concatenation (with identity "")

Stats objects are combine-able (see slides)

Sequences are combine-able (see homework)

---

# Laws

A monoid should have:

- a sensible identity


- an associative combine

---

# Next time

More monoids

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
