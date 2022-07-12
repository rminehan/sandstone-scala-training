---
author: Rohan
date: 2022-08-02
title: NonEmptyList
---

```
 _   _             _____                 _         _     _     _
| \ | | ___  _ __ | ____|_ __ ___  _ __ | |_ _   _| |   (_)___| |_
|  \| |/ _ \| '_ \|  _| | '_ ` _ \| '_ \| __| | | | |   | / __| __|
| |\  | (_) | | | | |___| | | | | | |_) | |_| |_| | |___| \__ \ |_
|_| \_|\___/|_| |_|_____|_| |_| |_| .__/ \__|\__, |_____|_|___/\__|
                                  |_|        |___/
```

---

# Today

Introduce the `NonEmptyList` from cats

---

# Why?

It's a good example of a strong type

---

# Agenda

- recap strong type concept


- recap motivation for `NonEmptyList`


- introduce `NonEmptyList`


- factory methods


- combinators


- peeking under the hood

---

# Common acronym

"nel" - short for `NonEmptyList`

---

```
 ____
|  _ \ ___  ___ __ _ _ __
| |_) / _ \/ __/ _` | '_ \
|  _ <  __/ (_| (_| | |_) |
|_| \_\___|\___\__,_| .__/
                    |_|
```

What is a "strong type"?

---

# Time for:

One-dollar-ionairre!!!

---

# One-dollar-ionairre

For 23c, a function is "complete" if:

```
(A) It's done everything on     (B) It doesn't throw exceptions
    its bucket list


(C) It returns a meaningful     (D) It has reached a state of Zen
    value for every input
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# C!

A function is "complete" if:

```
(A) It's done everything on     (B) It doesn't throw exceptions
    its bucket list


(C) It returns a meaningful     (D) It has reached a state of Zen
    value for every input
    ^^^^^^^^^^^^^^^^^^^^^^^
```

---

# B?

```
(A) It's done everything on     (B) It doesn't throw exceptions  <---- ?
    its bucket list


(C) It returns a meaningful     (D) It has reached a state of Zen
    value for every input
    ^^^^^^^^^^^^^^^^^^^^^^^
```

Complete functions won't throw exceptions, but that's not enough

Example:

```scala
def loop: Int = {
  while (true) {}
  0
}
```

Doesn't throw an exception, but it's not complete

---

# Things that cause incompleteness

- throwing exceptions


- infinite loops


- dead lock


- stopping the JVM


- returning meaningless data (`null`, `NaN`, `Infinity`)

---

# Practically

_Usually_ in scala the reason a function is incomplete is because of throwing exceptions

---

# Incomplete Example

```scala
def youngest(people: Seq[Person]): Person = people.minBy(_.age)
```

Function accepts data it can't process (empty sequence)

---

# Common Strategies

```scala
def youngest(people: Seq[Person]): Person = people.minBy(_.age)
```

- "cross your fingers"


- "defensive"

Both allow the bad data in - "weak bouncer"

---

# Strong Type Strategy

```scala
def youngest(people: NonEmptyList[Person]): Person = people.minBy(_.age)
```

Don't allow bad data in - "strong bouncer"

---

```
 __  __       _   _            _   _
|  \/  | ___ | |_(_)_   ____ _| |_(_) ___  _ __
| |\/| |/ _ \| __| \ \ / / _` | __| |/ _ \| '_ \
| |  | | (_) | |_| |\ V / (_| | |_| | (_) | | | |
|_|  |_|\___/ \__|_| \_/ \__,_|\__|_|\___/|_| |_|

  __
 / _| ___  _ __
| |_ / _ \| '__|
|  _| (_) | |
|_|  \___/|_|

 _   _             _____                 _         _     _     _
| \ | | ___  _ __ | ____|_ __ ___  _ __ | |_ _   _| |   (_)___| |_
|  \| |/ _ \| '_ \|  _| | '_ ` _ \| '_ \| __| | | | |   | / __| __|
| |\  | (_) | | | | |___| | | | | | |_) | |_| |_| | |___| \__ \ |_
|_| \_|\___/|_| |_|_____|_| |_| |_| .__/ \__|\__, |_____|_|___/\__|
                                  |_|        |___/
```

---

# Overhead

Introducing a strong type can be extra work

- another library to manage


- another class to manage


- conversion between types


- training devs

---

# Bang for buck

> Introducing a strong type can be extra work

It's good when it satisfies a lot of use cases

---

# NonEmptyList

Lots of use cases

---

# Empty sequences

Many gotchas related to empty sequences:

- head/tail


- last


- max/min


- average


- reduce

---

# Simple

From a "standard" library

Nice familiar api

Simple implementation

---

# Recap

- simple to import and use


- solves many use cases

ie. good "bang for buck"

---

```
 ___       _                 _            _
|_ _|_ __ | |_ _ __ ___   __| |_   _  ___(_)_ __   __ _
 | || '_ \| __| '__/ _ \ / _` | | | |/ __| | '_ \ / _` |
 | || | | | |_| | | (_) | (_| | |_| | (__| | | | | (_| |
|___|_| |_|\__|_|  \___/ \__,_|\__,_|\___|_|_| |_|\__, |
                                                  |___/
 _   _             _____                 _         _     _     _
| \ | | ___  _ __ | ____|_ __ ___  _ __ | |_ _   _| |   (_)___| |_
|  \| |/ _ \| '_ \|  _| | '_ ` _ \| '_ \| __| | | | |   | / __| __|
| |\  | (_) | | | | |___| | | | | | |_) | |_| |_| | |___| \__ \ |_
|_| \_|\___/|_| |_|_____|_| |_| |_| .__/ \__|\__, |_____|_|___/\__|
                                  |_|        |___/
```

---

# From cats

To the cats docs!

---

# Recap

The cats library provides:

- common type classes


- common data structures (including `NonEmptyList`)

---

```
 _____          _
|  ___|_ _  ___| |_ ___  _ __ _   _
| |_ / _` |/ __| __/ _ \| '__| | | |
|  _| (_| | (__| || (_) | |  | |_| |
|_|  \__,_|\___|\__\___/|_|   \__, |
                              |___/
 __  __      _   _               _
|  \/  | ___| |_| |__   ___   __| |___
| |\/| |/ _ \ __| '_ \ / _ \ / _` / __|
| |  | |  __/ |_| | | | (_) | (_| \__ \
|_|  |_|\___|\__|_| |_|\___/ \__,_|___/

```

ie. ways of constructing `NonEmptyList`'s

---

# Demo time!

We'll see that it's impossible to create an empty nel

Nonetheless, let's think like hackers and snoop around for weaknesses

To the repl!

---

# Summary

```scala
import $ivy.`org.typelevel::cats-core:2.7.0`

import cats.data.NonEmptyList

// `of` is a factory method
NonEmptyList.of(1, 2, 3)
// NonEmptyList(head = 1, tail = List(2, 3))

// Try to create an empty nel via `of`
NonEmptyList.of()
// Doesn't compile... Requires at least one parameter


// `fromList` is a factory method to build nel's from regular lists
NonEmptyList.fromList(List(1, 2, 3))
// Some(NonEmptyList(head = 1, tail = List(2, 3)))

// Try to create an empty nel with `fromList`
NonEmptyList.fromList(List.empty[Int])
// None


// Try to filter down an existing nel
NonEmptyList.of(1,2,3).filter(_ < 0)
// filter returns List, not NonEmptyList


// Try to access the tail of a nel with 1 element
NonEmptyList.of(1).tail
// tail returns List, not NonEmptyList
```

---

# Impossible

Our factory methods won't produce an empty nel

---

# Impossible

> Our factory methods won't produce an empty nel

In fact the `NonEmptyList` data structure can't be empty

(more later)

---

# Recap

- `NonEmptyList` is very "strong", ie. impossible to make an empty nel


- has nice factory methods

---

```
  ____
 / ___|___  _ __ ___  _ __ ___   ___  _ __
| |   / _ \| '_ ` _ \| '_ ` _ \ / _ \| '_ \
| |__| (_) | | | | | | | | | | | (_) | | | |
 \____\___/|_| |_| |_|_| |_| |_|\___/|_| |_|

  ____                _     _             _
 / ___|___  _ __ ___ | |__ (_)_ __   __ _| |_ ___  _ __ ___
| |   / _ \| '_ ` _ \| '_ \| | '_ \ / _` | __/ _ \| '__/ __|
| |__| (_) | | | | | | |_) | | | | | (_| | || (_) | |  \__ \
 \____\___/|_| |_| |_|_.__/|_|_| |_|\__,_|\__\___/|_|  |___/

```

---

# Seq-like

`NonEmptyList` _feels_ like a regular scala sequence

ie. has all the bells and whistles we like (e.g. `map`, `filter`)

---

# filter

---

# filter

```scala
val nel = NonEmptyList.of(-2, 1, 2, 3)

val positives = nel.filter(_ > 0)
```

---

# Type?

```scala
val nel = NonEmptyList.of(-2, 1, 2, 3)

val positives = nel.filter(_ > 0)
```

What is the type of `positives`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# List[Int]

```scala
val nel = NonEmptyList.of(-2, 1, 2, 3)

val positives: List[Int] = nel.filter(_ > 0)
```

> What is the type of `positives`?

`List[Int]`

Filter must return `List` as there's no guarantee at least one element will survive the predicate

---

# map

---

# map

```scala
val nel = NonEmptyList.of(1, 2, 3)

val doubled = nel.map(_ * 2)
```

---

# Type?

```scala
val nel = NonEmptyList.of(1, 2, 3)

val doubled = nel.map(_ * 2)
```

What should the type of `doubled` be?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# NonEmptyList[Int]

```scala
val nel = NonEmptyList.of(1, 2, 3)

val doubled: NonEmptyList[Int] = nel.map(_ * 2)
```

> What should the type of `doubled` be?

`NonEmptyList[Int]`

Mapping doesn't change length.

So if the input is non-empty, the output must be too

---

# Functor recap

For 31c, complete this sentence

> Functors are structure...

```
(A) loving            (B) preserving


(C) -ific             (D) schmucture
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Functor recap

For 31c, complete this sentence

> Functors are structure...

```
(A) loving            (B) preserving
                          ^^^^^^^^^^

(C) -ific             (D) schmucture
```

Option:

- Some -> Some
- None -> None

Sequences:

- length doesn't change

---

# flatMap

```scala
val nel = NonEmptyList.of(1, 2, 3)

val exploded = nel.flatMap(i => NonEmptyList.of(i, -i))
```

---

# flatMap

```scala
val nel = NonEmptyList.of(1, 2, 3)

val exploded = nel.flatMap(i => NonEmptyList.of(i, -i))
```

What should the type of `exploded` be?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# NonEmptyList[Int]

```scala
val nel = NonEmptyList.of(1, 2, 3)

val exploded: NonEmptyList[Int] = nel.flatMap(i => NonEmptyList.of(i, -i))
```

> What should the type of `exploded` be?

`NonEmptyList[Int]`

We start with at least 1 element

Each element gets exploded into a NonEmptyList

It must be non-empty!

---

# Aside

`NonEmptyList` supports `flatMap` and `map`

Hmm...

---

# Aside

`NonEmptyList` supports `flatMap` and `map`

What useful syntactic sugar does that let us use?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# for!

> `NonEmptyList` supports `flatMap` and `map`
>
> What useful syntactic sugar does that let us use?

for!

To the repl!

---

# for demo

```scala
for {
    i <- NonEmptyList.of(1, 2, 3)
    j = i * 2
  } yield j
// [2, 4, 6]
```

---

# Prepend

---

# Prepend

```scala
val nel = NonEmptyList.of(1, 2, 3)

val prepended = 0 :: nel
```

What is the type of `prepended`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# NonEmptyList[Int]

```scala
val nel = NonEmptyList.of(1, 2, 3)

val prepended: NonEmptyList[Int] = 0 :: nel
```

> What is the type of `prepended`?

NonEmptyList[Int]

The input is already non-empty

Adding a new element makes it even more non-empty

---

# Recap

Some combinators will give you nel and some `List`

Depends on what reasoning the authors could use

---

# Unsafe becomes Safe

These are unsafe on `List` but completely safe on `NonEmptyList`:

- head


- tail


- last


- min/max (after converting to `List`)


- reduceLeft

---

# Missing methods

Not everything from `List` is supported

e.g. `max/min`

To the repl!

---

# Summary

```scala
case class Person(name: String, age: Int)
def youngest(people: NonEmptyList[Person]): Person = people.toList.minBy(_.age)
```

---

# Converting back

Sometimes you need to do:

```
NonEmptyList ---> List ---> NonEmptyList
```

e.g. `sliding`

To the repl!

---

# Summary

```scala
/* Method to generate sliding windows of size 3
 * [1, 2, 3, 4, 5, 6] =>  [
 *                          [1, 2, 3],
 *                          [2, 3, 4],
 *                          [3, 4, 5],
 *                          [4, 5, 6]
 *                        ]
 */
def sliding3(nel: NonEmptyList[Int]): NonEmptyList[NonEmptyList[Int]] = {
  val windowsList: List[List[Int]] = nel.toList.sliding(3).toList

  // The outer list _must_ be empty because it came from a nel,
  // so this won't throw an exception:
  val windowsNel: NonEmptyList[List[Int]] = NonEmptyList.fromListUnsafe(windowsList)

  // We also know that the inner windows must be non-empty (because of how sliding works),
  // therefore it's safe to map each inner window to a nel
  windowsNel.map(window => NonEmptyList.fromListUnsafe(window))

  // We get a nel of nels!
}
```

---

# Recapping

```
NonEmptyList ----------> List ---------------------------> NonEmptyList
             .toList          NonEmptyList.fromListUnsafe

                              Makes sense if you _know_
                              it must be non-empty
```


---

```
 _   _           _
| | | |_ __   __| | ___ _ __
| | | | '_ \ / _` |/ _ \ '__|
| |_| | | | | (_| |  __/ |
 \___/|_| |_|\__,_|\___|_|

 _   _
| |_| |__   ___
| __| '_ \ / _ \
| |_| | | |  __/
 \__|_| |_|\___|

 _   _                 _
| | | | ___   ___   __| |
| |_| |/ _ \ / _ \ / _` |
|  _  | (_) | (_) | (_| |
|_| |_|\___/ \___/ \__,_|
```

---

# Disclaimer

This section deepens our knowledge,

but it's not essential for day to day usage of `NonEmptyList`

---

# Main takeaway

The time/space complexity of common operations on `NonEmptyList`

---

# Observation

It's called `NonEmpty*List*`, not `NonEmpty*Seq*`

---

# Observation

> It's called `NonEmpty*List*`, not `NonEmpty*Seq*`

It's a specific data structure very similar to the scala `List`

---

# Recap

What is a scala `List` again... ?

Time for: One-dollar-ionairre!!!

---

# One-dollar-ionairre

For 41c, which of the following best describes the structure of the scala `List`?

```
(A) Tree                             (B) Array-ish


(C) Hashmap                          (D) Linked list
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# D!

For 41c, which of the following best describes the structure of the scala `List`?

```
(A) Tree                             (B) Array-ish


(C) Hashmap                          (D) Linked list
                                         ^^^^^^^^^^^
```

Singly linked list of cons cells

---

# Singly linked list

```scala
List(0, 1, 2)
```

```
 ------ ---       ------ ---      ------ ---
|  0   |   |---> |  1   |   |--->|  2   |   |--->  Nil
 ------ ---       ------ ---      ------ ---
```

---

# Two concepts

- terminus


- cons cell

```
 ------ ---       ------ ---      ------ ---
|  0   |   |---> |  1   |   |--->|  2   |   |--->  Nil
 ------ ---       ------ ---      ------ ---
```

Build by "prepending"

---

# DIY Implementation

```scala
sealed trait List[+A]

case object Nil extends List[Nothing]

case class ConsCell[A](head: A, tail: List[A]) extends List[A]
```

```
 ------ ---       ------ ---      ------ ---
|  0   |   |---> |  1   |   |--->|  2   |   |--->  Nil
 ------ ---       ------ ---      ------ ---
```

```scala
// "Prepending"
ConsCell(0, ConsCell(1, ConsCell(2, Nil)))
```

---

# Question

Can a cons cell be empty?

```scala
sealed trait List[+A]

case object Nil extends List[Nothing]

case class ConsCell[A](head: A, tail: List[A]) extends List[A]
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# No

> Can a cons cell be empty?

```scala
sealed trait List[+A]

case object Nil extends List[Nothing]

case class ConsCell[A](head: A, tail: List[A]) extends List[A]
//                     ^^^^^^^
```

No, it always has a `head`

It's built in

---

# Question

Can `Nil` be non-empty?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# No

> Can `Nil` be non-empty?

No!

It's always empty

---

# Dividing the universe

- Nil: the empy list


- cons cells: all non-empty lists

---

# Dividing the universe

```scala
// Our DIY list
list match {
  case Nil =>                  println("I've got the empty list")
  case ConsCell(head, tail) => println(s"I got a cons cell with head:'$head' and tail:'$tail'")
}


// Standard library List
list match {
  case Nil =>                  println("I've got the empty list")
  case head :: tail =>         println(s"I got a cons cell with head:'$head' and tail:'$tail'")
}
```

This pattern match is really asking: are you empty or non-empty?

---

# Building a `NonEmptyList`

If you wanted a list-like structure that's non-empty,

what kind of structure could you use?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# ConsCell!

> If you wanted a list-like structure that's non-empty,
>
> what kind of structure could you use?

A cons cell!

---

# Source code

To the [cats source](https://github.com/typelevel/cats/blob/main/core/src/main/scala/cats/data/NonEmptyList.scala)!

---

# Comparing

```scala
case class ConsCell[A](head: A, tail: List[A])

case class NonEmptyList[A](head: A, tail: List[A])
```

The same structure!

---

# Another perspective

`NonEmptyList` is just the `List` ADT minus `Nil`:

```scala
sealed trait List[+A] // <--- unnecessary abstraction now

case object Nil extends List[Nothing] // <--- kill

case class ConsCell[A](head: A, tail: List[A]) extends List[A]
```

---

# Recap

`NonEmptyList` is really just a cons cell

It's inherently non-empty - impossible to construct an empty cons cell

---

# Time/Space Complexity

> `NonEmptyList` is really just a cons cell

So it has the same time/space complexity

---

# One-dollar-ionairre

For 51c, what is the time complexity for prepending to a list size n?

```
(A) O(1)                  (B) O(O(n))


(C) O(n!)                 (D) O(n + m)
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# A!

For 51c, what is the time complexity for prepending to a list size n?

```
(A) O(1)                  (B) O(O(n))
    ^^^^

(C) O(n!)                 (D) O(n + m)
```

Very cheap

Just creating a new cons cell

Space complexity is also O(1)

---

# One-dollar-ionairre

For 52c, what is the time complexity for appending to a list size n?

```
(A) O(7)                  (B) O(O(x))


(C) O(log(n))             (D) O(n)
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# D!

For 52c, what is the time complexity for appending to a list size n?

```
(A) O(7)                  (B) O(O(x))


(C) O(log(n))             (D) O(n)
                              ^^^^
```

Very expensive for big n

Space complexity also O(n) as a whole new list is created

---

# One-dollar-ionairre

For 53c, what is the time complexity for finding the length of a list size n?

```
(A) O(1)                  (B) O(-1)


(C) O(n)                  (D) O(log(log(n^2)))
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# C!

For 53c, what is the time complexity for finding the length of a list size n?

```
(A) O(1)                  (B) O(-1)


(C) O(n)                  (D) O(log(log(n^2)))
    ^^^^
```

Has to start at the head and walk through the list until it finds `Nil`

---

# .toList

```scala
val nel = NonEmptyList.of(1, 2, 3, ...) // size n

nel.toList
```

Any guesses as to the time/space complexity?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# O(1)

```scala
val nel = NonEmptyList.of(1, 2, 3, ...) // size n

nel.toList
```

> Any guesses as to the time/space complexity?

Both are O(1)

```scala
// From the cats source
def toList: List[A] = head :: tail
```

Take the tail and just prepend the head onto it

---

# Recap

`NonEmptyList` is just a rebranded cons cell

---

# Recap

> `NonEmptyList` is just a rebranded cons cell

`NonEmptyList` and `List` have the same time/space complexity

(fast near the head, slow towards the end)

---

# Converting

> `NonEmptyList` is just a rebranded cons cell

Converting back and forth between them is very cheap

---

# Disclaimer

If you didn't fully grasp that section, it's okay

But try to remember:

- O(1) prepend


- O(n) append


- O(n) .length

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

# NonEmptyList

A useful data structure provided by cats

---

# Under the hood

It's just a rebranded cons cell

Has that time/space complexity

---

# Factory methods

```scala
NonEmptyList.of(1, 2, 3)

NonEmptyList.fromList(list) // Option[NonEmptyList]

NonEmptyList.fromListUnsafe(list) // NonEmptyList
```

---

# Combinators

Will feel like a regular scala sequence (`map`, `flatMap`, `filter`)

Works with `for` comprehension

If something is missing, you can use `.toList`

---

# Bang for buck

Simple structure from a defacto standard library

Already intuitive if you know `List`

Addresses a lot of use cases

---

# Next time

Modelling time

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
