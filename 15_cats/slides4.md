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

It's more difficulty to find a type that _doesn't_ have equals

---

# Why is it useful?

We always need to compare things

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
1 == "abc" // compiler allows this, even though it doesn't make sense
```

---

# Java

So equality is a mess

(many articles about this kind of thing)

---

# Other issues...

---

# Any

When you define your own `equals`, the type is `Any`:

```scala
class Foo {
    override def equals(other: Any): Boolean = ...
    //                         ^^^
    //                         :(
}
```

No type safety...

---

# Asymmetry

```scala
class A {
    override def equals(other: Any): Boolean = ...
}

class B {
    override def equals(other: Any): Boolean = ...
}
```

```scala
a.equals(b) // logic defined in `A`
b.equals(a) // logic defined in `B`
```

Different logic is used when you switch them

Leads to bugs

---

# Ideally

Suppose we were comparing two strings,

think about a nice safe contract for that...

---

# Something like

```scala
def equals(left: String, right: String): Boolean = ...
```

---

# Attributes

```scala
def equals(left: String, right: String): Boolean = ...
```

## Symmetrical

```scala
equals(left, right)
equals(right, left)
```

both use the same logic

"Static" - both are args, neither is "caller"

## Type safe

The compiler won't allow: `equals(3, "abc")`

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
  def equals(left: String, right: String): Boolean = left == "abc"
}

"abc" === "def" // true, hmm...
"def" === "def" // false, hmm...
```

Something's clearly off about this implementation

---

# Laws

- reflexive


- symmetric


- transitive

---

# Reflexive

> you always equal yourself

ie. `equals(a, a)` always yields `true`

---

# Symmetry

> swapping places won't change anything

ie. `equals(a, b)` is the same as `equals(b, a)`

---

# Transitive

> a === b, b === c implies a === c

---

# Let's test it out

> Something's clearly off about this implementation

We have formal laws now to describe what's off

---

# Reflexive?

```scala
implicit object StringEq extends Eq[String] {
  def equals(left: String, right: String): Boolean = left == "abc"
}

StringEq.equals("def", "def") // false
```

FAIL

---

# Symmetric?

```scala
implicit object StringEq extends Eq[String] {
  def equals(left: String, right: String): Boolean = left == "abc"
}

StringEq.equals("abc", "def") // true
StringEq.equals("def", "abc") // false
```

FAIL

---

# Flexibility

Our laws let us apply some flexibility in comparing things

Two things don't have to be precisely the same to be considered equals

---

# Example

Let's consider two strings equal if they're case insensitively the same

`"ABC" === "abc"`

---

# Encoding that

```scala
implicit object StringCaseInsensitiveEq extends Eq[String] {
  def equals(left: String, right: String): Boolean = left.toLowerCase == right.toLowerCase
}
```

---

# Lawful?

```scala
implicit object StringCaseInsensitiveEq extends Eq[String] {
  def equals(left: String, right: String): Boolean = left.toLowerCase == right.toLowerCase
}
```

---

# Testing it

```scala
implicit object StringCaseInsensitiveEq extends Eq[String] {
  def equals(left: String, right: String): Boolean = left.toLowerCase == right.toLowerCase
}
```

## Reflexive?

```scala
equals("abc", "abc") // true
```

## Symmetrical?

We're applying lowercasing to the left and right symmetrically

## Transitive

Yeah

Harder to prove but it intuitively makes sense

---

# So

You can use `Eq` for these more liberal comparisons

---

# "Equivalence relations"

> You can use `Eq` for these more liberal comparisons

Formal name for this concept

---

# Formally

An equivalence relation is some function `(A, A) => Boolean`

which is:

- reflexive


- symmetric


- transitive

---

# Generalised

> An equivalence relation is some function `(A, A) => Boolean`

Strict equality (`==`) is a special case of this

---

# Real world example

```scala
def bloodRelatives(personA: Person, personB: Person): Boolean = ...
```

This is an equivalence relation

---

# Lawful

```scala
def bloodRelatives(personA: Person, personB: Person): Boolean = ...
```

## Reflexive

```scala
bloodRelatives(a, a)
```

ie. Is a person related to themselves?

Yeah

## Symmetry

Yep

> If John is related to Bob, then Bob is related to John

## Transitive

Yep, it's intrinsic to this concept "relative"

Example:

> If I'm related to my dad,
>
> and my dad is related to Grandpa
>
> then I'm related to Grandpa

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

```scala
HippyStringEq.equals(a, a)
```

Does it return `true`?

Yes because it always returns true for everything

## Symmetry

```scala
HippyStringEq.equals(a, b)
HippyStringEq.equals(b, a)
```

Will they always be the same?

Yes because they both always return true

## Transitive

```scala
// Suppose both of these return true
HippyStringEq.equals(a, b)
HippyStringEq.equals(b, c)

// Will this return true?
HippyStringEq.equals(a, c)
```

Yes because it always returns true!

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

`(A, A) => Boolean`

---

# Better than `==`

Avoids many edge cases `equals/==` have

---

# "Equals?" "Equivalence?"

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
