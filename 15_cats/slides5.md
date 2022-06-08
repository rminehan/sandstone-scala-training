---
author: Rohan
date: 2022-06-09
title: Traverse
---

```
 _____
|_   _| __ __ ___   _____ _ __ ___  ___
  | || '__/ _` \ \ / / _ \ '__/ __|/ _ \
  | || | | (_| |\ V /  __/ |  \__ \  __/
  |_||_|  \__,_| \_/ \___|_|  |___/\___|

```

---

# Today

- sequence (examples, concepts)


- traverse (examples, concepts)

---

# Sequence

Little brother of traverse

Easier to start there and work upwards to traverse

---

# Elevator Pitch

What is the `Traverse` type class?

> The type class of "switcheroo" type constructors...

---

# Terminology

`sequence != Seq`

---

# Familiar

`Future.traverse` and `Future.sequence`

---

# Cats

`Traverse` is part of cats

To the browser!

---

```
 ____
/ ___|  ___  __ _ _   _  ___ _ __   ___ ___
\___ \ / _ \/ _` | | | |/ _ \ '_ \ / __/ _ \
 ___) |  __/ (_| | |_| |  __/ | | | (_|  __/
|____/ \___|\__, |\__,_|\___|_| |_|\___\___|
               |_|
```

---

# Examples

We'll rangle a `Seq[Option[...]]` into `Option[Seq[...]]`

Let's solve this with `sequence` from cats

To the repl!

---

# Summary

```scala
import $ivy.`org.typelevel::cats-core:2.7.0`

import cats.syntax.traverse._

def getAgeFromId(id: String): Option[Int] = id match {
  case "boban" => Some(26)
  case "bobanita" => Some(25)
  case "tim" => Some(40)
  case _ => None
}

val goodIds = Seq("boban", "bobanita", "tim")

goodIds.map(getAgeFromId).sequence
// Type:  Option[Seq[Int]]
// Value: Some(Seq(26, 25, 40))

val badIds = Seq("boban", "jane", "bobanita", "feroz")

badIds.map(getAgeFromId).sequence
// Type:  Option[Seq[Int]]
// Value: None
```

---

# Conceptually

What we did:

```
                  sequence
Seq[Option[Int]]   ----->    Option[Seq[Int]]
```

---

# More generally

```
          sequence
F[G[A]]    ----->    G[F[A]]
```

F is a member of the `Traverse` type class,

if it has a way to switcheroo with other type constructors (represented by G)

---

# Future example

This time our searches are represented by `Future[Int]` instead of `Option[Int]`

To the repl!

---

# Summary

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

def getAgeFromId(id: String): Future[Int] = id match {
  case "boban" => Future.successful(26)
  case "bobanita" => Future.successful(25)
  case "tim" => Future.successful(40)
  case _ => Future.failed(new Exception("ID not found"))
}

goodIds.map(getAgeFromId).sequence
// Type:  Future[Seq[Int]]
// Value: Future(Success(Seq(26, 25, 40)))

badIds.map(getAgeFromId).sequence
// Type:  Future[Seq[Int]]
// Value: Future(Failure(java.lang.Exception: ID not found))
```

---

# What we did

```
                  sequence
F[G[A]]            ----->    G[F[A]]



F = Seq
G = Future
A = Int
                  sequence
Seq[Future[Int]]   ----->    Future[Seq[Int]]
```

---

# Aside

Deja vu

```scala
val futures: Seq[Future[Int]] = ...

// Built into stdlib, just for sequences of Future
Future.sequence(futures)

// Applies to any type class
futures.sequence
```

---

# Comparing

```scala
val futures: Seq[Future[Int]] = ...

// Built into stdlib, just for sequences of Future
Future.sequence(futures)

// Applies to any type class
futures.sequence
```

Both are expressions of:

```
Seq[Future[A]]   ---->   Future[Seq[A]]
```

The cats approach is based on a formal type class

---

# Try with Triplet

```
Seq[Triplet[Int]]  ----->   Triplet[Seq[Int]]

   a b c
   d e f
   x x x           ----->   a d x x x x x x
   x x x                    b e x x x x x x
   x x x                    c f x x x x x x
   ...
   x x x
```

To the repl!

---

# Summary

```scala
// 2x3 shape
val triplets = Seq(Triplet(1, 2, 3), Triplet(4, 5, 6))

triplets.sequence // Doesn't compile as there's no Applicative instance for Triplet in scope

import cats.Applicative
implicit object TripletApplicative extends Applicative[Triplet] {
  ...
}

// 3x2 shape
triplets.sequence // much better
// Triplet(Seq(1, 4), Seq(2, 5), Seq(3, 6))
```

---

# Important discovery

G must be applicative!

```
                  sequence
F[G[A]]            ----->    G[F[A]]



F = Seq
G = Triplet (applicative)
A = Int
                  sequence
Seq[Triplet[Int]]  ----->    Triplet[Seq[Int]]
```

---

# Why?

> G must be applicative!

Under the hood, applicative is being used to combine elements

---

# Option Example

```scala
Seq(Option(1), Option(2), Option(3), None, Option(5)).sequence
```

---

# Option Example

```scala
Seq(Option(1), Option(2), Option(3), None, Option(5)).sequence
```

```
Seq[Option[Int]]  ----->  Option[Seq[Int]]
```

Internally does a kind of fold:

```
next        acc
------------------------------
            Some(Seq.empty)
Some(1)     Some(Seq(1))
Some(2)     Some(Seq(1, 2))
Some(3)     Some(Seq(1, 2, 3))
None        None                   short circuits
Some(5)     None


     (acc, next).mapN {
       case (seq, i) => seq.append(i)
     }
```

Each step combines two `Option`'s to make a new `Option`

Effectively switcheroo's the type constructors

---

# Shorter summary

> Why is `G` applicative?

Because internally mapN is used to combine G's

and mapN requires applicative

---

# If that didn't make sense...

... don't worry

---

# Being practical

You don't need a deep understanding of `Applicative` to practically use `sequence` and `traverse`

---

# Summary: sequence

`F` is a member of the `Traverse` type class if it supports the switcheroo operator:

```
                  sequence
F[G[A]]            ----->    G[F[A]]
```

(provided `G` is `Applicative`)

---

# Summary: example

The only example we've seen so far is `Seq`:

```
Seq[Option[Int]]   ---->   Option[Seq[Int]]
Seq[Future[Int]]   ---->   Future[Seq[Int]]
Seq[Triplet[Int]]  ---->   Triplet[Seq[Int]]
```

---

# Question

Can you double switcheroo?

---

# Question

Can you double switcheroo?

ie. can you sequence twice and get back to where you started?

---

# Option Example

```scala
// Start here
Seq(Option(1), Option(2), Option(3))
// Type: Seq[Option[Int]]
// Value: Seq(Some(1), Some(2), Some(3))

Seq(Option(1), Option(2), Option(3)).sequence
// Type:  Option[Seq[Int]]
// Value: Some(Seq(1, 2, 3))

Seq(Option(1), Option(2), Option(3)).sequence.sequence
// Type:  Seq[Option[Int]]
// Value: Seq(Some(1), Some(2), Some(3))
```

This works

And we got back where we started!

---

# Seq example

```scala
// Start here
Seq(Seq(1, 2, 3), Seq(4, 5, 6, 7))
// Type:  Seq[Seq[Int]]

Seq(Seq(1, 2, 3), Seq(4, 5, 6, 7)).sequence
// Type:  Seq[Seq[Int]]
// Value: Seq(Seq(1, 4), Seq(1, 5), ...)  (length 3x4=12)
// (all the pairs with one representative from each Seq)

Seq(Seq(1, 2, 3), Seq(4, 5, 6, 7)).sequence.sequence
// Type:  Seq[Seq[Int]]
// Value: Seq(
//   Seq(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3),    |
//   Seq(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 7),    | 2^12=4096
//   Seq(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 6, 3),    | sub-sequences
//       ----------------------------------      | in total
//                 all size 12                   |
```

This works

But it definitely doesn't get us back where started!

Each `.sequence` drastically increases the size

The last one is formed by choosing one representative from 12 sub-sequences of size 2.

---

# Future example

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// Start here
Seq(Future.successful(1), Future.successful(2), Future.successful(3))
// Type:  Seq[Future[Int]]

Seq(Future.successful(1), Future.successful(2), Future.successful(3)).sequence
// Type:  Future[Seq[Int]]
// Value: Future(Success(Seq(1, 2, 3)))  (eventually)

Seq(Future.successful(1), Future.successful(2), Future.successful(3)).sequence.sequence
// computer says no...
// Error: "value sequence is not a member of scala.concurrent.Future[Seq[Int]]"
```

This isn't possible

`Future` is not a member of the `Traverse` type class and so doesn't support `sequence`

---

# Back to our question

> Can you double switcheroo?

The first `F[G[A]].sequence` requires:

- `F` is `Traverse`
- `G` is `Applicative`

Once they're switcheroo'd the next step is `G[F[A]].sequence` which requires:

- `G` is `Traverse`
- `F` is `Applicative`

---

# Back to our question

> Can you double switcheroo?

If `F` and `G` are both `Traverse` and `Applicative` then you can

---

# Self-inverting?

> ... and get back to where you started?

No

Even if you _can_ double switcheroo,

there's no guarantee you'll end up back at the same value

(as with `Seq` example)

---

# Next up: traverse

---

```
 _
| |_ _ __ __ ___   _____ _ __ ___  ___
| __| '__/ _` \ \ / / _ \ '__/ __|/ _ \
| |_| | | (_| |\ V /  __/ |  \__ \  __/
 \__|_|  \__,_| \_/ \___|_|  |___/\___|
```

(the function)

---

# traverse

A generalisation of `sequence`

---

# Example

Easiest to jump straight to an example

To the repl!

---

# Summary

```scala
goodIds.map(getAgeFromId).sequence
// Some(Seq(26, 25, 40))

goodIds.traverse(getAgeFromId)
// Some(Seq(26, 25, 40))
```

`.traverse(f)` is equivalent to `.map(f).sequence`

---

# Two steps combined

```
next          f(a)              acc
-------------------------------------------------
                          Some(Seq.empty)
"boban"      Some(26)     Some(Seq(26))
"bobanita"   Some(25)     Some(Seq(26, 25))
"tim"        Some(40)     Some(Seq(26, 25, 40))
"jane"       None         None



       (acc, f(a)).mapN {
         case (seq, i) => seq.append(i)
       }
```

Each iteration of the fold computes the next `f(a)`

then combines it into the accumulator

---

# Two steps combined

```
next          f(a)              acc
-------------------------------------------------
                          Some(Seq.empty)
"boban"      Some(26)     Some(Seq(26))
"bobanita"   Some(25)     Some(Seq(26, 25))
"tim"        Some(40)     Some(Seq(26, 25, 40))
"jane"       None         None
```

> Each iteration of the fold computes the next `f(a)`
>
> then combines it into the accumulator

`.map(f).sequence` would create the middle column as an intermediate collection

---

# Space Complexity

`.traverse(f)`: O(1)

`.map(f).sequence`: O(n)  (creates an intermediate collection)

---

# Conceptually

What would the type diagram for `traverse` look like?

---

# Specific example

> What would the type diagram for `traverse` look like?

Our specific example:

```scala
val ids = Seq("boban", "bobanita", "tim")

def getAgeFromId(id: String): Option[Int] = ...

ids.traverse(getAgeFromId)
// Some(Seq(26, 25, 40))
```

What are the inputs and output?

---

# Specific example

> What are the inputs and output?

```scala
// INPUTS
// Seq[String]
val ids = Seq("boban", "bobanita", "tim")
// String => Option[Int]
def getAgeFromId(id: String): Option[Int] = ...

// OUTPUT
// Option[Seq[Int]]
ids.traverse(getAgeFromId)
// Some(Seq(26, 25, 40))
```

---

# Abstracting

```
F[A]          traverse     G[F[B]]
A => G[B]     -------->


F = Seq
A = String
G = Option
B = Int


Seq[String]  -------->    Option[Seq[Int]]
String => Option[Int]
```

---

# Big brother traverse

```
F[A]          traverse     G[F[B]]
A => G[B]     -------->


              sequence
F[G[A]]       -------->    G[F[A]]
```

`sequence` is just a special case of `traverse`

ie. you can derive `sequence` from `traverse`

(homework)

---

# Summary: traverse

More powerful generalisation of `sequence`

Better memory complexity generally

---

# When to use each?

---

# Simple way to remember

Use `traverse` if you can

---

# When you can't

> Use `traverse` if you can

If the data is already mapped (e.g. you're given a `Seq[Future[...]]`)

then you're too late, so just `sequence` it

---

# Other examples

Every example we've done today was with F = Seq

(except one double switcheroo example with Option)

---

# Seq-y things

`Option` and `Triplet` are like mini-sequences

---

# Seq-y things

`Option` and `Triplet` are like mini-sequences

We could copy and specialise that pattern

(Realistically in the wild, it will just be `Seq`)

(See homework for more examples)

---

# Wrapping up

---

# Traverse

A type class for type constructors which support a "switcheroo" operation

---

# Small type class

Most common example is `Seq`

---

# Main use cases

- `Seq[Future[...]]` to `Future[Seq[...]]`


- `Seq[Option[...]]` to `Option[Seq[...]]`

---

# sequence and traverse

`.traverse(f)` is equivalent to `.map(f).sequence`

Use it where possible

---

# Very abstract

If `F` is `Traverse`, you can sequence with any G,

provided it's `Applicative`

---

# Being practical

You don't need a deep understanding of `Traverse` and `Applicative` type classes,

to use `traverse` and `sequence`

---

# Homework

If you want to dig deeper

---

# Homework 1

Earlier we said:

> `sequence` is just a special case of `traverse`

Prove it!

---

# Hint

The question's really saying that if you're provided with an implementation for `traverse`,

you should be able to derive an implementation of `sequence` off that without needing any more info

```scala
trait Traverse[F[_]] {
  def traverse[G[_], A, B](fa: F[A])(f: A => G[B])(implicit ev: Applicative[G]): G[F[B]]

  def sequence[G[_], A](fga: F[G[A]])(implicit ev: Applicative[G]): G[F[A]] = ... // use traverse
}
```

---

# Conceptually

```
F[A]               traverse    G[F[B]]
A => G[B]          -------->


                  sequence
F[G[A]]            ----->      G[F[A]]
```

They're already looking a bit similar

By a clever choice of A and our function f, we can simplify `traverse` to `sequence`

---

# Conceptually

> By a clever choice of A and our function f, we can simplify `traverse` to `sequence`

```
F[A]               traverse    G[F[B]]
A => G[B]          -------->


Replace A with G[A]
Replace B with A
f is identity function


F[G[A]]           traverse     G[F[A]]
G[B] => G[A]      --------->
```

---

# Translating to code

```scala
trait Traverse[F[_]] {
  def traverse[G[_], A, B](fa: F[A])(f: A => G[B])(implicit ev: Applicative[G]): G[F[B]]

  // See cats source: https://github.com/typelevel/cats/blob/188d45d830b27604160c8390c03ed1b5eb170a67/core/src/main/scala/cats/Traverse.scala#L106
  def sequence[G[_], A](fga: F[G[A]])(implicit ev: Applicative[G]): G[F[A]] = traverse(fga)(ga => ga)
}
```

We're passing a `F[G[A]]` into the first parameter of `traverse`

`traverse` is expecting an `F[A]`, to it's `A` is `sequence`'s `G[A]`

Thus for the second parameter it's expecting a `G[A] => G[A]` function

---

# Homework 2

Prove that a binary tree like this is a member of `Traverse`:

```scala
sealed trait Tree[A]
case class Leaf(a: A) extends Tree[A]
case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A]
```

ie. create an instance for this kind of type class:

```scala
import cats.Applicative

trait Traverse[F[_]] {
  def traverse[G[_], A, B](fa: F[A])(f: A => G[B])(implicit ev: Applicative[G]): G[F[B]]
}
```

---

# Solution

```scala
import cats.syntax.apply._ // for mapN

implicit object TreeTraverse extends Traverse[Tree] {
  def traverse[G[_]: Applicative, A, B](fa: Tree[A])(f: A => G[B]): G[Tree[B]] = fa match {
    case Leaf(a) => Applicative[G].map(f(a))(b => Leaf(b))
    case Node(leftA, rightA) => (traverse(leftA)(f), traverse(rightA)(f)).mapN {
      case (leftB, rightB) => Node(leftB, rightB)
    }
  }
}

val tree: Tree[Int] = Node(Node(Leaf(1), Leaf(2)), Leaf(3))
/*
                  /  \
                /  \  3
               1    2
 */

TreeTraverse.traverse(tree)(i => Option(i + 1))
// Type:  Option[Tree[Int]]
// Value: Some(Node(left = Node(left = Leaf(2), right = Leaf(3)), right = Leaf(4)))
```

---

# Note

We're using our own simple `Traverse` type class here, not the one from cats

The one from cats also requires a `Foldable` implementation which is unrelated to today's session

Because we're using our own one,

this means we can't leverage the nice `.sequence/.traverse` syntax so we use the instances directly

---

# Homework 3

Prove that `Triplet` is a member of `Traverse`

ie. create an instance for this kind of type class:

```scala
import cats.Applicative

trait Traverse[F[_]] {
  def traverse[G[_], A, B](fa: F[A])(f: A => G[B])(implicit ev: Applicative[G]): G[F[B]]
}

case class Triplet[A](_1: A, _2: A, _3: A)

implicit object TripletTraverse extends Traverse[Triplet] {
  ...
}
```

---

# Hint

Triplet is like a sequence with 3 elements

We don't need a fold when we know all 3 elements up front

We can feed all 3 through `f` to get 3 `G[B]`'s,

then because `G` is applicative, we can `map3` them into a `G[Triplet[B]]`

```scala
val Triplet(a1, a2, a3) = fa
(f(a1), f(a2), f(a3)).mapN {
  case (b1, b2, b3) => Triplet(b1, b2, b3)
}
```

---

# Solution 3

```scala
import cats.syntax.apply._ // for mapN

implicit object TripletTraverse extends Traverse[Triplet] {
  def traverse[G[_], A, B](fa: Triplet[A])(f: A => G[B])(implicit ev: Applicative[G]): G[Triplet[B]] = {
    val Triplet(a1, a2, a3) = fa
    (f(a1), f(a2), f(a3)).mapN {
      case (b1, b2, b3) => Triplet(b1, b2, b3)
    }
  }
}

// Example you can play with
// Let a Triplet represent a name we want to uppercase, but only if all parts of the name are non-empty
def upperCaseIfNonEmpty(namePart: String): Option[String] = if (namePart.nonEmpty) Some(namePart.toUpperCase) else None

val goodName = Triplet("Bobanita", "Hayworth", "Jones")
TripletTraverse.traverse(goodName)(upperCaseIfNonEmpty)
// Some(Triplet("BOBANITA", "HAYWORTH", "JONES"))

val badName = Triplet("Boban", "", "Jones")
TripletTraverse.traverse(badName)(upperCaseIfNonEmpty)
// None
```

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
