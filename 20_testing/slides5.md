---
author: Rohan
date: 2023-04-08
title: FP and Property Testing
---

```
 ____                            _
|  _ \ _ __ ___  _ __   ___ _ __| |_ _   _
| |_) | '__/ _ \| '_ \ / _ \ '__| __| | | |
|  __/| | | (_) | |_) |  __/ |  | |_| |_| |
|_|   |_|  \___/| .__/ \___|_|   \__|\__, |
                |_|                  |___/
 _____         _   _
|_   _|__  ___| |_(_)_ __   __ _
  | |/ _ \/ __| __| | '_ \ / _` |
  | |  __/\__ \ |_| | | | | (_| |
  |_|\___||___/\__|_|_| |_|\__, |
                           |___/
                 _
  __ _ _ __   __| |
 / _` | '_ \ / _` |
| (_| | | | | (_| |
 \__,_|_| |_|\__,_|

 ____                   __
|  _ \ _ __ ___   ___  / _|___
| |_) | '__/ _ \ / _ \| |_/ __|
|  __/| | | (_) | (_) |  _\__ \
|_|   |_|  \___/ \___/|_| |___/
```

---

# Agenda

- properties and proofs


- strong and weak properties

---

# Warning

Philosophy and maths incoming!

:O

---

```
 ____                            _   _
|  _ \ _ __ ___  _ __   ___ _ __| |_(_) ___  ___
| |_) | '__/ _ \| '_ \ / _ \ '__| __| |/ _ \/ __|
|  __/| | | (_) | |_) |  __/ |  | |_| |  __/\__ \
|_|   |_|  \___/| .__/ \___|_|   \__|_|\___||___/
                |_|
                 _
  __ _ _ __   __| |
 / _` | '_ \ / _` |
| (_| | | | | (_| |
 \__,_|_| |_|\__,_|

 ____                   __
|  _ \ _ __ ___   ___  / _|___
| |_) | '__/ _ \ / _ \| |_/ __|
|  __/| | | (_) | (_) |  _\__ \
|_|   |_|  \___/ \___/|_| |___/
```

---

# Example

Someone says to you:

> Prove that n! is a multiple of n, for all n > 0

---

# Engineer's Response

> Prove that n! is a multiple of n, for all n > 0

3! = 6

6 is a multiple of 3

Done!

---

# Survey

Who finds this proof satisfying?

How does it make you feel?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Mathematician's Response

> Prove that n! is a multiple of n, for all n > 0

Our definition of n! is:

> 0! = 1
>
> n! = n * (n-1)!, for all n > 0

So for n > 0, n! = n * (some whole number),

hence n! is a multiple of n

---

# Comparing them

Engineer's Response: "Proof by example"

Mathematician's Response: Formal proof

---

# More data points

What if instead the engineer said this:

> 3! = 6, which is a multiple of 3
>
> 6! = 720, which is a multiple of 6
>
> 10! = 3,628,800, which is a multiple of 10
>
> So it's clearly true, done!

---

# Survey

Who finds this proof satisfying?

How do you find it compared to the first proof by example?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# My guess

If you're uncomfortable with it,

my guess is it relates to extrapolating or generalising

---

# My guess

If you're uncomfortable with it,

my guess is it relates to extrapolating or generalising

You prove: the statement is true for 3 inputs

You generalise: the statement is true for all inputs

---

# Proof by example

> 3! = 6, which is a multiple of 3
>
> 6! = 720, which is a multiple of 6
>
> 10! = 3,628,800, which is a multiple of 10
>
> So it's clearly true, done!

Isn't this a bit like unit testing though?

---

# Unit tests

Imagine we are testing that some function `foo` is correct...

---

# Unit tests

Imagine we are testing that some function `foo` is correct...

```scala
foo(input1) mustBe output1
...
foo(input2) mustBe output2
...
foo(input3) mustBe output3
...
foo(input4) mustBe output4
```

---

# Unit tests

Imagine we are testing that some function `foo` is correct...

```scala
foo(input1) mustBe output1
...
foo(input2) mustBe output2
...
foo(input3) mustBe output3
...
foo(input4) mustBe output4
```

The function `foo` behaves correctly on these 4 inputs,

so we're happy that it's probably working okay on the other inputs

---

# Other factors

Weaknesses of my analogy

We get confidence from other places:

- compiler


- code review


- branch coverage


- engineering experience

---

# My analogy

Tests are acting like proofs

Like random sampling

---

# Engineering vs Mathematics

Engineers don't have a formal proof system like this:

> Our definition of n! is:
>
> 0! = 1
>
> n! = n * (n-1)!, for n > 0
>
> So for n > 0, n! = n * (some whole number),
>
> hence n! is a multiple of n

---

# Engineering vs Mathematics

> Engineers don't have a formal proof system like this:

We just have "proof by example"

---

# Proof by example

> Prove that n! is a multiple of n, for all n > 0

Suppose you provide 1000 sample points:

---

# Proof by example

> Prove that n! is a multiple of n, for all n > 0

Suppose you provide 1000 sample points:

> 2! = 2 which is a multiple of 2
>
> 4! = 24 which is a multiple of 4
>
> 10! = 3,628,800, which is a multiple of 10
>
> 3! = 6, which is a multiple of 3
>
> 6! = 720, which is a multiple of 6
>
...
> 800,000! = ... which is a multiple of 800,000
...
>
> So it's clearly true, done!

---

# Survey

How do you feel now that there's 1000 proof points?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Real world examples

Fermat's Last Theorem

```
a^n + b^n = c^n is impossible for integers a, b, c for n > 2
```

Riemann Hypothesis

Prime number conjectures

---

# Finite inputs

Suppose our `fac` had signature:

```scala
def fac(n: Int): BigInt = ...
//      ^^^^^^
```

---

# Finite inputs

Suppose our `fac` had signature:

```scala
def fac(n: Int): BigInt = ...
//      ^^^^^^
//      Finite
```

---

# Proof

```scala
def fac(n: Int): BigInt = ...
//      ^^^^^^
//      Finite
```

> Prove that fac(n) is a multiple of n, for all n > 0

Easy! I'll check every single value!

---

# Proof

```scala
def fac(n: Int): BigInt = ...
//      ^^^^^^
//      Finite
```

> Prove that fac(n) is a multiple of n, for all n > 0

Easy! I'll check every single value!

> 1! = 1, which is a multiple of 1
>
> 2! = 2, which is a multiple of 2
>
> 3! = 6, which is a multiple of 3
>
> 4! = 24, which is a multiple of 4
>
> 5! = 120, which is a multiple of 5

...

> Int.MaxValue! = ..., which is a multiple of Int.MaxValue

---

# Survey

How do you feel about this "proof by exhaustion"?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# My feeling

It's ugly, but it's absolutely sound

---

# My feeling

It's ugly, but it's absolutely sound

But I wouldn't want to write 2 billion unit tests...

---

# My feeling

It's ugly, but it's absolutely sound

But I wouldn't want to write 2 billion unit tests...

I'd get Pranali to do that

---

# My feeling

It's ugly, but it's absolutely sound

But I wouldn't want to write 2 billion unit tests...

I'd get Pranali to do that ("Proof by Pranali")

---

# Property testing?

You ask:

> What does this have to do with property testing?

---

# Property testing?

You ask:

> What does this have to do with property testing?

Property testing is the closest thing we have to formal proofs

---

# Property testing?

Over time, property tests will cover more and more of your input space

---

# Property testing?

Over time, property tests will cover more and more of your input space

"Proof by example" slowly becomes more like "Proof by exhaustion"

---

# Property testing?

Over time, property tests will cover more and more of your input space

"Proof by example" slowly becomes more like "Proof by exhaustion"

And "Proof by exhaustion" is as good as a formal proof

---

# All the proofs

```
           Proof by example

                 |
                \ /

           Proof by exhaustion

               / \
                |
                |
               \ /

           Formal proof
```

---

# All the proofs

```
           Proof by example

                 |
                \ /                                           Proof by Pranali

           Proof by exhaustion

               / \
                |
                |
               \ /

           Formal proof
```

---

# Property testing

A new perspective:

> Property testing is a practical way to achieve proof by exhaustion

---

# Property testing

A new perspective:

> Property testing is a practical way to achieve proof by exhaustion

By practical I mean:

- you don't have to write 2 billion unit tests


- the test time is amortised across many builds, each build is still fast

---

# Summary

Property testing is analogous to "proof by exhaustion"

Unit testing is analogous to "proof by example"

(This are not perfect analogies)

---

# Summary

> Property testing is analogous to "proof by exhaustion"

This gives us higher confidence

It starts to approach the confidence you'd get from a formal mathematical proof

---

# Summary

Property testing helps recapture some of the benefits of mathematics that we lose in traditional programming

---

```
 ____  _
/ ___|| |_ _ __ ___  _ __   __ _
\___ \| __| '__/ _ \| '_ \ / _` |
 ___) | |_| | | (_) | | | | (_| |
|____/ \__|_|  \___/|_| |_|\__, |
                           |___/
                 _
  __ _ _ __   __| |
 / _` | '_ \ / _` |
| (_| | | | | (_| |
 \__,_|_| |_|\__,_|

__        __         _
\ \      / /__  __ _| | __
 \ \ /\ / / _ \/ _` | |/ /
  \ V  V /  __/ (_| |   <
   \_/\_/ \___|\__,_|_|\_\

 ____                            _   _
|  _ \ _ __ ___  _ __   ___ _ __| |_(_) ___  ___
| |_) | '__/ _ \| '_ \ / _ \ '__| __| |/ _ \/ __|
|  __/| | | (_) | |_) |  __/ |  | |_| |  __/\__ \
|_|   |_|  \___/| .__/ \___|_|   \__|_|\___||___/
                |_|
```

---

# Correctness of functions

Objection!

> Do property tests actually prove that functions are correct?

---

# toUpperCase example

## Property test

```scala
  property("idempotent") = forAll { (s: String) =>
    val upper = toUpperCase(s)
    upper == toUpperCase(upper)
  }
```

## Unit tests

```scala
toUpperCase("") mustBe ""

toUpperCase("abc") mustBe "ABC"

toUpperCase("0123") mustBe "0123"
```

---

# toUpperCase example

## Property test

```scala
  property("idempotent") = forAll { (s: String) =>
    val upper = toUpperCase(s)
    upper == toUpperCase(upper)
  }
```

Objection! This property isn't proving that `toUpperCase` is correct,

it's proving that it's _idempotent_

## Unit tests

```scala
toUpperCase("") mustBe ""

toUpperCase("abc") mustBe "ABC"

toUpperCase("0123") mustBe "0123"
```

---

# Property Testing

Our properties are usually testing something weaker than:

> my function is correct

e.g.

> my function is idempotent

---

# Property Testing

Our properties are usually testing something weaker than:

> my function is correct

e.g.

> my function is idempotent

(But it proves it very thoroughly)

---

# Strong and weak statement

Some statements we prove are stronger than others

---

# isUpperCase

```scala
isUpperCase(s + "abc") == false // for all s
```

---

# isUpperCase

```scala
isUpperCase(s + "abc") == false // for all s

isUpperCase(s + "ABC") == isUpperCase(s) // for all s
```

---

# isUpperCase - stronger property

```scala
isUpperCase(s + "abc") == false // for all s

isUpperCase(s + "ABC") == isUpperCase(s) // for all s
```

These are actually special cases of a more universal property:

(Does anyone remember???)

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# isUpperCase - stronger property

```scala
isUpperCase(s + "abc") == false // for all s

isUpperCase(s + "ABC") == isUpperCase(s) // for all s
```

These are actually special cases of a more universal property:

```scala
isUpperCase(s1 + s2) == isUpperCase(s1) && isUpperCase(s2)
```

Show this by substituting:

---

# Substituting

```scala
isUpperCase(s + "abc") == false // for all s     <---------

isUpperCase(s + "ABC") == isUpperCase(s) // for all s
```

These are actually special cases of a more universal property:

```scala
isUpperCase(s1 + s2) == isUpperCase(s1) && isUpperCase(s2)
```

Show this by substituting:

```scala
// let s2 = "abc"

isUpperCase(s1 + "abc") == isUpperCase(s1) && isUpperCase("abc")

                        == isUpperCase(s1) && false

                        == false
```

---

# Substituting

```scala
isUpperCase(s + "abc") == false // for all s

isUpperCase(s + "ABC") == isUpperCase(s) // for all s   <----------
```

These are actually special cases of a more universal property:

```scala
isUpperCase(s1 + s2) == isUpperCase(s1) && isUpperCase(s2)
```

Show this by substituting:

```scala
// let s2 = "ABC"

isUpperCase(s1 + "ABC") == isUpperCase(s1) && isUpperCase("ABC")

                        == isUpperCase(s1) && true

                        == isUpperCase(s1)
```

---

# Putting it together

This property is stronger:

```scala
isUpperCase(s1 + s2) == isUpperCase(s1) && isUpperCase(s2)
```

By proving ^, you automatically prove:

```scala
isUpperCase(s + "abc") == false // for all s

isUpperCase(s + "ABC") == isUpperCase(s) // for all s
```

---

# Putting it together

This property is stronger:

```scala
isUpperCase(s1 + s2) == isUpperCase(s1) && isUpperCase(s2)
```

By proving ^, you automatically prove:

```scala
isUpperCase(s + "abc") == false // for all s

isUpperCase(s + "ABC") == isUpperCase(s) // for all s
```

So don't bother writing property tests for these weaker ones,

save CPU time

---

# Zooming out

There is a hierarchy of properties:

```
                                         isUpperCase is correct

                                        /                      \

isUpperCase(s1 + s2) == isUpperCase(s1) && isUpperCase(s2)        .....
```

---

# Zooming out

There is a hierarchy of properties:

```
                                         isUpperCase is correct

                                        /                      \

isUpperCase(s1 + s2) == isUpperCase(s1) && isUpperCase(s2)        .....

                  |                                     |

isUpperCase(s + "abc") == false       isUpperCase(s + "ABC") == isUpperCase(s)
```

---

# Hierarchy

```
 strong                               my function is correct

                                /            |           |       \

                             P1            P2            P3         P4

                            /  \          /  \          / \        / \

                         P1a   P1b     P2a   P2b      P3a  P3b   P4a  P4b

 weak                    .................................................

```

Proving one property, automatically proves all the ones underneath

Put the CPU time into the higest ones you can practically prove

---

# Attacking the emporer


```
 strong                               my function is correct      <<<<----------

                                /            |           |       \

                             P1            P2            P3         P4

                            /  \          /  \          / \        / \

                         P1a   P1b     P2a   P2b      P3a  P3b   P4a  P4b

 weak                    .................................................

```

Can it be done? What would it look like?

---

# Attacking the emporer


```
 strong                               my function is correct      <<<<----------

                                /            |           |       \

                             P1            P2            P3         P4

                            /  \          /  \          / \        / \

                         P1a   P1b     P2a   P2b      P3a  P3b   P4a  P4b

 weak                    .................................................

```

Can it be done? What would it look like?

```scala
property("correct") = forAll { (s: String) =>
  toUpperCase(s) == ???
}
```

---

# Attacking the emporer


```
 strong                               my function is correct      <<<<----------

                                /            |           |       \

                             P1            P2            P3         P4

                            /  \          /  \          / \        / \

                         P1a   P1b     P2a   P2b      P3a  P3b   P4a  P4b

 weak                    .................................................

```

Can it be done? What would it look like?

```scala
property("correct") = forAll { (s: String) =>
  toUpperCase(s) == ???
}
```

You'd need some other trustworthy implementation of `toUpperCase` to compare them

---

# Circular

> You'd need some other trustworthy implementation of `toUpperCase` to compare them

(Call it an "oracle")

If we have that,

then why are we building our own one?

Why not just use the oracle?

---


# Attacking the emporer


```
 strong                               my function is correct      <<<<----------

                                /            |           |       \

                             P1            P2            P3         P4

                            /  \          /  \          / \        / \

                         P1a   P1b     P2a   P2b      P3a  P3b   P4a  P4b

 weak                    .................................................

```

> Can it be done? What would it look like?

Usually no

We don't tend to reimplement things just for fun...

Usually you're building something new, and there isn't a pre-existing oracle

---


# Attack the generals:

Usual approach:

```
 strong                               my function is correct

                                /            |           |       \

                             P1            P2            P3         P4      <<<<----------

                            /  \          /  \          / \        / \

                         P1a   P1b     P2a   P2b      P3a  P3b   P4a  P4b

 weak                    .................................................

```

If you can attack the generals, usually there's nowhere left for the emporer to hide

---

# Generals?

Usually strong abstract properties:

- idempotency


- linearity


- commutativity

---

# Some exceptions

There are times you can attack the emporer directly and not his mini-bosses

(Like a cheat code)

---

# Some exceptions

There are times you can attack the emporer directly and not his mini-bosses

## Performance Improvements

Sometimes we're reimplementing a function to improve performance

The old function is battle tested and trustworthy, it can be used as an oracle

```scala
property("correct") = forAll { (s: String) =>
  toUpperCase(s) == oracle(s)
}
```

## Special cases

e.g. factorial

---

# Factorial example

```
fac(0) = 1
fac(n) = n * fac(n - 1), for all n > 0
```

Tell me some properties we could test?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Factorial example

Weaker ones:

- fac(n) > 0, for all n


- fac(n) divides n, for all n > 0


- fac(n) divides k, for all k in [1, ..., n], for all n


- fac(n) ends in 0, for all n > 10

---

# The big one

```
fac(0) = 1
fac(n) = n * fac(n - 1), for all n > 0
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
```

Factorial is defined as a property

---

# As a test

```scala
val positives = arbitrary[Int].filter(_ > 0)

property("correct") = forAll(positives) { (n: Int) =>
  fac(n) == n * fac(n - 1)
}
```

The definition translates very neatly into a property test

---

# Could a bug escape this?

```scala
property("correct") = forAll(positives) { (n: Int) =>
  fac(n) == n * fac(n - 1)
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

# Could a bug escape this?

```scala
property("correct") = forAll(positives) { (n: Int) =>
  fac(n) == n * fac(n - 1)
}
```

Yah

```scala
def fac(n: Int): BigInt = n match {
  case 0 => 2
  case _ => n * fac(n - 1)
}
```

(Pretend it won't stack overflow)

```
0! = 2
1! = 2
2! = 4
3! = 12
4! = 48
...
```

All the values are double what they should be

But the ratio between them is correct

---

# Close the gap

```
fac(0) = 1
fac(n) = n * fac(n - 1), for all n > 0      <--- property tested
```

How to close this gap?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Close the gap

```
fac(0) = 1                                  <--- unit test
fac(n) = n * fac(n - 1), for all n > 0      <--- property tested
```

> How to close this gap?

Unit test!

```scala
// Unit test
fac(0) mustEqual 1

// Property test
property("correct") = forAll(positives) { (n: Int) =>
  fac(n) == n * fac(n - 1)
}
```

The combination of these completely closes the gap

Combined, they are the emporer, no other tests are needed

---

# Remark

Property tests and unit tests can be friends

---

# Summary

Some properties are stronger than others

```
 strong                               my function is correct

                                /            |           |       \

                             P1            P2            P3         P4

                            /  \          /  \          / \        / \

                         P1a   P1b     P2a   P2b      P3a  P3b   P4a  P4b

 weak                    .................................................

```

Spend your CPU resources attacking the strong ones

---

# Summary

To attack the emporer directly, you need an oracle

```
 strong                               my function is correct

                                /            |           |       \

                             P1            P2            P3         P4

                            /  \          /  \          / \        / \

                         P1a   P1b     P2a   P2b      P3a  P3b   P4a  P4b

 weak                    .................................................

```

Usually that's not practical

---

# Summary

Often killing all the emporer's generals will kill the emporer

```
 strong                               my function is correct

                                /            |           |       \

                             P1            P2            P3         P4     <<<<------

                            /  \          /  \          / \        / \

                         P1a   P1b     P2a   P2b      P3a  P3b   P4a  P4b

 weak                    .................................................

```

ie. by proving all the strong properties, there's nowhere for bugs to hide

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

# Proofs

Property testing is like "proof by exhaustion"

Gives us high confidence

---

# Hierarchy

Some proofs are stronger than others

ie. proving the stronger ones automatically proves the weaker ones

---

# Thinking mathematically

There is value in thinking about these things in a mathematical way

You can start to leverage mathematical knowledge in how you reason about your testing

---

# Next time

Properties from FP

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
