---
author: Rohan
date: 2022-03-10
title: Functional data structures
---

```
 _____                 _   _                   _
|  ___|   _ _ __   ___| |_(_) ___  _ __   __ _| |
| |_ | | | | '_ \ / __| __| |/ _ \| '_ \ / _` | |
|  _|| |_| | | | | (__| |_| | (_) | | | | (_| | |
|_|   \__,_|_| |_|\___|\__|_|\___/|_| |_|\__,_|_|

 ____        _
|  _ \  __ _| |_ __ _
| | | |/ _` | __/ _` |
| |_| | (_| | || (_| |
|____/ \__,_|\__\__,_|

 ____  _                   _
/ ___|| |_ _ __ _   _  ___| |_ _   _ _ __ ___  ___
\___ \| __| '__| | | |/ __| __| | | | '__/ _ \/ __|
 ___) | |_| |  | |_| | (__| |_| |_| | | |  __/\__ \
|____/ \__|_|   \__,_|\___|\__|\__,_|_|  \___||___/

```

---

# Looking back

On a long journey related to structures and performance

- the structure of the cons list


- time/space complexity concepts


- time/space complexity for list

---

# Today

Functional data structures

Reusing structure to be efficient

---

# Agenda

- immutable vs mutable


- reuse of structure

---

```
 ___                           _        _     _
|_ _|_ __ ___  _ __ ___  _   _| |_ __ _| |__ | | ___
 | || '_ ` _ \| '_ ` _ \| | | | __/ _` | '_ \| |/ _ \
 | || | | | | | | | | | | |_| | || (_| | |_) | |  __/
|___|_| |_| |_|_| |_| |_|\__,_|\__\__,_|_.__/|_|\___|


                __   _____
                \ \ / / __|
                 \ V /\__ \
                  \_/ |___/

         __  __       _        _     _
        |  \/  |_   _| |_ __ _| |__ | | ___
        | |\/| | | | | __/ _` | '_ \| |/ _ \
        | |  | | |_| | || (_| | |_) | |  __/
        |_|  |_|\__,_|\__\__,_|_.__/|_|\___|
```

---

# What is mutability?

What do we mean when we say something is immutable or mutable?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Definitions

## Immutable

Once it's created, it can't be modified over its lifetime

## Mutable

Objects can be modified after they're created

---

# Immutable simplicity

> Once it's created, it can't be modified over its lifetime

If you observe it in a particular state at time t0,

you know it will be in that same state at time t1

---

# Mutable example

What is an example of a mutable object?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Mutable example

`Array`

To the repl!

---

# Array summary

```scala
val array = Array(0, 1, 2)

array(0) = -1

// Array(-1, 1, 2)
```

Changing "in place"

---

# Immutable example

What is an example of an immutable object?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Immutable examples

- `String`


- `List`

(All collections in `scala.collections.immutable`)

---

# Change?

How do you "modify" immutable objects?

e.g. prepend to a list? Upper case a string?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Copy

> How do you "modify" immutable objects?

Create a modified copy

```scala
val s1 = "abc"

val s2 = s1.toUpperCase

// s1 is not changed
```

---

# FP

Heavy use of immutable data:

- thread safe


- easy to reason about

But...

---

# Criticism

> Isn't it inefficient to copy objects all the time?

---

# Example

It's very fast and cheap to _modify_ an array in place

```scala
val array = Array(0, 1, 2)

array(0) = 10
array(1) = 11
array(2) = 12

// Array(10, 11, 12)
```

You wouldn't want to copy the entire array for every mutation

---

# Fair criticism

Sometimes it's inefficient to use an immutable model

An array makes sense for that example

---

# Functional data structures

Sometimes there are ways you can reuse existing structures

Then minimal copying is needed

---

# List example

Replace the head in a list with a new value

```scala
def replaceHead(list: List[Int], newHead: Int): List[Int] = ...

replaceHead(List(1, 2, 3, 4), 100)
// List(100, 2, 3, 4)
```

(assume the list is non-empty)

---

# Implementation

> Replace the head in a list with a new value

```scala
def replaceHead(list: List[Int], newHead: Int): List[Int] = newHead :: list.tail
```

---

# Structurally

> Replace the head in a list with a new value

```scala
def replaceHead(list: List[Int], newHead: Int): List[Int] = newHead :: list.tail

replaceHead(List(1, 2, 3, 4), 100)
```

```
     ------ ---      ------ ---      ------ ---      ------ ---
    |  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
     ------ ---      ------ ---      ------ ---      ------ ---
                    /
     ------ ---    /
    |  100 |   |---
     ------ ---
```

---

# Time/Space Complexity

What is it?

```scala
def replaceHead(list: List[Int], newHead: Int): List[Int] = newHead :: list.tail

replaceHead(List(1, 2, 3, 4), 100)
```

```
     ------ ---      ------ ---      ------ ---      ------ ---
    |  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
     ------ ---      ------ ---      ------ ---      ------ ---
                    /
     ------ ---    /
    |  100 |   |---
     ------ ---
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# O(1)

```
     ------ ---      ------ ---      ------ ---      ------ ---
    |  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
     ------ ---      ------ ---      ------ ---      ------ ---
                    /
     ------ ---    /
    |  100 |   |---
     ------ ---
```

Just creating a single cons cell

Time: O(1)

Space: O(1)

---

# Back to the criticism

Immutable approach: Don't mutate, make an immutable copy

> Isn't it inefficient to copy objects all the time?

---

# Our example

We made an immutable copy

(original list is unaffected)

```
     ------ ---      ------ ---      ------ ---      ------ ---
    |  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
     ------ ---      ------ ---      ------ ---      ------ ---
                    /
     ------ ---    / ------------------------ reused -------------------
    |  100 |   |---
     ------ ---
```

This was very efficient because we reused structure

---

# Functional data structures

Structures designed for sharing an reuse

---

# Sharing your data

Can feel strange

Usually we abstract over data and protect it

---

# Why?

> Usually we abstract over data and protect it

Scared that someone will modify it

```scala
class Foo {
  private var state = ...

  def getState: State = ...

  def setState(newState: State): Unit = ...
}
```

---

# Immutable

> Scared that someone will modify it

If your data is immutable, no one can modify it

Can share it freely

---

# Database vs Blockchain

## Database

Mutable

We put a service layer in front of it to protect it

Outsiders can only access the db through that layer

## Blockchain

Immutable

Outsiders can directly access it

---

# Sharing

The same bits of structure can get reused over and over

```scala
val list1 = List(1, 2, 3, 4)

val list2 = 0 :: list1

val list3 = list1.drop(2)

val list4 = 100 :: 101 :: list1.tail
```

---

# Structurally

```scala
val list1 = List(1, 2, 3, 4) // <---

val list2 = 0 :: list1

val list3 = list1.drop(2)

val list4 = 100 :: 101 :: list1.tail
```

```
                 ------ ---      ------ ---      ------ ---      ------ ---
                |  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
                 ------ ---      ------ ---      ------ ---      ------ ---
                ^
                list1
```

---

# Structurally

```scala
val list1 = List(1, 2, 3, 4)

val list2 = 0 :: list1 // <---

val list3 = list1.drop(2)

val list4 = 100 :: 101 :: list1.tail
```

```
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---
|  0   |   |--->|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---
^               ^
list2           list1
```

---

# Structurally

```scala
val list1 = List(1, 2, 3, 4)

val list2 = 0 :: list1

val list3 = list1.drop(2) // <---

val list4 = 100 :: 101 :: list1.tail
```

```
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---
|  0   |   |--->|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---
^               ^                               ^
list2           list1                           list3
```

---

# Structurally

```scala
val list1 = List(1, 2, 3, 4)

val list2 = 0 :: list1

val list3 = list1.drop(2)

val list4 = 100 :: 101 :: list1.tail // <---
```

```
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---
|  0   |   |--->|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---
^               ^               /               ^
list2           list1          /                 list3
                              /
 ------ ---      ------ ---  /
| 100  |   |--->| 101  |   |-
 ------ ---      ------ ---
^
list4
```

---

# So much reuse

```scala
val list1 = List(1, 2, 3, 4)

val list2 = 0 :: list1

val list3 = list1.drop(2)

val list4 = 100 :: 101 :: list1.tail
```

```
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---
|  0   |   |--->|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---
^               ^               /               ^
list2           list1          /                 list3
                              /
 ------ ---      ------ ---  /
| 100  |   |--->| 101  |   |-
 ------ ---      ------ ---
^
list4
```

We created so many lists reusing the same data over and over

---

# Best of both worlds

If you choose a functional data structure that matches your use case, you get:

- the benefits of immutability (threadsafe, easy to reason about)


- efficiency (smart reuse of existing structure)

---

# Essence of reuse

What is it that made reuse so easy with List?

---

# Essence of reuse

> What is it that made reuse so easy with List?

The recursive nature of the cons cell

```scala
sealed trait ConsList

case class ConsCell(head: Int, tail: ConsList) extends ConsList
//                             ^^^^^^^^^^^^^^

case object Terminus extends ConsList
```

Inside every cons cell, there's another list

---

# For example

```scala
val list1 = List(1, 2, 3, 4)
```

creates 4 lists:

```
     ------ ---      ------ ---      ------ ---      ------ ---
    |  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
     ------ ---      ------ ---      ------ ---      ------ ---
    ^               ^               ^               ^
```

---

# Previous example

```
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---
|  0   |   |--->|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---      ------ ---
                                /
                               /
                              /
 ------ ---      ------ ---  /
| 100  |   |--->| 101  |   |-
 ------ ---      ------ ---
```

Created 7 lists

7 (every cons cell is an independent list)

---

# That's it

---

# Homework

Create an ADT to represent a binary tree of integers

```
             1
           /   \
          3     4
               / \
             10   -3
```

A tree is either a:

- parent with two sub-trees (recursive)


- leaf

For extra points,

- implement `map`


- describe some operations that would be very efficient


- describe some operations that would be very inefficient

---

# Solution

Don't peek until you've had a try!

```scala
sealed trait BinaryTree {
  def value: Int
}

case class Parent(value: Int, left: BinaryTree, right: BinaryTree) extends BinaryTree

case class Leaf(value: Int) extends BinaryTree

object BinaryTree {
  def map(tree: BinaryTre, f: Int => Int): BinaryTree = tree match {
    // Note how it's structure preserving
    case Leaf(value) => Leaf(f(value))
    case Parent(value, left, right) => Parent(f(value), map(left, f), map(right, f))
  }
}
```

---

# Solution

> describe some operations that would be very efficient

Tree would be efficient for operations where you can reuse big branches of old trees,

```scala
val original = Parent(1, Leaf(3), Parent(4, Leaf(10), Leaf(-3)))

// replacing the root of a tree
val changeRoot = Parent(10, original.left, original.right)

// switching the branches around
val switched = original.copy(left = original.right, right = original.left)
```

```
           original          changeRoot           switched
             1                  10                   1
           /   \              /   \                /   \
          3     4            3     4              4     3
               / \                / \            / \
             10   -3            10   -3        10   -3
```

These are O(1) time and space

---

# Solution

> describe some operations that would be very inefficient

Any operation that modifies a leaf node requires all ancestors to be rebuilt up to the root

(analogous to making modifications at the right of a list)


```
           original          changeLeaf
             1                  1
           /   \              /   \
          3     4            3     4
               / \                / \
             10   -3            10   -4
                                      ^
```

```scala
val original = Parent(1, Leaf(3), Parent(4, Leaf(10), Leaf(-3)))

// replacing the root of a tree
val changeLeaf = original.copy(right = original.right.copy(right = -4))
```

This will be roughly log_2(n) which I guess isn't all that bad, not _very_ inefficient

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

# Functional data structures

Immutable structures designed for smart reuse

---

# Examples

- cons list


- trees


- vector (maybe look at later)


- chain (from the cats libary)

---

# Right tool for the job

If your structure matches your use case,

you can get the benefits of immutability and good performance

---

# Next time

Tail recursion

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\__,_|\___||___/\__|_|\___/|_| |_|___/

```
