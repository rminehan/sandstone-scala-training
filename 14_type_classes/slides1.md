---
author: Pranohan
date: 2022-03-24
title: Folding
---

```
 _____     _     _ _
|  ___|__ | | __| (_)_ __   __ _
| |_ / _ \| |/ _` | | '_ \ / _` |
|  _| (_) | | (_| | | | | | (_| |
|_|  \___/|_|\__,_|_|_| |_|\__, |
                           |___/
```

It's laundry day!

---

# Folding

Means "folding" a collection down to a single value

aka collapsing, aggregating

---

# Example: Summing

```
| 1 |
| 2 |
| 3 |    ----->  15
| 4 |
| 5 |
```

---

# Example: Multiplying

```
| 1 |
| 2 |
| 3 |    ----->  120
| 4 |
| 5 |
```

---

# Example: Averaging

```
| 1 |
| 2 |
| 3 |    ----->  3
| 4 |
| 5 |
```

---

# Example: Max

```
| 1 |
| 2 |
| 3 |    ----->  5
| 4 |
| 5 |
```

---

# Example: Min

```
| 1 |
| 2 |
| 3 |    ----->  1
| 4 |
| 5 |
```

---

# Write our own...

(learn more that way)

---

# DIY sum

Imagine aliens wiped out `sum` from the standard library

We need to make our own

```scala
def sum(numbers: Seq[Int]): Int = ???
```

---

# Loop

Put our java hats on and solve it with a loop

To the repl!

---

# Recap

```scala
def sum(numbers: Seq[Int]): Int = {
  var acc = 0

  for (number <- numbers)
    acc += number

  acc
}
```

---

# Next example

---

# DIY product

The alien invasion also deleted `product`

We need to rebuild that too:

```scala
def product(numbers: Seq[Int]): Int = ???

product(Seq(3, 4, 2)) // 24
```

---

# Loop

Again use a loop approach

To the repl!

---

# Recap

```scala
def product(numbers: Seq[Int]): Int = {
  var acc = 1

  for (number <- numbers)
    acc *= number

  acc
}
```

---

# Deja vu

These two implementations feel the same...

```scala
def sum(numbers: Seq[Int]): Int = {
  var acc = 0

  for (number <- numbers)
    acc += number

  acc
}

def product(numbers: Seq[Int]): Int = {
  var acc = 1

  for (number <- numbers)
    acc *= number

  acc
}
```

---

# DIY mkString

The aliens have now erradicated `mkString`

We need to rebuild:

```scala
def mkString(strings: Seq[String]): String = ???
```

To the repl!

---

# Recap

```scala
def mkString(strings: Seq[String]): String = {
  var acc = ""

  for (string <- strings)
    acc += string

  acc
}
```

---

# Deja vu

```scala
def sum(numbers: Seq[Int]): Int = {
  var acc = 0
  for (number <- numbers)
    acc += number
  acc
}

def product(numbers: Seq[Int]): Int = {
  var acc = 1
  for (number <- numbers)
    acc *= number
  acc
}

def mkString(strings: Seq[String]): String = {
  var acc = ""
  for (string <- strings)
    acc += string
  acc
}
```

What is the same and what is different between each?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Same and different

```scala
def sum(numbers: Seq[Int]): Int = {
  var acc = 0
  for (number <- numbers)
    acc = acc + number
  acc
}

def mkString(strings: Seq[String]): String = {
  var acc = ""
  for (string <- strings)
    acc = acc + string
  acc
}
```

## Same

General scaffolding (accumulator and loop)

## Different

Types

Bootstrap value ("seed")

Combine logic

---

# DRY

Don't Repeat Yourself

Writing out that same boilerplate every time would be tedious and error prone

```scala
def sum(numbers: Seq[Int]): Int = {
  var acc = 0
  for (number <- numbers)
    acc += number
  acc
}

def product(numbers: Seq[Int]): Int = {
  var acc = 1
  for (number <- numbers)
    acc *= number
  acc
}

def mkString(strings: Seq[String]): String = {
  var acc = ""
  for (string <- strings)
    acc += string
  acc
}
```

---

# Abstraction

Another way of looking at it:

We've stumbled on some more general abstract concept

```scala
def fold(numbers: Seq[Int]): Int = {
  var acc = ***
  for (number <- numbers)
    acc = acc *** number
  acc
}
```

---

# Abstraction

Isolate the parts that don't change,

inject the parts that do change

```scala
def fold(numbers: Seq[Int]): Int = {
  var acc = ***
  for (number <- numbers)
    acc = acc *** number
  acc
}
```

To the repl!

---

# Summary

```scala
def fold(numbers: Seq[Int], seed: Int)(combine: (Int, Int) => Int): Int = {
  var acc = seed

  for (number <- numbers)
    acc = combine(acc, number)

  acc
}
```

---

# mkString?

Our implementation was specific to Int

```scala
def fold(numbers: Seq[Int], seed: Int)(combine: (Int, Int) => Int): Int = {
  var acc = seed

  for (number <- numbers)
    acc = combine(acc, number)

  acc
}
```

What about our `mkString`?

```scala
def mkString(strings: Seq[String]): String = {
  var acc = ""

  for (string <- strings)
    acc += string

  acc
}
```

---

# Generic

Make our fold generic so that it can a sequence of any type

To the repl!

---

# Summary

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

# Recap

We've gradually generalised this process into something quite generic:

```scala
def fold[A](seq: Seq[A], seed: A)(combine: (A, A) => A): A = {
  var acc = seed

  for (number <- numbers)
    acc = combine(acc, number)

  acc
}
```

We can go further...

---

# Example

We want to sum the lengths of a `Seq[String]`:

```
| "abc"  |
| "defg" |  ------>  9
| "hi"   |
```

```scala
def sumLengths(strings: Seq[String]): Int = ...
```

What's different about this compared to previous folding examples?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Example

We want to sum the lengths of a `Seq[String]`:

```
| "abc"  |
| "defg" |  ------>  9
| "hi"   |
```

```scala
def sumLengths(strings: Seq[String]): Int = ...
```

> What's different about this compared to previous folding examples?

The types are different

```
| String |
| String |  ------>  Int
| String |
```

We're folding into another type

Our `fold` is too rigid for that:

```scala
def fold[A](seq: Seq[A], seed: A)(combine: (A, A) => A): A = {
```

---

# Work arounds

We could map our strings to their lengths and then fold them:

```scala
def sumLengths(strings: Seq[String]): Int = {
  val lengths = strings.map(_.length)

  fold(lengths, 0)((acc, next) => acc + next)
}
```

Or concatenate all the strings, then take the length:

```scala
def sumLengths(strings: Seq[String]): Int = {
  val concatenated = fold(strings, "")((acc, next) => acc + next)

  concatenated.length
}
```

---

# Intermediate structures

Both of those work arounds create large intermediate structures:

```scala
def sumLengths(strings: Seq[String]): Int = {
  val lengths = strings.map(_.length) // <---

  fold(lengths, 0)((acc, next) => acc + next)
}
```

Or concatenate all the strings, then take the length:

```scala
def sumLengths(strings: Seq[String]): Int = {
  val concatenated = fold(strings, "")((acc, next) => acc + next) // <---

  concatenated.length
}
```

Would be nice to do it one pass

---

# Return of the loop

How would you implement this using a loop and acc?

To the repl!

---

# Summary

```scala
def sumLengths(strings: Seq[String]): Int = {
  var acc = 0

  for (string <- strings)
    acc += string.length

  acc
}
```

Feels like fold with some tweaks

---

# Another example

Add all the expenses from a `Seq[Person]`:

```scala
case class Person(id: Id, expenses: BigDecimal)

def sumExpenses(people: Seq[Person]): BigDecimal = {
  var acc = BigDecimal(0)

  for (person <- people)
    acc += person.expenses

  acc
}
```

---

# Abstracting

There are two types now

- A: the type of the input elements (`Seq[A]`)

- B: the type of the output data

```scala
case class Person(id: Id, expenses: BigDecimal)

def sumExpenses(people: Seq[Person]): BigDecimal = {
//                          A         B
  var acc = BigDecimal(0)
  //  B     B

  for (person <- people)
  //   A
    acc += person.expenses
 // B = combine(B, A)

  acc
// B
}
```

---

# Implement it!

To the repl!

---

# Summary

```scala
def fold[B, A](seq: Seq[A], seed: B)(combine: (B, A) => B): B = {
  var acc = seed

  for (a <- seq)
    acc = combine(acc, a)

  acc
}

fold(Seq("abc", "de"), 0)((acc, next) => acc + next.length)
// 5
```

Only changes are to the type signature!

Does it all in one pass

---

# More examples

---

# Max

Find the largest element in a `Seq[Int]` (note it might be empty)

```scala
def max(numbers: Seq[Int]): Option[Int] = ???
```

---

# Max

Find the largest element in a `Seq[Int]` (note it might be empty)

```scala
def max(numbers: Seq[Int]): Option[Int] = ???
```

How would you implement this conceptually?

```
 ___
|__ \
  / /
 |_|
 (_)
```

(Hint: use `fold` - that's today's topic)

(Hint: picture it as a loop, then translate that back to fold)

---

# Picturing as a loop

Start acc with `None`

Iterate through the seq asking if the next element is bigger

---

# Picturing as a loop

Iterate through the seq asking if the next element is bigger

```
           acc
-------------------
(seed)     None
| 1 |      Some(1)
| 4 |      Some(4)
| 3 |      Some(4)
|-2 |      Some(4)
| 5 |      Some(5)

Return:    Some(5)

Types:
    A: Int

    B: Option[Int]

Seed: None
Combine: Pick the larger one
```

---

# Code it up!

To the repl!

---

# Summary

```scala
def max(numbers: Seq[Int]): Option[Int] = {
  fold[Option[Int], Int](numbers, None) {
    case (None, next) => Some(next)
    case (Some(currentMax), next) =>
      val newMax = if (currentMax >= next) currentMax else next
      Some(newMax)
  }
}

max(Seq.empty) // None
max(Seq(1, 2, 3)) // Some(3)
```

---

# Aside: min/max are unsafe

Often see code like this:

```scala
val seq = ...

seq.max
seq.min
```

What if they're empty?

Similar to calling `.get` on options

Fairies lose their wings when you do this...

Use `minOption` and `maxOption` generally

---

# Reverse

Use `fold` to reverse a list

```scala
def reverse[X](list: List[X]): List[X] = ???
```

Interesting: accumulator is also a list, not a simpler value

---

# Recall reversing

From talk 2 on tail recursion

We move forwards through one list,

prepending each element to the accumulator:

---

# Initially

```
input
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---


acc

                                                                 Nil
```

---

# 1

```
input
                 ------ ---      ------ ---      ------ ---
                |  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
                 ------ ---      ------ ---      ------ ---


acc
                                                 ------ ---
                                                |  1   |   |---> Nil
                                                 ------ ---
```

---

# 2

```
input
                                 ------ ---      ------ ---
                                |  3   |   |--->|  4   |   |---> Nil
                                 ------ ---      ------ ---


acc
                                 ------ ---      ------ ---
                                |  2   |   |--->|  1   |   |---> Nil
                                 ------ ---      ------ ---
```

---

# 3

```
input
                                                 ------ ---
                                                |  4   |   |---> Nil
                                                 ------ ---


acc
                 ------ ---      ------ ---      ------ ---
                |  3   |   |--->|  2   |   |--->|  1   |   |---> Nil
                 ------ ---      ------ ---      ------ ---
```

---

# 4

```
input

                                                                 Nil



acc
 ------ ---      ------ ---      ------ ---      ------ ---
|  4   |   |--->|  3   |   |--->|  2   |   |--->|  1   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

---

# Converting to fold

```scala
def reverse[X](list: List[X]): List[X] = {
  fold[???, ???](list, ???)(???)
}
```

A: X

B: List[X]

seed: `Nil`

combine: prepend each X onto the current List[X]

---

# Code it up!

To the repl!

---

# Summary

```scala
def reverse[X](list: List[X]): List[X] = {
  fold[List[X], X](list, Nil)((acc, next) => next :: acc)
}

reverse(Nil) // Nil
reverse(List(1, 2, 3)) // List(3, 2, 1)
```

---

# Standard library

The `fold` we built is analogous `foldLeft` from the standard library

We built it ourselves to get a concrete understanding

To the repl!

---

# Last comment

Why is it called "folding"?

Probably because it's analogous to folding up a blanket in sections

```
---->

  \    /
   \  /
_____._____._____._____._____



      _____
     ._____._____._____._____


              ...


                        _____
                        _____
                        _____
                        _____
                        _____
```

---

# Homework

Implement `filter` with `foldLeft`

```scala
def filter[A](seq: Seq[A])(pred: A => Boolean): Seq[A] = {
  ???
}

filter(Seq(1, 2, 3, 4))(_ > 2)) // Seq(3, 4)
```

For bonus points, what is the time/space complexity?

---

# Solution

```scala
def filter[A](seq: Seq[A])(pred: A => Boolean): Seq[A] = {
  seq.foldLeft(Seq.empty[A]) {
    case (acc, next) => if (pred(next)) acc :+ next else acc
  }
}
```

---

# Time/Space complexity

```scala
def filter[A](seq: Seq[A])(pred: A => Boolean): Seq[A] = {
  seq.foldLeft(Seq.empty[A]) {
    case (acc, next) => if (pred(next)) acc :+ next else acc
    //                                      ^^ append on every iteration
  }
}
```

> For bonus points, what is the time/space complexity?

Will depend on the underlying type of the sequence...

## Time

Many sequence types will have O(n) append (`:+`)

In those cases time would be O(n^2)

There are ways to make it O(n) by converting to a list internally

## Space

Space will generally be O(n) as a new structure is made

and we're not reusing any of the old structure

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

# Folding

A way to fold/collapse a sequence down to a single value

---

# Main use cases

"aggregation" concepts

ie. reducing the space complexity of a dataset

e.g. bringing it from O(n) to O(1) by summing all the values

---

# Concepts

Identify the "seed" and the "combine" logic,

then you can use fold

---

# Folding across types

You don't have to fold into the same type

e.g. summing the lengths of strings

---

# Fold

Very powerful and fundamental abstraction

Other combinators like `map` and `filter` can be implemented via folding

---

# Next time

Miscellaneous other stuff about fold

Then after that monoids and type classes

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
