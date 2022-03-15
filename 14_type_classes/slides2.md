---
author: Rohan
date: 2022-03-28
title: Folding continued
---

```
 _____     _     _ _
|  ___|__ | | __| (_)_ __   __ _
| |_ / _ \| |/ _` | | '_ \ / _` |
|  _| (_) | | (_| | | | | | (_| |
|_|  \___/|_|\__,_|_|_| |_|\__, |
                           |___/
```

(continued)

(more laundry to fold)

---

# Last time on scala training...

---

# What is folding?

Reducing a collection to a single value

e.g. summing

```
| 1 |
| 2 |
| 3 |    ----->  15
| 4 |
| 5 |
```

---

# Abstraction journey

Implementations gradually became more abstract and reusable

---

# Concrete implementations

```scala
def sum(numbers: Seq[Int]): Int = {
  var acc = 0
  for (number <- numbers)
    acc += number
  acc
}

def product(numbers: Seq[Int]): Int = {
  var acc = 1
  for (number <- numbers)
    acc *= number
  acc
}

def mkString(strings: Seq[String]): String = {
  var acc = ""
  for (string <- strings)
    acc += string
  acc
}
```

---

# Folding numbers

```scala
def fold(numbers: Seq[Int], seed: Int)(combine: (Int, Int) => Int): Int = {
  var acc = seed

  for (number <- numbers)
    acc = combine(acc, number)

  acc
}

fold(Seq(1, 2, 3), 0)((acc, next) => acc + next)
// 6
```

---

# Folding A's

```scala
def fold[A](seq: Seq[A], seed: A)(combine: (A, A) => A): A = {
  var acc = seed

  for (a <- seq)
    acc = combine(acc, a)

  acc
}

fold(Seq("a", "b", "c"), "")((acc, next) => acc + next)
// "abc"
```

---

# Folding Seq[A] to B

```scala
def fold[B, A](seq: Seq[A], seed: B)(combine: (B, A) => B): B = {
  var acc = seed

  for (a <- seq)
    acc = combine(acc, a)

  acc
}

fold(Seq("abc", "de"), 0)((acc, next) => acc + next.length)
// 5
```

---

# Complex examples

```scala
// Folding to an Option
def max(numbers: Seq[Int]): Option[Int] = {
  fold[Option[Int], Int](numbers, None) {
    case (None, next) => Some(next)
    case (Some(currentMax), next) =>
      val newMax = if (currentMax >= next) currentMax else next
      Some(newMax)
  }
}

// Folding to a List
def reverse[X](list: List[X]): List[X] = {
  fold[List[X], X](list, Nil)((acc, next) => next :: acc)
}
```

---

# Standard library

Saw that our `fold` is analogous to the `foldLeft` from the scala standard library

---

# Today

Misc things related to folding

- time/space complexity


- tail recursion


- foldRight


- reduceLeft, reduceRight


- double underscore syntax

---

```
  ____                      _           _ _
 / ___|___  _ __ ___  _ __ | | _____  _(_) |_ _   _
| |   / _ \| '_ ` _ \| '_ \| |/ _ \ \/ / | __| | | |
| |__| (_) | | | | | | |_) | |  __/>  <| | |_| |_| |
 \____\___/|_| |_| |_| .__/|_|\___/_/\_\_|\__|\__, |
                     |_|                      |___/
```

---

# Time/Space Complexity

```scala
def fold[B, A](seq: Seq[A], seed: B)(combine: (B, A) => B): B = {
  var acc = seed

  for (a <- seq)
    acc = combine(acc, a)

  acc
}
```

What is the time/space complexity of our loop based implementation?

(assume that `combine` takes constant time)

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Time/Space Complexity

```scala
def fold[B, A](seq: Seq[A], seed: B)(combine: (B, A) => B): B = {
  var acc = seed

  for (a <- seq)
    acc = combine(acc, a)

  acc
}
```

> What is the time/space complexity of our loop based implementation?

Time: O(n)   (assumes the sequence is sensible)

Space: O(1)  (ignoring the size of the returned B)

---

```
 _____     _ _
|_   _|_ _(_) |
  | |/ _` | | |
  | | (_| | | |
  |_|\__,_|_|_|
 ____                          _
|  _ \ ___  ___ _   _ _ __ ___(_) ___  _ __
| |_) / _ \/ __| | | | '__/ __| |/ _ \| '_ \
|  _ <  __/ (__| |_| | |  \__ \ | (_) | | | |
|_| \_\___|\___|\__,_|_|  |___/_|\___/|_| |_|
```

---

# Loops

Recall that tail recursion is just the FP representation of a loop

```scala
def fold[B, A](seq: Seq[A], seed: B)(combine: (B, A) => B): B = {
  var acc = seed

  for (a <- seq) // <------ loop!
    acc = combine(acc, a)

  acc
}
```

We used a loop last time as that's probably still more familiar

---

# Migrate

Make it tail recursive to remove that `var`

---

# How?

> Make it tail recursive to remove that `var`

Reconceptualise it as a transformation of state

`State => State`

and a starting value

---

# State transformation

What state is changing on each loop?

- the remainder of the sequence to be processed


- the accumulator

```scala
(seq, acc) => (seq.tail, combine(acc, seq.head))
```

---

# Translating to tail recursion

```scala
// (seq, acc) => (seq.tail, combine(acc, seq.head))

@tailrec
def foldTail(seq: Seq[A], acc: B): B = {
  ...
  // recursive case
  foldTail(seq.tail, combine(acc, seq.head))
}
```

---

# Put it together

To the repl!

---

# Summary

```scala
def fold[B, A](seq: Seq[A], seed: B)(combine: (B, A) => B): B = {
  @tailrec
  def foldTail(seq: Seq[A], acc: B): B = {
    if (seq.isEmpty) acc
    else foldTail(seq.tail, combine(acc, seq.head))
  }

  foldTail(seq, seed)
}

fold(Seq(1,2,3,4,5), 0)((acc, next) => acc + next)
// 15
fold(Seq.empty[Int], 0)((acc, next) => acc + next)
// 0
```

---

# Time for...

... Who wants to be a one-dollar-ionairre!

---

# Who wants to be a one-dollar-ionairre!

What is the time complexity of our tail recursive implementation?

```scala
def fold[B, A](seq: Seq[A], seed: B)(combine: (B, A) => B): B = {
  @tailrec
  def foldTail(seq: Seq[A], acc: B): B = {
    if (seq.isEmpty) acc
    else foldTail(seq.tail, combine(acc, seq.head))
  }

  foldTail(seq, seed)
}
```

(assume that `combine` takes constant time)

```
(A) O(n)       (B) O(n^2)
(C) O(n!)      (D) Not enough info
```

---

# D

> What is the time complexity of our tail recursive implementation?

```scala
def fold[B, A](seq: Seq[A], seed: B)(combine: (B, A) => B): B = {
  @tailrec
  def foldTail(seq: Seq[A], acc: B): B = {
    if (seq.isEmpty) acc
    else foldTail(seq.tail, combine(acc, seq.head))
    //                ^^^^
  }

  foldTail(seq, seed)
}
```

(assume that `combine` takes constant time)

```
(A) O(n)       (B) O(n^2)
(C) O(n!)      (D) Not enough info  <----
```

It will depend on the runtime type of `Seq`

(maybe you were assuming it was a `List`)

---

# .tail

Every iteration is creating a sub-sequence 

```scala
def fold[B, A](seq: Seq[A], seed: B)(combine: (B, A) => B): B = {
  @tailrec
  def foldTail(seq: Seq[A], acc: B): B = {
    if (seq.isEmpty) acc
    else foldTail(seq.tail, combine(acc, seq.head))
    //                ^^^^
  }

  foldTail(seq, seed)
}
```

The time/space complexity of `.tail` depends on the structure

- `List`    O(1)
- `Vector`  O(logn)
- `Array`   O(n) ouch!
- `Range`   O(1) 

---

# Array

```scala
def fold[B, A](seq: Seq[A], seed: B)(combine: (B, A) => B): B = {
  @tailrec
  def foldTail(seq: Seq[A], acc: B): B = {
    if (seq.isEmpty) acc
    else foldTail(seq.tail, combine(acc, seq.head))
    //                ^^^^
  }

  foldTail(seq, seed)
}
```

n loops, and each loop does an O(n) copy

So time is O(n^2)

---

# Aside

This is one downside to using `Seq`

You can't reason as much about the time/space complexity of your code

because you don't know what the runtime sequence will be

In our example, the time complexity could be O(n) or O(n^2)

---

# Standard library

`foldLeft` is probably implemented in a more optimised way

Folding an array is probably O(n)

---

# Observation

These 3 things are fundamentally the same:

- loop that updates state


- tail recursion


- folding

Each represents a transformation of state

---

# Alternatives

So anything you can do with tail recursion,

you can do with folding

(and vice versa)

Depending on your audience, they might prefer one to the other

---

```
  __       _     _ ____  _       _     _   
 / _| ___ | | __| |  _ \(_) __ _| |__ | |_ 
| |_ / _ \| |/ _` | |_) | |/ _` | '_ \| __|
|  _| (_) | | (_| |  _ <| | (_| | | | | |_ 
|_|  \___/|_|\__,_|_| \_\_|\__, |_| |_|\__|
                           |___/           
```

---

# foldRight

The standard library has `foldLeft`,

but it has a friend: `foldRight`

---

# What's the difference?

folding from the left

```
---->

  \    /
   \  /
_____._____._____._____._____


      _____
     ._____._____._____._____

```

folding from the right

```
                      <----

                     \    /
                      \  /
_____._____._____._____._____


                  _____
_____._____._____._____

```

---

# Demo time

To the repl!

---

# Summary - parameter order

foldLeft

```scala
list.foldLeft(seed) {
  case (acc, next) => ...
}
```
```
---->

  \    /
   \  /
_____._____._____._____._____


      _____
     ._____._____._____._____
       acc   next

```

The accumulator is rolling in from the left

---

# Summary - parameter order

foldRight

```scala
list.foldRight(seed) {
  case (next, acc) => ...
}
```
```
                      <----

                     \    /
                      \  /
_____._____._____._____._____


                  _____
_____._____._____._____
            next   acc

```

The parameters reverse in the combine

The accumulator is rolling in from the _right_

---

# Hmmm...

Feels just like `foldLeft`,

when would you use it?

---

# Difference?

> Feels just like `foldLeft`, when would you use it?

- some structures might iterate right to left more naturally


- performance (e.g. concatenating lists of lists)


- operators which aren't "nice"

(These are all going to be unusual)

---

# Performance example

To the repl!

---

# Summary

Logically the same result, but performance is very different

```scala
// O(n^2)
List(List(1), List(2, 3), List(3, 4), List(5, 6)).foldLeft(List.empty[Int]) {
  case (acc, next) => acc ++ next              // ^^^^^^^^
}
// Each concatenation is O(n)

// O(n)
List(List(1), List(2, 3), List(3, 4), List(5, 6)).foldRight(List.empty[Int]) {
  case (next, acc) => next ++ acc              // ^^^^^^^^^
}
// The combined concatenations are roughly O(n)
```

where n is the total number of elements in the final list

---

# Premature optimisation?

```scala
List(List(1), List(2, 3), List(3, 4), List(5, 6)).foldLeft(List.empty[Int]) {
  case (acc, next) => acc ++ next              // ^^^^^^^^
}

List(List(1), List(2, 3), List(3, 4), List(5, 6)).foldRight(List.empty[Int]) {
  case (next, acc) => next ++ acc              // ^^^^^^^^^
}
```

Here there is no additional complexity or cost to using `foldRight`

and it reduces the running time significantly,

so I would prefer it even at small scale

---

# "Nice" and "not nice" operators

---

# Nice operators

We're used to nice operators

---

# Left folding

Unroll this:

```scala
List(1, 2, 3, 4).foldLeft(0)((acc, next) => acc + next)


   0
   0 + 1
  (0 + 1) + 2
 ((0 + 1) + 2) + 3
(((0 + 1) + 2) + 3) + 4
```

Left nested

---

# Right folding

Unroll this:

```scala
List(1, 2, 3, 4).foldRight(0)((next, acc) => next + acc)


                   0
               4 + 0
          3 + (4 + 0)
     2 + (3 + (4 + 0))
1 + (2 + (3 + (4 + 0)))
```

Right nested

---

# Put it together

```scala
List(1, 2, 3, 4).foldLeft(0)((acc, next) => acc + next)
   0
   0 + 1
  (0 + 1) + 2
 ((0 + 1) + 2) + 3
(((0 + 1) + 2) + 3) + 4


List(1, 2, 3, 4).foldRight(0)((next, acc) => next + acc)
                   0
               4 + 0
          3 + (4 + 0)
     2 + (3 + (4 + 0))
1 + (2 + (3 + (4 + 0)))
```

These are not the same computation

They produce the same result because `+` and `0` have "nice" properties

---

# Not nice operators

What if we were folding a "not nice" operator?

Then the direction really matters

---

# Example: power chaining

Form a chain of powers with your seed at the top

```scala
(acc, next) => next^acc // pseudocode
```

1 is a good seed, because putting something to the power of 1 doesn't change it

---

# foldLeft

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

---

# foldRight

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

# So

Sometimes direction matters,

but these are unusual cases for day to day programming

---

# foldLeft vs foldRight

Usually `foldLeft` will make the most sense

In some unusual cases you'll need `foldRight`

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

# Folding and tail recursion

Two expressions of a loop

---

# foldRight

Unusual you'll need it, but can be helpful

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
