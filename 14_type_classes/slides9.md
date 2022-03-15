---
author: Rohan
date: 2022-05-12
title: Revisit Functor
---

```
 ____            _     _ _
|  _ \ _____   _(_)___(_) |_
| |_) / _ \ \ / / / __| | __|
|  _ <  __/\ V /| \__ \ | |_
|_| \_\___| \_/ |_|___/_|\__|

 _____                 _
|  ___|   _ _ __   ___| |_ ___  _ __
| |_ | | | | '_ \ / __| __/ _ \| '__|
|  _|| |_| | | | | (__| || (_) | |
|_|   \__,_|_| |_|\___|\__\___/|_|
```

---

# Today

Revisit Functor

Implement it formally using our type class pattern

Talk about functor laws

---

```
 ____
|  _ \ ___  ___ __ _ _ __
| |_) / _ \/ __/ _` | '_ \
|  _ <  __/ (_| (_| | |_) |
|_| \_\___|\___\__,_| .__/
                    |_|
```

What's functor again?

---

# Functor

Something "mappable"

Map is a "structure preserving transformation"

To the repl!

---

```
 _____                          _ _
|  ___|__  _ __ _ __ ___   __ _| (_)___  ___
| |_ / _ \| '__| '_ ` _ \ / _` | | / __|/ _ \
|  _| (_) | |  | | | | | | (_| | | \__ \  __/
|_|  \___/|_|  |_| |_| |_|\__,_|_|_|___/\___|

 _ _
(_) |_
| | __|
| | |_
|_|\__|

```

---

# Formalise it

Define a formal `Functor` type class

---

# Functor: Conceptual Recap

```
              map
F[A]       ------->  F[B]
A => B
```

e.g.

```
                   map
Option[String]  ------->  Option[Int]
String => Int
```

F = Option

A = String

B = Int

---

# What is F?

```
              map
F[A]       ------->  F[B]
A => B
```

`F` is not a simple type (like `Int`)

F is a...

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Type constructor

```
              map
F[A]       ------->  F[B]
A => B
```

F is a type constructor (e.g. `Option`, `Seq`)

---

# Code it up

```
              map
F[A]       ------->  F[B]
A => B
```

To the repl!

---

# Summary

```scala
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object OptionFunctor extends Functor[Option] {
  def map[A, B](optionA: Option[A])(f: A => B): Option[B] = optionA match {
    case None => None
    case Some(a) => Some(f(a))
  }
}

OptionFunctor.map(Option(3))(_ * 2)
// Some(value = 6)

// For None, we help the compiler to stop it inferring Option[Nothing]
OptionFunctor.map(None: Option[Int])(_ * 2)
// None
OptionFunctor.map[Int, Int](None)(_ * 2)
// None
```

---

# Triplet example

Let's prove it's a functor

To the repl!

---

# Summary

```scala
case class Triplet[A](_1: A, _2: A, _3: A)

object TripletFunctor extends Functor[Triplet] {
  def map[A, B](tripletA: Triplet[A])(f: A => B): Triplet[B] = {
    val Triplet(val1, val2, val3) = tripletA
    Triplet(f(val1), f(val2), f(val3))
  }
}

val stringTriplet = Triplet("a", "bcd", "efghi")

TripletFunctor.map(stringTriplet)(_.length)
// Triplet(_1 = 1, _2 = 3, _3 = 5)
```

---

```
 ____              _
/ ___| _   _ _ __ | |_ __ ___  __
\___ \| | | | '_ \| __/ _` \ \/ /
 ___) | |_| | | | | || (_| |>  <
|____/ \__, |_| |_|\__\__,_/_/\_\
       |___/
```

---

# Ugly

This is a bit ugly:

```scala
TripletFunctor.map(stringTriplet)(_.length)
```

---

# Alternative

> This is a bit ugly:

```scala
TripletFunctor.map(stringTriplet)(_.length)
```

Would be nice if we could do:

```scala
stringTriplet.map(_.length)
```

(like in the standard library)

---

# Rearranged

Both are expressions of:

```
              map
F[A]       ------->  F[B]
A => B
```

```scala
TripletFunctor.map(stringTriplet)(_.length)
stringTriplet.map(_.length)
```

(2 inputs, 1 output)

---

# Double mapping

Imagine we mapped to length, then mapped to add 1

```scala
// Nice left-to-right flow
stringTriplet.map(_.length).map(_ + 1)

// Icky
TripletFunctor.map(TripletFunctor.map(stringTriplet)(_.length))(_ + 1)
```

---

# Hack?

We _could_ hack a `map` function into our `Triplet`:

```scala
case class Triplet[A](...) {
  def map[B](f: A => B): Triplet[B] = TripletFunctor.map(this)(f)
}
```

---

# Deja vu

> We _could_ hack a `map` function into our `Triplet`:

```scala
case class Triplet[A](...) {
  def map[B](f: A => B): Triplet[B] = TripletFunctor.map(this)(f)
}
```

Feel like we've had this discussion before...

What would be the downside of this approach?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Hacky

> What would be the downside of this approach?

But we'd have to do that for every functor

And what about types we don't control?

---

# Deja vu

This is the same scenario as `|+|` for monoid

```scala
monday |+| tuesday |+| wednesday
```

Do you remember how we solved it?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Implicit extension

> This is the same scenario as `|+|` for monoid
>
> Do you remember how we solved it?

An implicit extension class

Takes the type class instance as implicit evidence

To the repl!

---

# Summary

```scala
implicit class FunctorOps[F[_], A](fa: F[A])(implicit ev: Functor[F]) {
  def map[B](f: A => B): F[B] = ev.map(fa)(f)
}

// Make type class instance available in implicit scope
implicit object TripletFunctor extends Functor[Triplet] {
  ...
}

val triplet = Triplet("abc", "defg", "hijkl")

triplet.map(_.length)
// Triplet(_1 = 3, _2 = 4, _3 = 5)

// Is really
(new FunctorOps(triplet)(TripletFunctor)).map(_.length)
```

---

```
 _         _   _
| |    ___| |_( )___
| |   / _ \ __|// __|
| |__|  __/ |_  \__ \
|_____\___|\__| |___/

 _
| |__   ___
| '_ \ / _ \
| |_) |  __/
|_.__/ \___|

                    _ _     _
 ___  ___ _ __  ___(_) |__ | | ___
/ __|/ _ \ '_ \/ __| | '_ \| |/ _ \
\__ \  __/ | | \__ \ | |_) | |  __/
|___/\___|_| |_|___/_|_.__/|_|\___|

```

---

# Laws

Like with `Monoid`,

just because you made a `Functor` instance that compiles...

---

# Laws

Like with `Monoid`,

just because you made a `Functor` instance that compiles...

doesn't mean it's a _true_ `Functor`

We are law abiding citizens, this isn't the wild west

---

# Demo

We'll implement an alternative "bad" `map` for our `Triplet` and `Option`

To the repl!

---

# Summary

```scala
implicit object TripletFunctor extends Functor[Triplet] {
  def map[A, B](triplet: Triplet[A])(f: A => B): Triplet[B] = {
    val Triplet(val1, val2, val3) = triplet

    Triplet(f(val2), f(val1) f(val3))
    //        ^^ swapped ^^
  }
}

Triplet("a", "bcdef", "").map(_.length)
// Pair(5, 1, 0)

object OptionFunctor extends Functor[Option] {
  def map[A, B](optionA: Option[A])(f: A => B): Option[B] = optionA match {
    case None => None
    case Some(a) => None // <--- ignore the data
  }
}

OptionFunctor.map(Option(3))(_ * 2)
// None
```

---

# Gut feeling

These compile...

But intuitively we know these are bad implementations

---

# Formalise

> intuitively we know these are bad implementations

We need a formal way to define what is good and bad

---

# Functor Laws

Easiest to show via a demo

To the repl!

---

# Summary: Identity law

> mapping the identity function shouldn't change anything

```scala
// Lawful
Seq(1, 2, 3).map(i => i)
// Seq(1, 2, 3)

Triplet("a", "b", "c").map(s => s)
// Triplet("b", "a", "c")
```

---

# Summary: Composition law

> two individual maps should produce the same thing as one combined map

```scala
// Lawful
Seq(1, 2, 3).map(_ + 1).map(_ * 2)
Seq(1, 2, 3).map(i => (i + 1) * 2)
// both give Seq(4, 6, 8)

// Unlawful
Triplet(1, 2, 3).map(_ + 1).map(_ * 2) // Triplet(4, 6, 8)
Triplet(1, 2, 3).map(i => (i + 1) * 2) // Triplet(6, 4, 8)
// Different!
// Each `map` swaps them around
// Two maps swaps it twice, one map swaps it just once
```

---

# That's enough for today!

---

# Homework 1

Define the `Functor` type class yourself

Implement the type class instance for `Option`

(peek at the slides if you can't remember)

---

# Homework 2

Suppose we had a functor implementation for `Seq` which lost the last element:

```scala
Seq(1, 2, 3, 4).map(_ * 2)
// Seq(2, 4, 6)
// 8 is MIA
```

Use the functor laws to show this isn't lawful

---

# Homework 2 Solution

Both functor laws would flag this:

## Identity law

```scala
Seq(1, 2, 3, 4).map(i => i)
// Seq(1, 2, 3)
```

Mapping the identity function changed the value

## Composition

One map would drop 1 element

Two maps would drop 2 elements

```scala
Seq(a, b, c, d).map(f).map(g) // length 2
Seq(a, b, c, d).map(f compose g) // length 3
// Different!
```

---

# Homework 3

Write a functor instance for `List`

Bonus Points: Make sure it's stack safe!

---

# Homework 3 Hint

We did this back when we built our own `List`

Back then we just hacked `map` directly into the `List`

You'd be taking that logic and rearranging it to fit the type class pattern

A naive recursive approach will be the simplest, but won't be stack safe

We rewrote it to be safe in our tail recursion lessons, but it led to the list getting reversed...

---

# Homework 3 Solution

```scala
import scala.annotation.tailrec

object ListFunctor extends Functor[List] {
  def map[A, B](list: List[A])(f: A => B): List[B] = {
    @tailrec
    def mapTail(list: List[A], acc: List[B]): List[B] = list match {
      case Nil => acc
      case head :: tail => mapTail(tail, f(head) :: acc)
    }

    mapTail(list, Nil).reverse
  }
}

ListFunctor.map(List(1, 2, 3))(_ * 2)
// List(2, 4, 6)
```

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

# Functor Type Class

The set of all type constructors that are map-able

---

# Today

We formalised `Functor` according to our type class pattern

```scala
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}
```

---

# Type class instances

We defined instances for `Option` and `Triplet`

Effectively we "proved" these types belong to this type class

---

# Functor laws

Just because an instance compiles,

doesn't mean it's a true instance

---

# Next time

The same formalising of `Monad`

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
