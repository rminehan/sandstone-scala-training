---
author: Rohan
date: 2022-04-26
title: Even more monoids
---

```
 _____
| ____|_   _____ _ __
|  _| \ \ / / _ \ '_ \
| |___ \ V /  __/ | | |
|_____| \_/ \___|_| |_|


 _ __ ___   ___  _ __ ___
| '_ ` _ \ / _ \| '__/ _ \
| | | | | | (_) | | |  __/
|_| |_| |_|\___/|_|  \___|

                             _     _
 _ __ ___   ___  _ __   ___ (_) __| |___
| '_ ` _ \ / _ \| '_ \ / _ \| |/ _` / __|
| | | | | | (_) | | | | (_) | | (_| \__ \
|_| |_| |_|\___/|_| |_|\___/|_|\__,_|___/

```

---

# Recap

Saw lots of monoid examples from last time

- booleans: and and or


- integer addition restricted to a subset of integers


- clock arithmetic

---

# Recap

Looked at two option examples:

```scala
buildCompleteName(Some("John"), Some("Alexander"), Some("Smith")) // Some("John Alexander Smith")
buildCompleteName(Some("Boban"), None, Some("Jones")) // None

pranaliPrize(Some(3), Some(4), Some(5)) // Some(value = 12)
pranaliPrize(Some(3), Some(4), None) // None
```

Internally they combine twice

---

# Today

- homework


- another complex example


- combine operator


- implicits

---

```
 _   _                                         _    
| | | | ___  _ __ ___   _____      _____  _ __| | __
| |_| |/ _ \| '_ ` _ \ / _ \ \ /\ / / _ \| '__| |/ /
|  _  | (_) | | | | | |  __/\ V  V / (_) | |  |   < 
|_| |_|\___/|_| |_| |_|\___| \_/\_/ \___/|_|  |_|\_\
                                                    
```

---


# Homework

Recall two option monoids that look the same:

```scala
object OptionStringConcat extends Monoid[Option[String]] {
  val identity: Option[String] = Some("")
  def combine(left: Option[String], right: Option[String]): Option[String] = (left, right) match {
    case (Some(leftVal), Some(rightVal)) => Some(s"$leftVal $rightVal")
    case _ => None
  }
}

object OptionIntAddition extends Monoid[Option[Int]] {
  val identity: Option[Int] = Some(0)
  def combine(left: Option[Int], right: Option[Int]): Option[Int] = (left, right) match {
    case (Some(leftInt), Some(rightInt)) => Some(leftInt + rightInt)
    case _ => None
  }
}
```

Only differences:

- types


- the value inside the identity `Some`


- the combining inside the `Some`

---

# What's really going on?

Two responsibilities:

- "option stuff" - `Some + Some = Some`, otherwise `None`


- "inner monoid" - handles the values inside the `Some`

```scala
object OptionStringConcat extends Monoid[Option[String]] {
  val identity: Option[String] = Some("")
  def combine(left: Option[String], right: Option[String]): Option[String] = (left, right) match {
    case (Some(leftVal), Some(rightVal)) => Some(s"$leftVal $rightVal")
    case _ => None
  }
}

object OptionIntAddition extends Monoid[Option[Int]] {
  val identity: Option[Int] = Some(0)
  def combine(left: Option[Int], right: Option[Int]): Option[Int] = (left, right) match {
    case (Some(leftInt), Some(rightInt)) => Some(leftInt + rightInt)
    case _ => None
  }
}
```

---

# Combine into one

Keep the outer scaffolding, inject the inner monoid

To the repl!

---

# Solution

```scala
class OptionCombine[A](ev: Monoid[A]) extends Monoid[Option[A]] {
   val identity: Option[A] = Some(ev.identity)
   def combine(left: Option[A], right: Option[A]): Option[A] = (left, right) match {
     case (Some(leftInner), Some(rightInner)) => Some(ev.combine(leftInner, rightInner))
     case _ => None
   }
}

object IntAddition extends Monoid[Int] {
  val identity: Int = 0
  def combine(left: Int, right: Int): Int = left + right
}

def pranaliPrize(ferozPrize: Option[Int], yuhanPrize: Option[Int], jamesPrize: Option[Int]): Option[Int] = {
  val combiner = new OptionCombine(IntAddition)
  combiner.combine(combiner.combine(ferozPrize, yuhanPrize), jamesPrize)
}

pranaliPrize(Some(3), Some(4), Some(5))
// Some(value = 12)

pranaliPrize(Some(3), Some(4), None)
// None
```

---

# The importance of this example

```scala
class OptionCombine[A](ev: Monoid[A]) extends Monoid[Option[A]] { ... }
```

Built a bigger monoid from a little one

---

# Onto another complex example!

---

```
 _____     _       _      _
|_   _| __(_)_ __ | | ___| |_
  | || '__| | '_ \| |/ _ \ __|
  | || |  | | |_) | |  __/ |_
  |_||_|  |_| .__/|_|\___|\__|
            |_|
```

---

# Triplet

3 values of the same type `A`

---

# Triplet

> 3 values of the same type `A`

```scala
case class Triplet[A](_1: A, _2: A, _3: A)
```

---

# Monoid?

```scala
case class Triplet[A](_1: A, _2: A, _3: A)
```

If we know that `A` is a monoid,

then we can see that a triplet is also a monoid...

To the repl!

---

# Summary

We can extend a smaller monoid across structures by combining "pointwise"

```scala
class TripletMonoid[A](ev: Monoid[A]) extends Monoid[Triplet[A]] {
  val identity: Triplet[A] = Triplet(ev.identity, ev.identity, ev.identity)

  def combine(left: Triplet[A], right: Triplet[A]): Triplet[A] = Triplet(
    _1 = ev.combine(left._1, right._1),
    _2 = ev.combine(left._2, right._2),
    _3 = ev.combine(left._3, right._3)
  )
}

val triplet1 = Triplet(1, 2, 3)
val triplet2 = Triplet(10, 20, 30)

val tripletClockAddition = new TripletMonoid(ClockAddition)
tripletClockAddition.combine(triplet1, triplet2)
// Triplet(_1 = 1, _2 = 2, _3 = 3)

val tripletIntAddition = new TripletMonoid(IntegerAddition)
tripletIntAddition.combine(triplet1, triplet2)
// Triplet(_1 = 11, _2 = 22, _3 = 33)
```

---

# True Monoid?

Is our triplet monoid a true monoid?

---

# True Monoid?

> Is our triplet monoid a true monoid?

Yes...

Provided the mini-monoid is a true monoid too

(Homework exercise later about this)

---

# Deja vu?

```scala
case class Triplet[A](_1: A, _2: A, _3: A)

class TripletMonoid[A](ev: Monoid[A]) extends Monoid[Triplet[A]] {
  val identity: Triplet[A] = Triplet(ev.identity, ev.identity, ev.identity)

  def combine(left: Triplet[A], right: Triplet[A]): Triplet[A] = Triplet(
    _1 = ev.combine(left._1, right._1),
    _2 = ev.combine(left._2, right._2),
    _3 = ev.combine(left._3, right._3)
  )
}
```

Does this construction remind you of something from a previous lesson?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Deja vu?

```scala
case class Triplet[A](_1: A, _2: A, _3: A)

class TripletMonoid[A](ev: Monoid[A]) extends Monoid[Triplet[A]] {
  val identity: Triplet[A] = Triplet(ev.identity, ev.identity, ev.identity)

  def combine(left: Triplet[A], right: Triplet[A]): Triplet[A] = Triplet(
    _1 = ev.combine(left._1, right._1),
    _2 = ev.combine(left._2, right._2),
    _3 = ev.combine(left._3, right._3)
  )
}
```

Stats!

```scala
case class Stats(fullMatches: Int, partialMatches: Int, noMatches: Int)

object StatsAddition extends Monoid[Stats] {
  val identity: Stats = Stats(fullMatches = 0, partialMatches = 0, noMatches = 0)

  def combine(left: Stats, right: Stats): Stats = Stats(
    fullMatches = left.fullMatches + right.fullMatches,
    partialMatches = left.partialMatches + right.partialMatches,
    noMatches = left.noMatches + right.noMatches
  )
}
```

`StatsAddition` is really like `new TripletMonoid(IntAddition)`

---

# Building monoids

We now have two tools to build bigger monoids from smaller ones

If smaller ones are valid, the larger ones will be too

---

```
  ____                _     _
 / ___|___  _ __ ___ | |__ (_)_ __   ___
| |   / _ \| '_ ` _ \| '_ \| | '_ \ / _ \
| |__| (_) | | | | | | |_) | | | | |  __/
 \____\___/|_| |_| |_|_.__/|_|_| |_|\___|

  ___                       _
 / _ \ _ __   ___ _ __ __ _| |_ ___  _ __
| | | | '_ \ / _ \ '__/ _` | __/ _ \| '__|
| |_| | |_) |  __/ | | (_| | || (_) | |
 \___/| .__/ \___|_|  \__,_|\__\___/|_|
      |_|
```

---

# Ugly code

We have a nice concept,

but translating to code is a bit icky...

---

# Stats Example

```scala
val mondayStats    = Stats(fullMatches = 3, partialMatches = 4, noMatches = 2)
val tuesdayStats   = Stats(fullMatches = 4, partialMatches = 3, noMatches = 4)
val wednesdayStats = Stats(fullMatches = 2, partialMatches = 6, noMatches = 5)

val threeDays = StatsAddition.combine(StatsAddition.combine(mondayStats, tuesdayStats), wednesdayStats)
```

Have to combine two at a time

A bit ugly...

---

# Operator?

```scala
val mondayStats    = Stats(fullMatches = 3, partialMatches = 4, noMatches = 2)
val tuesdayStats   = Stats(fullMatches = 4, partialMatches = 3, noMatches = 4)
val wednesdayStats = Stats(fullMatches = 2, partialMatches = 6, noMatches = 5)

val threeDays = StatsAddition.combine(StatsAddition.combine(mondayStats, tuesdayStats), wednesdayStats)

// Would look nicer
val threeDays = mondayStats |+| tuesdayStats |+| wednesdayStats
```

We know it's associative, would be nice to embrace our lazy side and drop the brackets

---

# Hack in `|+|`

To the repl!

---

# Summary

```scala
case class Stats(fullMatches: Int, partialMatches: Int, noMatches: Int) {
  def |+|(other: Stats): Stats = StatsAddition.combine(this, other) // <---
}

object StatsAddition extends Monoid[Stats] {
  val identity: Stats = Stats(fullMatches = 0, partialMatches = 0, noMatches = 0)

  def combine(left: Stats, right: Stats): Stats = Stats(
    fullMatches = left.fullMatches + right.fullMatches,
    partialMatches = left.partialMatches + right.partialMatches,
    noMatches = left.noMatches + right.noMatches
  )
}

val mondayStats    = Stats(fullMatches = 3, partialMatches = 4, noMatches = 2)
val tuesdayStats   = Stats(fullMatches = 4, partialMatches = 3, noMatches = 4)
val wednesdayStats = Stats(fullMatches = 2, partialMatches = 6, noMatches = 5)

val threeDays = mondayStats |+| tuesdayStats |+| wednesdayStats
```

---

# Other monoids

What about all the other monoids?

With this approach have to keep hacking this in for each of them...

---

# More generally

> With this approach have to keep hacking this in for each of them...

It would be nice if you got this operator for free,

ie. once you prove something is a monoid, it automatically gets a combine operator...

---

# Combine as an operator

> It would be nice if you got this operator for free

Use implicits to achieve this...

To the repl!

---

# Recap

```scala
implicit class MonoidOps[A](left: A)(implicit ev: Monoid[A]) {
  def |+|(right: A): A = ev.combine(left, right)
}

implicit object StatsAddition extends MonoidOps[Stats] { // implicit now
  ...
}

val threeDays = mondayStats |+| tuesdayStats |+| wednesdayStats
```

Now all monoids automatically get a combine operator `|+|`

---

```
 ___                 _ _      _ _
|_ _|_ __ ___  _ __ | (_) ___(_) |_
 | || '_ ` _ \| '_ \| | |/ __| | __|
 | || | | | | | |_) | | | (__| | |_
|___|_| |_| |_| .__/|_|_|\___|_|\__|
              |_|
 _____     _     _ _
|  ___|__ | | __| (_)_ __   __ _
| |_ / _ \| |/ _` | | '_ \ / _` |
|  _| (_) | | (_| | | | | | (_| |
|_|  \___/|_|\__,_|_|_| |_|\__, |
                           |___/
```

---

# Introduced implicits

We've introduced making the monoid evidence implicit:

```scala
implicit class MonoidOps[A](left: A)(implicit ev: Monoid[A]) {
  def |+|(right: A): A = ev.combine(left, right)
}

mondayStats |+| tuesdayStats |+| wednesdayStats
```

---

# Modify fold

Do the same thing for `fold`

To the repl!

---

# Summary

```scala
def fold[A](seq: Seq[A])(implicit ev: Monoid[A]): A = {
  var acc = ev.identity

  for (a <- seq)
    acc = ev.combine(acc, a)

  acc
}


fold(Seq(mondayStats, tuesdayStats, wednesdayStats))
// Stats(fullMatches = 6, partialMatches = 9, noMatches = 22)
```

---

# Why do this?

```scala
fold(Seq(mondayStats, tuesdayStats, wednesdayStats))(StatsAddition)

// vs

fold(Seq(mondayStats, tuesdayStats, wednesdayStats))
```

---

# Why do this?

It's a bit like Highlander, usually "there can only be one"

---

# Why do this?

> It's a bit like Highlander, usually "there can only be one"

So having to explicitly pass it all the time becomes tedious

```scala
fold(Seq(mondayStats, tuesdayStats, wednesdayStats))(StatsAddition)

// vs

fold(Seq(mondayStats, tuesdayStats, wednesdayStats))
```

Adds noise

---

# That's enough for today!

---

# Homework

Prove that `Triplet[A]` is a monoid as long as `A` is a monoid

---

# Solution - Triplet

## Identity

```scala
  Triplet(a, b, c) |+| triplet identity

= Triplet(a, b, c) |+| Triplet(identity, identity, identity) // expanding definition of identity

= Triplet(a |+| identity, b |+| identity, c |+| identity)   // pointwise addition

= Triplet(a, b, c) // as `A` is a monoid, so its identity does nothing to individual A's
```

Same argument works with adding the identity on the left

## Associativity

```scala
  A |+| (B |+| C) // right clustered brackets

= Triplet(a1, a2, a3) |+| (Triplet(b1, b2, b3) |+| Triplet(c1, c2, c3)) // expanding out the triplets

= Triplet(a1, a2, a3) |+| Triplet(b1 |+| c1, b2 |+| c2, b3 |+| c3) // combining B and C

= Triplet(a1 |+| (b1 |+| c1), a2 |+| (b2 |+| c2), a3 |+| (b3 |+| c3)) // combining all

= Triplet((a1 |+| b1) |+| c1, (a2 |+| b2) |+| c2, (a3 |+| b3) |+| c3) // as |+| is associative for individual A's

    //    ^^^^^^^^^^^^^^^^^^  ^^^^^^^^^^^^^^^^^^  ^^^^^^^^^^^^^^^^^^
    // note how the brackets are left clustered now,
    // this is what you'd get if you'd started with (A |+| B) |+| C and expanded it out like above,
    // so just do the same steps as above in reverse order

...

= (A |+| B) |+| C // left clustered brackets
```

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

# Small to big

Saw how you can construct monoids from other monoids

---

# Combine Operator

Learnt how to use implicits to get `|+|` for free on any monoid

---

# FP mindset

Hopefully starting to get a sense for this style of coding

---

# Why learn about monoid?

It's a nice gateway into type classes

---

# Next time

Limitations of traditional OO

Introducing type classes

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
