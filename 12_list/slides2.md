---
author: Rohan
date: 2022-02-28
title: List
---

```
 _     _     _
| |   (_)___| |_
| |   | / __| __|
| |___| \__ \ |_
|_____|_|___/\__|

```

(continued)

---

# Today

Carrying on with List

- recap


- homework


- api enhancements


- ADT

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

```scala
trait ConsList

case class ConsCell(head: Int, tail: ConsList) extends ConsList

case object Terminus extends ConsList
```

---

# DIY api enhancements

- foreach


- map


- apply

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

Define `drop` analogous to `List`'s

```scala
val list = ConsList(1, 2, 3, 4)

val afterFirst2 = ConsList.drop(list, 2)

ConsList.foreach(afterFirst2, println)
// 3
// 4
```

---

# Defining the problem

Edge cases:

- dropping 0? Just return the list


- dropping a negative amount? Hmmm... okay, just return the list


- dropping more than the length of the list? Return terminus

---

# Solution

To the editor!

---

# Solution recap

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
 __  __                
|  \/  | ___  _ __ ___ 
| |\/| |/ _ \| '__/ _ \
| |  | | (_) | | |  __/
|_|  |_|\___/|_|  \___|
                       
            _                                               _       
  ___ _ __ | |__   __ _ _ __   ___ ___ _ __ ___   ___ _ __ | |_ ___ 
 / _ \ '_ \| '_ \ / _` | '_ \ / __/ _ \ '_ ` _ \ / _ \ '_ \| __/ __|
|  __/ | | | | | | (_| | | | | (_|  __/ | | | | |  __/ | | | |_\__ \
 \___|_| |_|_| |_|\__,_|_| |_|\___\___|_| |_| |_|\___|_| |_|\__|___/
                                                                    
```

(`::` operator)

---

# Operator recap

- operators are just functions with funny names

```scala
def method(): Unit = ...

def <*>(): Unit = ...
```

- scala supports infix mode

```scala
foo.bar(baz)

foo bar baz
```

- operators ending with `:` switch positions in infix mode

```scala
caller.::(arg)

arg :: caller
```

---

# Prettier prepending

With the real list, we can prepend with `::`

To the repl!

---

# Prettify our list

```scala
// Ugly
ConsCell(0, ConsCell(1, otherList))

// Pretty
0 :: 1 :: otherList
```

Let's add a `::` method!

To the editor!

---

# Prepend Summary

```scala
trait ConsList {
  def ::(newHead: Int): ConsList = ConsCell(newHead, this)
}

1 :: otherList

1 :: 2 :: 3 :: 4 :: Terminus
```

---

```
    _    ____ _____ _     
   / \  |  _ \_   _( )___ 
  / _ \ | | | || | |// __|
 / ___ \| |_| || |   \__ \
/_/   \_\____/ |_|   |___/
                          
```

---

# ADT's

No they're not sexually transmitted diseases

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
case class HackerList(data: String) extends ConsList
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
case class HackerList(data: String) extends ConsList

map(HackerList("boo!"), _ * 2)

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

Will compile and then throw a `MatchError`

---

# Attack it!

We'll make a separate file with an attacker list in it,

then map over it

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
-       trait ConsList
+sealed trait ConsList

 case class ConsCell(head: Int, tail: ConsList) extends ConsList

 case object Terminus extends ConsList
```

Can only be extended in the same file

---

# Let's seal it

We'll seal it and see if we can still attack it

To the editor!

---

# sealed summary

Helps us encode the "enum"iness of our ADT

The compiler makes sure there's only ever two kinds of cons list

Any code processing a cons list only has to worry about those 2 cases

---

# Scala 3

Has advanced language features that make it easier to encode ADT's

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

# Prepend operator

```scala
// Instead of
ConsCell(1, tail)

// can now do:
1 :: tail

// which is really
tail.::(1)
// because methods ending with `:` swap caller and arg
// in infix position
```

"Declarative" - code is more representative of the runtime structure

---

# ADT

"Algrebraic Data Type"

A List is either:

- cons cell (head data + tail pointer)


- terminus (`Nil`)

---

# Direction

Where is this all going?

Why focus so much on little list?

---

# Direction

> Where is this all going?
> 
> Why focus so much on little list?

List is a great vehicle to introduce FP concepts:

- tail recursion


- functional data structures


- ADT's

---

# Next time

Time/Space complexity

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
