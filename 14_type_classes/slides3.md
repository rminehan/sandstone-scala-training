---
author: Rohan
date: 2022-03-31
title: Even more folding
---

```
 _____
| ____|_   _____ _ __
|  _| \ \ / / _ \ '_ \
| |___ \ V /  __/ | | |
|_____| \_/ \___|_| |_|

 __  __
|  \/  | ___  _ __ ___
| |\/| |/ _ \| '__/ _ \
| |  | | (_) | | |  __/
|_|  |_|\___/|_|  \___|

 _____     _     _ _
|  ___|__ | | __| (_)_ __   __ _
| |_ / _ \| |/ _` | | '_ \ / _` |
|  _| (_) | | (_| | | | | | (_| |
|_|  \___/|_|\__,_|_|_| |_|\__, |
                           |___/
```

---

# Last time

Didn't quite finish

---

# Next couple of weeks

Talking about tech vision

---

# Today

So will just reinforce fold concepts today

Rather than pushing forward into monoid

---

# Today

- recap


- reduce


- double underscore syntax


- complex fold example

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

# All the same

All these can be used for iteratively doing: `State => State`

- loop


- tail recursion


- fold

---

# Standard library

The `fold` we built was a DIY version of `foldLeft` from the standard library

---

# foldLeft vs foldRight

- performance can be very different


- result can be very different

---

# Performance Example

```scala
List(m elements) ++ List(n elements) // O(m) time
```

Keep big lists on the right

---

# foldLeft

> Keep big lists on the right

```scala
.foldLeft(Nil)((acc, next) => acc ++ next)
```

Accumulator is on the left

With each iteration it gets bigger and bigger

You recopy the same elements over and over

O(n^2)

---

# foldRight

> Keep big lists on the right

```scala
.foldLeft(Nil)((next, acc) => next ++ acc)
```

Accumulator is on the right

Once data gets into the accumulator, it never has to be recopied

O(n)

---

# Complex example

In this example the `combine` logic itself has significant time complexity

When `combine` is O(1), folding from the left or right should produce O(n)

---

# Aside

> In this example the `combine` logic itself has significant time complexity

Watch out for hidden cost with intermediate structures

---

# String example

> Watch out for hidden cost with intermediate structures

```scala
Seq("abc", "def", "ghi", "jkl").foldLeft("")((acc, next) => acc + next)
```

---

# Unwind

```scala
Seq("abc", "def", "ghi", "jkl").foldLeft("")((acc, next) => acc + next)
```

Lots of copying

```
     "abc" "def" "ghi" "jkl"
_____._____._____._____._____
""


     _____ "def" "ghi" "jkl"
     _____._____._____._____
     "abc"


           _____
           _____ "ghi" "jkl"
           _____._____._____
          "abcdef"

                 _____
                 _____
                 _____ "jkl"
                 _____._____
               "abcdefghi"

                      _____
                      _____
                      _____
                      _____
                      _____
               "abcdefghijkl"

```

O(n^2)

---

# Analogous

```scala
Seq("abc", "def", "ghi", "jkl").foldLeft("")((acc, next) => acc + next)
```

String is like `List[Char]`

This fold is analogous to our earlier example as `List[List[Char]]`

---

# mkString

Methods like `mkString` will be more performant

```scala
// bad
Seq("abc", "def", "ghi", "jkl").foldLeft("")((acc, next) => acc + next)

// good
Seq("abc", "def", "ghi", "jkl").mkString
```

Also has some nice optional arguments

Probably uses the `StringBuilder` under the hood

---

# Not nice operators

`foldLeft` and `foldRight` might give different results for "not nice" operators

---

# Example

## foldLeft

```scala
List(2, 3, 4).foldLeft(1)((acc, next) => next^acc)
```

```
            acc
-------------------
(seed)        1
| 2 |       2^1
| 3 |    3^(2^1)
| 4 | 4^(3^(2^1))

        262,144
```

## foldRight

```scala
List(2, 3, 4).foldRight(1)((next, acc) => next^acc)
```

```
            acc
-------------------
(seed)        1
| 4 |       4^1
| 3 |    3^(4^1)
| 2 | 2^(3^(4^1))

       2,417,851,639,229,258,349,412,352
```

---

# Unusual example

More likely you'd use `foldRight` for performance reasons

---

```
              _
 _ __ ___  __| |_   _  ___ ___
| '__/ _ \/ _` | | | |/ __/ _ \
| | |  __/ (_| | |_| | (_|  __/
|_|  \___|\__,_|\__,_|\___\___|

```

---

# Cousins

`foldLeft/Right` has cousins `reduceLeft/Right`

---

# Demo time

To the repl!

---

# Recap

`reduce` functions just wrap their respective `fold` functions:

```scala
def reduceLeft[A](seq: Seq[A])(combine: (A, A) => A): A = seq.tail.foldLeft(seq.head)(combine)
```

- passes the head


- calls `foldLeft` on the tail


- only 1 type parameter (as the seed is `A`)

---

# Safety alert

Reduce functions are unsafe...

```scala
def reduceLeft[A](seq: Seq[A])(combine: (A, A) => A): A = seq.tail.foldLeft(seq.head)(combine)
```

Why?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Safety alert

Reduce functions are unsafe...

```scala
def reduceLeft[A](seq: Seq[A])(combine: (A, A) => A): A = seq.tail.foldLeft(seq.head)(combine)
//                                                            ^^^^              ^^^^
```

Assumes the sequence is non-empty

Will throw exceptions if it's not

---

# Safety alert

Whenever you use `head` or `tail` without a non-emptiness check,

a baby seal is used to club a puppy to death

```scala
def reduceLeft[A](seq: Seq[A])(combine: (A, A) => A): A = seq.tail.foldLeft(seq.head)(combine)
//                                                            ^^^^              ^^^^
```

---

# Generally

Unusual that you need to use `reduce` functions

`fold` functions are safer and force you to consider the emptiness case

---

```
 ____              _     _
|  _ \  ___  _   _| |__ | | ___
| | | |/ _ \| | | | '_ \| |/ _ \
| |_| | (_) | |_| | |_) | |  __/
|____/ \___/ \__,_|_.__/|_|\___|

 _   _           _
| | | |_ __   __| | ___ _ __ ___  ___ ___  _ __ ___  ___
| | | | '_ \ / _` |/ _ \ '__/ __|/ __/ _ \| '__/ _ \/ __|
| |_| | | | | (_| |  __/ |  \__ \ (_| (_) | | |  __/\__ \
 \___/|_| |_|\__,_|\___|_|  |___/\___\___/|_|  \___||___/

```

---

# Double underscore syntax

Sometimes you see code like:

```scala
List(1, 2, 3).foldLeft(0)(_ + _)
```

---

# Confusion

> Sometimes you see code like:

```scala
List(1, 2, 3).foldLeft(0)(_ + _)
```

So we can use double underscores?

Could I refactor this:

```scala
List(1, 2, 3, 4).map(i => i + i)
// List(2, 4, 6, 8)
```
to this:

```scala
List(1, 2, 3, 4).map(_ + _)
```

---

# Computer says no

```scala
List(1,2,3,4).map(_ + _)
```

```
missing parameter type for expanded function...
val res19 = List(1,2,3,4).map(_ + _)
                              ^
missing parameter type for expanded function...
val res19 = List(1,2,3,4).map(_ + _)
                                  ^
Compilation Failed
```

---

# Underscore syntax

An underscore refers to an unnamed parameter used _once_

---

# Huh?

> An underscore refers to an unnamed parameter used once

But it's used twice here:

```scala
List(1, 2, 3).foldLeft(0)(_ + _)
```

---

# Two parameters

> An underscore refers to an unnamed parameter used once

Each underscore refers to a different parameter:

```scala
List(1, 2, 3).foldLeft(0)(_ + _)
//                        ^   ^
//                       acc next

// is really

List(1, 2, 3).foldLeft(0)((acc, next) => acc + next)
```

ie. you can use two underscores because there's 2 parameters

---

# Ordering

The underscores are bound in left to right order:

```scala
List(1, 2, 3).foldLeft(0)((acc, next) => acc + next)

List(1, 2, 3).foldLeft(0)(_ + _)
//                        ^   ^
//                       acc next
```

---

# foldRight example

If we want to do: ((24/4)/3)/2

```scala
Seq(2, 3, 4).foldRight(24)(_ / _)
```

Test in the repl!

---

# Failure?

```scala
Seq(2, 3, 4).foldRight(24)(_ / _)
```

Expecting 1, but got:

```
java.lang.ArithmeticException: / by zero
```

Any ideas?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Underscores swapped

```scala
// Wanted this:
Seq(2, 3, 4).foldRight(24) { (next, acc) => acc / next }

// But this:
Seq(2, 3, 4).foldRight(24)(_ / _)
//                         1   2

// is really this:
Seq(2, 3, 4).foldRight(24) { (next, acc) => next / acc }
//                            1     2       1      2

```

We did: `2/(3/(4/24))`

and 4/24 is 0

---

# Careful

Multi underscore syntax only makes sense

if your parameters appear in the same left to right order in your expression

---

# Note

Use this double underscore prudently

Sometimes it makes code much cleaner

Sometimes having explicit names is really helpful

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

---

# Victories

Collect all the numbers from a `Seq[Int]` that are larger than their predecessors

---

# Victories

> Collect all the numbers from a `Seq[Int]` that are larger than their predecessors

```scala
def victories(seq: Seq[Int]): Seq[Int] = ???

victories(Seq(4, 10, -3, 2, 1, 0, 10, 8, 11))

//        Seq(?, 10,     2,       10,    11)
```

---

# Clarify the spec

> Is the first number considered greater than its predecessor?

Let's say no, ie. a predecessor must exist

> Strictly larger or geq?

Let's say strictly larger

> Just confirming, if the input was empty or a single element, you'd expect empty output?

Yup

> Does the order of victors matter?

Yup

> Should we remove duplicate victors?

No

---

# Filter?

```scala
def victories(seq: Seq[Int]): Seq[Int] = seq.filter(...)

victories(Seq(4, 10, -3, 2, 1, 0, 10, 8, 11))

//        Seq(   10,     2,       10,    11)
```

Feels like a `filter` problem...

---

# Filter?

```scala
def victories(seq: Seq[Int]): Seq[Int] = seq.filter(i => ...)

victories(Seq(4, 10, -3, 2, 1, 0, 10, 8, 11))

//        Seq(   10,     2,       10,    11)
```

> Feels like a `filter` problem...

But filter can only examine elements in isolation...

You can't see the previous number

---

# Aside: sliding and filter

```scala
def victories(seq: Seq[Int]): Seq[Int] = seq.sliding(2).toSeq.collect {
  case Seq(first, second) if first < second => second
}
```

But we're here to learn about fold

---

# Fold based approach

```scala
def victories(seq: Seq[Int]): Seq[Int] = ???
```

Hit me with some ideas!

```
 ___
|__ \
  / /
 |_|
 (_)
```

Hint: it might help to imagine it as a loop

Hint: systematically figure out `A` and `B`, direction, the seed and the combine

---

# Systematic

```scala
def victories(seq: Seq[Int]): Seq[Int] = ???
```

> Hint: systematically figure out `A` and `B`, direction, the seed and the combine

A: `Int`

B: `(Option[Int], Seq[Int])` - previous number (if any), and current victories

seed: `(None, Seq.empty[Int])`

combine: "the current number becomes the new previous, if the current is a victor append it to the victors"

---

# Unwind example

```scala
def victories(seq: Seq[Int]): Seq[Int] = seq.filter(i => ...)

victories(Seq(4, 10, -3, 2, 1, 0, 10, 8, 11))

//        Seq(   10,     2,       10,    11)
```

```
next                 acc
              last        victors
-----------------------------------
              None       Seq.empty
4             Some(4)    Seq.empty
10            Some(10)   Seq(10)
-3            Some(-3)   Seq(10)
2             Some(2)    Seq(10, 2)
1             Some(1)    Seq(10, 2)
...
11            Some(11)   Seq(10, 2, 10, 11)

                         ^^^^^^^^^^^^^^^^^^
                         Just return this bit
```

Left part of the accumulator is an internal implementation detail

Once the fold is done we don't need it

---

# Fill in the gaps

```scala
def victories(seq: Seq[Int]): Seq[Int] = {
  val (_, result) = seq.foldLeft[(Option[Int], Seq[Int])]((None, Seq.empty[Int])) { ... }
                    // direction --------- B -----------   ------ seed --------
                    //               last      victors     last     victors

  result
}
```

A: `Int`

B: `(Option[Int], Seq[Int])` - previous number (if any), and current victories

direction: left to right makes sense because then we can know the predecessor

seed: `(None, Seq.empty[Int])`

combine: "update the previous number to Some(current), if it's a victory append it to sequence of victories"

---

# Code it up!

---

# Summary

```scala
def victoriesFold(seq: Seq[Int]): Seq[Int] = {
  val (_, result) = seq.foldLeft[(Option[Int], Seq[Int])]((None, Seq.empty[Int])) {
    case ((None, victors), next) => (Some(next), victors)
    case ((Some(last), victors), next) =>
      val newVictors = if (next > last) victors :+ next else victors
      (Some(next), newVictors)
  }
  result
}
```

---

# Alternative

Having that `Option` to handle just one case is a little icky

Also we know that the first element can't be a victor...

```scala
def victoriesNoOption(seq: Seq[Int]): Seq[Int] = seq match {
  case _ if seq.isEmpty => seq
  case head :: tail =>
    val (_, result) = tail.foldLeft[(Int, Seq[Int])]((head, Seq.empty[Int])) {
      case ((previous, victors), next) =>
        val newVictors = if (previous < next) victors :+ next else victors
        (next, newVictors)
    }
    result
}
```

Has a "reduce" vibe, but you can't reduce into other types

---

# Tweak the example: super victors

> Collect all the victors from a `Seq[Int]` that are larger than the previous super victor

```scala
def victories(seq: Seq[Int]): Seq[Int] = ???

victories(Seq(4, 10, -3, 2, 1, 0, 10, 8, 11))

//        Seq(   10,     2,       10,    11)  // original

//        Seq(   10,                     11)  // tweaked
```

---

# Clarify spec

> Assuming that our concept of victor hasn't changed

Correct!

> So regular victors need a predecessor, but does the first super victor need a predecessor victor?

Nope, otherwise you'd never get your initial super victor

```scala
def victories(seq: Seq[Int]): Seq[Int] = ???

victories(Seq(4, 10, -3, 2, 1, 0, 10, 8, 11))

//        Seq(   10,     2,       10,    11)  // original

//        Seq(   10,                     11)  // tweaked
//               ^ no predecessor victor
```

---

# Systematic

```scala
def victories(seq: Seq[Int]): Seq[Int] = ???

victories(Seq(4, 10, -3, 2, 1, 0, 10, 8, 11))

//        Seq(   10,     2,       10,    11)  // original

//        Seq(   10,                     11)  // tweaked
```

A - `Int`

B - `(last element, last super victor, super victors)`

direction - left to right

seed - (`None`, `None`, `Seq.empty[Int]`)

combine - figure out if you're a victor, and if so, a super victor

---

# Code it up?

Nah, it's for homework

---

# Solution

```scala
def superVictories(seq: Seq[Int]): Seq[Int] = {
  val (_, _, result) = seq.foldLeft[(Option[Int], Option[Int], Seq[Int])]((None, None, Seq.empty[Int])) {
    case ((None, _, victors), next) => (Some(next), None, victors)
    case ((Some(last), lastSuperVictorOpt, victors), next) =>
      val isVictor = next > last
      val isSuperVictor = isVictor && lastSuperVictorOpt.map { _ < next }.getOrElse(true)
      val newVictors = if (isSuperVictor) victors :+ next else victors
      val newSuperVictorOpt = if (isSuperVictor) Some(next) else lastSuperVictorOpt
      (Some(next), newSuperVictorOpt, newVictors)
  }
  result
}

superVictories(Seq(3, -3, 4, 10, 6, 7, 11, -3, 0, 12))
// Seq(4, 10, 11, 12)
```

Note this solution is overly complex

You only need to keep track of the last super victor

If your `next` is larger than the last super victor, it must be larger than everything in between as well,

for if it weren't, the last super victor would have been replaced by a larger value

You'd end up with a solution very similar to the original problem's

I implemented it this way to stay within the spirit of the problem, i.e tackling a problem with complex state

---

# foldRight use case

If victor was redefined as "larger than the next element" (instead of previous)

this would have been a legitimate `foldRight` use case

---

# Toy example

But there are examples of this kind of complex aggregation

The complexity comes from relationships between elements

---

# fold use case

> The complexity comes from relationships between elements

Simple combinators like `map` and `filter` often fail here

because they examine elements in isolation

---

# Enough for today

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

# Careful

When folding, _be mindful_ of wasteful intermediate state and copying

Fold is only O(n) time if `combine` is O(1)

Inefficient `combine` can make your fold O(n^2) (or worse!)

---

# reduceLeft, reduceRight

Unsafe versions of fold

Use the first element as a seed

Everytime you use it, Chuck Norris round house kicks a pony

---

# double underscore syntax

```scala
List(1, 2, 3).foldLeft(0)(_ + _)
```

Can reduce noise in some cases

Newbies find it harder to read

Beware of accidentally reversing them

---

# fold

Very powerful for cases where there is complex accumulation logic,

where processing relies on knowledge of previous elements

---

# Coming up...

Two weeks to talk about tech vision

Then Monoids! (sounds scary!)

And probably a survey...

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
