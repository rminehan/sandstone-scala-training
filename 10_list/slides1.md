---
author: Rohan
date: 2022-01-10
title: List
---

```
 _     _     _
| |   (_)___| |_
| |   | / __| __|
| |___| \__ \ |_
|_____|_|___/\__|

```

The cons List

---

# Today

The mighty cons `List`

Used all the time,

but often under appreciated

---

# Why learn about List?

Often useful with recursion and FP

Simple and easy to reason about

---

# Important things to understand

What it's good at (prepending)

What it's bad at (appending, random inserting)

---

# Assumptions about audience

Not sure what you guys know about lists

Will be more beginner friendly

---

# Today

- conceptual structure of `List`


- build our own


- build an api around it

---

# Coming up

Performance

Functional data structures

---

```
__        __                   _
\ \      / /_ _ _ __ _ __ ___ (_)_ __   __ _
 \ \ /\ / / _` | '__| '_ ` _ \| | '_ \ / _` |
  \ V  V / (_| | |  | | | | | | | | | | (_| |
   \_/\_/ \__,_|_|  |_| |_| |_|_|_| |_|\__, |
                                       |___/

 _   _ _ __
| | | | '_ \
| |_| | |_) |
 \__,_| .__/
      |_|
```

---

# Tell me what you know

What is a `List`?

How is it structured?

What does it do well?

What does it do badly?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Ambiguous "List"

Different languages use "List"

e.g. python, java, scala

---

# Ambiguous "List"

> Different languages use "List"
>
> e.g. python, java, scala

but they are often fundamentally different structures

Just share the same name

---

# My approach

Understand it conceptually

Then implement it ourselves in scala code

---

```
 _     _     _
| |   (_)___| |_
| |   | / __| __|
| |___| \__ \ |_
|_____|_|___/\__|

 ____  _                   _
/ ___|| |_ _ __ _   _  ___| |_ _   _ _ __ ___
\___ \| __| '__| | | |/ __| __| | | | '__/ _ \
 ___) | |_| |  | |_| | (__| |_| |_| | | |  __/
|____/ \__|_|   \__,_|\___|\__|\__,_|_|  \___|

```

---

# Scala's List

In CS jargon,

most closely resembles a _singly linked list_

---

# The cons cell

A cons cell is a piece of data and a pointer linking it to another List

```
 ------ ---
|  4   |   |--->
 ------ ---

 data    pointer
 "head"  "tail"
````

---

# Linking many cells

```
 ------ ---       ------ ---       ------ ---
|  0   |   |---> |  1   |   |---> |  2   |   |--->  ...
 ------ ---       ------ ---       ------ ---

   cell 0           cell 1          cell 2
````

---

# Singly linked"

```
 ------ ---       ------ ---      ------ ---
|  0   |   |---> |  1   |   |--->|  2   |   |--->  ...
 ------ ---       ------ ---      ------ ---

   cell 0           cell 1          cell 2
````

Each pointing forward

cell 0 can see cell 1

but not vice versa

---

# If all we had is a cons cell...

... what problem do you see?

```
 ------ ---       ------ ---      ------ ---
|  0   |   |---> |  1   |   |--->|  2   |   |--->  ...
 ------ ---       ------ ---      ------ ---

   cell 0           cell 1          cell 2
````

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# If all we had is a cons cell...

> ... what problem do you see?

```
 ------ ---       ------ ---      ------ ---
|  0   |   |---> |  1   |   |--->|  2   |   |--->  ...   ???
 ------ ---       ------ ---      ------ ---

   cell 0           cell 1          cell 2
```

It never ends!

---

# Analogies

```
 ------ ---       ------ ---      ------ ---
|  0   |   |---> |  1   |   |--->|  2   |   |--->  ...   ???
 ------ ---       ------ ---      ------ ---

   cell 0           cell 1          cell 2
```

> It never ends!

Like a recursion without a base case

Like trying to push a lump out of an infinite carpet

---

# Terminus

> Like a recursion without a base case

Cons cell's aren't enough

We also need a terminus concept

---

# Terminus

Called 'Nil'

```
 ------ ---       ------ ---      ------ ---
|  0   |   |---> |  1   |   |--->|  2   |   |---> Nil
 ------ ---       ------ ---      ------ ---

   cell 0           cell 1          cell 2
```

---

# Properties of `Nil`

```
 ------ ---       ------ ---      ------ ---
|  0   |   |---> |  1   |   |--->|  2   |   |---> Nil
 ------ ---       ------ ---      ------ ---

   cell 0           cell 1          cell 2
```

No data

Just exists to end the chain

---

# Building a List

We start from the back and prepend

Example: Build `List(1, 2, 3, 4)`

---

# Start from the back - Nil

```

                                                                 Nil

```

---

# Prepend 4

```
                                                 ------ ---
                                                |  4   |   |---> Nil
                                                 ------ ---
```

---

# Prepend 3

```
                                 ------ ---      ------ ---
                                |  3   |   |--->|  4   |   |---> Nil
                                 ------ ---      ------ ---
```

---

# Prepend 2

```
                 ------ ---      ------ ---      ------ ---
                |  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
                 ------ ---      ------ ---      ------ ---
```

---

# Prepend 1

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

---

# Done!

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

```scala
List(1, 2, 3, 4)
```

---

# Recap of concepts

A List is a "singly linked" chain of cells all pointing forward

---

# Recap of concepts

Terminates at `Nil`

---

# Recap of concepts

Built from the back

---

```
 ____        _ _     _
| __ ) _   _(_) | __| |
|  _ \| | | | | |/ _` |
| |_) | |_| | | | (_| |
|____/ \__,_|_|_|\__,_|


  ___  _   _ _ __
 / _ \| | | | '__|
| (_) | |_| | |
 \___/ \__,_|_|


  _____      ___ __     ___  _ __   ___
 / _ \ \ /\ / / '_ \   / _ \| '_ \ / _ \
| (_) \ V  V /| | | | | (_) | | | |  __/
 \___/ \_/\_/ |_| |_|  \___/|_| |_|\___|

```

---

# List

A "list" is either:

- a cons cell (data + pointer)


- `Nil`

---

# Demo time

To the editor!

---

# Summary

```scala
trait ConsList

case class ConsCell(head: Int, tail: ConsList) extends ConsList

case object Terminus extends ConsList

// Create something analogous to List(1, 2, 3, 4)
val list = ConsCell(1, ConsCell(2, ConsCell(3, ConsCell(4, Terminus))))
```

---

```
 _____       _
| ____|_ __ | |__   __ _ _ __   ___ ___
|  _| | '_ \| '_ \ / _` | '_ \ / __/ _ \
| |___| | | | | | | (_| | | | | (_|  __/
|_____|_| |_|_| |_|\__,_|_| |_|\___\___|

 _   _                        _
| |_| |__   ___    __ _ _ __ (_)
| __| '_ \ / _ \  / _` | '_ \| |
| |_| | | |  __/ | (_| | |_) | |
 \__|_| |_|\___|  \__,_| .__/|_|
                       |_|
```

---

# Currently

Can't do much with this list

Not friendly to use

---

# Build foreach

To the editor!

---

# foreach summary

```scala
def foreach(list: ConsList, doSomething: Int => Unit): Unit = {
  list match {
    case ConsCell(head, tail) =>
      doSomething(head)
      foreach(tail, doSomething)
    case Terminus => // do nothing
  }
}

foreach(list, i => println(i))
```

---

# Make it easier to build

```scala
// Ugly
val list = ConsCell(1, ConsCell(2, ConsCell(3, ConsCell(4, Terminus))))

// Nicer
val list = ConsList(1, 2, 3, 4)
// Just like List(1, 2, 3, 4)
```

---

# apply summary

```scala
def apply(ints: Int*): ConsList = {
  var list: ConsList = Terminus
  ints.reverse.foreach { i =>
    list = ConsCell(i, list)
  }
  list
}

val list = ConsList(1, 2, 3, 4)
```

This is a bit nasty,

later we'll learn how to use folding for this

---

# map?

Could we map a function over our `ConsList` to get a new `ConsList`?

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---

                            _ * 2

 ------ ---      ------ ---      ------ ---      ------ ---
|  2   |   |--->|  4   |   |--->|  6   |   |--->|  8   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

To the editor!

---

# map summary

```scala
def map(list: ConsList, f: Int => Int): ConsList = {
  list match {
    case Terminus => Terminus
    case ConsCell(head, tail) =>
      val newHead: Int = f(head)
      val newTail: ConsList = map(tail, f)
      ConsCell(newHead, newTail)
  }
}
```

---

# Aside: Recursion

```scala
def map(list: ConsList, f: Int => Int): ConsList = {
  list match {
    case Terminus => Terminus
    case ConsCell(head, tail) =>
      val newHead: Int = f(head)
      val newTail: ConsList = map(tail, f)
      ConsCell(newHead, newTail)
  }
}
```

Our cons list fits very naturally with recursion

- attack the head


- recursively process the tail


- terminus is the base case where we stop

---

# Problem with our design

---

# Stray lists

```scala
trait ConsList

case class ConsCell(head: Int, tail: ConsList) extends ConsList

case object Terminus extends ConsList
```

What stops someone else making their own list:

```scala
case object HackerList extends ConsList
```

---

# Implicit assumption

e.g. map

```scala
def map(list: ConsList, f: Int => Int): ConsList = {
  list match {
    case Terminus => Terminus
    case ConsCell(head, tail) =>
      val newHead: Int = f(head)
      val newTail: ConsList = map(tail, f)
      ConsCell(newHead, newTail)
  }
}
```

Implicit assumption here that there's only two kinds of list:

- terminus


- cons cell

---

# Hacker

```scala
case object HackerList extends ConsList

map(HackerList, _ * 2)

def map(list: ConsList, f: Int => Int): ConsList = {
  list match {
    case Terminus => Terminus
    case ConsCell(head, tail) =>
      val newHead: Int = f(head)
      val newTail: ConsList = map(tail, f)
      ConsCell(newHead, newTail)
  }
}
```

Will compile and then throw a MatchError

---

# Like Option

`ConsList` is like `Option`

"enum"y

---

# "enum"y

We only want a predefined set of values:

- Option: None and Some


- ConsList: Terminus and ConsCell

---

# ADT

We've stumbled on something:

> algebraic data type

A data abstraction with only certain allowed forms

---

# Translating to code

How do you tell the compiler to not allow other subtypes?

```scala
trait ConsList

case class ConsCell(head: Int, tail: ConsList) extends ConsList

case object Terminus extends ConsList
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# sealed

Make it sealed

```diff
-trait ConsList
+sealed trait ConsList

 case class ConsCell(head: Int, tail: ConsList) extends ConsList

 case object Terminus extends ConsList
```

Can only be extended in the same file

---

# Let's seal it

We'll seal it and try to attack it

To the editor!

---

# sealed summary

Makes it a true ADT

The compiler makes sure there's only ever two kinds of cons List

---

# Prettier prepending

```scala
// Ugly
ConsCell(1, otherList)

// Pretty
1 :: otherList
```

Let's add a `::` method!

To the editor!

---

# Prepend Summary

```scala
sealed trait ConsList {
  def ::(newHead: Int): ConsList = ConsCell(newHead, this)
}

1 :: otherList

1 :: 2 :: 3 :: 4 :: Terminus
```

---

# The "real" list

We've been reimplementing the real `List` from scratch

Hopefully you have a stronger intuition for it!

---

# Keeping it simple

We simplified things

e.g. not generic, just used `Int`

but the cons cell structure is the same

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

# List

A single linked list

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  2   |   |--->|  4   |   |--->|  6   |   |--->|  8   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

---

# ADT

A List is either:

- cons cell (head data + tail pointer)


- terminus (`Nil`)

---

# Recursion

List is very well suited to head first recursion

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  2   |   |--->|  4   |   |--->|  6   |   |--->|  8   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---

   ------------------------------------------------------------->
            recurse         recurse         recurse         recurse
```

- deal with the head


- recurse on the tail


- stop at `Nil`

---

# Next time

Performance of List:

- good use cases


- bad use cases

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
