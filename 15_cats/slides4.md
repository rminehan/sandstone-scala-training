---
author: Joman
date: 2022-06-06
title: Eq
---

```
 _____
| ____|__ _
|  _| / _` |
| |__| (_| |
|_____\__, |
         |_|
```

(As in Equals)

---

# Eq

Another type class!

---

# Eq

The set of all types with an "equals" concept

---

# Examples

Pretty much everything

It's more difficulty a type that _doesn't_ have equals

---

# Why is it useful?

Provides a way to compare things

---

# Java equals

You say:

> But James, doesn't java already come with a built in equals concept,
>
> why have a separate type class for this?

---

# Issues

Java's equals is a bit of a mess

Demo time!

---

# Summary

```scala
class Base(val int: Int) {
  override def equals(other: Any): Boolean = {
    if (other.isInstanceOf[Base])
      other.asInstanceOf[Base].int == this.int
    else false
  }
}

val base1 = new Base(3)

val base2 = new Base(3)

base1.equals(base2) // true, good!
base1.equals(4) // false, but why would you even let this compile?

class Child(int: Int, val string: String) extends Base(int) {
  override def equals(other: Any): Boolean = {
    if (other.isInstanceOf[Child]) {
      val otherChild = other.asInstanceOf[Child]
      otherChild.int == this.int && otherChild.string == this.string
    }
    else false
  }
}

val child1 = new Child(3, "hi")
val child2 = new Child(3, "hi")

child1.equals(child2) // true, good!
base1.equals(child1) // true, good!
child1.equals(base1) // false, bad!

(child1: Base).equals(new Child(3, "bye")) // false, confusing!
```

---

# Also

We forgot to override the hashcode

---

# Java

So equality is a mess

---

# Root cause: Any

A lot of it comes back to `equals` having to support all types:

```scala
def equals(other: Any): Boolean = ...
//                ^^^
//                :(
```

---

# Root cause: being a member function

```scala
base1.equals(child1) // logic defined in `Base`
child1.equals(base1) // logic defined in `Child`
```

Asymmetry

---

# Ideally

If I were to compare two things...

---

# Example 1

If I were to compare two `String`'s, I'd want a function like:

```scala
def equals(left: String, right: String): Boolean = ...
```

---

# Example 2

If I were to compare two `Image`'s, I'd want a function like:

```scala
def equals(left: Image, right: Image): Boolean = ...
```

---

# Attributes

```scala
def equals(left: Image, right: Image): Boolean = ...
```

## Symmetrical

```scala
equals(left, right)
equals(right, left)
```

both use the same "static" logic

Better expresses that equality is a symmetrical binary operator

## Type safe

The compiler wouldn't allow me do: `equals(3, image)`

## Clarity

It's clear what they're being compared as `Image`'s

(and not as some child class)

---

# Type class

If I were to compare two `String`'s, I'd want a function like:

```scala
def equals(left: String, right: String): Boolean = ...
```

If I were to compare two `Image`'s, I'd want a function like:

```scala
def equals(left: Image, right: Image): Boolean = ...
```

etc...

---

# Generalising

If I were to compare two `A`'s, I'd want a function like:

```scala
def equals(left: A, right: A): Boolean = ...
```

---

# Type class

Let's create a type class definition

To the repl!

---

# Summary

```scala
trait Eq[A] {
  def equals(left: A, right: A): Boolean
}

object IntEq extends Eq[Int] {
  def equals(left: Int, right: Int): Boolean = left == right // use the existing `==`
}

// Looks how we want
IntEq.equals(1, 1) // true

IntEq.equals(1, 2) // false

IntEq.equals(1, "abc") // won't compile
```

---

# Syntax

---

# Syntax

We want something like the `==` syntax:

```scala
// Ugly
IntEq.equals(1, 1) // true

// Looks nicer
1 == 1
```

---

# Triple Equals

`==` is taken already

`===` will be our operator!

To the repl!

---

# Summary

```scala
// Our usual syntactic trick to make static functions feel like member functions
implicit class EqOps[A](a: A)(implicit ev: Eq[A]) {
  def ===(other: A): Boolean = ev.equals(a, other)
}

// Make sure your instance is available in implicit scope
implicit object IntEq extends Eq[Int] {
  def equals(left: Int, right: Int): Boolean = left == right
}

1 === 2 // false

1 === 1 // true

1 === "abc" // Doesn't compile, hoorah!

1 == "abc" // Compiles, silly ==
```

---

# Laws

---

# Laws

It's very easy to create an `Eq` that compiles, but doesn't make sense

To the repl!

---

# Demo

```scala
implicit object StringEq extends Eq[String] {
  def equals(left: String, right: String): Boolean = if (left == "abc") true else false
}

"abc" === "def" // true, hmm...
"def" === "def" // false, hmm...
```

---

# Laws

```
reflexive:  a === a

symmetry:   a === b   implies    b === a

transitivity: a === b, b === c   implies a === c
```

---

# Let's test it out

---

# Reflexive?

```scala
implicit object StringEq extends Eq[String] {
  def equals(left: String, right: String): Boolean = if (left == "abc") true else false
}
```

a === a

"def" === "def" // false

FAIL

---

# Symmetric?

```scala
implicit object StringEq extends Eq[String] {
  def equals(left: String, right: String): Boolean = if (left == "abc") true else false
}
```

## Symmetry?

a === b   implies   b === a

"abc" === "def" // true

"def" === "abc" // false

FAIL

---

# A lawful interesting example

```scala
implicit object StringCaseInsensitiveEq extends Eq[String] {
  def equals(left: String, right: String): Boolean = left.toLowerCase == right.toLowerCase
}
```

---

# Testing it

## Reflexive?

Yep,

`a.toLowerCase == a.toLowerCase`

## Symmetrical

Yep

Example: "ABC", "abc"

## Transitive

Yep

Example: "ABC", "Abc", "abc"

---

# Aside

You can use `Eq` for these more liberal comparisons

Called "equivalence relations"

---

# Real world example

Person A === Person B if they're blood relatives

---

# Lawful

> Person A === Person B if they're blood relatives

## Transitive

Is a person related to themselves?

Yeah

## Symmetry

If I'm your blood relative, then you're mine

## Transitive

If I'm related to Uncle Bob, and Uncle Bob is related to you,

then I'm related to you

---

# Another interesting example

```scala
implicit object HippyStringEq extends Eq[String] {
  def equals(left: String, right: String): Boolean = true // free love man! Everyone is equal!
}
```

Is it lawful?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Technically yes!

```scala
implicit object HippyStringEq extends Eq[String] {
  def equals(left: String, right: String): Boolean = true // free love man! Everyone is equal!
}
```

## Reflexive

Does `a === a`? Yep, in fact it equals everything

## Symmetry

If `a === b`, does `b === a`? Yep, because it equals everything

## Transitive

If `a === b` and `b === c`, does `a === c`? Yep, because it equals everything

## Summary

So it's a lawful instance (but a bit useless)

---

# Cats

Already has this

To the repl!

---

# Summary

```scala
import $ivy.`org.typelevel::cats-core:2.7.0`

import cats.syntax.eq._

3 === 4 // false

3 === "abc" // won't compile

3 =!= 4 // true
```

---

```
__        __                     _
\ \      / / __ __ _ _ __  _ __ (_)_ __   __ _
 \ \ /\ / / '__/ _` | '_ \| '_ \| | '_ \ / _` |
  \ V  V /| | | (_| | |_) | |_) | | | | | (_| |
   \_/\_/ |_|  \__,_| .__/| .__/|_|_| |_|\__, |
                    |_|   |_|            |___/
 _   _
| | | |_ __
| | | | '_ \
| |_| | |_) |
 \___/| .__/
      |_|
```

---

# Eq

The type class of all things with an equals concept

---

# Better than `==`

Avoids many edge cases `equals/==` have

---

# "Equals?"

Doesn't have to be the strict kind of equality you're used to

e.g. case insensitive equals

---

# Cats

Has support for all the common types

---

# Type safe

Makes sense to start using `===` to avoid subtle bugs

---

# Next time

`Traverse` with Pranali!

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
