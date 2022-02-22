---
author: Rohan
date: 2022-03-17
title: Tail Recursion
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

Continued...

---

# Recall!

Naive recursion has issues with:

- using lots of stack space


- stack overflow

---

# Recall!

Tail recursion solves those issues

---

# Today

Strengthening intuition

---

# Agenda

- homework


- more examples


- tail recursion under the hood (for fun)


- tips for how to think tail recursively

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

Write a tail recursive function that reverses a `List[A]`

```scala
def reverse[A](list: List[A]): List[A] = ...

reverse(List(1, 2, 3))
// List(3, 2, 1)
```

---

# Conceptual solution

Work your way through the list prepending each element to the accumulator

---

# Initially

```
input
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---


acc

                                                                 Nil
```

---

# 1

```
input
                 ------ ---      ------ ---      ------ ---
                |  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
                 ------ ---      ------ ---      ------ ---


acc
                                                 ------ ---
                                                |  1   |   |---> Nil
                                                 ------ ---
```

---

# 2

```
input
                                 ------ ---      ------ ---
                                |  3   |   |--->|  4   |   |---> Nil
                                 ------ ---      ------ ---


acc
                                 ------ ---      ------ ---
                                |  2   |   |--->|  1   |   |---> Nil
                                 ------ ---      ------ ---
```

---

# 3

```
input
                                                 ------ ---
                                                |  4   |   |---> Nil
                                                 ------ ---


acc
                 ------ ---      ------ ---      ------ ---
                |  3   |   |--->|  2   |   |--->|  1   |   |---> Nil
                 ------ ---      ------ ---      ------ ---
```

---

# 4

```
input

                                                                 Nil



acc
 ------ ---      ------ ---      ------ ---      ------ ---
|  4   |   |--->|  3   |   |--->|  2   |   |--->|  1   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

---

# Nil

```
input

                                                                 Nil



acc
 ------ ---      ------ ---      ------ ---      ------ ---
|  4   |   |--->|  3   |   |--->|  2   |   |--->|  1   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

Stop!

Return accumulator

---

# Code it up

To the repl!

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

# Sum a list

```scala
def sum(list: List[Int]): Int = ???

sum(List(1, 2, 3))
// 6
```

---

# Approach

Solve naively

Then convert to tail rec

To the repl!

---

# Summary

```scala
// Naive recursion
def sum(list: List[Int]): Int = list match {
  case Nil => 0
  case head :: tail => head + sum(tail)
}

// Tail recursion
@tailrec
def sum(list: List[Int], acc: Int = 0): Int = list match {
  case Nil => acc
  case head :: tail => sum(tail, acc + head)
}
```

---

# Next up

Map a list

---

# Map a list

Already implemented this naively for `ConsList`

Will do again for proper `List`

Then make tail recursive

To the repl!

---

# Reversed?

---

# Initially

```scala
  def mapTail(list: List[A], acc: List[B]): List[B] = list match {
    case Nil => acc
    case head :: tail => mapTail(tail, f(head) :: acc)
  }
```

```
list
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---

                            _ * 2

acc

                                                                 Nil

```

---

# mapped 1

```scala
  def mapTail(list: List[A], acc: List[B]): List[B] = list match {
    case Nil => acc
    case head :: tail => mapTail(tail, f(head) :: acc)
  }
```

```
list
                 ------ ---      ------ ---      ------ ---
                |  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
                 ------ ---      ------ ---      ------ ---

                            _ * 2

acc
                                                 ------ ---
                                                |  2   |   |---> Nil
                                                 ------ ---
```

---

# mapped 2

```scala
  def mapTail(list: List[A], acc: List[B]): List[B] = list match {
    case Nil => acc
    case head :: tail => mapTail(tail, f(head) :: acc)
  }
```

```
list
                                 ------ ---      ------ ---
                                |  3   |   |--->|  4   |   |---> Nil
                                 ------ ---      ------ ---

                            _ * 2

acc
                                 ------ ---      ------ ---
                                |  4   |   |--->|  2   |   |---> Nil
                                 ------ ---      ------ ---
```

---

# mapped 3

```scala
  def mapTail(list: List[A], acc: List[B]): List[B] = list match {
    case Nil => acc
    case head :: tail => mapTail(tail, f(head) :: acc)
  }
```

```
list
                                                 ------ ---
                                                |  4   |   |---> Nil
                                                 ------ ---

                            _ * 2

acc
                 ------ ---      ------ ---      ------ ---
                |  6   |   |--->|  4   |   |--->|  2   |   |---> Nil
                 ------ ---      ------ ---      ------ ---
```

---

# map 4

```scala
  def mapTail(list: List[A], acc: List[B]): List[B] = list match {
    case Nil => acc
    case head :: tail => mapTail(tail, f(head) :: acc)
  }
```

```
list

                                                                 Nil


                            _ * 2

acc
 ------ ---      ------ ---      ------ ---      ------ ---
|  8   |   |--->|  6   |   |--->|  4   |   |--->|  2   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

---

# Hmmm....

Mapping tail recursively reverses our list

We can reverse the accumulator before returning it

Back to the repl!

---

# Summary

```scala
// Naive
def map[A, B](list: List[A], acc: List[B] = Nil)(f: A => B): List[B] = list match {
  case Nil => acc
  case head :: tail =>
    val newHead: B = f(head)
    map(tail, newHead :: acc)(f)
}

// Tail
def map[A, B](list: List[A])(f: A => B): List[B] = {
  @tailrec
  def mapTail(list: List[A], acc: List[B]): List[B] = list match {
    case Nil => acc
    case head :: tail => mapTail(tail, f(head) :: acc)
  }
  mapTail(list, Nil).reverse
}
```

---

# Complexity?

What is the time/space complexity of our tail rec implementation?

```scala
def map[A, B](list: List[A])(f: A => B): List[B] = {
  @tailrec
  def mapTail(list: List[A], acc: List[B]): List[B] = list match {
    case Nil => acc
    case head :: tail => mapTail(tail, f(head) :: acc)
  }
  mapTail(list, Nil).reverse
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

# Complexity

> What is the time/space complexity of our tail rec implementation?

```scala
def map[A, B](list: List[A])(f: A => B): List[B] = {
  @tailrec
  def mapTail(list: List[A], acc: List[B]): List[B] = list match {
    case Nil => acc
    case head :: tail => mapTail(tail, f(head) :: acc)
  }
  mapTail(list, Nil).reverse
}
```

## Time

Two loops (map, reverse)

O(n)

## Space

Intermediate collection

(but we have to generate a whole new structure anyway)

O(n)

(but at least it's stack safe)

---

# Note

Tail recursive processing (and folding) will often reverse your list

Processing moves forward through the list

But prepends onto the accumulator

---

# Note

> Tail recursive processing (and folding) and folding will often reverse your list
>
> Processing moves forward through the list
>
> But prepends onto the accumulator

You can also reverse your list _before_ sending it into the tail recursive loop

---

# Homework

Implement `flatMap` for lists

```scala
def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] = ???

flatMap(List(1, 2, 3))(i => List(i, i))
// List(1, 1, 2, 2, 3, 3)
```

For extra points, what is the time/space complexity?

Would it make sense to reverse our list _before_ putting it into the recursive loop?

---

# Hints

- follow the same basic structure of `map`


- use `++` instead of `::` when adding the accumulator


- don't forget to reverse it

---

# Solution

```scala
def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] = {

  def flatMapTail(list: List[A], acc: List[B]): List[B] = list match {
    case Nil => acc
    case head :: tail => flatMapTail(tail, f(head) ++ acc)
  }

  flatMapTail(list, Nil).reverse
}
```

---

# Time/Space

```scala
def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] = {

  def flatMapTail(list: List[A], acc: List[B]): List[B] = list match {
    case Nil => acc
    case head :: tail => flatMapTail(tail, f(head) ++ acc)
  }

  flatMapTail(list, Nil).reverse
}
```

O(m) for both,

where m is the length of the returned list (not the input list)

(but at least it's stack safe)

---

# Reverse?

> Would it make sense to reverse our list _before_ putting it into the recursive loop?

Not really

Try it on a transformation like this:

```scala
flatMap(List(10, 20, 30))(i => List(i - 1, i, i + 1))
```

Each chunk will come out backwards

---

For fun

```
 _   _           _
| | | |_ __   __| | ___ _ __
| | | | '_ \ / _` |/ _ \ '__|
| |_| | | | | (_| |  __/ |
 \___/|_| |_|\__,_|\___|_|

 _   _
| |_| |__   ___
| __| '_ \ / _ \
| |_| | | |  __/
 \__|_| |_|\___|

 _                     _
| |__   ___   ___   __| |
| '_ \ / _ \ / _ \ / _` |
| | | | (_) | (_) | (_| |
|_| |_|\___/ \___/ \__,_|

```

What does the compiler actually do?

---

# Recapping so far

The compiler is given a function to compile:

- sees that it's tail recursive


- rewrites the code into a loop that uses one stack frame

---

# Decompile it!

See what goes on under the hood

(Note: Don't worry if you can't follow all the details, not essential)

To the shell!

---

# What did we find?

No recursive method calls

goto emulating a loop

---

# Rewritten code

```scala
// What we wrote
def facTail(n: Int, acc: BigInt): BigInt = n match {
  case 0 => acc
  case _ =>
    val newN = n - 1
    val newAcc = n * acc
    facTail(newN, newAcc)
}

// What the compiler effectively wrote
def facTail(n: Int, acc: BigInt): BigInt = {
  // Capture initial values into variables
  var _n = n
  var _acc = acc

  while (true) {
    _n match {
      case 0 => return _acc
      case _ =>
        _acc = _n * _acc
        _n = _n - 1
    }
  }
}
```

---

```
 _   _      _       _
| | | | ___| |_ __ | |
| |_| |/ _ \ | '_ \| |
|  _  |  __/ | |_) |_|
|_| |_|\___|_| .__/(_)
             |_|
```

Sometimes it's hard to make something tail recursive

---

# My tip

Think about how you'd solve it with a loop

(that's what tail rec really is)

---

# Example

Summing a list

How would you implement this with a loop?

---

# Example

Summing a list

> How would you implement this with a loop?

```scala
// State
var acc = 0
//        ^ initial acc

for (i <- list)
  acc += i

acc
```

---

# while vs for

What would it look like as a while loop?

(that's closer to a tail rec solution)

---

# List is state

```scala
// Setup state with initial conditions
var acc = 0
var curr = list
//         ^ initial list passed in

while (true) {
  curr match {
    case Nil => return acc
    case head :: tail =>
      acc += head
      curr = tail
  }
}
```

---

# Essence of a loop

Every iteration updates some state,

like a transformation `State => State`

```scala
while (true) {
  // (head :: tail, acc)  --->  (tail, head + acc)
}
```

---

# Loop to tail rec

```scala
while (true) {
  // (head :: tail, acc)  --->  (tail, head + acc)
}

// becomes
def sumTail(list: List[Int], acc: Int): List[Int] =
  ...
    case head :: tail =>
      sumTail(tail, head + acc)
```

---

# That's it!

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

# More examples!

Hopefully starting to get a feeling for the pattern

---

# Tail recursion and loops

Tail recursion and loops are both expressions of a deeper fundamental concept

`State => State` repeatedly

---

# Tip

Think about how you'd solve it with a loop

What is the `State => State` logic?

---

# Next time

Limitations of tail recursion and alternatives

---

# Homework

Don't forget!

`flatMap`

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
