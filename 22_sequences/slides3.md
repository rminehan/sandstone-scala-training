---
author: Rohan
date: 2023-05-22
title: Sequences and Functions
---

```
 ____
/ ___|  ___  __ _ _   _  ___ _ __   ___ ___  ___
\___ \ / _ \/ _` | | | |/ _ \ '_ \ / __/ _ \/ __|
 ___) |  __/ (_| | |_| |  __/ | | | (_|  __/\__ \
|____/ \___|\__, |\__,_|\___|_| |_|\___\___||___/
               |_|
       ___
      ( _ )
      / _ \/\
     | (_>  <
      \___/\/

 _____                 _   _
|  ___|   _ _ __   ___| |_(_) ___  _ __  ___
| |_ | | | | '_ \ / __| __| |/ _ \| '_ \/ __|
|  _|| |_| | | | | (__| |_| | (_) | | | \__ \
|_|   \__,_|_| |_|\___|\__|_|\___/|_| |_|___/
```

---

# Today

Going to explore the relationship between sequences and functions

---

# New perspective

You probably think of sequences as little chunks of data in memory

---

# New perspective

You probably think of sequences as little chunks of data in memory

Today we'll view them from the perspective of "partial functions"

---

# Agenda

- partial functions


- sequences as partial functions


- lifting

---

```
 ____            _   _       _
|  _ \ __ _ _ __| |_(_) __ _| |
| |_) / _` | '__| __| |/ _` | |
|  __/ (_| | |  | |_| | (_| | |
|_|   \__,_|_|   \__|_|\__,_|_|

 _____                 _   _
|  ___|   _ _ __   ___| |_(_) ___  _ __  ___
| |_ | | | | '_ \ / __| __| |/ _ \| '_ \/ __|
|  _|| |_| | | | | (__| |_| | (_) | | | \__ \
|_|   \__,_|_| |_|\___|\__|_|\___/|_| |_|___/

```

---

# Syncing up

What do you guys know about partial functions?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Regular Functions

```scala
def foo(s: String): Int = ...
```

```
Mathematically:

     Domain    Codomain
foo: String => Int
```

---

# Regular Functions

```scala
def foo(s: String): Int = ...
```

```
Mathematically:

     Domain    Codomain
foo: String => Int
```

To be a true function in the mathematical sense, `foo` must be complete,

ie. gracefully return a value on every input in the domain

---

# Completeness recap

What are common things that make functions incomplete?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Completeness recap

> What are common things that make functions incomplete?

- throwing exceptions/errors


- infinite loops


- stopping the JVM (e.g. `System.exit`)


- returning meaningless values like `NaN` or `null` (depending on your philosophical view point)

---

# Example

```scala
def invert(f: Float): Float = 1f / f
```

Is this complete?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Example

```scala
def invert(f: Float): Float = 1f / f
```

> Is this complete?

Not really because it's not well defined at `0f`

---

# Partial Functions

Conceptually partial functions don't have to be defined for all domain values

```
      Domain                        Codomain

       A         -------------->      f(A)

       B  (not defined)

       C         -------------->      f(C)

       D         -------------->      f(D)

       E  (not defined)

       F         -------------->      f(F)
```

---

# Encoding this

```scala
trait PartialFunction[A, B] {

  def isDefinedAt(a: A): Boolean

  // Trust callers to only put in values here
  // that return true for `isDefinedAt`
  def apply(a: A): B
}
```

---

# Example

Turn our invert example into a partial function

To the repl!

---

# Summary

```scala
val invert = new PartialFunction[Float, Float] {
  def isDefinedAt(f: Float): Boolean = f != 0f

  def apply(f: Float): Float = 1f / f
}
```

It's up to the user of the pf to ensure it's defined at a value before applying it

---

# collect

Let's see how it relates to pf's

To the repl!

---

# Summary

```scala
val seq = Seq(2f, 1f, 0f, 3f, 1.5f, 0f)

// These give the same answer:

seq.collect(new PartialFunction[Float, Float] {
  def isDefinedAt(f: Float): Boolean = f != 0f
  def apply(f: Float): Float = 1f / f
}

seq.collect {
  case f if f != 0f => 1f / f
}
```

---

# Pattern matches and Partial Functions

To the repl!

---

# Recap

```scala
val invert: PartialFunction[Float, Float] = {
  case f if f != 0f => 1f / f
}

invert.isDefinedAt(0f) // false

invert.isDefinedAt(1f) // true

invert(2f) // 0.5f
```

Incomplete pattern matches _are_ partial functions

The guard is translated to `isDefinedAt`

The code after `=>` is translated to `apply`

---

# Summary of partial functions

- a formal way to capture that a function is not defined on all inputs


- first class language support - pattern matches translate to partial functions


- great with `collect`

---

```
 ____
/ ___|  ___  __ _ _   _  ___ _ __   ___ ___  ___
\___ \ / _ \/ _` | | | |/ _ \ '_ \ / __/ _ \/ __|
 ___) |  __/ (_| | |_| |  __/ | | | (_|  __/\__ \
|____/ \___|\__, |\__,_|\___|_| |_|\___\___||___/
               |_|
```

---

# Recap

Conceptually, what is the essence of a sequence?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Recap

> Conceptually, what is the essence of a sequence?

Deterministic ordering of values

---

# Mapping

```scala
val greetings = Seq("yo", "bro", "hi")
```

```
greetings:

  0       ---------->      "yo"

  1       ---------->      "bro"

  2       ---------->      "hi"
```

---

# Let's make a custom implementation

To the repl!

---

# Summary

```scala
val greetings = Seq("yo", "bro", "hi")

val greetings: Seq[String] = new Seq[String] {
  def apply(i: Int): String = i match {
    case 0 => "yo"
    case 1 => "bro"
    case 2 => "hi"
  }

  val length: Int = 3

  def iterator: Iterator[String] = ...
}
```

From the outside, these are equivalent (just different implementations)

First implementation uses a `List` in memory

Second implementation doesn't use a data structure, it's essentially a function

---

# Function?

Agree or disagree:

> A `Seq[A]` is conceptually a deterministic function: `Int => A`

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Function?

Agree or disagree:

> A `Seq[A]` is conceptually a deterministic function: `Int => A`

_Mostly_ agree, "partial" function would be a better description:

(only defined outside from 0 to length-1)

---

# Example

```scala
val greetings = Seq("yo", "bro", "hi")
```

```
greetings:

 Int                      String

 ...

 -2

 -1

  0       ---------->      "yo"

  1       ---------->      "bro"

  2       ---------->      "hi"

  3

  4

  ...
```

> A `Seq[A]` is conceptually a deterministic function: `Int => A`

Not defined for values below 0 or above 2

---

# Example

Reimplement our example with a pf

To the repl!

---

# Recap

```scala
val greetings: Seq[String] = new Seq[String] {
  private val pf: PartialFunction[Int, String] = {
    case 0 => "yo"
    case 1 => "bro"
    case 2 => "hi"
  }

  def apply(i: Int): String =
    if (pf.isDefinedAt(i)) pf(i) // Checks first if the pf is defined on i
    else throw new Exception("Bro, index is out of range")

  ...
}
```

---

# Recall: Snippets from the docs

> Another way to see a sequence is as a PartialFunction from Int values to the element type of the sequence.
>
> The isDefinedAt method of a sequence returns true for the interval from 0 until length.

Cowabunga! This makes complete sense now

---

# Data structures?

How we often think about sequences:

> What data structure is behind this sequence?

---

# Data structures?

How we often think about sequences:

> What data structure is behind this sequence?

Data structures are one family of implementations

---

# Data structures?

How we often think about sequences:

> What data structure is behind this sequence?

Data structures are one family of implementations

But sequences can also be powered by partial functions

```scala
val greetings: Seq[String] = new Seq[String] {
  private val pf: PartialFunction[Int, String] = {
    case 0 => "yo"
    case 1 => "bro"
    case 2 => "hi"
  }

  def apply(i: Int): String =
    if (pf.isDefinedAt(i)) pf(i) // Checks first if the pf is defined on i
    else throw new Exception("Bro, index is out of range")

  ...
}
```

---

# Sequences _are_ partial functions

To the repl!

---

# Recap

```scala
trait Seq[+A] extends ... with PartialFunction[Int, A]

// Compiles
val pf: PartialFunction[Int, Boolean] = Seq(true, true, false)
```

`Seq[A]` _is_ a `PartialFunction[Int, A]` from the type system perspective

---

# Space complexity

What is the space complexity of `greetings`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Space complexity

> What is the space complexity of `greetings`?

O(1)

---

# Recap

- sequences are partial functions


- sequences don't have to be implemented with O(n) space data structures

---

```
 _     _  __ _   _
| |   (_)/ _| |_(_)_ __   __ _
| |   | | |_| __| | '_ \ / _` |
| |___| |  _| |_| | | | | (_| |
|_____|_|_|  \__|_|_| |_|\__, |
                         |___/
```

---

# Recall: invert

```scala
val invert: PartialFunction[Float, Float] = {
  case f if f != 0f => 1f / f
}
```

We trust people to check values with `isDefinedAt` before using `apply`

---

# Trust

Should we trust people to do the right thing?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Compiler error?

```scala
val invert: PartialFunction[Float, Float] = {
  case f if f != 0f => 1f / f
}

// Would the compiler catch that there was no check?
invert(0f)
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Incomplete

The `apply` method of a partial function is incomplete

But nothing stops a developer putting an undefined value into it

---

# invert

```
      Domain                      Codomain
      Float                        Float

        3f        ---------->      0.33f

        2.8f      ---------->      0.357143f

        2f        ---------->      0.5f

        1f        ---------->      1f

(hole)  0f

        -5f       ---------->      0.2f

        ...
```

---

# Move the hole

```
      Domain                      Codomain                      Domain                      Codomain
      Float                        Float                        Float                      Option[Float]

        3f        ---------->      0.33f                          3f        ---------->      Some(0.33f)

        2.8f      ---------->      0.357143f                      2.8f      ---------->      Some(0.357143f)

        2f        ---------->      0.5f                           2f        ---------->      Some(0.5f)

        1f        ---------->      1f                             1f        ---------->      Some(1f)

(hole)  0f                                                        0f        ---------->      None          (hole)

        -5f       ---------->      0.2f                           -5f       ---------->      Some(0.2f)

        ...                                                       ...
```

We've moved the hole to the codomain

---

# Build a lifter

To the repl!

---

# Summary

```scala
def lift[A, B](pf: PartialFunction[A, B]): A => Option[B] = { a =>
  if (pf.isDefinedAt(a)) Some(pf(a))
  else None
}

val liftedInvert = lift(invert)
// liftedInvert: Float => Option[Float]

invert(2f) // 0.5F

liftedInvert(2f) // Some(0.5F)

invert(0f) // throws

liftedInvert(0f) // None
```

---

# Recap

- lifting a partial function makes it complete


- holes are captured by the `Option` and the compiler will force you to deal with them

---

# .lift

You don't have to build your own lift

To the repl!

---

# Lifting sequences

---

# Example

Annoying range checks when parsing

To the repl!

---

# Summary

```scala
case class User(name: String, role: String, hobby1: Option[String], hobby2: Option[String])

// Parse these
val user1 = "Pranali Dev Parties Cricket"
val user2 = "Varun Dev DJ"
val user3 = "Rohan Dev"
val user4 = "James Dev Real-estate Cars Bro-time"

// First attempt with tedious range checking logic
def parse(userText: String): User = {
  val tokens = userText.split("\\s+").filter(_.nonEmpty)

  if (tokens.length < 2) throw new Exception("Name and role required")
  else {
    val name = tokens(0)
    val role = tokens(1)

    if (tokens.length >= 3) {
      val hobby1: Option[String] = Some(tokens(2))
      val hobby2: Option[String] =
        if (tokens.length >= 4) Some(tokens(3))
        else None
      User(name, role, hobby1, hobby2)
    }
    else {
      val hobby1: Option[String] = None
      val hobby2: Option[String] = None
      User(name, role, hobby1, hobby2)
    }
  }
}
```

---

# Summary

```scala
case class User(name: String, role: String, hobby1: Option[String], hobby2: Option[String])

// Parse these
val user1 = "Pranali Dev Parties Cricket"
val user2 = "Varun Dev DJ"
val user3 = "Rohan Dev"
val user4 = "James Dev Real-estate Cars Bro-time"

// Second attempt using lifted
def parse(userText: String): User = {
  val tokensLifted: Int => Option[String] = userText.split("\\s+").filter(_.nonEmpty).lift
//                                                                                    ^^^^
  val name = tokensLifted(0).getOrElse(throw new Exception("Name is missing"))
  val role = tokensLifted(1).getOrElse(throw new Exception("Role is missing"))
  val hobby1 = tokensLifted(2)
  val hobby2 = tokensLifted(3)

  User(name, role, hobby1, hobby2)
}
```

- less nesting


- less chance of subtle range bugs


- easier to read

This comes from using a complete function to drive the logic

---

# Note

Validation logic is short circuiting though...

(That's a tangential topic)

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

# Partial functions

A formal way of describing incomplete functions

```
      Domain                        Codomain

       A         -------------->      f(A)

       B  (not defined)

       C         -------------->      f(C)

       D         -------------->      f(D)

       E  (not defined)

       F         -------------->      f(F)
```

---

# Sequences

Sequences are partial functions

```scala
val greetings = Seq("yo", "bro", "hi")
```

```
greetings:

 Int                      String

 ...

 -2

 -1

  0       ---------->      "yo"

  1       ---------->      "bro"

  2       ---------->      "hi"

  3

  4

  ...
```

---

# Lifting

Transforms a partial function into a complete function

Makes range checking code safer and simpler

---

# Next time

Infinite sequences

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
