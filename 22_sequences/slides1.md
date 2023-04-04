---
author: Pranohan
date: 2023-05-04
title: All the Sequences
---

```
    _    _ _     _   _
   / \  | | |   | |_| |__   ___
  / _ \ | | |   | __| '_ \ / _ \
 / ___ \| | |   | |_| | | |  __/
/_/   \_\_|_|    \__|_| |_|\___|

 ____
/ ___|  ___  __ _ _   _  ___ _ __   ___ ___  ___
\___ \ / _ \/ _` | | | |/ _ \ '_ \ / __/ _ \/ __|
 ___) |  __/ (_| | |_| |  __/ | | | (_|  __/\__ \
|____/ \___|\__, |\__,_|\___|_| |_|\___\___||___/
               |_|
```

---

# Yo

---

# Today's focus

Get a better understanding for the `Seq` type in scala

---

# Agenda

- concepts: what is a sequence?


- different kinds of sequences


- conventions

---

# Clarification

Just dealing with _immutable_ sequences today

---

# Yo!

Let's go yo!

---

```
  ____                           _
 / ___|___  _ __   ___ ___ _ __ | |_ ___
| |   / _ \| '_ \ / __/ _ \ '_ \| __/ __|
| |__| (_) | | | | (_|  __/ |_) | |_\__ \
 \____\___/|_| |_|\___\___| .__/ \__|___/
                          |_|
```

---

# What is a Sequence?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# What is a Sequence?

```
 ___
|__ \
  / /
 |_|
 (_)
```

How is it different to a Set?

Does it have to be finite?

What about order?

---

# Snippets from the docs

> Sequences always have a defined order of elements
>
> Indices range from 0 up to the length of a sequence

---

# Snippets from the docs

> Sequences always have a defined order of elements
>
> Indices range from 0 up to the length of a sequence
>
> Another way to see a sequence is as a PartialFunction from Int values to the element type of the sequence.
>
> The isDefinedAt method of a sequence returns true for the interval from 0 until length.

Ooh how mathematical sounding...

---

# Deterministic ordering

```
       [0]    [1]    [2]    [3]    [4]    [5]    [6]    ...

       "yo"   "a"    "b"    "hi"   "yo"   "r"    "z"    ...
```


(unlike `Set` and `Map`)

---

# No "gaps"

```
       [0]    [1]           [3]    [4]    [5]    [6]

       "yo"   "a"           "hi"   "yo"   "r"    "z"
```

---

# Possibly infinite

```
       [0]    [1]    [2]    [3]    [4]    [5]    [6]    ...  [100_000_000] ...

       "yo"   "a"    "b"    "hi"   "yo"   "r"    "z"    ...  "whoah!"      ...
```

(more on this later)

---

# Scala Hierarchy

```
                            scala.collection.Seq

                /                                       \

    scala.collection.IMMUTABLE.Seq               scala.collection.MUTABLE.Seq

    |          |          |         |                    ...

 List       Vector      Range      ...
```

---

# Predef

`Seq` = `scala.collection.immutable.Seq`

---

# Abstract?

Doesn't this show that `Seq` is concrete?

```scala
val seq = Seq(0, 1, 2)
```

To the repl!

---

# Array

Is `Array` a sequence?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Array

> Is `Array` a sequence?

Conceptually yes: deterministically ordered

But from the type system perspective it's not a `Seq`

Implicit magic is used to make it feel like a `Seq`

(Also it's mutable...)

---

# Summary

- sequences conceptually are ordered collections of elements


- sequences can be infinite


- `Seq` is an abstraction for immutable sequences


- common implementations are `List`, `Vector` and `Range`


- `Array` is spiritually a mutable `Seq` and some implicit magic makes the type system accept it

---

```
  ____
 / ___|___  _ __ ___  _ __ ___   ___  _ __
| |   / _ \| '_ ` _ \| '_ ` _ \ / _ \| '_ \
| |__| (_) | | | | | | | | | | | (_) | | | |
 \____\___/|_| |_| |_|_| |_| |_|\___/|_| |_|

 ___                 _                           _        _   _
|_ _|_ __ ___  _ __ | | ___ _ __ ___   ___ _ __ | |_ __ _| |_(_) ___  _ __   ___
 | || '_ ` _ \| '_ \| |/ _ \ '_ ` _ \ / _ \ '_ \| __/ _` | __| |/ _ \| '_ \ / __|
 | || | | | | | |_) | |  __/ | | | | |  __/ | | | || (_| | |_| | (_) | | | |\__ \
|___|_| |_| |_| .__/|_|\___|_| |_| |_|\___|_| |_|\__\__,_|\__|_|\___/|_| |_||___/
              |_|
```

---

# Common Implementations

- List


- Vector


- Range


- ArraySeq

---

# List

---

# List

What do you remember about `List`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# List

> What do you remember about `List`?

- functional data structure


- a singly linked list


- ADT: `Nil` and cons cell `::`


- O(1) prepend, head


- O(n) append, size

(see talks from section 12 for more info)

---

# List thoughts

Very simple

Very easy to reason about

Good for recursion

---

# Vector

---

# Vector

Adapted from [the docs](https://docs.scala-lang.org/overviews/collections-2.13/concrete-immutable-collection-classes.html):

> List is an efficient data structure in some specific use cases,
>
> but inefficient in other use cases:
>
> for instance, prepending an element is constant for List
>
> and, conversely, indexed access is linear for List.

ie. getting element n from a `List` is O(n) times

---

# Vector

> Vector is a collection type that provides good performance for all its operations.
>
> Vectors allow accessing any element of the sequence in “effectively” constant time.

ie. a "general purpose" collection where you don't have to think so hard

---

# How?

Internally it stores your elements in a very flat tree

The tree makes search and insert operations logarithmic with large base

(More details in docs and online)

---

# List vs Vector

List will be faster than Vector in its wheel house (e.g. `.head` and prepend)

Vector is safer though

---

# Range

---

# Range

Represents a span of numbers

To the repl!

---

# Range

A mini-family of sequences

Ranges can be constructed for integral types like:

- `Int`


- `Long`


- `BigInt`


(and also `BigDecimal`)

---

# Space

How much space do you think a range across n values takes?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Space

> How much space do you think a range across n values takes?

O(1)

It just stores the start, end and step

The intermediate values are generated on the fly

---

# Time

How fast is prepending to a range?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Time

> How fast is prepending to a range?

The question isn't very clear

It gives the impression that prepending to a range gives another range

```scala
-100 +: (0 until 10)

-100 0 1 2 3 ... 9 // Not a range - it's not periodic
```

To the repl!

---

# Conversion

Range transformations will generally "explode" out the range into another sequence type

---

# Time and Space

What is the time and space complexity of most transformations on range?

---

# Time

> What is the time and space complexity of most transformations on range?

At least O(n) for both,

as it will explode out the structure prior to doing the transformation

---

# Careful!

Ranges can be counter-intuitive


```scala
   (0 until 1_000_000).filter(i != 20)
// ^^^^^^^^^^^^^^^^^^^ ^^^^^^^^^^^^^^^
//    O(1) space         O(n) space
//  size=1_000_000      size=999_999
```

e.g. filtering elements out of a range makes it take more space

---

# ArraySeq

---

# ArraySeq

The immutable version of java's `Array`

To the repl!

---

# ArraySeq

- scala's spiritual equivalent to java's Array


- immutable


- child of `Seq`


- implicit conversions often convert `Array` to `ArraySeq`

---

# Time/Space

Should be same as `Array`

---

# Summary

- `List`


- `Vector`


- `Range`


- `ArraySeq`

There's many more, but these are the ones you'd interact with the most

---

```
  ____                           _   _
 / ___|___  _ ____   _____ _ __ | |_(_) ___  _ __  ___
| |   / _ \| '_ \ \ / / _ \ '_ \| __| |/ _ \| '_ \/ __|
| |__| (_) | | | \ V /  __/ | | | |_| | (_) | | | \__ \
 \____\___/|_| |_|\_/ \___|_| |_|\__|_|\___/|_| |_|___/
```

---

# Seq is everywhere

You've probably noticed,

ie. we define methods to receive `Seq`:

```scala
def gimmeData(seq: Seq[String]): Int = ...
```

We usually don't use specific sequence types:

```scala
def gimmeData(list: List[String]): Int = ...

def gimmeData(vector: Vector[String]): Int = ...
```

---

# Analysis

Let's analyse the pro's and con's

---

# Time/Space complexity

```scala
def doStuff(seq: Seq[String]): Int = {
  // Prepend an element
  val seq2 = "yo!" +: seq

  // Access size
  println(seq.size)

  // Do some filtering
  seq.filter(_.nonEmpty)

  0
}
```

What is the time/space complexity of these actions?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Time/Space complexity

```scala
def doStuff(seq: Seq[String]): Int = {
  // Prepend an element
  val seq2 = "yo!" +: seq

  // Access size
  println(seq.size)

  // Do some filtering
  seq.filter(_.nonEmpty)

  0
}
```

> What is the time/space complexity of these actions?

No idea

It depends on the underlying sequence

---

# Abstraction vs Performance

Abstraction often comes at the cost of performance

---

# Abstraction vs Performance

> Abstraction often comes at the cost of performance

Abstraction: hide the details

Performance: I need details!

---

# Abstracting behaviour

Generally feels like a good thing right?

---

# Animal Example

```scala
def feed(animal: Animal): Unit = {
  val food = new Food
  animal.eat(food)
}

// Animal abstraction defines how to eat food
trait Animal {
  def eat(foo: Food): Unit
}

class Dog extends Animal {
  def eat(foo: Food): Unit = {
    println("Woof woof! Yummy")
    ...
  }
}

class Cat extends Animal {
  def eat(foo: Food): Unit = {
    println("Not good enough!")
    ...
  }
}
```

An example where an inheritance hierarchy is used to abstract over a behaviour

---

# Animal Example

```scala
def feed(animal: Animal): Unit = {
  val food = new Food
  animal.eat(food)
}

// Animal abstraction defines how to eat food
trait Animal {
  def eat(foo: Food): Unit
}
```

`Animal`'s job is to define how to eat food

It's a behaviour

Very clear responsiblity

---

# Behaviour vs Data

```scala
def feed(animal: Animal): Unit = {
  val food = new Food
  animal.eat(food)
}

def doStuff(seq: Seq[String]): Int = {
  ...
}
```

What is `seq` responsible for here?

---

# Behaviour vs Data

```scala
def feed(animal: Animal): Unit = {
  val food = new Food
  animal.eat(food)
}

def doStuff(seq: Seq[String]): Int = {
  ...
}
```

What is `seq` responsible for here?

It's job is to just "be data"

---

# Behaviour vs Data

Data isn't behaviour,

data is... data

---

# Case classes

Do we every abstract over case classes?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Case classes

> Do we every abstract over case classes?

Usually not

It doesn't make sense to hide data

---

# Case classes

> Do we every abstract over case classes?

Usually not

It doesn't make sense to hide data

Abstraction hides details

But the job of data is to provide details

---

# Summing that up

Using data abstractions hides details about our data:

```scala
def doStuff(seq: Seq[String]): Int = {
  ...
}
```

We can't reason about the time/space complexity of this sequence

---

# Counter arguments

---

# Counter argument 1

> Using data abstractions hides details about our data:

```scala
def doStuff(seq: Seq[String]): Int = {
  ...
}
```

> We can't reason about the time/space complexity of this sequence

Who cares?

The sequences are so small that time/space complexity is meaningless anyway

IO is orders of magnitude more significant than CPU

---

# Counter argument 2

Having a simple rule: "just use `Seq`" avoids decision fatigue

It's one less thing you have to think about

---

# Counter Counter arguments

---

# Counter Counter argument 1

> The sequences are so small that time/space complexity is meaningless anyway

But there will be _some_ situations where it does matter

Using `Seq` everywhere trains developers to have these blind spots

---

# Counter Counter argument 1

> The sequences are so small that time/space complexity is meaningless anyway

But there will be _some_ situations where it does matter

Using `Seq` everywhere trains developers to have these blind spots

Counter Counter Counter argument:

Those situations will be rare, if and when they happen our monitoring should detect them,

so trust our future selves to be able to deal with and fix performance issues _if_ they arise

don't optimise too much

---

# Counter Counter argument 2

> The sequences are so small that time/space complexity is meaningless anyway

If you don't care about performance, then why not just pick something more concrete? There's no downside

> Having a simple rule: "just use `Seq`" avoids decision fatigue

We could have a simple rule:

> just use `Vector`

- this also avoids decision fatigue


- the time/space complexity is good across the board


- it guarantees finite sequences

Flip it around: if we used `Vector` everywhere, would you bother to change it back to `Seq`?

---

# What do you think?

```
 ___
|__ \
  / /
 |_|
 (_)
```

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

# What is a sequence?

A deterministically ordered collection

ie. the i'th element is well defined

---

# Seq

Scala's abstraction for immutable sequences

---

# Common children of Seq

- `List`


- `Vector`


- `Range`


- `ArraySeq`

---

# Abstraction vs Performance

You can't reason about the time/space complexity of `Seq`

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
