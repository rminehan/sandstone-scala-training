---
author: Rohan
date: 2022-03-14
title: Tail recursion
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

# Today

- limitations of regular recursion


- benefits of tail recursion

---

# Factorial Homework

Back when we looked at time/space complexity

---

# Homework

```scala
// fac(0) = 1
// fac(n) = n * fac(n - 1), for n > 0
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
}
```

What is the time/space complexity of this implementation of factorial?

(and what are some issues with my definition and implementation)

---

# Time Complexity?

```scala
// fac(0) = 1
// fac(n) = n * fac(n - 1), for n > 0
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
}
```

O(n), it does n recursions with a multiplication in each

```
fac(n) = n * (n-1) * (n-2) * ... * 3 * 2 * 1
```

---

# Space Complexity?

```scala
// fac(0) = 1
// fac(n) = n * fac(n - 1), for n > 0
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
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

# Space Complexity?

```scala
// fac(0) = 1
// fac(n) = n * fac(n - 1), for n > 0
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
}
```

O(n)

Every time it recurses, it adds a frame to the callstack (which uses memory)

It recurses about n times, hence n stack frames

---

# Visually

```scala
// fac(0) = 1
// fac(n) = n * fac(n - 1), for n > 0
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
}
```

```
 fac(0) / \  (base case)
 fac(1)  |
 fac(2)  |                      n stack frames
 fac(3)  |
 fac(4)  |
 fac(5)  |
```

---

# Visually

```scala
// fac(0) = 1
// fac(n) = n * fac(n - 1), for n > 0
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
}
```

```
 fac(0) / \  (base case)   |
 fac(1)  |                 |
 fac(2)  |                 | pop frames
 fac(3)  |                 | at the end
 fac(4)  |                 |
 fac(5)  |                \ /
```

---

# Other issues

> (and what are some issues with my definition and implementation)

```scala
// fac(0) = 1
// fac(n) = n * fac(n - 1), for n > 0
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
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

# Integer overflow

```scala
def fac(n: Int): Int = ...
//               ^^^
```

How many bytes is an int on the JVM?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Integer overflow

```scala
def fac(n: Int): Int = ...
//               ^^^
```

> How many bytes is an int on the JVM?

4 (signed)

Meaning they max out around 2.1 billion

Factorial will race past that at n=13

To the repl!

---

# Stack frames

What happens if we put a really large n in?

(imagine we have unlimited memory)

```scala
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
}
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

Hint: stackoverflow.com might help here

---

# Stack overflow! (SO)

> What happens if we put a really large n in?

```scala
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
}
```

You run out of stack frames and the JVM throws a stack overflow!

Let's try it! To the repl!

---

# Negative input?

Don't handle it properly,

would recurse all the way to Int.MinValue,

then loop around to Int.MaxValue

and down to 0

(assuming we fixed SO)

```scala
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
  //                  ^
}
```

---

# Summary of issues

- O(n) space complexity


- stack overflow


- integer overflow


- negative inputs

---

# Summary of issues

- O(n) space complexity


- stack overflow


- integer overflow


- negative inputs

First 2 are related to recursion, last 2 are specific to this problem (ignore them for simplicity)

---

# Java Developers

> - O(n) space complexity
> - stack overflow

Understandably a lot of java developers don't like recursion

It doesn't scale well

---

# Alternatives

> Understandably a lot of java developers don't like recursion

Put on your java hats (sorry if it gives you a migraine),

how would you solve factorial such that:

- O(1) space complexity


- "stack safe" (ie. no stack overflows)

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Loop and accumulator

To the repl!

---

# Summary

Loop vs Recursion

```
fac(0) = 1
fac(n) = n * fac(n - 1)
```

## Loop

```scala
def fac(n: Int): Int = {
  var acc = 1

  for (i <- 1 to n)
    acc *= i

  acc
}
```

Stack safe

O(1) space

Ugly, doesn't encode our mathematical understanding cleanly

---

# Summary

Loop vs Recursion

```
fac(0) = 1
fac(n) = n * fac(n - 1)
```

## Recursion

```scala
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
}
```

Stack overflows

O(n) space

Elegantly encodes the problem

---

# Tension

Beauty vs Performance/Safety

---

# Introducing...

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

# "Naive" recursion

```scala
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
}
```

What we did is called "naive" recursion

It's the first way you'd think to do it

(and it tends to have issues)

---

# "Tail" recursion summary

An alternative form of recursion

- keeps some of the elegance of recursion


- removes performance/safety issues

---

# "Tail" recursion summary

An alternative form of recursion

- keeps some of the elegance of recursion


- removes performance/safety issues

Best of both worlds mostly

---

# So...

If you've got a java developer's distrust of recursion,

the recursion you distrust is _naive_ recursion,

not tail recursion

---

# FP world

FP devs would always prefer tail recursion to a loop

(or they use some other trick)

---

# What is it?

---

# Definition

A function is tail recursive, if all recursive calls are in "tail" position

ie. it's the last thing done in its code path

---

# ConsList Examples

> A function is tail recursive, if all recursive calls are in "tail" position

```scala

  def map(list: ConsList, f: Int => Int): ConsList = {
    list match {
      case Terminus => Terminus
      case ConsCell(head, tail) =>
        val newHead: Int = f(head)
        val newTail: ConsList = map(tail, f)
        //                      ^^^ Not in tail position
        ConsCell(newHead, newTail)
    }
  }


  def drop(list: ConsList, length: Int): ConsList = {
    if (length <= 0) list
    else list match {
      case Terminus => Terminus
      case ConsCell(_, tail) => drop(tail, length - 1)
      //                        ^^^^
    }
  }
```

---

# Factorial?

```scala
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
}
```

Is `fac` tail recursive?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Nope

```scala
def fac(n: Int): Int = n match {
  case 0 => 1
  case n =>
    val left = n
    val right = fac(n - 1)
    left * right
}
```

> Is `fac` tail recursive?

Nope

The last operation on that code path is `*`

---

# Who cares?

Why does it matter if recursive calls are in tail position?

---

# Who cares?

> Why does it matter if recursive calls are in tail position?

Because it allows the compiler to rewrite your code into a loop

- one stack frame


- constant stack space


- no stack overflow

---

# Factorial

Let's try to make it tail recursive

To the repl!

---

# Summary

```scala
def fac(n: Int): BigInt = {
  def facTail(n: Int, acc: BigInt): BigInt = n match {
    case 0 => acc
    case _ => facTail(n - 1, n * acc)
  }

  facTail(n, 1)
}
```

No stack overflow on large numbers

---

# What we did

Transformed our problem to be tail recursive

```scala
// Naive
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
}

def fac(n: Int): BigInt = {
  // Tail recursion
  def facTail(n: Int, acc: BigInt): BigInt = n match {
    case 0 => acc
    case _ => facTail(n - 1, n * acc)
    //        ^^^^^^^ Tail position
  }

  facTail(n, 1)
}
```

Our solution takes 2 stack frames:

- outer call to `fac`


- inner stack frame for `facTail`

---

# Tail position

To make it more clear:

```scala
def fac(n: Int): BigInt = {
  // Tail recursion
  def facTail(n: Int, acc: BigInt): BigInt = n match {
    case 0 => acc
    case _ =>
      val newN = n - 1
      val newAcc = n * acc
      facTail(newN, newAcc)
  }

  facTail(n, 1)
}
```

---

# That's enough for today

---

# Homework

Write a tail recursive function that reverses a `List[A]`

```scala
def reverse[A](list: List[A]): List[A] = ...

reverse(List(1, 2, 3))
// List(3, 2, 1)
```

---

# Solution

```scala
def reverse[A](list: List[A]): List[A] = {

  @tailrec
  def reverseTail(list: List[A], acc: List[A]): List[A] = list match {
    case Nil => acc
    case head :: tail => reverseTail(tail, head :: acc)
  }

  reverseTail(list, Nil)
}
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

# Naive recursion

Uses a lot of stack memory

Stack overflow

But often is a nicer encoding of the problem

---

# Loops and acc

Stack safe in that it uses one stack frame

Often ugly and imperative

---

# Tail recursion

Solves the problems of naive recursion

Still fairly elegant encoding of the problem

---

# Next time

More examples

Tail recursion under the hood

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
