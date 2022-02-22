---
author: Rohan
date: 2022-02-24
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

Good introduction to functional data structures

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

"List" is to data structures,

as "service" is to ops

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

```
1 --> 2 --> 3 --> ...
```

---

# The cons cell

A cons cell is a piece of data and a pointer linking it to another List

```
 ------ ---
|  4   |   |--->
 ------ ---

 data    pointer
 "head"  "tail"
```

---

# Linking many cells

```
 ------ ---       ------ ---       ------ ---
|  0   |   |---> |  1   |   |---> |  2   |   |--->  ...
 ------ ---       ------ ---       ------ ---

   cell 0           cell 1          cell 2
```

---

# "Singly linked"

```
 ------ ---       ------ ---      ------ ---
|  0   |   |---> |  1   |   |--->|  2   |   |--->  ...
 ------ ---       ------ ---      ------ ---

   cell 0           cell 1          cell 2
```

Each pointing forward

cell 0 can see cell 1

but not vice versa

---

# If all we had is a cons cell...

```
 ------ ---       ------ ---      ------ ---
|  0   |   |---> |  1   |   |--->|  2   |   |--->  ...
 ------ ---       ------ ---      ------ ---

   cell 0           cell 1          cell 2
```

... what problem do you see?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# If all we had is a cons cell...

```
 ------ ---       ------ ---      ------ ---
|  0   |   |---> |  1   |   |--->|  2   |   |--->  ...   ???
 ------ ---       ------ ---      ------ ---

   cell 0           cell 1          cell 2
```

> ... what problem do you see?

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

No data - the "empty List"

Just exists to end the chain

---

# Example

Build `List(1, 2, 3, 4)`

Using just our two concepts:

- cons cell


- terminus (`Nil`)

---

# Example

> Build `List(1, 2, 3, 4)`

We start from the _back_ and prepend to the front

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

Built from the back, prepending

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

# Homework

Define `drop` analogous to `List`'s

```scala
val list = ConsList(1, 2, 3, 4)

val afterFirst2 = ConsList.drop(list, 2)

ConsList.foreach(afterFirst2, println)
// 3
// 4
```

(do `take` for bonus points)

---

# Solutions

No peeking until you've tried!

```scala
import scala.annotation.tailrec

...

object ConsList {
  ...

  @tailrec
  def drop(list: ConsList, length: Int): ConsList = {
    if (length <= 0) list
    else list match {
      case Terminus => Terminus
      case ConsCell(_, tail) => drop(tail, length - 1)
    }
  }

  ...

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

# Keeping it simple

We simplified things

e.g. not generic, just used `Int`

but the cons cell structure is the same

---

# List

A singly linked list

```
 ------ ---      ------ ---      ------ ---      ------ ---
|  1   |   |--->|  2   |   |--->|  3   |   |--->|  4   |   |---> Nil
 ------ ---      ------ ---      ------ ---      ------ ---
```

---

# Two concepts

- cons cell


- terminus

---

# Recursive processing

- cons cell (process head and recurse onto the tail)


- terminus (base case)

---

# DIY

Hopefully it's helped you see under the hood

---

# Next time

Enhance the api more

Introduce algegbraic data types

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\__,_|\___||___/\__|_|\___/|_| |_|___/

```
