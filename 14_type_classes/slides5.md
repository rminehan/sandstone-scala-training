---
author: Rohan
date: 2022-04-21
title: More monoids
---

```
 __  __
|  \/  | ___  _ __ ___
| |\/| |/ _ \| '__/ _ \
| |  | | (_) | | |  __/
|_|  |_|\___/|_|  \___|

 __  __                   _     _
|  \/  | ___  _ __   ___ (_) __| |___
| |\/| |/ _ \| '_ \ / _ \| |/ _` / __|
| |  | | (_) | | | | (_) | | (_| \__ \
|_|  |_|\___/|_| |_|\___/|_|\__,_|___/
```

---

# Recap

Monoid captures the idea of being "combine-able"

---

# Two concepts

```scala
trait Monoid[A] {
  val identity: A

  def combine(left: A, right: A): A
}
```

---

# Laws

- `identity` doesn't change anything


- `combine` is associative

---

# Today

- homework


- more examples

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

"Prove" that sequences are monoids under concatenation

ie. construct something like:

```scala
class SeqConcatenation[A] extends Monoid[Seq[A]] {
  ...
}
```

---

# Hints

Concatenating two `Seq[A]`'s gives you another `Seq[A]`

Concatenating an empty sequence does nothing

---

# Solve it!

To the repl!

---

# Solution

```scala
class SeqConcatenation[A] extends Monoid[Seq[A]] {
  val identity: Seq[A] = Seq.empty[A]
  def combine(left: Seq[A], right: Seq[A]): Seq[A] = left ++ right
}
```

Using `class` instead of `object` due to the generic parameter

---

# Note

`String` concatenation is like a `Seq[Char]` being concatenated

The empty string is like `Seq.empty[Char]`

---

```
 __  __
|  \/  | ___  _ __ ___
| |\/| |/ _ \| '__/ _ \
| |  | | (_) | | |  __/
|_|  |_|\___/|_|  \___|

 _____                           _
| ____|_  ____ _ _ __ ___  _ __ | | ___  ___
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \/ __|
| |___ >  < (_| | | | | | | |_) | |  __/\__ \
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___||___/
                          |_|
```

Lots of examples to build intuition

---

# Very common

Examples will show how prevalent monoids are

---

```
 ____              _
| __ )  ___   ___ | | ___  __ _ _ __
|  _ \ / _ \ / _ \| |/ _ \/ _` | '_ \
| |_) | (_) | (_) | |  __/ (_| | | | |
|____/ \___/ \___/|_|\___|\__,_|_| |_|

 _____                           _
| ____|_  ____ _ _ __ ___  _ __ | | ___  ___
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \/ __|
| |___ >  < (_| | | | | | | |_) | |  __/\__ \
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___||___/
                          |_|
```

---

# And

Combine two booleans by "and"-ing them

---

# And

> Combine two booleans by "and"-ing them

What is the identity?

```scala
object BooleanAnd extends Monoid[Boolean] {
  val identity: Boolean = ???
  def combine(left: Boolean, right: Boolean): Boolean = left && right
}
```

---

# And - true!

> Combine two booleans by "and"-ing them

"And"-ing with true doesn't change your value

```scala
object BooleanAnd extends Monoid[Boolean] {
  val identity: Boolean = true
  def combine(left: Boolean, right: Boolean): Boolean = left && right
}

true && true  == true
true && false == false
//        |_______| same
```

---

# Associative?

```scala
a && (b && c)   =?=   (a && b) && c
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Yarp

```scala
a && (b && c)   ==    (a && b) && c
```

Simple proof:

> Line up all the booleans: a b c
>
> If there's a `false` anywhere in the expression, the final value is `false`
>
> otherwise if there'll all true, the final value is `true`.
>
> This logic isn't affected by brackets, hence both expressions are the same.

---

# Or-ing?

If we or'd, what would the identity be?

```scala
object BooleanOr extends Monoid[Boolean] {
  val identity: Boolean = ???
  def combine(left: Boolean, right: Boolean): Boolean = left || right
}
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# false!

> If we or'd, what would the identity be?

Or-ing with `false` doesn't change a value

```scala
object BooleanOr extends Monoid[Boolean] {
  val identity: Boolean = ???
  def combine(left: Boolean, right: Boolean): Boolean = left || right
}

false || true  == true
false || false == false
//         |_______| same
```

---

```
 ____       _
/ ___|  ___| |_ ___
\___ \ / _ \ __/ __|
 ___) |  __/ |_\__ \
|____/ \___|\__|___/

```

---

# Sets

`Set[A]` is a monoid under intersection and union

Homework: think about what the identity elements will be

Intersection is analogous to boolean "and" example

Union is analogous to boolean "or" example

---

```
  ___        _      _
 / _ \ _   _(_)_ __| | ___   _
| | | | | | | | '__| |/ / | | |
| |_| | |_| | | |  |   <| |_| |
 \__\_\\__,_|_|_|  |_|\_\\__, |
                         |___/
    _       _     _ _ _   _
   / \   __| | __| (_) |_(_) ___  _ __
  / _ \ / _` |/ _` | | __| |/ _ \| '_ \
 / ___ \ (_| | (_| | | |_| | (_) | | | |
/_/   \_\__,_|\__,_|_|\__|_|\___/|_| |_|

```

Restrict the set of numbers we're adding

---

# Addition of odd numbers

Our normal integer addition,

but restricted to odd numbers

---

# Monoid?

> Our normal integer addition,
>
> but restricted to odd numbers

Is it a proper monoid?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Nope

> Our normal integer addition,
>
> but restricted to odd numbers

Two problems:

- adding two odd numbers gives an even number (it's not "closed")


- 0 isn't odd!

---

# Thinking hats

What are some subsets of integers which would be closed under addition?

ie. you can't escape that set by adding elements from that set

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Thinking hats

> What are some subsets of integers which would be closed under addition?
>
> ie. you can't escape that set by adding elements from that set

- even integers


- multiples of 3, 4, 5 etc...


- non-negative integers (0+)


- non-positive integers (0-)

(might have some problems with overflow)

---

# Clock arithmetic

---

# Clock arithmetic

Add two integers, and take the remainder modulo 10

Examples

```
 6 + 5 mod 10 = 11 mod 10 = 1
 2 + 4 mod 10 =  6 mod 10 = 6
-5 + 3 mod 10 = -2 mod 10 = 8
```

(ie. put the answer in [0, 9]

---

# "Clock" arithmetic?

Because once you get to 10,

it loops around

(like the hands on a clock)

---

# Code it up

---

# Summary

```scala
object ClockAddition extends Monoid[Int] {
  val identity: Int = 0
  def combine(left: Int, right: Int): Int = {
    val sum = (left + right) % 10
    if (sum < 0) sum + 10 else sum
  }
}

ClockAddition.combine(6, 5)
// 1

ClockAddition.combine(-5, 3)
// 8

ClockAddition.combine(2, 4)
// 6

ClockAddition.combine(12, 14)
// 6
```

---

# Monoid?

Does our monoid satisfy our laws?

- associative


- identity element

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Monoid?

> associative

Yep

> identity element

Not quite

```scala
ClockAddition.combine(12, 0)
// 2
```

Combining with 0 didn't return the same thing

It didn't change it modulo 10

Depends on your definition of "the same"

---

```
 ____                                       _
/ ___| _   _ _ __ _ __ ___  _   _ _ __   __| |
\___ \| | | | '__| '__/ _ \| | | | '_ \ / _` |
 ___) | |_| | |  | | | (_) | |_| | | | | (_| |
|____/ \__,_|_|  |_|  \___/ \__,_|_| |_|\__,_|

 _____                           _
| ____|_  ____ _ _ __ ___  _ __ | | ___
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \
| |___ >  < (_| | | | | | | |_) | |  __/
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___|
                          |_|
```

---

# Surround operator

Combine two strings by surrounding one with the other

```scala
object StringSurround extends Monoid[String] {
  val identity: String = ""

  def combine(left: String, right: String): String = s"$left$right$left"
}

StringSurround.combine("|", "cat")
// "|cat|"

StringSurround.combine("", "cat")
// "cat"
```

---

# Monoid?

```scala
object StringSurround extends Monoid[String] {
  val identity: String = ""

  def combine(left: String, right: String): String = s"$left$right$left"
}
```

Does it follow our laws?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Nah...

```scala
object StringSurround extends Monoid[String] {
  val identity: String = ""

  def combine(left: String, right: String): String = s"$left$right$left"
}
```

> Does it follow our laws?

When the identity is on the left, it works:

```scala
StringSurround.combine("", "cat")
// "cat"
```

but not on the right:

```scala
StringSurround.combine("cat", "")
// "catcat"
```

Also not associative...

---

```
  ___        _   _
 / _ \ _ __ | |_(_) ___  _ __
| | | | '_ \| __| |/ _ \| '_ \
| |_| | |_) | |_| | (_) | | | |
 \___/| .__/ \__|_|\___/|_| |_|
      |_|
```

---

# Scenario

Want to combine first, middle and last names,

but only if they're all defined...

---

# Scenario

> Want to combine first, middle and last names,
>
> but only if they're all defined...

Inputs and outputs represented by Options:

```scala
def buildCompleteName(
  first: Option[String],
  middle: Option[String],
  last: Option[String]): Option[String] = {
  ...
}

buildCompleteName(Some("Boban"), None, Some("Jones")) // None
buildCompleteName(Some("John"), Some("Alexander"), Some("Smith")) // Some("John Alexander Smith")
```

---

# Monoid?

Suppose we had a way to combine `Option[String]`'s

```scala
object OptionStringConcat extends Monoid[Option[String]] {
  ...
}
```

We could use it to combine name parts two at a time...

> (first + middle)
>
> (first + middle) + last

---

# Implement it

To the repl!

---

# Summary

```scala
object OptionStringConcat extends Monoid[Option[String]] {
  val identity: Option[String] = Some("")
  def combine(left: Option[String], right: Option[String]): Option[String] = (left, right) match {
    case (Some(leftVal), Some(rightVal)) => Some(s"$leftVal $rightVal")
    case _ => None
  }
}

def buildCompleteName(
  first: Option[String],
  middle: Option[String],
  last: Option[String]): Option[String] = {

  OptionStringConcat.combine(OptionStringConcat.combine(first, middle), last)
}

buildCompleteName(Some("Boban"), None, Some("Jones")) // None
buildCompleteName(Some("John"), Some("Alexander"), Some("Smith")) // Some("John Alexander Smith")
```

---

# Monoid?

```scala
object OptionStringConcat extends Monoid[Option[String]] {
  val identity: Option[String] = Some("")
  def combine(left: Option[String], right: Option[String]): Option[String] = (left, right) match {
    case (Some(leftVal), Some(rightVal)) => Some(s"$leftVal $rightVal")
    case _ => None
  }
}
```

Does it obey the laws?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# No!

```scala
object OptionStringConcat extends Monoid[Option[String]] {
  val identity: Option[String] = Some("")
  def combine(left: Option[String], right: Option[String]): Option[String] = (left, right) match {
    case (Some(leftVal), Some(rightVal)) => Some(s"$leftVal $rightVal")
    case _ => None
  }
}
```

> Does it obey the laws?

Not quite:

```scala
OptionStringCombine.combine(Some("Boban"), OptionStringCombine.identity)
// Some(value = "Boban ")
//                    ^  pesky little space character
```

---

# Similar example

Feroz, Yuhan and James enter a lottery

---

# Similar example

Feroz, Yuhan and James enter a lottery

The lottery has a twist:

> if they all win a prize, Pranali gets a prize which is the sum of their individual prizes

---

# Option

Represent whether or not you win a prize with `Option`

```scala
def pranaliPrize(
  ferozPrize: Option[Int],
  yuhanPrize: Option[Int],
  jamesPrize: Option[Int]): Option[Int] = ...

pranaliPrize(
  ferozPrize = Some(10),
  yuhanPrize = Some(30),
  jamesPrize = Some(50)
) // Some(90)

pranaliPrize(
  ferozPrize = Some(1000),
  yuhanPrize = None,
  jamesPrize = Some(5000)
) // None
```

---

# Deja vu

Methods that have 3 optional inputs and an optional output.

```scala
def buildCompleteName(
  first: Option[String],
  middle: Option[String],
  last: Option[String]): Option[String] = ...

def pranaliPrize(
  ferozPrize: Option[Int],
  yuhanPrize: Option[Int],
  jamesPrize: Option[Int]): Option[Int] = ...
```

Combine the values two by two again...

To the repl!

---

# Summary

```scala
object OptionIntAddition extends Monoid[Option[Int]] {
  val identity: Option[Int] = Some(0)
  def combine(left: Option[Int], right: Option[Int]): Option[Int] = (left, right) match {
    case (Some(leftInt), Some(rightInt)) => Some(leftInt + rightInt)
    case _ => None
  }
}

OptionIntAddition.combine(Some(3), Some(4)) // Some(value = 7)
OptionIntAddition.combine(Some(3), None) // None

def pranaliPrize(ferozPrize: Option[Int], yuhanPrize: Option[Int], jamesPrize: Option[Int]): Option[Int] =
  OptionIntAddition.combine(OptionIntAddition.combine(ferozPrize, yuhanPrize), jamesPrize)

pranaliPrize(Some(3), Some(4), Some(5)) // Some(value = 12)
pranaliPrize(Some(3), Some(4), None) // None
```

---

# DRY?

Aren't these the same?

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

# Two layers

Option layer wrapped around an inner monoid layer

```scala
object OptionIntAddition extends Monoid[Option[Int]] {
  val identity: Option[Int] = Some(0) // 0 is the identity of IntAddition
  def combine(left: Option[Int], right: Option[Int]): Option[Int] = (left, right) match {
    case (Some(leftInt), Some(rightInt)) => Some(leftInt + rightInt) // + is the combine for IntAddition
    case _ => None
  }
}
```

---

# Homework

Remove the duplication

Generalise this into a monoid that can be used for both scenarios

---

# Hint

This is like when we had many concrete methods for folding sequences,

we saw how they all have the same scaffolding,

so we created a more abstract method and injected the unique parts

In our case, the unique part is the inner monoid

---

# Hint

```scala
class OptionCombine[A](ev: Monoid[A]) extends Monoid[Option[A]] {
   val identity: Option[A] = ???
   def combine(left: Option[A], right: Option[A]): Option[A] = ???
}
```

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

# Homework

Prove that `StringSurround` isn't associative (find a counter-example)

---

# Solution

```scala
"A" + ("B" + "C") = "A" + "BCB" = "ABCBA"

("A" + "B") + "C" = "ABA" + "C" = "ABACABA"
```

Not the same!

(Here I'm using `+` as shorthand for combining them)

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

# Monoids everywhere

Lots of examples of monoids

Once you know what a monoid is, you see them everywhere

---

# Lawful?

Some examples aren't quite true monoids

Depending on context that might be fine

---

# Layers

Our Option example showed how you can build bigger monoids from smaller ones

More on this next time

---

# Many examples

Hopefully strengthened your intuition for monoid

---

# My encouragement

Do the `OptionCombine` homework,

it will help you get comfortable with more abstract concepts

Will make understanding implicits easier later

---

# Next time

More examples that are more abstract

Make this kind of combine code prettier

```scala
def pranaliPrize(ferozPrize: Option[Int], yuhanPrize: Option[Int], jamesPrize: Option[Int]): Option[Int] =
  OptionIntAddition.combine(OptionIntAddition.combine(ferozPrize, yuhanPrize), jamesPrize)
```

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
