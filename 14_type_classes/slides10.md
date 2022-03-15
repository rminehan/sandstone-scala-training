---
author: Rohan
date: 2022-05-16
title: Revisit Monad
---

```
 ____            _     _ _
|  _ \ _____   _(_)___(_) |_
| |_) / _ \ \ / / / __| | __|
|  _ <  __/\ V /| \__ \ | |_
|_| \_\___| \_/ |_|___/_|\__|

 __  __                       _
|  \/  | ___  _ __   __ _  __| |
| |\/| |/ _ \| '_ \ / _` |/ _` |
| |  | | (_) | | | | (_| | (_| |
|_|  |_|\___/|_| |_|\__,_|\__,_|

```

---

# Last time...

We revisited `Functor`

Gave it a true "type class" representation

---

# Looked like

```scala
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object TripletFunctor extends Functor[Triplet] {
  def map[A, B](tripletA: Triplet[A])(f: A => B): Triplet[B] = {
    val Triplet(val1, val2, val3) = tripletA
    Triplet(f(val1), f(val2), f(val3))
  }
}

val stringTriplet = Triplet("a", "bcd", "efghi")

TripletFunctor.map(stringTriplet)(_.length)

// With some syntax
stringTriplet.map(_.length)
```

---

# Laws

Saw that a true functor instance needs to satisfy some laws

---

# Today

Do the same thing for `Monad`

Then revisit `for` comprehensions

---

# Agenda

- recap monad


- essence of a monad


- type class


- syntax


- laws


- `for`

---

```
 __  __                       _
|  \/  | ___  _ __   __ _  __| |
| |\/| |/ _ \| '_ \ / _` |/ _` |
| |  | | (_) | | | | (_| | (_| |
|_|  |_|\___/|_| |_|\__,_|\__,_|

 ____
|  _ \ ___  ___ __ _ _ __
| |_) / _ \/ __/ _` | '_ \
|  _ <  __/ (_| (_| | |_) |
|_| \_\___|\___\__,_| .__/
                    |_|
```

---

# Firstly

Monad != Monoid

- monad = flatMap


- monoid = combine

Unfortunate that they sound the same

(apologies if I mix them up during the talk)

---

# Elevator pitch

> Q: What's a monad?
>
> A: Something with "flatMap"

---

# Examples

If you had your laptop you'd show them `map` vs `flatMap`

To the repl!

---

# `map` vs `flatMap`

```scala
Seq(1, 10, 1000).map(i => Seq(i, i + 1))
// Seq(Seq(1, 2), Seq(10, 11), Seq(1000, 1001))

Seq(1, 10, 1000).flatMap(i => Seq(i, i + 1))
// Seq(1, 2, 10, 11, 1000, 1001)
```

> see, it maps then flattens!

---

# Give a few examples

- `Seq`


- `Option`


- `Future`

> they all implement `flatMap` in a way that makes sense to them

---

# Close enough

> something with "flatMap"

A good simple practical approximation

Helpful for beginners

---

# Approximation

Missing some details and nuance

Good starting point, but not the full picture

---

# Missing details

- lift


- the "essence" of monad


- monad laws

---

```
 _     _  __ _
| |   (_)/ _| |_
| |   | | |_| __|
| |___| |  _| |_
|_____|_|_|  \__|

```

---

# Monoid conceptually

Convention is to use `M`

```
                 flatMap
M[A]        -------------->  M[B]
A => M[B]
```

e.g.

```
                 flatMap
Seq[String] -------------->  Seq[Int]
String => Seq[Int]


M = Seq
A = String
B = Int
```

---

# Lift?

Lift is missing from this picture

```
                 flatMap
M[A]        -------------->  M[B]
A => M[B]


???
```

---

# Lift

Lift is missing from this picture

```
                 flatMap
M[A]        -------------->  M[B]
A => M[B]

                  lift
A           -------------->  M[A]
```

It just "lifts" a simple value up into the monad

---

# Lift examples

To the repl!

---

# Summary

Each monad has its own concept of lifting:

```scala
// Simple value
1

// Lifted into a sequence:
Seq(1) // Sequence of 1 element

// Lifted into an Option
Some(1) // Some wrapped around the data

// Lifted into a Future
Future.successful(1) // Successful future that yields 1
```

---

# Aside: many names!

"Lift" is sometimes called "pure" or "unit"

I'm using "lift" to avoid confusion with scala's "Unit"

It's also a more intuitive name

---

# lift summary

The "Tasmania" of monad

Small extra bit that's easy to forget

Often gets left off the map

---

```
 _____
| ____|___ ___  ___ _ __   ___ ___
|  _| / __/ __|/ _ \ '_ \ / __/ _ \
| |___\__ \__ \  __/ | | | (_|  __/
|_____|___/___/\___|_| |_|\___\___|

        __
  ___  / _|
 / _ \| |_
| (_) |  _|
 \___/|_|

                                 _
 _ __ ___   ___  _ __   __ _  __| |
| '_ ` _ \ / _ \| '_ \ / _` |/ _` |
| | | | | | (_) | | | | (_| | (_| |
|_| |_| |_|\___/|_| |_|\__,_|\__,_|

```

Thinking more abstractly

What is a monadic computation?

---

# Dependent computations

---

# Example

```scala
for {
  user1 <- getUserById(user1Id)
  user2 <- getUserById(user2Id)
  ...
} yield ...
```

Like gotcha 1 from our `Future` gotchas

This uses `flatMap` but it's not "monadic"

Computations are independent, should parallelise

---

# Monadic Example

```scala
for {
  user1 <- getUserById(user1Id)
  user2 <- getUserById(user1.nextOfKinId)
  ...
} yield ...
```

The later doesn't make sense without the first

---

# So...

Using `flatMap` but it's not monadic

---

# Short circuiting

---

# Short circuiting monads

Dependent computations usually require a "short circuiting" concept

---

# Example

```scala
for {
  user1 <- getUserById(user1Id)            // Step 1
  user2 <- getUserById(user1.nextOfKinId)  // Step 2
  user3 <- getUserById(user2.nextOfKinId)  // Step 3
  user4 <- getUserById(user3.nextOfKinId)  // Step 4
  ...
} yield ...
```

Would you be able to do step 2 if step 1 failed?

You have to short circuit

---

# Demo time

Show examples of short circuiting

---

# Summary - Option

```scala
for {
  a <- Option(1)
  _ = println("Will print")
  b <- Option(2)
  c <- None
  _ = println("Won't print")
  ...
} yield ...
```

Short circuits at the first `None`

---

# Summary - Seq

```scala
for {
  i <- 0 until 10
  j <- i until 10
  _ = println("Will print")
  k <- Seq.empty[Int]
  _ = println("Won't print")
  ...
} yield ...
```

Short circuits at the first empty sequence

---

# Empty state

If a monad has a "empty" state

then it must have short circuiting behaviour

```scala
for {
  i <- ... // might return "empty" state
  j <- doStuff(i)
}
```

There would be no data to bind `i` to

---

# Empty examples

- Seq: empty sequence


- Option: None


- Future: failed future

---

# Note

Monads don't _have_ to have a "short circuiting" concept

But many common ones do

---

# Hierarchy

---

# Hierarchy

```
      Functor
        / \
         |
         |
     Applicative
        / \
         |
         |
       Monad
```

Arrow means "is a"

---

# Hierarchy

```
      Functor
        / \
         |
         |
     Applicative
        / \
         |
         |
       Monad
```

- every applicative is a functor


- every monad is an applicative


- every monad is a functor (transitively)

(but not vice versa)

---

# Hierarchy

```
      Functor
        / \
         |
         |
     Applicative
        / \
         |
         |
       Monad
```

monad is strong

functor is weak

applicative is in the middle

---

# Example

We saw that `Triplet` is a functor

Is it a monad?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# No

> Is it a monad?

Think about what it would look like:

```scala
val stringTriplet = Triplet("a", "bcd", "efghi")

stringTriplet.flatMap(s => Triplet(s, s, s))
```

Each string is expanded into 3 strings

You have 9 strings in total now

How do you squeeze that into a triplet?

---

# Compare with Seq

Analogous `Seq` example would be:

```scala
val seq = Seq("a", "bcd", "efghi")

seq.flatMap(s => Seq(s, s, s))
// Seq("a", "a", "a", "bcd", "bcd", "bcd", "efghi", "efghi", "efghi")
```

Seq is able to handle this because it doesn't have a fixed length

---

# Back to Hierarchy

```
      Functor (Triplet)
        / \
         |
         |
     Applicative
        / \
         |
         |
       Monad
```

Triplet is an example of a Functor that's not a monad

---

```
 _____
|_   _|   _ _ __   ___
  | || | | | '_ \ / _ \
  | || |_| | |_) |  __/
  |_| \__, | .__/ \___|
      |___/|_|
      _
  ___| | __ _ ___ ___
 / __| |/ _` / __/ __|
| (__| | (_| \__ \__ \
 \___|_|\__,_|___/___/

     _       __ _       _ _   _
  __| | ___ / _(_)_ __ (_) |_(_) ___  _ __
 / _` |/ _ \ |_| | '_ \| | __| |/ _ \| '_ \
| (_| |  __/  _| | | | | | |_| | (_) | | | |
 \__,_|\___|_| |_|_| |_|_|\__|_|\___/|_| |_|

```

---

# Type class definition

Want something like:

```scala
trait Monad[M[_]] {
  ...
}
```

(convention to use `M` for "monad")

---

# Recall Functor

```
              map
F[A]       ------->  F[B]
A => B
```

```scala
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}
```

Fairly mechanical translation from concept to code

---

# Monad

```
                 flatMap
M[A]        -------------->  M[B]
A => M[B]

                  lift
A           -------------->  M[A]
```

```scala
trait Monad[M[_]] {
  def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]

  def lift[A](a: A): M[A]
}
```

---

# Demo time

Let's define the type class in the repl

Then make a type class instance for `Option`

i.e. prove that `Option` is a monad

---

# Summary

```scala
object OptionMonad extends Monad[Option] {
  def flatMap[A, B](optA: Option[A])(f: A => Option[B]): Option[B] = optA match {
    case None => None
    case Some(a) => f(a)
  }

  def lift[A](a: A): Option[A] = Some(a)
}

OptionMonad.lift(3)
// Some(value = 3)

OptionMonad.flatMap(Option(3))(i => Option(i + 1))
// Some(value = 4)

OptionMonad.flatMap[Int, Int](None)(i => Option(i + 1))
// None

OptionMonad.flatMap[Int, Int](Option(3))(i => None)
// None
```

Suggested homework: define a type class instance for `List`

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

# Syntax

A bit ugly:

```scala
OptionMonad.flatMap(a)(f)
```

We'd like:

```scala
a.flatMap(f)
```

---

# Implicits

Achieve this with our usual `implicit` shenanigans

To the repl!

---

# Summary

```scala
implicit class MonadOps[M[_], A](ma: M[A])(implicit ev: Monad[M]) {
  def flatMap[B](f: A => M[B]): M[B] = ev.flatMap(ma)(f)
}

implicit object OptionMonad extends Monad[Option] {
  ... // like before
}

Option(3).flatMap(...)
```

---

```
 _
| |    __ ___      _____
| |   / _` \ \ /\ / / __|
| |__| (_| |\ V  V /\__ \
|_____\__,_| \_/\_/ |___/

```

---

# Monad laws

Just mentioning for completeness sake

(They don't come up much)

---

# One example

Flat-mapping lift over something gives back the same thing:

```scala
Some(3).flatMap(lift) // Some(3)

List(1, 2, 3).flatMap(lift) // List(1, 2, 3)
```

---

# Other laws

There are some other laws but they're a bit confusing

Practically they don't matter much

---

```
  __
 / _| ___  _ __
| |_ / _ \| '__|
|  _| (_) | |
|_|  \___/|_|

```

---

# Recap

`for` comprehensions are "syntactic sugar"

---

# Example

To intellij!

---

# Functor, Monad?

Is it that the scala compiler has a concept of functor and monad?

---

# Experiment

Let's try this in a fresh repl:

```scala
case class Triplet[A](_1: A, _2: A, _3: A)

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

implicit object TripletFunctor extends Functor[Triplet] {
  def map[A, B](fa: Triplet[A])(f: A => B): Triplet[B] = {
    val Triplet(val1, val2, val3) = fa
    Triplet(f(val2), f(val1), f(val3))
  }
}

for {
  i <- Triplet("a", "bcd", "edfgh")
} yield i.toUpperCase
```

---

# Result

```scala
for {
  i <- Triplet("a", "bcd", "edfgh")
} yield i.toUpperCase
// Error: value map is not a member of Triplet[String]
```

---

# What happened?

The compiler mechanically translated:

```scala
for {
  i <- Triplet("a", "bcd", "edfgh")
} yield i.toUpperCase
```

into:

```scala
Triplet("a", "bcd", "edfgh").map(i => i.toUpperCase)
```

then realised `Triplet[String]` doesn't have a `map` function:

```
Error: value map is not a member of Triplet[String]
```

---

# Back to our question

> Is it that the scala compiler has a concept of functor and monad?

No

It just mechanically translates `for` code into map/flatMap methods

---

# Back to our question

> It just mechanically translates `for` code into map/flatMap methods

If your type has those methods then it will compile

---

# So

To be able to use your type with `for`,

it needs to support `.map` and/or `.flatMap` syntax

---

# Fixing our example

Back to the repl

---

# Summary

Just need to make sure `Triplet.map` compiles:

```scala
implicit class FunctorOps[F[_], A](fa: F[A])(implicit ev: Functor[F]) {
  def map[B](f: A => B): F[B] = ev.map(fa)(f)
}

for {
  i <- Triplet("a", "bcd", "edfgh")
} yield i.toUpperCase

Triplet("a", "bcd", "edfgh").map(i => i.toUpperCase)
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

# Formalise monad concept

Defined the type class and an instance

---

# Lift

The "Tasmania" of monad

Easy to forget, but it's still there

---

# Helpful concepts

- dependent computations


- many monad are "short circuiting" in nature

---

# Laws

Like functor, it has some laws

Not important that you memorise them

---

# for

A mechanical translation

Requires your type to have `.map`/`.flatMap` defined

---

# Homework

Prove that `List` is a monad

Make sure it's stack safe!

---

# Hint

> Prove that `List` is a monad

Really means:

> Create a monad type class instance for `List`

```scala
object ListMonad extends Monad[List] {
  ...
}
```

---

# Hint

Do the `map` from previous lessons first as it's a little simpler

Then `flatMap` just requires some small alterations

---

# Solution

```scala
object ListMonad extends Monad[List] {
  def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] = {

    def flatMapTail(list: List[A], acc: List[B]): List[B] = list match {
      case Nil => acc
      case head :: tail => flatMapTail(tail, f(head) ++ acc)
    }

    flatMapTail(list, Nil).reverse
  }

  def lift[A](a: A): List[A] = List(a)
}

ListMonad.flatMap(List(1, 2, 3, 4))(i => List(i, i, i))
// List(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4)

// Prove it's stack safe
val bigList = List.fill(30_000)(1)

ListMonad.flatMap(bigList)(i => List(i, i, i))
// should produce a list of 90K 1's without stack overflow
```

---

# Next time

Cats!

```
 /\_/\
( o.o )
 > ^ <
```

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
