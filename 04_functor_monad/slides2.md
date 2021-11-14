---
author: Rohan
date: 2021-11-12
title: for comprehensions
---

```
  __
 / _| ___  _ __
| |_ / _ \| '__|
|  _| (_) | |
|_|  \___/|_|

                                    _                    _
  ___ ___  _ __ ___  _ __  _ __ ___| |__   ___ _ __  ___(_) ___  _ __  ___
 / __/ _ \| '_ ` _ \| '_ \| '__/ _ \ '_ \ / _ \ '_ \/ __| |/ _ \| '_ \/ __|
| (_| (_) | | | | | | |_) | | |  __/ | | |  __/ | | \__ \ | (_) | | | \__ \
 \___\___/|_| |_| |_| .__/|_|  \___|_| |_|\___|_| |_|___/_|\___/|_| |_|___/
                    |_|
```

---

# Last time on scala training...

---

# Functor

Something with a "map" concept

in a way that preserves structure/shape

---

# Monad

Something with a "flatMap" and "pure" concept

Prevents deep nesting

---

# Functor vs Monad

```
                         |  |1 1|  |2 2|  |
                         |  |1 1|  |2 2|  |
            map f        |                |
            ---->        |  |3 3|  |4 4|  |
                         |  |3 3|  |4 4|  |
                         |                |
                         |  |5 5|  |6 6|  |
|  1  2  |               |  |5 5|  |6 6|  |
|  3  4  |
|  5  6  |


                         | 1 1 2 2 |
                         | 1 1 2 2 |
                         | 3 3 4 4 |
          flatMap f      | 3 3 4 4 |
           ---->         | 5 5 6 6 |
                         | 5 5 6 6 |


where

             |  a  a  |
 f: a  --->  |  a  a  |    Int => Matrix[Int]
```

---

# Today

Understand how these concepts have first class support in the for comprehension

---

# Semantics

Using the right terminology will help reinforce this

## java

for _loop_

```java
for (int i = 0; i < 10; i++) {
  println(i);
}
```

## scala

for _comprehension_

```scala
for (i <- 0 until 10) {
  println(i)
}
```

## Why care?

They are so different under the hood

---

# Scala

`for` comprehensions are powered by `map` and `flatMap`

`for` is "syntactic sugar"

---

# Example

We want to capture this 2D structure of coords:

```
(0,0)  (1,0)  (2,0)  (3,0)
(0,1)  (1,1)  (2,1)  (3,1)
(0,2)  (1,2)  (2,2)  (3,2)
```

into a flattened form going down the columns:

```
(0,0)
(0,1)
(0,2)
(1,0)
(1,1)
(1,2)
...
(3,2)
```

---

# Java brain

2D structure will make you think: double nested for loop

```
          c ---->
 r  (0,0)  (1,0)  (2,0)  (3,0)
 |  (0,1)  (1,1)  (2,1)  (3,1)
\|/ (0,2)  (1,2)  (2,2)  (3,2)

     \/

    (0,0)
    (0,1)
    (0,2)
    (1,0)
    (1,1)
    (1,2)
    ...
    (3,2)
```

```java
// Made up code
Buffer<Pair<Int, Int>> buffer = new Buffer<Pair<Int, Int>>();

for (int c = 0; c < 4; c++) {
  for (int r = 0; r < 3; r++) {
    buffer.append((c, r));
  }
}
```

---

# Converting to scala

```
          c ---->
 r  (0,0)  (1,0)  (2,0)  (3,0)
 |  (0,1)  (1,1)  (2,1)  (3,1)
\|/ (0,2)  (1,2)  (2,2)  (3,2)

     \/

    (0,0)
    (0,1)
    (0,2)
    (1,0)
    (1,1)
    (1,2)
    ...
    (3,2)
```

Scala:

```scala
for {
  c <- 0 until 4
  r <- 0 until 3
} yield (c, r)
```

Java:

```java
for (int c = 0; c < 4; c++) {
  for (int r = 0; r < 3; r++) {
    buffer.append((c, r));
  }
}
```

Looks the same?

---

# Under the hood

What's really going on here?

```scala
for {
  c <- 0 until 4
  r <- 0 until 3
} yield (c, r)
```

Let's "desugar" it

To intellij!

---

# Results

```scala
for {
  c <- 0 until 4
  r <- 0 until 3
} yield (c, r)

// becomes

(0 until 4).flatMap(c =>
  (0 until 3).map(r => (c, r))
)
```

---

# Understanding it (if we used map)

```scala
(0 until 4).map(c =>
  (0 until 3).map(r => (c, r))
)
```

```
|   0    |    1    |    2    |    3    |   List[...]

                  map(...)

| (0,0)  |  (1,0)  |  (2,0)  |  (3,0)  |
| (0,1)  |  (1,1)  |  (2,1)  |  (3,1)  |   List[List[...]]
| (0,2)  |  (1,2)  |  (2,2)  |  (3,2)  |
```

To the repl!

---

# Understanding it (change to `flatMap`)

```scala
(0 until 4).flatMap(c =>
  (0 until 3).map(r => (c, r))
)
```

```
|   0    |    1    |    2    |    3    |   List[...]

                  flatMap(...)

| (0,0)  |  (1,0)  |  (2,0)  |  (3,0)  |
| (0,1)  |  (1,1)  |  (2,1)  |  (3,1)  |
| (0,2)  |  (1,2)  |  (2,2)  |  (3,2)  |


              |         |         |
  (0,0)       |         |         |       List[...]
  (0,1)       |         |         |
  (0,2)       |         |         |
  (1,0) <-----          |         |
  (1,1)                 |         |
  (1,2)                 |         |
  (2,0) <---------------          |
  (2,1)                           |
  (2,2)                           |
  (3,0) <-------------------------
  (3,1)
  (3,2)
```

To the repl!

---

# If you got lost in all that mapping...

Don't worry

The point of for comprehensions is to hide those details and be more intuitive

```scala
for {
  c <- 0 until 4
  r <- 0 until 3
} yield (c, r)
```

---

# Recap

Code like this:

```scala
for {
  c <- 0 until 4
  r <- 0 until 3
} yield (c, r)
```

is really powered by `flatMap` and `map`

It's not a traditional "loop"

---

# Why is this cool?

You can use for comprehensions on any functor/monad type

It generalises beyond loops

---

# Option example

Option is not a "loopy" type, but it has `map` and `flatMap`

We'll use it with `for`

---

# Option example

```scala
case class Person(..., nextOfKinOpt: Option[Person], addressOpt: Option[Address])

case class Address(..., streetTypeOpt: Option[StreetType])
```

---

# Option example

```scala
case class Person(..., nextOfKinOpt: Option[Person], addressOpt: Option[Address])

case class Address(..., streetTypeOpt: Option[StreetType])
```

Find the first letter of the street type of the next of kin

```scala
def nextOfKinStreetType(person: Person): Option[Char] = ???
```

---

# Traditional approach

```scala
case class Person(..., nextOfKinOpt: Option[Person], addressOpt: Option[Address])

case class Address(..., streetTypeOpt: Option[StreetType])
```

> Find the street type of the next of kin

```scala
def nextOfKinStreetType(person: Person): Option[Char] = {
  person.nextOfKinOpt match {
    case None => None
    case Some(nextOfKin) => nextOfKin.addressOpt match {
      case None => None
      case Some(address) => address.streetTypeOpt match {
        case None => None
        case Some(streetType) => Some(streetType.asString.head)
      }
    }
  }
}
```

---

# flatMap approach

```scala
case class Person(..., nextOfKinOpt: Option[Person], addressOpt: Option[Address])

case class Address(..., streetTypeOpt: Option[StreetType])
```

> Find the street type of the next of kin

```scala
def nextOfKinStreetType(person: Person): Option[StreetType] = {
  person.nextOfKinOpt.flatMap { nextOfKin =>
    nextOfKin.addressOpt.flatMap { address =>
      address.streetTypeOpt.map { streetType =>
        streetType.asString.head
      }
    }
  }

  // Previous version
  person.nextOfKinOpt match {
    case None => None
    case Some(nextOfKin) => nextOfKin.addressOpt match {
      case None => None
      case Some(address) => address.streetTypeOpt match {
        case None => None
        case Some(streetType) => Some(streetType.asString.head)
      }
    }
  }
}
```

---

# for approach

```scala
case class Person(..., nextOfKinOpt: Option[Person], addressOpt: Option[Address])

case class Address(..., streetTypeOpt: Option[StreetType])
```

> Find the street type of the next of kin

```scala
def nextOfKinStreetType(person: Person): Option[StreetType] = {
  for {
    nextOfKin <- person.nextOfKinOpt
    address <- nextOfKin.addressOpt
    streetType <- address.streetTypeOpt
  } yield streetType.asString.head


  // Previous version
  person.nextOfKinOpt.flatMap { nextOfKin =>
    nextOfKin.addressOpt.flatMap { address =>
      address.streetTypeOpt.map { streetType =>
        streetType.asString.head
      }
    }
  }
}
```

---

# Summary of Option

We used a `for` with `Option` to transform short-circuiting `flatMap/map` logic

---

# Future

Really useful here too

(For another day)

---

# flatMap vs map+flatten

```scala
list.flatMap(i => List(i - 1, i + 1))

list.map(i => List(i - 1, i + 1)).flatten
```

---

# Pro's of flatMap form

```scala
list.flatMap(i => List(i - 1, i + 1))

list.map(i => List(i - 1, i + 1)).flatten
```

## For

Can use with a `for`

`flatten` doesn't have special support

```scala
for {
  i <- list
  j <- List(i - 1, i + 1)
} yield j
```

## Performance

Avoids an intermediate collection and an extra pass over your data

---

# Summary so far

If your type has `map` and `flatMap` methods,

you can use the `for` comprehension syntax

---

# Filtering

If your type has a `filter` method,

that also integrates into `for` as an `if` statement

---

# Example

Generate pairs like before

```scala
// Old code
for {
  c <- 0 to 3
  r <- 0 to 2
} yield (c, r)
```

but ...

---

# Example

Generate pairs like before

```scala
// Old code
for {
  c <- 0 to 3
  r <- 0 to 2
} yield (c, r)
```

but exclude the "diagonal" (where c == r)

```
-----   (1,0)   (2,0)   (3,0)
(0,1)   -----   (2,1)   (3,1)
(0,2)   (1,2)   -----   (3,2)
```

To the repl!

---

# Solution

```diff
 for {
   c <- 0 to 3
   r <- 0 to 2
+  if c != r
 } yield (c, r)
```

```
-----   (1,0)   (2,0)   (3,0)
(0,1)   -----   (2,1)   (3,1)
(0,2)   (1,2)   -----   (3,2)
```

---

# Under the hood?

```scala
 for {
   c <- 0 to 3
   r <- 0 to 2
   if c != r
 } yield (c, r)
```

Desugar it in intellij

---

# For comprehensions are expressions

To the repl

---

# More complex examples

Generate tuples (i, j, i+j) where:

- i is from 0 to 10

- j is from 0 to 10

- i + j is less than 10

(order doesn't matter)

To the repl!

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

# loop vs comprehension

Very different under the hood

---

# Universal

It's not limited to iteration

Syntactic sugar for `map`, `flatMap` and `filter`

---

# Universal

Lets us use it with common types like:

- sequence types


- `Option`


- `Either`


- `Future`

---

# Readability

`for` can often make your logic and structure much more readable

(Don't overuse them though)

---

# Desugaring

Can use intellij or command line tools

---

# Next time

We'll put this to use with `Future`

---

```
  ___                  _   _                 
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___ 
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \ ?
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
                                             
```
