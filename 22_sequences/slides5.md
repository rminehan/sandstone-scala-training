---
author: Pranohan
date: 2023-06-29
title: Size comparisons
---

```
 ____
/ ___|  ___  __ _ _   _  ___ _ __   ___ ___
\___ \ / _ \/ _` | | | |/ _ \ '_ \ / __/ _ \
 ___) |  __/ (_| | |_| |  __/ | | | (_|  __/
|____/ \___|\__, |\__,_|\___|_| |_|\___\___|
               |_|
 ____  _
/ ___|(_)_______
\___ \| |_  / _ \
 ___) | |/ /  __/
|____/|_/___\___|

  ____                                 _
 / ___|___  _ __ ___  _ __   __ _ _ __(_)___  ___  _ __  ___
| |   / _ \| '_ ` _ \| '_ \ / _` | '__| / __|/ _ \| '_ \/ __|
| |__| (_) | | | | | | |_) | (_| | |  | \__ \ (_) | | | \__ \
 \____\___/|_| |_| |_| .__/ \__,_|_|  |_|___/\___/|_| |_|___/
                     |_|
```

---

# Common examples

```scala
if (seq1.size == seq2.size) {
  ...
}

if (seq.size > 5) {
  ...
}
```

---

# Common examples

```scala
if (seq1.size == seq2.size) {
  ...
}

if (seq.size > 5) {
  ...
}
```

Subtle performance traps exist with this kind of code

---

# Hypothetical scenario

You own a warehouse full of boxes

---

# Hypothetical scenario

You own a warehouse full of boxes

Someone asks you: "Does your warehouse contain exactly 2 boxes?"

---

# Hypothetical scenario

You own a warehouse full of boxes

Someone asks you: "Does your warehouse contain exactly 2 boxes?"

Would you count all the boxes in your warehouse to answer their question?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Hypothetical scenario

> Someone asks you: "Does your warehouse contain exactly 2 boxes?"

How high do you have to count to definitely know that you _don't_ have exactly 2 boxes?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Hypothetical scenario

> Someone asks you: "Does your warehouse contain exactly 2 boxes?"
>
> How high do you have to count to definitely know that you _don't_ have exactly 2 boxes?

3

ie. more than 2

Once you've found at least 3 boxes, you know that there can't be exactly 2

---

# Coding example 1

```scala
val list: List[String] = ...

if (list.size == 2) {
  ...
}
```

What is the time complexity of the `if`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Coding example 1

```scala
val list: List[String] = ...

if (list.size == 2) {
  ...
}
```

> What is the time complexity of the `if`?

O(n)

Computing a `List`'s size is O(n)

---

# Coding example 2

```scala
val list1: List[String] = ...
val list2: List[String] = ...

if (list1.size == list2.size) {
  ...
}
```

What is the time complexity of the `if`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Coding example 2

```scala
val list1: List[String] = ...
val list2: List[String] = ...

if (list1.size == list2.size) {
  ...
}
```

> What is the time complexity of the `if`?

Also O(n) (where n is the larger list)

But up to twice as bad as before

---

# Code example 3

```scala
val seq: Seq[String] = ...

if (seq.size == 6) {
  ...
}
```

What other weird issues might we hit here?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Code example 3

```scala
val seq: Seq[String] = ...

if (seq.size == 6) {
  ...
}
```

> What other weird issues might we hit here?

`seq` doesn't have a well defined length,

e.g.

- infinite


- length is greater than `Int.MaxValue`

---

# Back to the warehouse

You have a warehouse with potentially unlimited boxes in it

Someone asks you: "Do you have exactly 6 boxes in your warehouse?"

You say: "Let me go and count them all, then I'll tell you"

---

# Back to the warehouse

You have a warehouse with potentially unlimited boxes in it

Someone asks you: "Do you have exactly 6 boxes in your warehouse?"

You say: "Let me go and count them all, then I'll tell you"

Do you need to count all the boxes to know if you have 6?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# 7 boxes

> Do you need to count all the boxes to know if you have 6?

Nope, just 7

---

# (Bad) Warehouse algorithm

- request to know if there's exactly `s` boxes


- warehouse managers counts all the boxes in the warehouse


- checks if that number matches `s`

O(n) time, where n is num boxes

---

# (Better) Warehouse algorithm

- request to know if there's exactly `s` boxes


- warehouse managers counts boxes until reaching `s + 1` or running out


- if manager hit `s + 1`, then response with `false`


- if manager ran out of boxes at `s`, returns `true`


- if manager ran out of boxes before `s`, returns `false`

---

# (Better) Warehouse algorithm

- request to know if there's exactly `s` boxes


- warehouse managers counts boxes until reaching `s + 1` or running out


- if manager hit `s + 1`, then response with `false`


- if manager ran out of boxes at `s`, returns `true`


- if manager ran out of boxes before `s`, returns `false`

Worst case running time?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# (Better) Warehouse algorithm

- request to know if there's exactly `s` boxes


- warehouse managers counts boxes until reaching `s + 1` or running out


- if manager hit `s + 1`, then response with `false`


- if manager ran out of boxes at `s`, returns `true`


- if manager ran out of boxes before `s`, returns `false`

> Worst case running time?

O(s)

Actual running time more like min(s + 1, n)

---

# Note

`s` is always finite

So `min(s + 1, n)` will be finite

---

# Short circuiting

The better algorithm short circuits when it has enough info to answer the question

It doesn't need to know the total number of boxes

---

# Two warehouses

Suppose there was two warehouses

---

# Two warehouses

Suppose there was two warehouses

You're asked: do these warehouses have the same number of boxes?

---

# How would you solve it?

> You're asked: do these warehouses have the same number of boxes?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Bad approach

Count the number of boxes in both and compare them (if that's even possible)

---

# Better approach

Pair them up "one for one" across both warehouses

```
Warehouse 1   ---    ---    ---    ---    ---    ---
             | x |  | x |  |   |  |   |  |   |  |   | ...
              ---    ---    ---    ---    ---    ---
                          ---->
Warehouse 2   ---    ---    ---    ---
             | x |  | x |  |   |  |   |
              ---    ---    ---    ---
               paired off
               and counted
```

---

# Better approach

Pair them up "one for one" across both warehouses

```
Warehouse 1   ---    ---    ---    ---    ---    ---
             | x |  | x |  | x |  | x |  |   |  |   | ...
              ---    ---    ---    ---    ---    ---
                          ---->
Warehouse 2   ---    ---    ---    ---
             | x |  | x |  | x |  | x |  "I'm finished"
              ---    ---    ---    ---
               paired off
               and counted
```

Continue until one warehouse runs out of boxes

---

# Better approach

> Continue until one warehouse runs out of boxes

If they both ran out simultaneously, then they have the same number of boxes

If only one ran out, then they have a different number of boxes

---

# Running time?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Running time?

The number of boxes in the smaller warehouse

(But at least we avoid fully counting the larger warehouse)

---

# Surprise twist!

"Warehouse" was a cunning analogy!

I was really talking about programming!

---

# Surprise twist!

What is a warehouse analogous to?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Surprise twist!

> What is a warehouse analogous to?

`Seq` (or any kind of iterable structure)

---

# Introducing .lengthCompare

To the repl!

---

# Summary

```scala
val seq = Seq(0, 1, 2)
seq.lengthCompare(Seq(0, 1, 2, 3)) // -1, ie. "shorter"
seq.lengthCompare(Seq(0, 1, 2))    //  0,  ie. "same length"
seq.lengthCompare(Seq(0, 1))       //  1, ie.  "longer"

// These are equivalent
seq1.size < seq2.size
seq1.lengthCompare(seq2) < 0


Seq(0, 1, 2).lengthCompare(Seq(0, 1))       //  1, ie.  "longer"
```

---

# Comparing with int's

To the repl!

---

# Summary

```scala
val seq = Seq(0, 1, 2)
seq.lengthCompare(4) // -1, ie. shorter
seq.lengthCompare(3) //  0, ie. same length
seq.lengthCompare(2) //  1, ie. longer

// These are equivalent
seq.size > 3
seq.lengthCompare(3) > 0
```

---

# Recap

Using `.lengthCompare` will often reduce these comparisons from O(n) to O(1)

It also increases safety when dealing with infinite sequences or sequences too large for `Int`

---

# Objection!

> Yo Bro! Anna!

you cry

> using `lengthCompare` is so much harder to read!

---

# Readability vs Performance

```scala
seq.length == 3           // potentially O(n) (depends on the underlying sequence implementation)

seq.lengthCompare(3) == 0 // O(1) - but hard to read :(
```

---

# Introducing .lengthIs

To the repl!

---

# Summary

```scala
val seq = Seq(0, 1, 2)
seq.lengthIs == 3 // true
seq.lengthIs > 5  // false
seq.lengthIs < 5  // true
```

---

# lengthIs to the rescue

You can use `lengthIs (operator)` to compare a sequence length to an int

It's efficient and much more readable

(unfortunately no equivalent syntax for comparing sequences with each other)

---

# Best practices?

---

# Best practices?

Similar debate to whether we should use `Seq` everywhere

---

# Factors in the debate

- readability


- decision fatigue


- performance


- good habits

---

# Proposal

When comparing length to a constant, use `lengthIs` as it's both readable and performant

For comparing two sequences, it's up to you

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

# Hidden danger

Comparing sequence lengths often leads to wasted work

---

# lengthCompare

This is more performant and safer

But not easy to read

---

# lengthIs

A more readable alternative for comparing with constant values

Makes sense to use this

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
