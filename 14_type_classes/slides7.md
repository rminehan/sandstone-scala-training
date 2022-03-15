---
author: Rohan
date: 2022-04-28
title: Type Classes
---

```
 _____
|_   _|   _ _ __   ___
  | || | | | '_ \ / _ \
  | || |_| | |_) |  __/
  |_| \__, | .__/ \___|
      |___/|_|
  ____ _
 / ___| | __ _ ___ ___  ___  ___
| |   | |/ _` / __/ __|/ _ \/ __|
| |___| | (_| \__ \__ \  __/\__ \
 \____|_|\__,_|___/___/\___||___/
```

---

# Type classes

A new way of thinking about design/abstraction

Very different to traditional approaches

---

# Type classes

> A new way of thinking about abstraction
>
> Very different to traditional classes

Need to undo the java brainwashing,

apply some scala/FP brainwashing

---

# Today

- the problem with traditional design using classes/inheritance


- type classes and adjectives


- show and monoid type classes

---

```
 _____              _ _ _   _                   _
|_   _| __ __ _  __| (_) |_(_) ___  _ __   __ _| |
  | || '__/ _` |/ _` | | __| |/ _ \| '_ \ / _` | |
  | || | | (_| | (_| | | |_| | (_) | | | | (_| | |
  |_||_|  \__,_|\__,_|_|\__|_|\___/|_| |_|\__,_|_|

    _                                     _
   / \   _ __  _ __  _ __ ___   __ _  ___| |__
  / _ \ | '_ \| '_ \| '__/ _ \ / _` |/ __| '_ \
 / ___ \| |_) | |_) | | | (_) | (_| | (__| | | |
/_/   \_\ .__/| .__/|_|  \___/ \__,_|\___|_| |_|
        |_|   |_|
```

---

# The dominant paradigm

Most major languages share a similar style of using class inheritance

(Java, C++, C#, Python...)

It just becomes the norm

---

# Typical example

"is" relationships

```scala
class Animal

// Dog "is" an Animal
class Dog extends Animal
```

---

# Inheritance

We used class inheritance to represent "is"

```scala
class Animal

// Dog "is" an Animal
class Dog extends Animal
```

---

# Familiar

> We used class inheritance to represent "is"

This probably feels very familiar and is considered normal

---

# Aside

I'm using "inheritance" in a lazy way to mean both

- class inheritance


- interface implementation

---

# Problems

> We used class inheritance to represent "is"

But there's actually a lot of problems with using class inheritance...

Deep down inside you've felt this too, even if you won't admit it to yourself...

---

# Problems

- multiple "is"'s


- code you don't own


- static logic

---

# Multiple "is"'s

---

# Multiple "is"'s

What if your class is two things simultaneously?

```scala
class CakeEater
class Animal

class Dog extends CakeEater with Animal ?
```

---

# Multiple "is"'s

> What if your class is two things simultaneously?

```scala
class CakeEater
class Animal

// Won't compile
class Dog extends CakeEater with Animal
```

Many OO languages don't support multiple inheritance (and with good reason)

---

# Multiple "is"'s

Sometimes the same type can implement a behaviour two different ways

e.g. `Set` is a monoid via intersection _and_ union

---

# Code you don't own

---

# Example

James created a `Functor` abstraction for a library he built

```scala
trait Functor {
  ...
}
```

Feroz made a `Matrix` library that has a concept of mapping

```scala
class Matrix {
  ...
}
```

---

# Realisation

> James created a `Functor` abstraction for a library he built
>
> Feroz made a `Matrix` class that has a concept of mapping

James realises that Feroz' `Matrix` is actually a `Functor`

---

# Code you don't own

> James realises that Feroz' `Matrix` is actually a `Functor`

But James can't do this:

```diff
- class Matrix {
+ class Matrix extends Functor {
```

It's not his code!

---

# Wrapper

James would probably make some kind of wrapper to adapt the logic

---

# Inconsistent

Classes that James wrote himself directly extend `Functor`

Other classes need this wrapper

---

# Example closer to home

The java array is a conceptually a scala `Seq`

But we can't go into the jdk source and change it:

```diff
- class Array<T>
+ class Array<T> extends Seq<T>
```

---

# Primitives too

How do you express that a primitive has a behaviour?

e.g. `Int` is a monoid?

Even if you could control the source, you can't use inheritance

---

# Baking analogy

Inheritance is like baking a behaviour into your type

```scala
class ChocolateCake extends Cake
```

(baked into the source code itself)

---

# Re-bake?

> Inheritance is like baking a behaviour into your type

If someone else gives you a cake, you can't bake new behaviour into it

The cake is already baked!

---

# Adding behaviour

If you control the cake, you can rebake it with new ingredients

But now you've made a _different_ cake

```scala
// v1
class ChocolateCake extends Cake

// v2
class ChocolateCake extends Cake with Chocolatey
```

---

# Adding behaviour

> If you control the cake, you can rebake it with new ingredients
>
> But now you've made a _different_ cake

From a source/binary compatibility perspective,

this is a _different_ type and may cause binary compatibility

---

# Static logic

---

# Think about monoid

Monoid has two concepts:

- identity element ("static")


- combine logic

---

# Picture using traditional approach

Static concepts feel weird here:

```scala
trait Monoid[A] {
  def identity: A

  def combine(other: A): A
}

case class Stats(...) extends Monoid[Stats] {
  def identity: Stats = Stats(0, 0, 0) // hmmm...

  def combine(other: Stats): Stats = ...
}
```

To get the identity, I need to construct a stats object:

```scala
(new Stats(1, 2, 3)).identity
```

---

# Static concepts

Static/class level concepts don't gel well with an inheritance based approach

---

# So...

Traditional approaches often don't work well

---

# Fresh eyes

Try to overcome years of indoctrination,

look at class inheritance with fresh eyes

---

# Fresh eyes

> Try to overcome years of indoctrination,
>
> look at class inheritance with fresh eyes

It often fails or leads to adhoc designs

Not a very strong approach

---

# Why use it?

> Not a very strong approach

---

# Why use it?

> Not a very strong approach

Syntactically easy

```scala
class Animal

class Dog extends Animal
```

---

# Syntactically easy

```scala
class Animal

class Dog extends Animal
```

Like a siren song, it lures you in with how simple it looks

Then it eats you...

(like python)

---

# Path of least resistance

Sometimes languages make it easy to do the wrong thing,

(and hard to do the right thing)

---

# Another tangential example

Maybe you're heard the saying:

> Prefer composition to inheritance

---

# Another tangential example

Maybe you're heard the saying:

> Prefer composition to inheritance

But most languages make it much easier to use inheritance:

- Inheritance has first class syntactic support


- Composition doesn't have any support in most languages (groovy?)

---

# Sad reality

There are alternatives which are _architecturally stronger_, but _syntactically harder_

---

# Sad reality

> There are alternatives which are _architecturally stronger_, but _syntactically harder_

Developers tend to use solutions that are syntactically simpler

---

# Summary

Traditional inheritance based approaches have issues

Work okay for simple cases, but start to fail as complexity grows

---

# Hmmm...

If only there was a better way...

---

```
 _____
|_   _|   _ _ __   ___
  | || | | | '_ \ / _ \
  | || |_| | |_) |  __/
  |_| \__, | .__/ \___|
      |___/|_|
  ____ _
 / ___| | __ _ ___ ___  ___  ___
| |   | |/ _` / __/ __|/ _ \/ __|
| |___| | (_| \__ \__ \  __/\__ \
 \____|_|\__,_|___/___/\___||___/
```

---

# Type classes

My rough definition:

> A set of types that satisfy some property/adjective

---

# Examples

## Monoid

> "Monoid is the set of all types that are combine-able"

```
Monoid = { Int, String, Stats, Option, ... }
```

## Functor

> "Functor is the set of all types that are map-able"

```
Functor = { Seq, Future, Tree, Matrix, ... }
```

---

# Informal concept

This concept exists in the minds of developers

The scala compiler doesn't have a concept of a "type class"

---

# Haskell

> The scala compiler doesn't have a concept of a "type class"

In Haskell it's a first class concept

(don't think they even have traditional class inheritance)

---

# Scala

Type classes are an informal concept

There's patterns we use to emulate type classes

---

# Scala

> Type classes are an informal concept
>
> There's patterns we use to emulate type classes

These patterns need more code than traditional inheritance,

but they are architecturally stronger

---

# Example

"Show" type class

---

# Show

> Show is the type class of all types that have a human readable "print" representation

ie. show-y types

---

# Show vs toString

You say:

> Don't all types have toString? Aren't they all show-y?

---

# Show vs toString

You say:

> Don't all types have toString? Aren't they all show-y?

Technically yes...

But some just use the default implementation

Abiguous: Sometimes the string is intended for humans, and sometimes for computers

---

# Show

Unambiguous

"Opt-in"

Intended for consumption by people

---

# Let's build it!

To the repl!

---

# Summary

```scala
trait Show[A] {
  def show(a: A): String
}

case class Stats(fullMatches: Int, partialMatches: Int, noMatches: Int)

implicit object StatsShow extends Show[Stats] {
  def show(stats: Stats): String = {
    val Stats(full, partial, no) = stats
    val total = full + partial + no
    s"Stats(fullMatches = $full, partialMatches = $partial, noMatches = $no, total = $total)"
  }
}

val mondayStats = Stats(0, 3, 4)

StatsShow.show(mondayStats)
// "Stats(fullMatches = 0, partialMatches = 3, noMatches = 4, total = 7)"

log(mondayStats, "Stats for Monday")
// * Stats for Monday [Stats(fullMatches = 0, partialMatches = 3, noMatches = 4, total = 7)]
```

---

# Show

Unambiguous

"Opt-in" - will only work with types that have a "show" concept designed for humans

---

# Compare with Monoid

```scala
// Type class definition
trait Monoid[A] {
  val identity: A
  def combine(left: A, right: A): A
}

// Use case for the type class
def fold[A](seq: Seq[A])(implicit ev: Monoid[A]): A = {
  var acc = ev.identity

  for (i <- seq)
    acc = ev.combine(acc, i)

  acc
}

// Type class instance
object IntAddition extends Monoid[Int] {
  val identity: Int = 0
  def combine(left: Int, right: Int): Int = left + right
}
```

---

# The general pattern

- define a "type class" to represent your behaviour


- define use cases for that type class (they'll require evidence)


- define "type class instances" (and make available in implicit scope)

---

# Comparing approaches

What would "show" look like if we used a traditional approach?

---

# Traditional approach

```scala
trait Show {
  def show: String
}

case class Stats(...) extends Show {
  def show: String = ...
}

def log(show: Show, message: String): Unit = ...
```

---

# Compare them

## Traditional approach

```scala
trait Show {
  def show: String
}

case class Stats(...) extends Show {
  def show: String = ...
}

def log(show: Show, message: String): Unit = ...
```

## Type class approach

```scala
trait Show[A] {
  def show(a: A): String
}

case class Stats(...)

implicit object StatsShow extends Show[Stats] {
  def show(stats: Stats): String = ...
}

def log[A](a: A, message: String)(implicit ev: Show[A]): Unit = ...
```

---

# Reaction

You might say:

> Seems like the traditional approach is simpler,
>
> there's less stuff and less lines of code,
>
> it must be better

---

# No!

> Seems like the traditional approach is simpler,
>
> there's less stuff and less lines of code,
>
> it must be better

Don't listen to their siren song!

---

# The sad reality

Type classes don't have native language support

There is more "stuff" you have to write

---

# The sad reality

> Type classes don't have native language support
>
> There is more "stuff" you have to write

But don't confuse weak syntax with weak design

---

# Recapping

```scala
trait Show {
  def show: String
}

case class Stats(...) extends Show {
  def show: String = ...
}
```

How would you apply this pattern to a primitive or a type you don't control?

How would you define two show implementations for the same type?

Adding "show" in later yields a different type potentially creating binary comp.

---

# Type class approach?

> How would you apply this pattern to a primitive or a type you don't control?

Very simple, use the exact same approach

> How would you define two show implementations for the same type?

Just create two separate type class instances

> Adding "show" in later yields a different type potentially creating binary comp.

With type classes you add a behaviour without modifying the underlying class

---

# Consistency

You can use the same consistent approach for:

- classes you control


- classes you don't control


- primitives, arrays and other special things

---

# Extra Boilerplate

In reality it's not too much

If you apply the pattern consistently you get used to it

---

# A natural question

> I get it now,

you say

> so should I always use type classes?

---

# A natural question

> so should I always use type classes?

Type classes make sense for very universal concepts:

- functors


- monoids


- show

---

# A natural question

> so should I always use type classes?

If you're defining a very specific abstraction

e.g. "document analyser"

then it makes less sense

---

# Narrow abstractions

> If you're defining a very specific abstraction, then it makes less sense

You're going to control all of the instances

Regular class inheritance will probably be fine (and is a bit simpler)

---

# That's enough brainwashing for one day

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

# Traditional approaches

Usually rely on inheritance and are quite limited

---

# Type classes

A much more flexible, powerful, consistent tool

But it is more complex

---

# Type class lingo

- the "type class" (usually manifests as a trait)


- a "type class instance" (a proof that a particular type belongs in that type class)

---

# Examples of type classes

- monoid


- show


- functor


- monad

(many more!)

---

# The type class pattern

Scala doesn't have first class support for type classes

We implement them informally using other tools like traits and implicits

---

# When to use type classes

For more universal abstract concepts

Goes hand in hand with category theory

---

# Next time

Reimplement functor and monad using the type class pattern

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
