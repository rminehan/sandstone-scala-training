---
author: Rohan
date: 2022-01-12
title: Functional Data Structures
---

```
 _____                 _   _                   _
|  ___|   _ _ __   ___| |_(_) ___  _ __   __ _| |
| |_ | | | | '_ \ / __| __| |/ _ \| '_ \ / _` | |
|  _|| |_| | | | | (__| |_| | (_) | | | | (_| | |
|_|   \__,_|_| |_|\___|\__|_|\___/|_| |_|\__,_|_|

 ____        _
|  _ \  __ _| |_ __ _
| | | |/ _` | __/ _` |
| |_| | (_| | || (_| |
|____/ \__,_|\__\__,_|

 ____  _                   _
/ ___|| |_ _ __ _   _  ___| |_ _   _ _ __ ___  ___
\___ \| __| '__| | | |/ __| __| | | | '__/ _ \/ __|
 ___) | |_| |  | |_| | (__| |_| |_| | | |  __/\__ \
|____/ \__|_|   \__,_|\___|\__|\__,_|_|  \___||___/

```

---

# Recap

We learnt about the cons list

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

- cons cell has `head` data and `tail` pointer


- `Nil` acts as the terminus

---

# Today

General focus on performance

---

# Today

- recap of time/space complexity analysis


- discuss immutable vs mutable


- understand List as a functional data structure


- performance characteristics

---

```
 _____ _                   ______
|_   _(_)_ __ ___   ___   / / ___| _ __   __ _  ___ ___
  | | | | '_ ` _ \ / _ \ / /\___ \| '_ \ / _` |/ __/ _ \
  | | | | | | | | |  __// /  ___) | |_) | (_| | (_|  __/
  |_| |_|_| |_| |_|\___/_/  |____/| .__/ \__,_|\___\___|
                                  |_|
  ____                      _           _ _
 / ___|___  _ __ ___  _ __ | | _____  _(_) |_ _   _
| |   / _ \| '_ ` _ \| '_ \| |/ _ \ \/ / | __| | | |
| |__| (_) | | | | | | |_) | |  __/>  <| | |_| |_| |
 \____\___/|_| |_| |_| .__/|_|\___/_/\_\_|\__|\__, |
                     |_|                      |___/
```

aka "Big O"

aka asymptotic analysis

---

# University

Maybe it's been a while since university...

---

# Time and Space

Time - how long something takes

Space - how much memory it takes

---

# Example

```scala
def printReport(customers: Seq[Customer]): Unit = {
  val totalSpent = customers.map(_.totalSpent).sum
  println(s"Customers spent $totalSpent in total")
}
```

If we have `n` customers, roughly what is the time and space complexity?

(Assume that `.totalSpent` is a field, not a method)

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Linear

```scala
def printReport(customers: Seq[Customer]): Unit = {
  val totalSpent = customers.map(_.totalSpent).sum
  println(s"Customers spent $totalSpent in total")
}
```

## Time

2n: loops twice (map, then sum)

O(n)

## Space

`map` creates an intermediate collection size `n`

O(n)

---

# Why we do this analysis

Usually worried about "scale"

- lots of requests


- lots of data

---

# Back to example

Time and space is O(n) or linear (unbounded)

If we had millions of customers this could become an issue

```scala
def printReport(customers: Seq[Customer]): Unit = {
  val totalSpent = customers.map(_.totalSpent).sum
  println(s"Customers spent $totalSpent in total")
}
```

Might need a more scalable approach

---

# Double loop example

```scala
def doSomething(customers: Seq[Customer]): Unit = {
  for {
    customer1 <- customers
    customer2 <- customers
  } ...

  for (customer <- customers) {
    ...
  }
}
```

What is the time complexity here?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Double loop example

```scala
def doSomething(customers: Seq[Customer]): Unit = {
  // n^2
  for {
    customer1 <- customers
    customer2 <- customers
  } yield ...

  // n
  for (customer <- customers) {
    ...
  }
}
```

> What is the time complexity here?

O(n^2) "quadratic"

Effectively looking at every pairing of customers

The second loop is O(n) but it's overshadowed

---

# Major term

We just care about the fastest growing term

It will make the other terms insignificant

---

# Example

> It will make the other terms insignificant

Suppose an algorithm takes time: 2n^2 + 30n

Think about a large n

---

# Example

> It will make the other terms insignificant

Suppose an algorithm takes time: 2n^2 + 30n

When n = 10,000

Quadratic term is 2 * 10,000^2 = 200,000,000

Linear term is 30 * 10,000 = 300,000

---

# Example

> It will make the other terms insignificant

Suppose an algorithm takes time: 2n^2 + 30n

When n = 10,000

Quadratic term is 2 * 10,000^2 = 200,000,000

Linear term is 30 * 10,000 = 300,000

Orders of magnitude different

Constants don't really matter either

---

# Last example

```scala
def sampleCustomers(customers: Seq[Customer]): Seq[Customer] = customers.take(5)
```

What is the time/space complexity?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Last example

```scala
def sampleCustomers(customers: Seq[Customer]): Seq[Customer] = customers.take(5)
```

> What is the time/space complexity?

Probably "constant" or O(1) for both

(making some assumptions about the runtime type of the sequence)

---

# Bounded: O(1)

```scala
def sampleCustomers(customers: Seq[Customer]): Seq[Customer] = customers.take(5)
```

It's "bounded": there is a ceiling it won't cross regardless of how many customers you pass

ie. it's independent of the number of customers

---

# Common shapes

## Nice ones

- O(1) "constant"


- O(logn) "logarithmic"

## Unwieldy ones

- O(n) "linear"


- O(n*logn)


- O(n^2) "quadratic"


- (and all the other polynomials, n^3, n^4, ...)


- O(e^n) "exponential"


- O(n!) "factorial"

---

# What makes them nice

> O(1) "constant"
>
> O(logn) "logarithmic"

Effectively bounded

You can provision a certain amount of resources and know it will always be enough

---

# Objection

> Effectively bounded

But log isn't bounded!

---

# Objection overruled!

> But log isn't bounded!

Not technically, but it grows so slowly that it basically is

---

# Example

Algorithm is logn, where n = #customers

You'll never have more than 7 billion customers

log_10(7 billion) ~ 10

log_10(700 billion) ~ 12

---

# Bounded vs Unbounded

Bounded means we can tackle it with finite resources

Unbounded means we'll need some smart scaling

---

# Recap: complexity analysis

Gives us a simple way to think about how something will behave at scale

---

# Main points

Usually discussed in the context of scaling:

- just worry about the major factor


- don't worry about the constants, just the shape of the curve
    - "linear", "quadratic" etc...

---

# Generally

Life is simpler when it's "constant" or "logarithmic"

---

# Why are we talking about this?

Helps us understand what `List` is good and bad at

Will also help later with tail recursion

---

```
 ___                           _        _     _
|_ _|_ __ ___  _ __ ___  _   _| |_ __ _| |__ | | ___
 | || '_ ` _ \| '_ ` _ \| | | | __/ _` | '_ \| |/ _ \
 | || | | | | | | | | | | |_| | || (_| | |_) | |  __/
|___|_| |_| |_|_| |_| |_|\__,_|\__\__,_|_.__/|_|\___|


                __   _____
                \ \ / / __|
                 \ V /\__ \
                  \_/ |___/

 __  __       _        _     _
|  \/  |_   _| |_ __ _| |__ | | ___
| |\/| | | | | __/ _` | '_ \| |/ _ \
| |  | | |_| | || (_| | |_) | |  __/
|_|  |_|\__,_|\__\__,_|_.__/|_|\___|

```

---

# What is mutability?

What do we mean when we say something is immutable or mutable?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Definitions

## Immutable

Once it's created, it can't be modified for its whole life

## Mutable

Objects can be modified after they're created

---

# Mutable example

What is an example of a mutable object?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Mutable example

`Array`

To the repl!

---

# Array summary

```scala
val array = Array(0, 1, 2)

array(0) = -1

// Array(-1, 1, 2
```

---

# Immutable example

What is an example of a immutable object?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Immutable examples

- `String`


- `List`

(All collections in `scala.collections.immutable`)

---

# Change?

How do you "modify" immutable objects?

e.g. prepend to a list?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Copy

> How do you "modify" immutable objects?

A modified copy

```scala
val s1 = "abc"

val s2 = s1.toUpperCase

// s1 is not changed
```

---

# FP

Heavy use of immutable data:

- thread safe


- easy to reason about

But...

---

# Criticism

> Isn't it inefficient to copy objects all the time?

---

# Example

It's very fast and cheap to modify an array

```scala
val array = Array(0, 1, 2)

array(0) = 10
array(1) = 11
array(2) = 12

// Array(10, 11, 12)
```

You wouldn't want to copy the entire array for every mutation

---

# Fair criticism

Sometimes it's inefficient to use an immutable model

An array makes sense for that example

---

# Usually...

... there is a particular kind of operation you need, e.g.

```
         -------------------------------------------------
        |        |        |          |         |          |
        |        |  READ  |  REMOVE  | INSERT  | REPLACE  |
        |        |        |          |         |          |
         -------------------------------------------------
        |        |        |          |         |          |
        | FRONT  |        |  pop     | prepend |          |
        |        |        |          |         |          |
          ------------------------------------------------
        |        |        |          |         |          |
        |  BACK  |        |  pop     | append  |          |
        |        |        |          |         |          |
          ------------------------------------------------
        |  ANY   |        |          |         |          |
        |  WHERE |        |          |         |          |
        |        |        |          |         |          |
          ------------------------------------------------
```

Usually only need a few of these

e.g. front insert:

```scala
val currentState = List(1, 2, 3, 4)

val newState = 0 :: currentState
```

and see who the last inserted was

```scala
newState.head
```

---

# The right structure

Once you know what you need,

there is often a very efficient immutable structure

Good time/space complexity

---

```
 _     _     _
| |   (_)___| |_
| |   | / __| __|
| |___| \__ \ |_
|_____|_|___/\__|

 ____            __
|  _ \ ___ _ __ / _| ___  _ __ _ __ ___   __ _ _ __   ___ ___
| |_) / _ \ '__| |_ / _ \| '__| '_ ` _ \ / _` | '_ \ / __/ _ \
|  __/  __/ |  |  _| (_) | |  | | | | | | (_| | | | | (_|  __/
|_|   \___|_|  |_|  \___/|_|  |_| |_| |_|\__,_|_| |_|\___\___|

```

---

# List Performance

Understand how `List` goes on a subset of this box:

```
         -------------------------------------------------
        |        |        |          |         |          |
        |        |  READ  |  REMOVE  | INSERT  | REPLACE  |
        |        |        |          |         |          |
         -------------------------------------------------
        |        |        |          |         |          |
        | FRONT  |        |          |         |          |
        |        |        |          |         |          |
          ------------------------------------------------
        |        |        |          |         |          |
        |  BACK  |        |          |         |          |
        |        |        |          |         |          |
          ------------------------------------------------
        |  ANY   |        |          |         |          |
        |  WHERE |        |          |         |          |
        |        |        |          |         |          |
          ------------------------------------------------
```

---

# Our picture of `List`

```scala
val list = List(1, 2, 3, 4, ...) // length n
```

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> ... ---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
^
list
```

---

# Pop Quiz Time

Ready?

MVP gets Simon's long service leave

---

# Pop Quiz 1 - last

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> ... ---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
^
list
```

What is the time complexity for reading the _last_ element in the list?

```scala
list.last
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Pop Quiz 1 - last

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> ... ---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
^           --->            --->            --->            --->     --->
list
```

> What is the time complexity for reading the _last_ element in the list?

```scala
list.last
```

Time: O(n)

Has to make about n hops to get to the last element

Moral: Accessing the back of a list is slow

---

# Pop Quiz 2 - head

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> ... ---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
^
list
```

What is the time complexity for reading the _first_ element in the list?

```scala
list.head
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Pop Quiz 2 - head

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> ... ---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
^
list
```

> What is the time/space complexity for reading the _first_ element in the list?

```scala
list.head
```

O(1)

---

# Pop Quiz 3 - pop head

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> ... ---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
^
list
```

Want a new list missing the head element:

```
 ------ ---      ------ ---      ------ ---
|  2   |   |--->|  3   |   |--->|  4   |   |---> ... ---> Nil
 ------ ---      ------ ---      ------ ---
```

```scala
list.head
```

O(1)

---

# MVP?
