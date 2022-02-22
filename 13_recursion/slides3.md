---
author: Rohan
date: 2022-03-21
title: Tail recursion limitations
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

(limitations)

---

# Today

Understanding that not all recursions can be made tail recursive

(but a lot can)

What are some alternatives?

---

# Agenda

- troublesome examples


- trampolining


- aside: `@tailrec` annotation

---

# TLDR

Tail recursion will be fine most of the time

There are some edge cases where it's not

---

```
  ____ _               _
 / ___| | __ _ ___ ___(_) ___
| |   | |/ _` / __/ __| |/ __|
| |___| | (_| \__ \__ \ | (__
 \____|_|\__,_|___/___/_|\___|

 _____                           _
| ____|_  ____ _ _ __ ___  _ __ | | ___
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \
| |___ >  < (_| | | | | | | |_) | |  __/
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___|
                          |_|
```

odd and even

---

# Odd and Even

We need to write these methods:

```scala
def isEven(n: Int): Boolean = ...

def isOdd(n: Int): Boolean = ...
```

(assume `n >= 0`)

(and aliens erased the modulo 2 trick from our brains)

---

# Observations

> We know that 0 is even
>
> We also know that is oscillates:

```
...
5  odd
4  even
3  odd
2  even
1  odd
0  even
```

---

# Example

> We also know that is oscillates:

The question:

> is 3 odd?

is equivalent to:

> is 2 even?

---

# Recurse down to 0

> is 3 odd?
>
> is 2 even?
>
> is 1 odd?
>
> is 0 even? (Yes)

---

# Formulate it recursively

```
isEven:
  0 => true
  n => isOdd(n - 1)

isOdd:
  0 => false
  n => isEven(n - 1)
```

---

# Code it up

To the repl!

---

# Code it up

```scala
{
  def isOdd(n: Int): Boolean = n match {
    case 0 => false
    case _ => isEven(n - 1)
  }

  def isEven(n: Int): Boolean = n match {
    case 0 => true
    case _ => isOdd(n - 1)
  }
}
```

(outer braces is a trick to get around co-dependence)

---

# Stack safe?

```scala
  def isOdd(n: Int): Boolean = n match {
    case 0 => false
    case _ => isEven(n - 1)
  }

  def isEven(n: Int): Boolean = n match {
    case 0 => true
    case _ => isOdd(n - 1)
  }
```

Nope

---

# Co-dependence

Is it that the recursion itself is inherently non-tail recursive?

---

# Co-dependence

> Is it that the recursion itself is inherently non-tail recursive?

```scala
  // Original
  def isOdd(n: Int): Boolean = n match {
    case 0 => false
    case _ => isEven(n - 1)
  }

  def isEven(n: Int): Boolean = n match {
    case 0 => true
    case _ => isOdd(n - 1)
  }

  // Inlined
  def isOdd(n: Int): Boolean = n match {
    case 0 => false
    case _ => n - 1 match { // isEven inlined
      case 0 => true
      case m => isOdd(m - 1) // tail position
    }
  }
```

No

Theoretically if the compiler was really smart it could inline

and make them tail recursive

(so it's not an issue with the problem itself)

---

# Realistically

```scala
  def isOdd(n: Int): Boolean = n match {
    case 0 => false
    case _ => isEven(n - 1) // hidden recursion
  }

  def isEven(n: Int): Boolean = n match {
    case 0 => true
    case _ => isOdd(n - 1) // hidden recursion
  }
```

The compiler is only going to analyse what it sees locally

It won't see the hidden recursive call

You could theoretically inline the logic

---

# Aside

For a silly case like this, there are obvious work arounds

```scala
def isOdd(n: Int): Boolean = n % 2 == 1

@tailrec
def isOdd(n: Int): Boolean = n match {
  case 0 => false
  case 1 => true
  case _ => isOdd(n - 2)
}
```

---

# Co-dependence

> For a silly case like this, there are obvious work arounds

Was using it as a simple example to demonstrate a more general problem of co-dependence

---

# Refactoring

Suppose you have a very complex tail recursive method

```scala
def myBigComplexMethod(...): Int = {
  foo match {

    case 0 =>
      ... // lots of logic
      myBigComplexMethod(...)

    case 1
      ... // lots of logic
      myBigComplexMethod(...)

    ...
  }

}
```

You realise there is code duplication,

or you just want to move it into a private method

---

# Refactoring

```scala
def myBigComplexMethod(...): Int = {
  foo match {

    case 0 =>
      helper(0)

    case 1
      helper(1)

    ...
  }

}

def helper(i: Int): Int = {
  ... // lots of logic
  myBigComplexMethod(...)
}
```

Logically the same

No longer tail recursive

---

# Summary of example

Compiler has limitations

Even if something is conceptually tail recursive,

the compiler can't always optimise it

---

```
 _____ _ _                                _
|  ___(_) |__   ___  _ __   __ _  ___ ___(_)
| |_  | | '_ \ / _ \| '_ \ / _` |/ __/ __| |
|  _| | | |_) | (_) | | | | (_| | (_| (__| |
|_|   |_|_.__/ \___/|_| |_|\__,_|\___\___|_|

```

Classic algorithm

---

# Fibonacci

```
fib(0) = 1
fib(1) = 1
fib(n) = fib(n - 2) + fib(n - 1)
```

So:

```
fib(2) = 2
fib(3) = 3
fib(4) = 5
fib(5) = 8
```

---

# Code it up

To the repl!

---

# Naive implementation

```scala
def fib(n: Int): Int = n match {
  case 0 | 1 => 1
  case _ => fib(n - 2) + fib(n - 1)
}
```

---

# Issues

```scala
def fib(n: Int): Int = n match {
  case 0 | 1 => 1
  case _ => fib(n - 2) + fib(n - 1)
}
```

What are potential issues with this implementation?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Issues

```scala
def fib(n: Int): Int = n match {
  case 0 | 1 => 1
  case _ => fib(n - 2) + fib(n - 1)
}
```

> What are potential issues with this implementation?

- not stack safe


- exponential time complexity


- linear space complexity

That's a shame, it looks really nice...

---

# Tail recursion?

Try our usual trick

```scala
// Naive
def fib(n: Int): Int = n match {
  case 0 | 1 => 1
  case _ => fib(n - 2) + fib(n - 1)
}

// Tail
def fib(n: Int): Int = {

  def fibTail(n: Int, acc: Int): Int = n match {
    case 0 | 1 => acc
    case _ => fibTail(???, ???)
  }

  fibTail(n, 1)
}
```

Hmm...

---

# Tail recursion?

This isn't very "loopy"

```scala
// Naive
def fib(n: Int): Int = n match {
  case 0 | 1 => 1
  case _ => fib(n - 2) + fib(n - 1)
}
```

Iteration is more tree like:

```
                 n
              /     \
        n-2             n-1
     /       \         /    \
  n-4        n-3   n-3       n-2
```

Tree structures don't fit with a top down loop

---

# Tail rec ~ Loop

To solve it tail recursively you'd need to reconceptualise as a loopy computation

---

# Homework

> To solve it tail recursively you'd need to reconceptualise as a loopy computation

Do this!

What is the time/space complexity?

---

# Hint

> Tree structures don't fit with a top down loop

Try a bottom up loop

---

# Solution

```scala
def fib(n: Int): Int = {

  def fibTail(m: Int, currentFib: Int, lastFib: Int): Int = {
    if (m == n + 1)
      lastFib
    else
      fibTail(m + 1, currentFib + lastFib, currentFib)
  }

  fibTail(0, 1, 0)
}
```

---

# Complexity?

## Time

O(n)

Much better

## Space

O(1)

Much better

---

# Summary

Some problems aren't "loopy"

If you can't reconceptualise them to something loopy, then no tail recursion

---

```
 _____                                _ _       _
|_   _| __ __ _ _ __ ___  _ __   ___ | (_)_ __ (_)_ __   __ _
  | || '__/ _` | '_ ` _ \| '_ \ / _ \| | | '_ \| | '_ \ / _` |
  | || | | (_| | | | | | | |_) | (_) | | | | | | | | | | (_| |
  |_||_|  \__,_|_| |_| |_| .__/ \___/|_|_|_| |_|_|_| |_|\__, |
                         |_|                            |___/
```

---

# Trampolining

Not as fun as it sounds...

---

# When tail recursion fails...

- compiler limitations


- some recursive algorithms resist tail recursion

---

# Stuck

Suppose you're stuck with implementing it naively...

---

# Stack safety

> Suppose you're stuck with implementing it naively...

But that's not stack safe...

---

# Trampolining

An advanced FP technique you can use

Just want you to know it exists

Won't go into details

---

# TLDR

Moves the recursion off the stack onto the heap

(makes it more stack safe, but will still use at least O(n) memory)

---

# Rare

Unlikely you'll need it

(but it's interesting and that approach has other applications in FP)

---

```
   ____  _        _ _
  / __ \| |_ __ _(_) |_ __ ___  ___
 / / _` | __/ _` | | | '__/ _ \/ __|
| | (_| | || (_| | | | | |  __/ (__
 \ \__,_|\__\__,_|_|_|_|  \___|\___|
  \____/
```

Aside

Just cramming it in here

---

# Annotation

> What about that `@tailrec` thing?

```scala
import scala.annotation.tailrec

...

@tailrec
def myTailRecMethod(...): ...
```

---

# Why annotate it?

> Often we haven't needed it,
>
> the compiler figured it out by itself,
>
> so why annotate it?

---

# Why annotate it?

> Often we haven't needed it,
>
> the compiler figured it out by itself,
>
> so why annotate it?

Making sure it really is tail recursive

Signals your intention to readers and the compiler

---

# Example

Otherwise someone comes along and says:

> this looks really over engineered and weird,

Changes it back to naive recursion

---

# Example

New code path added

```diff
 def myTailRecMethod(...): Unit = foo match {
   case 0 => 0
   case 1 => myTailRecMethod(...)
+  case 2 => 3 * myTailRecMethod(...)
 }
```

Subtley changes it to be not tail recursive

---

# Annotation summary

You don't strictly need it, but it's recommended

It signals your intention to the compiler and readers that it needs to be tail recursive

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

# Limits to tail recursion

Compiler has limitations

Some problems aren't "loopy"

---

# Rework the problem

Sometimes can you can re-express the algorithm as a loop

---

# In rare cases

You're unable to re-express it,

can use trampolining to make it stack safe

---

# tailrec annotation

Good practice to use it

---

# Next time

Pranali will fold things

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
