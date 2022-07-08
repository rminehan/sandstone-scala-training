---
author: Rohan
date: 2022-07-26
title: Maths and Computing
---

```
 __  __       _   _
|  \/  | __ _| |_| |__  ___
| |\/| |/ _` | __| '_ \/ __|
| |  | | (_| | |_| | | \__ \
|_|  |_|\__,_|\__|_| |_|___/

                 _
  __ _ _ __   __| |
 / _` | '_ \ / _` |
| (_| | | | | (_| |
 \__,_|_| |_|\__,_|

  ____                            _   _
 / ___|___  _ __ ___  _ __  _   _| |_(_)_ __   __ _
| |   / _ \| '_ ` _ \| '_ \| | | | __| | '_ \ / _` |
| |__| (_) | | | | | | |_) | |_| | |_| | | | | (_| |
 \____\___/|_| |_| |_| .__/ \__,_|\__|_|_| |_|\__, |
                     |_|                      |___/
```

---

# What's today about?

Philosophical detour

Explore the relationship between maths and computing

---

# Relevance

Deepens our understanding of FP

Helps us understand what functions and types really are

Gives more context to strong types (coming up)

---

# Defining terms

"Maths" - theoretical maths/logic/category theory concepts

"Computing" - software engineering on the JVM

---

# Analogy of two cities

Maths: heavenly city

Computing: earthly city - an approximation

---

# Approximations

Many terms have different meanings depending on context:

```
 --------------------------------------------------
|           |                  |                   |
|  Concept  |  Maths context   | Computing context |
|           |                  |                   |
 --------------------------------------------------
| Integer   |                  |                   |
 --------------------------------------------------
| Recursion |                  |                   |
 --------------------------------------------------
| Function  |                  |                   |
 --------------------------------------------------
| sqrt      |                  |                   |
 --------------------------------------------------
```

We'll explore this by filling out this table

---

```
    _                              _                 _   _
   / \   _ __  _ __  _ __ _____  _(_)_ __ ___   __ _| |_(_) ___  _ __  ___
  / _ \ | '_ \| '_ \| '__/ _ \ \/ / | '_ ` _ \ / _` | __| |/ _ \| '_ \/ __|
 / ___ \| |_) | |_) | | | (_) >  <| | | | | | | (_| | |_| | (_) | | | \__ \
/_/   \_\ .__/| .__/|_|  \___/_/\_\_|_| |_| |_|\__,_|\__|_|\___/|_| |_|___/
        |_|   |_|
```

Computation borrows heavily from maths,

but many ideas are approximations

---

# Integers

---

# Mathematics

An infinite collection of numbers:

```
<---  -3, -2, -1, 0, +1, +2, +3  --->
```

(unbounded)

---

# Computing

A _finite_ collection of numbers:

```
-2^31, ..., -3, -2, -1, 0, +1, +2, +3, ..., 2^31-1
```

4 bytes

Overflows

---

# Approximation

Mathematical int's are unbounded

JVM int's are bounded and overflow

Most of the time that's fine

---

# Related approximations

`Float` and `Double` are bounded approximations for "real" numbers

---

# sqrt

---

# Clarification

Talking about sqrt of positive real numbers

(no imaginary business - keeping things real)

---

# Mathematics

Function that takes a positive real number and returns a positive real number

```scala
sqrt: R+ => R+

      9  => 3
```

(not defined for negative inputs)

---

# Standard library

A function that takes a double and returns a double

```scala
sqrt: Double => Double
```

(allows negative inputs)

---

# Comparing them

```scala
// Maths
sqrt: R+ => R+

// Standard library
sqrt: Double => Double
```

Standard library method allows in negative values

---

# Recursion

---

# Mathematics

Common to define concepts recursively:

```
fac: N => N
     0 => 1
     n => n * fac(n - 1), if n > 0
```

(N = "naturals" = 0, 1, 2, ...)

---

# Translating to a computation

```
fac: N => N
     0 => 1
     n => n * fac(n - 1), if n > 0
```

as:

```scala
def fac(n: BigInt): BigInt = n match {
  case BigInt(0) => BigInt(1)
  case _ => n * fac(n - 1)
}
```

What's the problem here?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Translating to a computation

```
fac: N => N
     0 => 1
     n => n * fac(n - 1), if n > 0
```

as:

```scala
def fac(n: BigInt): BigInt = n match {
  case BigInt(0) => BigInt(1)
  case _ => n * fac(n - 1)
}
```

> What's the problem here?

Not stack safe

(Also it accepts negative inputs)

---

# Comparing them

## Mathematical version

Works for all natural inputs

## Computational version

Works up to about 30,000

Throws exceptions beyond that

(And also accepts negative inputs)

---

# Reality

Many recursive concepts don't translate well to software

The JVM has practical limitations about the number of stack frames it can use

---

# Functions

(the concept itself)

---

# Working example

```scala
def compute(i: Int): Int = i * i * 2
```

---

# Computing

```scala
def compute(i: Int): Int = i * i * 2
```

Like a black box

```
             -----------------
            |                 |
 Int   ---> |     compute     | ---> Int
  3         |                 |       18
             -----------------
```

- stack frame created


- CPU used to produce a value


- takes time

---

# Mathematics

No concept of "computation"

More like a mapping from inputs to outputs

---

# Our example

```scala
def compute(i: Int): Int = i * i * 2
```

```
Domain           Codomain
Integers         Integers
-------------------------
 0       ------>  0
 1       ------>  2
-1       ------>  2
 2       ------>  8
-2       ------>  8
...
```

Not a computation, a mapping

---

# Computing analogy

> Not a computation, a mapping

Analogous to:

```scala
private val computeMap = Map(
   0 -> 0,
   1 -> 2,
  -1 -> 2,
   2 -> 8,
  -2 -> 8,
  ... // infinite
)

def compute(i: Int): Int = computeMap(i) // no multiplication performed
```

Compare with computational version:

```scala
def compute(i: Int): Int = i * i * 2
```

---

# Association

Mathematical functions are just associations/pairings between domain and codomain

```
 (0, 0)
 (1, 1)
 (2, 8)
 ...
```

Like drawing an arrow from each input to an output

---

# Functions as computations

```
             -----------------
            |                 |
 Int   ---> |     compute     | ---> Int
  3         |                 |       18
             -----------------
```

Introduces all of these concepts:

- side effects


- exceptions


- shutdown the JVM (`System.exit(0)`)


- deadlock


- infinite loops


- non-deterministic


- latency (time)

---

# Functions as mappings

An association between values from domain and codomain

```
 (0, 0)
 (1, 1)
 (2, 8)
 ...
```

All associations known when the function is defined

No concept of time or computation

---

# Functions as mappings

```
 (0, 0)
 (1, 1)
 (2, 8)
 ...
```

> No concept of time or computation

## Concepts that don't make sense

- side effects


- incompleteness


- non-determinism

---

# Concepts that don't make sense

> side effects
>
> incompleteness
>
> non-determinism

ie. mathematical functions are

- free of side effects


- complete


- deterministic

This is the Holy Trinity of purity!

---

# Maths and purity

What are we really doing when we make computations pure?

---

# Maths and purity

> What are we really doing when we make computations pure?

We're making them like mathematical functions

---

# Not found in the heavenly city

- throwing exceptions


- infinite loops


- deadlocks


- shutting down the JVM


- latency

---

# Maths and purity

The closest you can get to a mathematical function is a pre-memoized map

---

# Maths and purity

The closest you can get to a mathematical function is a pre-memoized map

But that's not practical

---

# Maths and purity

The closest you can get to a mathematical function is a pre-memoized map

But that's not practical

The closest you can get practically is a pure computation,

(with good modelling of types)

---

# Recapping that

Mathematicians and Software Engineers mean different things when they say "function"

(causes conflation and fuzzy thinking)

---

# Recapping that

Mathematicians and Software Engineers mean different things when they say "function"

Mathematician: a mapping

Software Engineer: computation that might produce a value

---

# Wild computations

- side effects


- exceptions


- non-deterministic


- infinite loops


- latency (time)

---

# Holy Trinity of purity

Pure functions and mathematical functions both respect this

Practically pure functions are the closest you can get to a mathematical function

---

```
__        ___
\ \      / / |__  _   _
 \ \ /\ / /| '_ \| | | |
  \ V  V / | | | | |_| |
   \_/\_/  |_| |_|\__, |
                  |___/
                                 _                 _      ___
  __ _ _ __  _ __  _ __ _____  _(_)_ __ ___   __ _| |_ __|__ \
 / _` | '_ \| '_ \| '__/ _ \ \/ / | '_ ` _ \ / _` | __/ _ \/ /
| (_| | |_) | |_) | | | (_) >  <| | | | | | | (_| | ||  __/_|
 \__,_| .__/| .__/|_|  \___/_/\_\_|_| |_| |_|\__,_|\__\___(_)
      |_|   |_|
```

Why do mathematical concepts end up as approximations?

---

# Recap

```
 --------------------------------------------------
|           |                  |                   |
|  Concept  |  Maths context   | Computing context |
|           |                  |                   |
 --------------------------------------------------
| Integer   |   unbounded      |   bounded         |
 --------------------------------------------------
| sqrt      |  positive inputs | pos and neg inputs|
 --------------------------------------------------
| Recursion |   works          |   SO/incomplete   |
 --------------------------------------------------
| Function  |   pairing        |  wild computation |
 --------------------------------------------------
```

Concepts differ

---

# Why don't they translate perfectly?

---

# Why don't they translate perfectly?

Pragmatic tradeoffs

- performance


- finite constraints


- convenience/laziness

---

# Integers

Replace with `BigInt`?

---

# Integers

Having them bound to 4 bytes makes them fast and simple

JVM has special byte codes for integers

---

# Sqrt

```scala
// Maths
sqrt: R+ => R+

// Computation
sqrt: Double => Double
```

The jdk doesn't have "positive" types (e.g. `UnsignedDouble`)

---

# Convenience

```scala
// Maths
sqrt: R+ => R+

// Computation
sqrt: Double => Double
```

> The jdk doesn't have "positive" types (e.g. `UnsignedDouble`)

So they use a "convenient" type like `Double`

(which widens the input)

---

# Recursion

Stack frames use memory

The JVM can't let you have unlimited stack frames

Memory is finite

---

# Functions

A mathematical function is like a pre-memoized hashmap

---

# Functions

> A mathematical function is like a pre-memoized hashmap

But if the domain is massive (e.g. all Strings),

your hashmap will run out of memory

Memory is finite

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

# Two cities

Mathematics: like a heavenly city

Software: earthly city, a dim reflection

---

# New perspective

FP

A way of bringing some of the heavenly city back

- pure functions


- strong types


- tail recursion


- laziness

---

# My hope

Gives you a new perspective

This philosophical perspective gives you a mental framework and vocabulary

Increases clarity about what functions and types really are

Remove the fuzziness

---

# Up next

Strong types

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
