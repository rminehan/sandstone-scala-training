---
author: Rohan
date: 2022-05-09
title: Higher Kinded Types
---

```
 _   _ _       _
| | | (_) __ _| |__   ___ _ __
| |_| | |/ _` | '_ \ / _ \ '__|
|  _  | | (_| | | | |  __/ |
|_| |_|_|\__, |_| |_|\___|_|
         |___/
 _  ___           _          _
| |/ (_)_ __   __| | ___  __| |
| ' /| | '_ \ / _` |/ _ \/ _` |
| . \| | | | | (_| |  __/ (_| |
|_|\_\_|_| |_|\__,_|\___|\__,_|

 _____
|_   _|   _ _ __   ___  ___
  | || | | | '_ \ / _ \/ __|
  | || |_| | |_) |  __/\__ \
  |_| \__, | .__/ \___||___/
      |___/|_|
```

---

# Today

Advanced syntax for working with complex types:

"Higher Kinded Types"

---

# Why?

Useful later for exploring type classes

---

# Today

We'll do some thought experiments related to types

Simple to hard

Gradually arrive at higher kinded types

---

# Experiment 1

We want to write a function that allows any type to be passed in

---

# Experiment 1

> We want to write a function that allows any type to be passed in

```scala
// All compile
gate(1)
gate(1.5f)
gate(true)
gate(Seq(1, 2, 3))
gate(None)
```

---

# Aside

NOTE: We don't care what the function does,

we're just interested in what it allows in

---

# How?

> We want to write a function that allows any type to be passed in

```scala
def gate(thing: ???): Unit = ...
```

How can we express this?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Solutions

## Any

```scala
def gate(thing: Any): Unit = ...
```

Recall `Any` is the "top type" (even for primitives)

## Type parameter

```scala
def gate[A](thing: A): Unit = ...
```

---

# Bouncer

Type system acting like a bouncer

```scala
def gate(thing: Any): Unit = ...

def gate[A](thing: A): Unit = ...
```

Here the bouncer is a chill guy

---

# Experiment 2

We only want to allow sequence types in

The sequence can be of anything

```scala
// Compiles
gate(Seq(1, 2, 3))
gate(Seq("abc", "def"))

// Won't compile
gate(3)
gate("hi")
gate(Option(3))
gate(balrog) // You shall not pass
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Solution

```scala
def gate[A](thing: Seq[A]): Unit = ...
//       ^         ^^^
//      free      fixed
```

> We only want to allow sequence types in

Represented by the outer `Seq[_]`

> The sequence can be of anything

Represented by free `A`

---

# Tangent: variance

If `Seq[A]` is "covariant" in A,

we could also have done:

```scala
def gate(thing: Seq[Any]): Unit = ...
```

Hopefully we'll cover variance one day...

---

# Experiment 3

Switch it around

## Previously

The outer type was fixed to `Seq`

The inner type was free

```scala
def gate[A](thing: Seq[A]): Unit = ...
//       ^         ^^^
//      free      fixed
```

## This time

The outer type is free

The inner type is fixed to `Int`

ie. an "anything of `Int`"

---

# Examples

> The outer type is free
>
> The inner type is fixed to `Int`

```scala
// Compiles
gate(Seq(1, 2, 3))         // Seq[Int]
gate(Option(10))           // Option[Int]
gate(Future.successful(0)) // Future[Int]

// Doesn't compile
gate("abc")                // String
gate(4)                    // Int
gate(true)                 // Boolean
gate(Seq("abc", "def"))    // Seq[String]
gate(Option("abc"))        // Option[String]
```

---

# How?

```scala
def gate(thing: ???): Unit = ...
```

> The outer type is free
>
> The inner type is fixed to `Int`

```scala
// Compiles
gate(Seq(1, 2, 3))
gate(Option(10))

// Doesn't compile
gate("abc")
gate(Seq("abc", "def"))
gate(Option("abc"))
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Thinking it through

Could you use an inheritance based approach?

---

# Thinking it through

> Could you use an inheritance based approach?

ie. pick a base type which all our good types inherit from?

```
                    Any
                 /       \
                ...........
                          Base
                        /      \
                     Seq[Int]  Option[Int]
```

```scala
def gate(thing: Base): Unit = ...
```

---

# Nah...

The strongest ancestor to `Seq`, `Option`, `Future` is `Object`

```
                    Any
                 /       \
                ...........
                          Base   <--- Object
                        /      \
                     Seq[Int]  Option[Int]
```

```scala
def gate(thing: Object): Unit = ...
```

Too permissive

```scala
gate("abc") // compiles
```

---

# New kind of problem

Won't be able to solve this with typical approaches

To the repl!

---

# Solution

```scala
def gate[C[_]](thing: C[Int]): Unit = ...
```

Only allows in types with shape `C[Int]` where `C` is free

---

# Comparing Syntax

## Experiment 1

```scala
def gate[A](thing: A): Unit = ...
```

## Experiment 2

```scala
def gate[A](thing: Seq[A]): Unit = ...
```

## Experiment 3

```scala
def gate[C[_]](thing: C[Int]): Unit = ...
```

New syntax: `C[_]`

---

# New syntax

`C[_]`

Represents a shape with a "hole" in it

---

# Incomplete

`C` can't be used in type position

To the repl!

---

# Incomplete

```scala
// Fails
def gate[C[_]](thing: C[Int]): Unit = {
  val c: C = ???
}
// C takes type parameters
//  val c: C = ???
//         ^

// Passes, the hole is filled
def gate[C[_]](thing: C[Int]): Unit = {
  val c: C[Double] = ???
}
```

`C` can't be used in type position

A type needs to fill the hole

---

# Experiment 4

Only allow in maps where the key is `Int`

---

# Examples

```scala
// Compiles
gate(Map(0 -> "boban", 1 -> "bobanita"))
gate(Map(0 -> true, 1 -> false))

// Doesn't compile
gate(1)
gate(1.5f)
gate(Option(3))
gate(Map(true -> "boban", false -> "bobanita"))
gate(Map("boban" -> 0, "bobanita" -> 1))
```

---

# How

> Only allow in maps where the key is `Int`

```scala
def gate(thing: ???): Unit = ...
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Solution

To the repl!

---

# Solution

This is like experiment 2: "Sequence of anything"

```scala
def gate[A](thing: Map[Int, A]): Unit = ...
//                 ^   ^    ^
//             fixed fixed free
```

---

# Experiment 5

Only allow containers with two type parameters String and Int

ie. the container is free, the types inside are fixed

---

# Examples

> Only allow containers with two type parameters String and Int

```scala
// Compiles
gate(Map("a" -> 1))        // Map[String, Int]
gate(("a", 1))             // Tuple2[String, Int]

// Doesn't compile
gate(1)                    // Int
gate(Seq(1, 2, 3))         // Seq[Int]
gate(Map("a" -> true))     // Map[String, Boolean]
```

To the repl!

---

# Solution

```scala
def gate[C[_, _]](thing: C[String, Int]): Unit = ...
```

---

```
 _____
|_   _|   _ _ __   ___
  | || | | | '_ \ / _ \
  | || |_| | |_) |  __/
  |_| \__, | .__/ \___|
      |___/|_|
  ____                _                   _
 / ___|___  _ __  ___| |_ _ __ _   _  ___| |_ ___  _ __ ___
| |   / _ \| '_ \/ __| __| '__| | | |/ __| __/ _ \| '__/ __|
| |__| (_) | | | \__ \ |_| |  | |_| | (__| || (_) | |  \__ \
 \____\___/|_| |_|___/\__|_|   \__,_|\___|\__\___/|_|  |___/

```

---

# "Type constructor"

A term developers use for types with a "hole"

---

# Example

```scala
def gate[C[_]](thing: C[Int]): Unit = ...
```

`C` is a "type constructor"

---

# Clarifications

---

# Developer speak

"Type constructor" is terminology used informally by developers

The compiler doesn't have this concept

---

# They construct types

```scala
def gate[C[_]](thing: C[Int]): Unit = ...
```

Like a function

e.g. `C[Int]`

---

# Incomplete

Type constructors are not types themselves

Runtime values can't have them as types

```scala
// Fails
def gate[C[_]](thing: C[Int]): Unit = {
  val c: C = ??? // incomplete, has a hole
}
// C takes type parameters
//  val c: C = ???
//         ^

// Passes, the hole is filled
def gate[C[_]](thing: C[Int]): Unit = {
  val c: C[Double] = ???
}
```

---

# Jargon

"`Option` is a type constructor"

"`Option[Int]` is a type"

---

```
 _   _ _       _
| | | (_) __ _| |__   ___ _ __
| |_| | |/ _` | '_ \ / _ \ '__|
|  _  | | (_| | | | |  __/ |
|_| |_|_|\__, |_| |_|\___|_|
         |___/
 _  ___           _          _
| |/ (_)_ __   __| | ___  __| |
| ' /| | '_ \ / _` |/ _ \/ _` |
| . \| | | | | (_| |  __/ (_| |
|_|\_\_|_| |_|\__,_|\___|\__,_|

 _____
|_   _|   _ _ __   ___  ___
  | || | | | '_ \ / _ \/ __|
  | || |_| | |_) |  __/\__ \
  |_| \__, | .__/ \___||___/
      |___/|_|
```

---

# Higher Kinded Types

`C[_]` and `C[_, _]` are examples of "higher kinded types"

---

# Higher Kinded Types

Think of them like functions

e.g. `C[_]` is a function that takes a type and returns a type

e.g. `C[_, _]` is a function that takes two types and returns a type

---

# Common visual representations

> `C[_]` is a function that takes a type and returns a type

```
* ~> *
```

> `C[_, _]` is a function that takes two types and returns a type

```
(*, *) ~> *

* ~> * ~> *
```

---

# That's enough

Don't want to melt your brains too much

---

# Homework

Write `gate` functions which only allow:

(1) maps, where the key is a sequence of anything, the value is `Int`

(2) maps, where the key is a sequence of any `A`, and the value must be an option of that `A`

(3) a high kinded type of two type parameters, where the first parameter is free, but the second is `String`

For each one, play around with some examples to convince yourself it's working

---

# Solution 1

> (1) maps, where the key is a sequence of anything, the value is `Int`

```scala
def gate[A](thing: Map[Seq[A], Int]): Unit = {}

// Compiles
gate(Map(Seq("abc", "def") -> 1, Seq("ghi") -> 2))
gate(Map(Seq(true, false) -> 1, Seq(true) -> 2))

// Doesn't compile
gate(Map(Seq("abc", "def") -> true, Seq("ghi") -> false))
```

---

# Solution 2

> (2) maps, where the key is a sequence of any `A`, and the value must be an option of that `A`

```scala
def gate[A](thing: Map[Seq[A], Option[A]]): Unit = {}

// Compiles
gate(Map(Seq("abc", "def") -> Option("ghi")))
gate(Map(Seq(1, 2) -> Option(3)))

// Doesn't compile
gate(Map(Seq(1, 2) -> Option("String")))
```

---

# Solution 3

> (3) a high kinded type of two type parameters, where the first parameter is free, but the second is `String`

```scala
def gate[C[_, _], A](thing: C[A, String]): Unit = {}

// Compiles
gate(Map(3 -> "abc"))
gate(Map(true -> "abc"))

// Doesn't compile
gate(Map(true -> 3))
gate(Seq(1, 2, 3))
```

Oddly though, these ones compile:

```scala
gate(Seq("abc", "def"))
gate(Seq("abc", "def", "ghi"))
```

The type is `Seq[String]` which has a different shape to `C[A, String]` but it somehow works...

Extra homework: Figure out why before going to the next slide

---

# Seq

`Seq` inherits from lots of traits

```scala
trait Seq[+A]
  extends Iterable[A]
    with PartialFunction[Int, A]
    with SeqOps[A, Seq, Seq[A]]
    with IterableFactoryDefaults[A, Seq]
    with Equals {
```

---

# Seq

> `Seq` inherits from lots of traits

In particular `PartialFunction` has shape `(*, *) ~> *`

```scala
trait Seq[+A]
  extends Iterable[A]
    with PartialFunction[Int, A] // <-----------
    with SeqOps[A, Seq, Seq[A]]
    with IterableFactoryDefaults[A, Seq]
    with Equals {
```

---

# Viewing as PartialFunction

```scala
trait Seq[+A]
    ...
    with PartialFunction[Int, A] // <-----------

def gate[C[_, _], A](thing: C[A, String]): Unit = {}
```

The compiler sees `Seq[String]` as a `PartialFunction[Int, String]`

It has the right shape now for gate (with `A=Int`)

---

# Experiment

Swap the type parameters for `gate`

```scala
// Old
def gate[C[_, _], A](thing: C[A, String): Unit = {}

// New
def gate[C[_, _], A](thing: C[String, A]): Unit = {}
```

Now that `String` isn't the first type parameter,

it shouldn't allow passing a `Seq[String]`

`Seq[String]` is a `PartialFunction[Int, String]` which doesn't fit as `Int != String`

---

```
 ____                            _
|  _ \ ___  ___ __ _ _ __  _ __ (_)_ __   __ _
| |_) / _ \/ __/ _` | '_ \| '_ \| | '_ \ / _` |
|  _ <  __/ (_| (_| | |_) | |_) | | | | | (_| |
|_| \_\___|\___\__,_| .__/| .__/|_|_| |_|\__, |
                    |_|   |_|            |___/
```

---

# Higher Kinded Types

Give us a way to express more complex type requirements

e.g.

> containers of Int, where the container can be anything
>
> Option[Int], Seq[Int], Future[Int]

Traditional inheritance based approaches can't express this

---

# Type constructors

Informal terminology used by developers to mean a type with a hole in it

`C[_]`

---

# Clarifications

They are not regular types themselves,

but they construct them

---

# Next time

We'll use these tools to revisit the `Functor` and `Monad` type classes

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
