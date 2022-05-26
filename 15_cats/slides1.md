---
author: Rohan
date: 2022-05-23
title: Cats
---

```
  ____      _
 / ___|__ _| |_ ___
| |   / _` | __/ __|
| |__| (_| | |_\__ \
 \____\__,_|\__|___/

```

---

# Looking back

- type classes


- examples (show, monoid, functor, monad)

---

# Recap: The "type class" pattern

- define a type class `trait`


- define instances for common types


- define useful syntax and use cases associated with that type class

---

# Example: Monoid

> define a type class `trait`

```scala
trait Monoid[A] {
  val identity: A
  def combine(left: A, right: A): A
}
```

> define instances for common types

```scala
object IntAddition extends Monoid[Int] { ... }

object IntMultiplication extends Monoid[Int] { ... }

object StringConcatenation extends Monoid[String] { ... }

object BooleanAnd extends Monoid[Boolean] { ... }

...
```

> define useful syntax and use cases associated with that type class

```scala
def fold[A](seq: Seq[A])(implicit ev: Monoid[A]): A = ...

implicit class MonoidOps[A](a: A)(implicit ev: Monoid[A]) {
  def |+|(other: A): A = ev.combine(a, other)
}
```

---

# Example: Functor

> define a type class `trait`

```scala
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): B
}
```

> define instances for common types

```scala
object OptionFunctor extends Functor[Option] { ... }

object ListFunctor extends Functor[List] { ... }

...
```

> define useful syntax and use cases associated with that type class

```scala
implicit class FunctorOps[F[_], A](fa: F[A])(implicit ev: Functor[A]) {
  def map[B](f: A => B): F[B] = ev.map(fa)(f)
}
```

---

# A lot of work

Lots of type classes...

Lots of instances...

Unit tests etc...

---

# Duplication

Want to avoid duplicate definitions of universal type classes like `Show` and `Functor`

---

# Cats

Scala library

Intended to be the canonical representation of these universal type classes

Provides all the bells and whistles associated with each type class

---

# Aside

Cats isn't _just_ about type classes

This is a good starting point for us though

---

# Coming up

Next set of sessions on the cats library

Learn how to use it

---

```
 _____
|_   _|__  _   _ _ __
  | |/ _ \| | | | '__|
  | | (_) | |_| | |
  |_|\___/ \__,_|_|
```

---

# Tour

To the website!

---

# Overwhelming?

Don't need to understand every single concept

Can pick and choose, like a buffet!

---

# Useful type classes

- functor


- applicative


- monad


- monoid


- show


- traverse


- eq

(and we're already familiar with a lot of those!)

---

```
 _   _     _
| | | |___(_)_ __   __ _
| | | / __| | '_ \ / _` |
| |_| \__ \ | | | | (_| |
 \___/|___/_|_| |_|\__, |
                   |___/
  ____      _
 / ___|__ _| |_ ___
| |   / _` | __/ __|
| |__| (_| | |_\__ \
 \____\__,_|\__|___/

```

---

# Demo time

Import the dependency

Then show `Triplet` is a functor

To the repl!

---

# Summary

```scala
import $ivy.`org.typelevel::cats-core:2.7.0`

case class Triplet[A](_1: A, _2: A, _3: A)

import cats.Functor

implicit object TripletFunctor extends Functor[Triplet] {
  def map[A, B](fa: Triplet[A])(f: A => B): Triplet[B] = {
    val Triplet(val1, val2, val3) = fa
    Triplet(f(val1), f(val2), f(val3))
  }
}

import cats.syntax.functor._

val stringTriplet = Triplet("a", "bcd", "edfgh")

stringTriplet.map(_.length)
// Triplet(1, 3, 5)
```

---

```
    _                          _
   / \   ___ ___  ___  ___ ___(_)_ __   __ _
  / _ \ / __/ __|/ _ \/ __/ __| | '_ \ / _` |
 / ___ \\__ \__ \  __/\__ \__ \ | | | | (_| |
/_/   \_\___/___/\___||___/___/_|_| |_|\__, |
                                       |___/
  ____      _
 / ___|__ _| |_ ___
| |   / _` | __/ __|
| |__| (_| | |_\__ \
 \____\__,_|\__|___/

```

---

```
<soap-box>
```

---

# Before adding a library...

... it's important to analyse it

---

# Before adding a library...

... it's important to analyse it

Don't just throw new dependencies in willy-nilly!

---

# Analogy of babies

Every dependency is like a baby

They need attention

---

# So

Analysis needed

Value needs to justify the maintenance cost

---

```
</soap-box>
```

---

# Things to consider

- is it mature


- is it well maintained


- does it have good docs


- do they seem to know what they're doing (relates to source/binary compatibility)


- footprint, e.g. does it have lots of transitive dependencies


- transparent, ie. easy to peek into the source

---

# My quick example analysis

---

# Mature

> is it mature

Yeah, been around a while

---

# Maintenance

> is it well maintained

Part of the typelevel stack

Fairly active community

Has some financial contribution?

There's activity on github

But has been a while since there was a major release

---

# Docs

> does it have good docs

Yep quite comprehensive and user friendly

---

# Professional

> do they seem to know what they're doing (relates to source/binary compatibility)

Discussion [here](https://github.com/typelevel/cats#binary-compatibility-and-versioning)

My impression is that the authors wear grown up pants

---

# Footprint

> e.g. does it have lots of transitive dependencies

Hard to know without more investigation

My guess is a library like this defines all its own concepts,

wouldn't need many unusual dependencies

---

# Transparent

> transparent, ie. easy to peek into the source

Internally it's a typical confusing scala library

---

# Overall

Pretty solid library

---

```
 ____  _               _   _
|  _ \(_)_ __ ___  ___| |_(_) ___  _ __
| | | | | '__/ _ \/ __| __| |/ _ \| '_ \
| |_| | | | |  __/ (__| |_| | (_) | | | |
|____/|_|_|  \___|\___|\__|_|\___/|_| |_|
```

Where are we going with this?

---

# Current state of things

Don't really use cats

(One reference in `TransactionCategoriesService` which Feroz is killing)

---

# End destination

Hoping you'll be open to using cats at Sandstone

---

# Upcoming training

Explore some of the type classes in cats

Much later we can look at some handy data structures

---

# Specifics

- `mapN` and `Applicative`


- `Eq`


- `Traverse`

---

# Hopefully...

... I can prove it's worth it

---

# Hope?

Good engineers don't hope, they make sure!

Will do my best to brain wash you

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

# Cats

A buffet of useful type classes and data structures

---

# De facto standard

Used by many scala teams

Like an extension to the standard library

---

# Strong

Good library all things considered

---

# Coming up

Look at some of the useful features in cats

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
