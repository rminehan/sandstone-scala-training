---
author: Rohan
date: 2022-07-12
title: Dealing with impurity
---


```
 ____             _ _
|  _ \  ___  __ _| (_)_ __   __ _
| | | |/ _ \/ _` | | | '_ \ / _` |
| |_| |  __/ (_| | | | | | | (_| |
|____/ \___|\__,_|_|_|_| |_|\__, |
                            |___/
          _ _   _
__      _(_) |_| |__
\ \ /\ / / | __| '_ \
 \ V  V /| | |_| | | |
  \_/\_/ |_|\__|_| |_|

 _                            _ _
(_)_ __ ___  _ __  _   _ _ __(_) |_ _   _
| | '_ ` _ \| '_ \| | | | '__| | __| | | |
| | | | | | | |_) | |_| | |  | | |_| |_| |
|_|_| |_| |_| .__/ \__,_|_|  |_|\__|\__, |
            |_|                     |___/
```

---

# Recap

Pure functions can be inlined without changing program behaviour

---

# Holy Trinity of purity

- no side effects


- complete, ie. returns a value for every input


- deterministic

---

# Useful!

- easy to refactor


- easy to reason about, no hidden traps


- equational reasoning


- easy to memoize

---

# In an ideal world...

... we would only ever deal with pure functions

Stay within our warm cozy ivory tower of functional programming

---

# The harsh reality

For a program to do something useful,

it must be impure...

---

# Quarantine

There are strategies we can use to minimise/contain the impurity

---

# Today

- inevitable impurity


- composition of functions


- dealing with impurity

---

```
 ___                  _ _        _     _
|_ _|_ __   _____   _(_) |_ __ _| |__ | | ___
 | || '_ \ / _ \ \ / / | __/ _` | '_ \| |/ _ \
 | || | | |  __/\ V /| | || (_| | |_) | |  __/
|___|_| |_|\___| \_/ |_|\__\__,_|_.__/|_|\___|

 ___                            _ _
|_ _|_ __ ___  _ __  _   _ _ __(_) |_ _   _
 | || '_ ` _ \| '_ \| | | | '__| | __| | | |
 | || | | | | | |_) | |_| | |  | | |_| |_| |
|___|_| |_| |_| .__/ \__,_|_|  |_|\__|\__, |
              |_|                     |___/
```

---

# Inevitable impurity

Software that is useful enough to pay money for,

will have some kind of impurity

---

# Example

Most software has a concept of a user

- user details


- user preferences

---

# User change

- change address


- change their settings


- change plan

---

# User's change

We model users

Users change

We have state

---

# Database

A big dirty impure piece of mutable state

A viper's nest of side effects

---

# Dirty world

The world is dirty and impure

(e.g. mutable)

---

# Dirty software

> The world is dirty and impure

Software that models the world,

must be dirty and impure

---

# Impure tools

- updating records in a database


- PUT request


- most clock based logic


- exceptions/errors


- logging


- producing and consuming kafka messages

---

# Oh no!

Our ivory tower of functional programming is under attack

from the cruel practical realities of our world

---

# Don't surrender

There are ways we can manage/contain the impurity

(later)

---

But first a detour:

```
 _____                 _   _
|  ___|   _ _ __   ___| |_(_) ___  _ __
| |_ | | | | '_ \ / __| __| |/ _ \| '_ \
|  _|| |_| | | | | (__| |_| | (_) | | | |
|_|   \__,_|_| |_|\___|\__|_|\___/|_| |_|

  ____                                _ _   _
 / ___|___  _ __ ___  _ __   ___  ___(_) |_(_) ___  _ __
| |   / _ \| '_ ` _ \| '_ \ / _ \/ __| | __| |/ _ \| '_ \
| |__| (_) | | | | | | |_) | (_) \__ \ | |_| | (_) | | | |
 \____\___/|_| |_| |_| .__/ \___/|___/_|\__|_|\___/|_| |_|
                     |_|
```

---

# Example

Two functions:

```scala
def square(i: Int): Int = i * i

def obscured(length: Int): String = "#" * length
```

---

# Black boxes

```scala
def square(i: Int): Int = i * i

def obscured(length: Int): String = "#" * length
```

```
          -----------------
         |                 |
Int ---> |    square       | ---> Int
 3       |                 |       9
          -----------------


          -----------------
         |                 |
Int ---> |    obscured     | ---> String
 9       |                 |      "#########"
          -----------------
```

---

# Connect the wires

```scala
def square(i: Int): Int = i * i

def obscured(length: Int): String = "#" * length
```

```
          -----------------
         |                 |
Int ---> |    square       | ---> Int
 3       |                 |       9
          -----------------


          -----------------
         |                 |
Int ---> |    obscured     | ---> String
 9       |                 |      "#########"
          -----------------



             -----------------        -----------------
            |                 |      |                 |
Int --->    |    square       | ---> |     obscured    |    ---> String
 3          |                 |  9   |                 |         "#########"
             -----------------        -----------------


```

---

# Zooming out

```scala
def square(i: Int): Int = i * i

def obscured(length: Int): String = "#" * length
```

```
          -----------------
         |                 |
Int ---> |    square       | ---> Int
 3       |                 |       9
          -----------------


          -----------------
         |                 |
Int ---> |    obscured     | ---> String
 9       |                 |      "#########"
          -----------------

           -----------------------------------------------
          |                                               |
          |  -----------------        -----------------   |
          | |                 |      |                 |  |
Int --->  | |    square       | ---> |     obscured    |  | ---> String
 3        | |                 |  9   |                 |  |      "#########"
          |  -----------------        -----------------   |
          |                                               |
           -----------------------------------------------
```

This "composition" is itself a function

---

# Scala

Scala has built in tools to represent this

To the repl!

---

# Summary

`Func`'s have a method `andThen` for composing

```scala
val square: Int => Int = i => i * i

square(3) // 9

val obscure: Int => String = length => "#" * length

obscure(4) // "####"

val squareAndThenObscure = square andThen obscure
// or:
val squareAndThenObscure = square.andThen(obscure)

squareAndThenObscure(2) // "####"
squareAndThenObscure(3) // "#########"
```

---

# Recap

f: A => B

g: B => C

We can connect the two functions with `andThen`

f andThen g: A => C

---

# Visually

```
          -----------------
         |                 |
 A  ---> |        f        | ---> B
         |                 |
          -----------------


          -----------------
         |                 |
 B  ---> |        g        | ---> C
         |                 |
          -----------------

           -----------------------------------------------
          |                 f andThen g                   |
          |  -----------------        -----------------   |
          | |                 |      |                 |  |
 A  --->  | |        f        | ---> |        g        |  | --->  C
          | |                 |      |                 |  |
          |  -----------------        -----------------   |
          |                                               |
           -----------------------------------------------
```

---

# Complex example

```scala
def foo(i: Int): Int = (square(i) * 2) + (i * 2)
```

---

# Visually

```scala
def foo(i: Int): Int = (square(i) * 2) + (i * 2)
```

```
              -----------------        -----------------
             |                 |      |                 |
         --> |     square      | ---> |      * 2        | ---
        |    |                 |  9   |                 | 18 |
        |     -----------------        -----------------     |    -----------------
Int ----                                                      -> |                 |
 3      |                              -----------------      -> |       +         | ---> 24
        |                             |                 |    |   |                 |
         ---------------------------> |      * 2        | ---     -----------------
                                      |                 |  6
                                       -----------------


```

More complex topology

---

# Relevance?

> Why are we talking about composition all of a sudden?
>
> Get back to the FP brainwashing!

---

# Why?

Understanding how smaller components are used to build larger components

---

# Why?

> Understanding how smaller components are used to build larger components

Purity of smaller components affects purity of larger components

---

# Question

f: A => B

g: B => C

Suppose f and g are pure

Will `f andThen g` also be pure?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Yes!

f: A => B

g: B => C

Suppose f and g are pure

> Will `f andThen g` also be pure?

Yes

---

# Informal proof

If `f` and `g`:

- have no side-effects, `f andThen g` won't either


- don't throw exceptions, `f andThen g` won't either


- are deterministic, `f andThen g` will be too

(not a very rigorous proof)

---

# Question

Is this function pure?

```scala
val squareAndThenObscureAndThenDuplicate = square andThen obscure andThen duplicate
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Yes!

> Is this function pure?

```scala
val squareAndThenObscureAndThenDuplicate = square andThen obscure andThen duplicate
//             complex                     simple         simple          simple
```

Pure + Pure = Pure

Simple components can be analysed in isolation

---

# Pure complex programs

If all your small components are pure,

then you know your large complex program is pure

---

# Pure + Impure?

f: A => B  (impure)

g: B => C  (pure)

Will `f andThen g` be pure?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Probably not

f: A => B  (impure)

g: B => C  (pure)

> Will `f andThen g` be pure?

Probably not

---

# Example

```scala
// Side-effect
val square: Int => Int = i => {
  println("Hi")
  i * i
}

val obscure: Int => String = length => "#" * length

// Inherits its Daddy's side-effect
val squabscure = square andThen obscure

squabscure(3)
// prints "Hi"
```

---

# Algebra of purity

Pure andThen Pure = Pure

Pure andThen Impure = Impure (most of the time)

---

# Infectious impurity

Pure andThen Impure = Impure (most of the time)

Means impurity "spreads"

---

# Visually

Suppose `square` was impure

```
                                        foo
     ------------------------------------------------------------------------------------
    |                                                                                    |
    |                                                                                    |
    |         -----------------        -----------------                                 |
    |        |                 |      |                 |                                |
    |    --> |     square      | ---> |      * 2        | ---                            |
    |   |    |    IMPURE!      |  9   |                 | 18 |                           |
    |   |     -----------------        -----------------     |    -----------------      |
Int-|---                                                     |   |                 |     |
 3  |   |                              -----------------     |-> |       +         | ----|--> 24
    |   |                             |                 |    |   |                 |     |
    |    ---------------------------> |      * 2        | ---     -----------------      |
    |                                 |                 |  6                             |
    |                                  -----------------                                 |
    |                                                                                    |
    |                                                                                    |
     ------------------------------------------------------------------------------------
```

`foo` is impure

Anything that uses it is also impure

---

# Spreading up through the layers

```
-----------------------------------------
    |         |         |         |
imp | impure  | impure  | impure  |
    |         |         |         |
-----------------------------------------
|         |         |         |         |
| impure  | impure  | impure  |         |
|         |         |         |         |
-----------------------------------------
    |         |         |         |
    | impure  | impure  |         |
    |         |         |         |
-----------------------------------------
|         |         |         |         |
|         | impure  |         |         |
|         |         |         |         |
-----------------------------------------
             ^ patient 0
```

Spreads up through the layers

---

```
 ____             _ _
|  _ \  ___  __ _| (_)_ __   __ _
| | | |/ _ \/ _` | | | '_ \ / _` |
| |_| |  __/ (_| | | | | | | (_| |
|____/ \___|\__,_|_|_|_| |_|\__, |
                            |___/
          _ _   _
__      _(_) |_| |__
\ \ /\ / / | __| '_ \
 \ V  V /| | |_| | | |
  \_/\_/ |_|\__|_| |_|

 _                            _ _
(_)_ __ ___  _ __  _   _ _ __(_) |_ _   _
| | '_ ` _ \| '_ \| | | | '__| | __| | | |
| | | | | | | |_) | |_| | |  | | |_| |_| |
|_|_| |_| |_| .__/ \__,_|_|  |_|\__|\__, |
            |_|                     |___/
```

---

# Recapping so far

- real world applications have impurity


- impurity is contagious

---

# What to do?

How do you handle an outbreak of a disease?

---

# Quarantine strategy

```
-----------------------------------------
    |         |         |         |       impurities
    |         |         |         |
    |         |         |         |                                    def higher(i: Int): Int = {
-----------------------------------------                                println(i)
|         |         |         |         |                                lower(i)
|         |         |         |         | pure                         }
|         |         |         |         |
-----------------------------------------                  / \
    |         |         |         |                         |
    |         |         |         |       pure              | contamination
    |         |         |         |                         |
-----------------------------------------                   |
|         |         |         |         |                   |
|         |         |         |         | pure
|         |         |         |         |
-----------------------------------------
```

Push the impurity "up"

Impurity can only infect upwards

---

# Examples

> Push the impurity "up"

---

# Time example

```scala
def isAfternoon(): Boolean = ZonedDateTime.now().getHour >= 12
```

---

# Writing tests

```scala
def isAfternoon(): Boolean = ZonedDateTime.now().getHour >= 12
```

I'd expect at least 2 tests

---

# But how?

```scala
def isAfternoon(): Boolean = ZonedDateTime.now().getHour >= 12

...

"isAfternoon" should {
  "return true when run in the afternoon" in {

  }

  "return false when run in the morning" in {

  }
}
```

---

# But how?

```scala
def isAfternoon(): Boolean = ZonedDateTime.now().getHour >= 12

...

"isAfternoon" should {
  "return true when run in the afternoon" in {
    // Dear Jenkins, please only run this test during the afternoon UTC
    isAfternoon() mustBe true
  }

  "return false when run in the morning" in {
    // Dear Jenkins, please only run this test during the morning UTC
    isAfternoon() mustBe false
  }
}
```

---

# Testing...

Non-deterministic functions are much harder to control and test

---

# How's it used?

```scala
def isAfternoon(): Boolean = ZonedDateTime.now().getHour >= 12

if (isAfternoon())
  haveNap()
```

---

# "Push it up"

```scala
def isAfternoon(): Boolean = ZonedDateTime.now().getHour >= 12

if (isAfternoon())
  haveNap()
```

Push the impurity up a layer:

```scala
def isAfternoon(now: ZonedDateTime): Boolean = now.getHour >= 12

if (isAfternoon(ZonedDateTime.now())
  haveNap()
```

---

# "Push it up"

We didn't remove the impurity,

just moved it higher

---

# Back to testing

```scala
def isAfternoon(now: ZonedDateTime): Boolean = now.getHour >= 12

...

"isAfternoon" should {
  "return true for an evening time" in {
    isAfternoon(ZoneDateTime.parse("2022-06-13T22:23:01Z")) mustBe true
  }

  "return false for a morning time" in {
    isAfternoon(ZoneDateTime.parse("2022-06-13T05:04:04Z")) mustBe false
  }
}
```

Much easier

---

# Exception example

---

# Exception example

```scala
def divide(numerator: Int, denominator: Int): Double = {
  if (denominator == 0)
    throw new IllegalArgumentException("Denominator of division is 0")
  else
    numerator.toDouble / denominator
}
```

Not complete

Everything "above" this function can potentially throw an exception now

---

# Push up

## Old

```scala
def divide(numerator: Int, denominator: Int): Double = {
  if (denominator == 0)
    throw new IllegalArgumentException("Denominator of division is 0")
  else
    numerator.toDouble / denominator
}
```

## New

```scala
def divide(numerator: Int, denominator: Int): Either[Exception, Double] = {
  if (denominator == 0)
    Left(new IllegalArgumentException("Denominator of division is 0"))
  else
    Right(numerator.toDouble / denominator)
}
```

Remember exceptions aren't impure, throwing them is

---

# Caller's perspective

## Old

```scala
def divide(numerator: Int, denominator: Int): Double = {
  if (denominator == 0)
    throw new IllegalArgumentException("Denominator of division is 0")
  else
    numerator.toDouble / denominator
}

divide(num, den) // potentially throws - might forget to check
```

---

# Caller's perspective

## New

```scala
def divide(numerator: Int, denominator: Int): Either[Exception, Double] = {
  if (denominator == 0)
    Left(new IllegalArgumentException("Denominator of division is 0"))
  else
    Right(numerator.toDouble / denominator)
}

divide(num, den) match {
  case Left(error) => ...
  case Right(value) => ...
}
```

Forced to deal with it

Responsibility of handling the error is pushed to the caller (which usually makes more sense)

---

# Aside - purity and testing

Hopefully previous example showed that purity makes testing easier

---

# Aside - Unholy Trinity resists testing

These things are hard/annoying/impossible to test:

- side effects


- incomplete (exceptions, infinite loops, crashes the JVM)


- non-deterministic logic

Pure functions avoid these traps!

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

# Composition

Use `andThen` to compose functions together

f: A => B

g: B => C

f andThen g: A => C

---

# Inevitable Impurity

Real world software needs impurity

---

# Algebra of purity

Pure + Pure = Pure

Pure + Impure = Impure

Impurity is "infectious"

---

# Push it "up"

Impurity is inevitable,

but we can push it higher up our software layers,

then at least the lower layers aren't so infected

---

# Purity and testing

Also saw that pure functions are easier to test

"Data in, data out"

No traps

Fits black box unit testing well

---

# Next time

Unsafe things with Pranali

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
