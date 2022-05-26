---
author: Rohan
date: 2022-05-30
title: Applicative and mapN
---

```
    _                _ _           _   _
   / \   _ __  _ __ | (_) ___ __ _| |_(_)_   _____
  / _ \ | '_ \| '_ \| | |/ __/ _` | __| \ \ / / _ \
 / ___ \| |_) | |_) | | | (_| (_| | |_| |\ V /  __/
/_/   \_\ .__/| .__/|_|_|\___\__,_|\__|_| \_/ \___|
        |_|   |_|
                 _
  __ _ _ __   __| |
 / _` | '_ \ / _` |
| (_| | | | | (_| |
 \__,_|_| |_|\__,_|

                       _   _
 _ __ ___   __ _ _ __ | \ | |
| '_ ` _ \ / _` | '_ \|  \| |
| | | | | | (_| | |_) | |\  |
|_| |_| |_|\__,_| .__/|_| \_|
                |_|
```

---

# Last time

Tackled this problem with `for`:

- combine 3 `Option`'s into an `Option`


- combine 3 `Triplet`'s into a `Triplet`


- combine 3 `Future`'s into a `Future`

```scala
for {
  james <- jamesThing
  feroz <- ferozThing
  yuhan <- yuhanThing
} yield james + feroz + yuhan
```

---

# Results

- worked okay for `Option`


- doesn't work for `Triplet` because it doesn't have `flatMap`


- works with `Future` but there's issues

```scala
for {
  james <- jamesThing
  feroz <- ferozThing
  yuhan <- yuhanThing
} yield james + feroz + yuhan
```

---

# Overall

The prizes for James, Feroz and Yuhan are independent of each other

Using a for/flatMap is a bad fit because it's intended for dependent computations

---

# Today

- solve the problem with `mapN`


- look under the hood at `mapN` (Applicative)

---

```
                       _   _
 _ __ ___   __ _ _ __ | \ | |
| '_ ` _ \ / _` | '_ \|  \| |
| | | | | | (_| | |_) | |\  |
|_| |_| |_|\__,_| .__/|_| \_|
                |_|
```

The best friend you didn't know you wanted

---

# mapN

I'm going to solve this with the mysterious "mapN"

---

# What you'll see

All the solutions will look the same

---

# What you'll see

> All the solutions will look the same

That's a sign we've landed on the right abstraction

---

# What is mapN?

Like `map` on 'roids

Will explain later

Just go with it for now

---

# Demo time!

To the repl!

---

# Full demo

See `mapN_demo.sc`

The pinacle:

```scala
def superPranali[C[_]](jamesPrize: C[Int], ferozPrize: C[Int], yuhanPrize: C[Int])(implicit ev: Applicative[C]): C[Int] = {
  (jamesPrize, ferozPrize, yuhanPrize).mapN {
    case (james, feroz, yuhan) => james + feroz + yuhan
  }
}
```

---

# mapN

A tool for combining independent "things"

---

# More playing!

Back to the repl

Use different values of N, like 2 and 4

---

# Why "mapN"?

It's a generalisation of `map`

`map` is really `map1`

To the repl!

---

# map1

`map` is really `map1`

`Applicative` => `mapN` => `map1` => `Functor`

---

# Hierarchy

> `Applicative` => `mapN` => `map1` => `Functor`

ie. all `Applicative`'s are `Functor`'s

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

---

# Being practical

---

# Being practical

You don't have to understand `Applicative` deeply to use `mapN`

---

# Being practical

> You don't have to understand `Applicative` deeply to use `mapN`

That's why I covered `mapN` first

We'll look deeper at `Applicative`, but don't worry if you don't understand it

---

```
    _                _ _           _   _
   / \   _ __  _ __ | (_) ___ __ _| |_(_)_   _____
  / _ \ | '_ \| '_ \| | |/ __/ _` | __| \ \ / / _ \
 / ___ \| |_) | |_) | | | (_| (_| | |_| |\ V /  __/
/_/   \_\ .__/| .__/|_|_|\___\__,_|\__|_| \_/ \___|
        |_|   |_|
```

---

# Type class

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

Type class for type constructors (e.g. `Option`, `Seq`)

---

# Adjectives

- Functor - map-able things


- Monad - flatMap-able things


- Applicative - ap-able things (?)

---

# Adjectives

> Applicative - ap-able things (?)

Doesn't have a nice intuitive concept

It's a bit more abstract

---

# Applicative

Main usage: Powers `mapN`

---

# Conceptually

```
F[A]            ap      F[B]
F[A => B]     ----->
```

---

# Comparing ap with map

```
F[A]            ap      F[B]
F[A => B]     ----->


F[A]           map      F[B]
  A => B      ----->
```

Pretty similar

---

# And don't forget lift!

```
F[A]            ap      F[B]
F[A => B]     ----->

               lift
A             ----->    F[A]
```

---

# Demo time

Let's make our own type class definition

Then instances for `Option` and `Triplet`

To the repl!

---

# Summary

```scala
trait Applicative[F[_]] {
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]
  def pure[A](a: A): F[A]
}

object OptionApplicative extends OurApplicative[Option] {
  def ap[A, B](ff: Option[A => B])(fa: Option[A]): Option[B] = (ff, fa) match {
    case (Some(f), Some(a)) => Some(f(a))
    case _ => None
  }

  def pure[A](a: A): Option[A] = Option(a)
}

object TripletApplicative extends Applicative[Triplet] {
  def ap[A, B](ff: Triplet[A => B])(fa: Triplet[A]): Triplet[B] = {
    val Triplet(fun1, fun2, fun3) = ff
    val Triplet(val1, val2, val3) = fa
    Triplet(fun1(val1), fun2(val2), fun3(val3))
  }

  def pure[A](a: A): Triplet[A] = Triplet(a, a, a)
}
```

---

# Cats

We don't need our DIY type class and instances

Cats already has all this

---

# Using cats

Show how to import Applicative

Show how to summon type class instances

To a fresh repl!

---

# Summary

```scala
import $ivy.`org.typelevel::cats-core:2.7.0`

import cats.Applicative

// Summon the instance for Option
val optionApplicative = Applicative[Option]

optionApplicative.pure(1)

optionApplicative.ap[String, Int](Option(_.length))(Option("abc"))
// Some(3)

// Summon the instance for Seq
val seqApplicative = Applicative[Seq]

seqApplicative.ap[String, Int](Seq(_.length, _.count(_ == 'a')))(Seq("abc", "abcdef", "aaaaa"))
// Seq(3, 6, 5, 1, 1, 5)
```

---

# Confused

You look at code like this:

```scala
optionApplicative.ap[String, Int](Option(_.length))(Option("abc"))
//                                       ^^^^^^^^
```

and say:

> I can't think of a single time I've had a function inside a type constructor,
>
> how is this useful?

---

# Used indirectly

> I can't think of a single time I've had a function inside a type constructor

Probably true yes

`Applicative` is usually not directly used

It's often used to power `mapN`

---

# How?

> It's often used to power `mapN`

That's too complex for today

There's a homework question that starts you on that journey

---

# How?

> That's too complex for today

Phew!

And remember, you don't have to deeply understand `Applicative` to use `mapN`

I won't dob you in to the scala compiler

---

# Homework

---

# Functor and Applicative

Applicative can do everything Functor can do

ie. it can map

Prove it!

---

# Conceptual Proof

Suppose `F` is applicative

```
F[A]           ap       F[B]
F[A => B]    ------>

              pure
A            ------>    F[A]
```

We want to derive `map` from that somehow

```
F[A]                    F[B]
  A => B     --??-->
```

ie. get from the LHS to RHS using applicative machinery

---

# Already Similar

`ap` and `map` are already very similar

```
F[A]           ap       F[B]
F[A => B]    ------>

F[A]          map       F[B]
  A => B     ------>
```

If we turn `A => B` into `F[A => B]`, then we can use `ap` to get our `F[B]`

---

# Lifting

> If we turn `A => B` into `F[A => B]`, then we can use `ap`

This is just lifting!

---

# Putting it together

- lift the `A => B` into `F[A => B]` with pure


- combine that with `F[A]` to get `F[B]`

---

# Translate it to code

This function takes in an `Applicative` instance and returns a `Functor` instance for the same `F`

```scala
def applicativeToFunctor[F[_]](ev: Applicative[F]): Functor[F] = new Functor[F] {
  def map[A, B](fa: F[A])(f: A => B): F[B] = ev.ap(ev.pure(f))(fa)
}

val optionFunctor = applicativeToFunctor(Applicative[Option])

optionFunctor.map(Option(3))(_ + 1)
// Some(value = 4)
```

It proves that you can always derive `Functor` behaviour from `Applicative`

---

# Going further

We effectively just used `Applicative` to create `map1`

Show that with `Applicative` you can create `map2`

---

# Monad and Applicative

Show that all monads are applicative

ie. use `flatMap` to derive `ap`

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

# mapN

An extension of `map`

Very useful!

---

# mapN

The pin up boy of `Applicative`

ie. the thing that makes it useful

---

# Applicative

An abstraction that sits between `Functor` and `Monad`

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

---

# Examples of Applicative

- all monads


- `Triplet`


- validation types (coming later)

---

# Confusing Applicative

The practical take away from today is `mapN`

You can use it without fully understanding `Applicative`

But of course I'd encourage you to dig deeper!

---

# Next time

`Eq` type class with James

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
