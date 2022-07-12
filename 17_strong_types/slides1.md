---
author: Rohan
date: 2022-08-02
title: Strong Types
---

Introducing:

```
 ____  _
/ ___|| |_ _ __ ___  _ __   __ _
\___ \| __| '__/ _ \| '_ \ / _` |
 ___) | |_| | | (_) | | | | (_| |
|____/ \__|_|  \___/|_| |_|\__, |
                           |___/
 _____
|_   _|   _ _ __   ___  ___
  | || | | | '_ \ / _ \/ __|
  | || |_| | |_) |  __/\__ \
  |_| \__, | .__/ \___||___/
      |___/|_|
```

---

# Strong Types!

A way to make functions more pure

Simpler, safer code

Getting the compiler to work for us

---

# What is today about?

- understand the common problems caused by incomplete functions


- analyse pro's and con's of common approaches


- introduce strong types as a new approach

---

```
__        __         _    _
\ \      / /__  _ __| | _(_)_ __   __ _
 \ \ /\ / / _ \| '__| |/ / | '_ \ / _` |
  \ V  V / (_) | |  |   <| | | | | (_| |
   \_/\_/ \___/|_|  |_|\_\_|_| |_|\__, |
                                  |___/
 _____                           _
| ____|_  ____ _ _ __ ___  _ __ | | ___
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \
| |___ >  < (_| | | | | | | |_) | |  __/
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___|
                          |_|
```

---

# Working example

```scala
object Service {
  private def helper(i: Int, j: Int): Double = math.sqrt(i) * math.sqrt(j)

  ...
}
```

---

# Many uses

```scala
object Service {
  private def helper(i: Int, j: Int): Double = math.sqrt(i) * math.sqrt(j)

  def method1(i: Int): Double = if (i > 0) helper(i, 3) else 2.0

  def method2(): Double = helper(3, 2)

  def method3(i: Int): Double = if (i > 0) helper(i, 2) else 1.5

  ...
}
```

---

# sqrt recap

Dangerous function:

```scala
math.sqrt(-1) // NaN
```

"False negative"

---

# Safe?

Is our service safe from this error? Do we need checks?

```scala
object Service {
  private def helper(i: Int, j: Int): Double = math.sqrt(i) * math.sqrt(j)

  def method1(i: Int): Double = if (i > 0) helper(i, 3) else 2.0

  def method2(): Double = helper(3, 2)

  def method3(i: Int): Double = if (i > 0) helper(i, 2) else 1.5
}
```

---

# Contextual proof

> Is our service safe from this error? Do we need checks?

```scala
object Service {
  private def helper(i: Int, j: Int): Double = math.sqrt(i) * math.sqrt(j)

  def method1(i: Int): Double = if (i > 0) helper(i, 3) else 2.0

  def method2(): Double = helper(3, 2)

  def method3(i: Int): Double = if (i > 0) helper(i, 2) else 1.5
}
```

> helper is private so all uses must be within this object.
>
> For each usage I know the inputs are always safe.
>
> Therefore the i and j values are always safe (no checks needed).

---

# But then...

... an intern joins your team

---

# Beware the "intern factor"

```scala
object Service {
  private def helper(i: Int, j: Int): Double = math.sqrt(i) * math.sqrt(j)

  def method1(i: Int): Double = if (i > 0) helper(i, 3) else 2.0

  def method2(): Double = helper(3, 2)

  def method3(i: Int): Double = if (i > 0) helper(i, 2) else 1.5

  // Intern adds this
  def method4(j: Int): Double = helper(3, j)
}
```

---

# Nasty bug

"False negative"

It doesn't blow up, it returns `NaN`

---

# Not future proof

```scala
object Service {
  private def helper(i: Int, j: Int): Double = math.sqrt(i) * math.sqrt(j)

  ...

  def method4(j: Int): Double = helper(3, j)
}
```

Unfair to pick on interns

Easy mistake anyone could make

The implicit nature of a contextual proof makes it not very "future proof"

---

# Fundamental Problem

---

# Fundamental Problem

```scala
object Service {
  private def helper(i: Int, j: Int): Double = math.sqrt(i) * math.sqrt(j)

  ...
}
```

`helper` accepts data it can't process (negative i and j)

---

# Fundamental Problem

```scala
object Service {
  private def helper(i: Int, j: Int): Double = math.sqrt(i) * math.sqrt(j)

  ...

  def method4(j: Int): Double = helper(3, j)
}
```

> `helper` accepts data it can't process (negative i and j)

It's effectively incomplete and that potentially infects the methods that use it

---

```
 ____  _  __  __                     _
|  _ \(_)/ _|/ _| ___ _ __ ___ _ __ | |_
| | | | | |_| |_ / _ \ '__/ _ \ '_ \| __|
| |_| | |  _|  _|  __/ | |  __/ | | | |_
|____/|_|_| |_|  \___|_|  \___|_| |_|\__|

    _                                     _
   / \   _ __  _ __  _ __ ___   __ _  ___| |__   ___  ___
  / _ \ | '_ \| '_ \| '__/ _ \ / _` |/ __| '_ \ / _ \/ __|
 / ___ \| |_) | |_) | | | (_) | (_| | (__| | | |  __/\__ \
/_/   \_\ .__/| .__/|_|  \___/ \__,_|\___|_| |_|\___||___/
        |_|   |_|
```

---

# Different Approaches

- "fingers crossed"


- defensive


- strong types

---

# Fingers Crossed Approach

---

# Fingers Crossed Approach

Just trust the caller not to put bad values in

```scala
  // No checking
  private def helper(i: Int, j: Int): Double = math.sqrt(i) * math.sqrt(j)
```

---

# "Fingers Crossed"

> Just trust the caller not to put bad values in

Relies on a implicit convention that isn't enforced

> "please don't pass a negative value in"

---

# Common mindset

> If they _do_ pass in bad data we can't process,
>
> something will crash,
>
> so what's the point in me checking for it explicitly and crashing

---

# Example

## Rely on implementation to crash

```scala
def youngestAdult(users: Seq[User]): User = {
  users
    .filter(_.age >= 18)
    .minBy(_.age) // <--- will explode if no adults
}
```

Looks nice

---

# Example

## Rely on implementation to crash

```scala
def youngestAdult(users: Seq[User]): User = {
  users
    .filter(_.age >= 18)
    .minBy(_.age) // <--- will explode if no adults
}
```

Looks nice

## Explicitly check

```scala
def youngestAdult(users: Seq[User]): User = {
  val adults = users.filter(_.age >= 18)

  if (adults.isEmpty)
    throw new Exception("....")
  else
    adults.minBy(_.age) // safe now
}
```

Feels like "extra work"

---

# Subconsciously...

It's easier to ignore error handling

Makes code simpler

Don't have to think about icky exceptions (e.g. which type to use)

---

# Counter arguments

- bad input won't always cause a crash


- explicit exceptions are usually more meaningful


- undefined behaviour


- side effects aren't transactional

---

# Bad input won't always cause a crash

---

# ie. false negatives

- `math.sqrt`


- java libraries that return `null`

---

# Data leakage

Bad data flows downstream

Causes confusing exceptions/errors downstream

Can get into the database...

---

# Explicit exceptions are usually more meaningful

---

# Meaningless exception

```scala
def youngestAdult(users: Seq[User]): User = {
  users
    .filter(_.age >= 18)
    .minBy(_.age) // <--- will explode if no adults
}
```

Exception:

```
java.lang.UnsupportedOperationException: empty.minBy
  scala.collection.IterableOnceOps.maxBy(IterableOnce.scala:978)
  scala.collection.IterableOnceOps.maxBy$(IterableOnce.scala:976)
  scala.collection.AbstractIterable.maxBy(Iterable.scala:919)
  ...
```

---

# Meaningful exception

```scala
def processUsers(users: Seq[User]): Users = {
  val adults = users.filter(_.age >= 18)

  if (adults.isEmpty)
    throw new Exception("Unable to process users as there's no adults")
  else
    adults.minBy(_.age) // safe now
}
```

I would rather catch this

---

# Undefined behaviour

---

# Undefined behaviour

Error handling behaviour is usually undefined

ie. no tests

---

# Changes

As the implementation changes,

the error behaviour changes

---

# Changes

> As the implementation changes,
>
> the error behaviour changes

Potential compatibility error

---

# Case study

Infinite loop

---

# Side effects aren't transactional

---

# Example

```scala
def youngestAdult(users: Seq[User]): User = {

  println("Effect 1")

  val youngest = users.minBy(_.age)

  println("Effect 2")

  youngest
}
```

If the input is empty, we get effect 1, but not 2

(usually that's not what you want)

---

# Validating inputs up front

```scala
def youngestAdult(users: Seq[User]): User = {
  if (users.isEmpty)
    throw new IllegalArgumentException("...")
  else {
      println("Effect 1")

      val youngest = users.minBy(_.age)

      println("Effect 2")

      youngest
  }
}
```

More transactional now

---

# Recap: "Cross your fingers" approach

---

# Convention

Relying on callers to do the right thing

---

# No explicit error handling

The behaviour when you get bad input isn't defined

- bad error messages


- potential false negatives and data corruption


- break backwards compatibility


- non-transactional logic

---

# My feeling

Harder to maintain

Not robust

Shortcuts now and pain later

---

# Reality

This is the most common approach

---

# Why?

> This is the most common approach

Path of least resistance

Just pretend the issue can't happen

Don't have to think about icky error handling, write tests etc...

---

# Alternatives?

You say:

> Okay, okay, that approach has some problems...
>
> But defensive checking is quite tedious...

---

# Defensive Approach

---

# Defensive Approach

- explicitly validate data


- write tests to explicitly define what happens when data is bad

---

# Tedious

> But defensive checking is quite tedious...

Yep

Leads to a lot of extra checks and tests

Pollutes your code

---

# Back to our example

```scala
object Service {
  private def helper(i: Int, j: Int): Double = {
    if (i >= 0 && j >= 0)
      math.sqrt(i) * math.sqrt(j)
    else
      throw new IllegalArgumentException("...")
  }

  ...
}
```

Bad signal to noise ratio

---

# Pass through methods

```scala
  def method4(j: Int): Double = helper(3, j)

  def method5(i: Int, j: Int): Double = helper(i, j) * 2
```

---

# More tests!

```scala
  def method4(j: Int): Double = helper(3, j)

  def method5(i: Int, j: Int): Double = helper(i, j) * 2

"method4" should {
  "throw an IllegalArgumentException if j < 0" in {
    assertThrows[IllegalArgumentException](Service.method4(-1))
  }
}

"method5" should {
  "throw an IllegalArgumentException if i < 0" in {
    assertThrows[IllegalArgumentException](Service.method5(-1, 10))
  }
  "throw an IllegalArgumentException if j < 0" in {
    assertThrows[IllegalArgumentException](Service.method4(1, -1))
  }
}
```

Duplicate tests

The incompleteness of `helper` has leaks out

---

# Recap - defensive approach

Software is much more robust

But bad signal to noise ratio

---

# Use Either?

---

# Capture error with Either

## Exception approach

```scala
  private def helper(i: Int, j: Int): Double = {
    if (i >= 0 && j >= 0)
      math.sqrt(i) * math.sqrt(j)
    else
      throw new IllegalArgumentException("...")
  }
```

## Either approach

```scala
  private def helper(i: Int, j: Int): Either[Exception, Double] = {
    if (i >= 0 && j >= 0)
      Right(math.sqrt(i) * math.sqrt(j))
    else
      Left(new IllegalArgumentException("..."))
  }

  ...

  def method4(j: Int): Double = helper(3, j) match {
    case Left(ex) => throw ex
    case Right(d) => d
  }

  def method5(i: Int, j: Int): Double = (helper(i, j) * 2) match {
    case Left(ex) => throw ex
    case Right(d) => d
  }
```

---

# Tests will be the same

Methods still accept invalid inputs

```scala
"method4" should {
  "throw an IllegalArgumentException if j < 0" in {
    assertThrows[IllegalArgumentException](Service.method4(-1))
  }
}

"method5" should {
  "throw an IllegalArgumentException if i < 0" in {
    assertThrows[IllegalArgumentException](Service.method5(-1, 10))
  }
  "throw an IllegalArgumentException if j < 0" in {
    assertThrows[IllegalArgumentException](Service.method4(1, -1))
  }
}
```

---

# Either?

It's a bit better

But still bad signal to noise ratio

Duplicate tests

---

# Fundamental Problem

Functions are accepting values they can't process

---

# Bouncer Analogy

---

# Translating to code

```scala
  private def helper(i: Int, j: Int): Either[Exception, Double] = {
    if (i >= 0 && j >= 0)
      Right(math.sqrt(i) * math.sqrt(j))
    else
      Left(new IllegalArgumentException("..."))
  }
```

`Int` is the bouncer

It's letting in values like -1 that can't be processed

---

# Exception/Either

Doesn't matter which you use

The bad data has already been let in

---

```
 ____  _
/ ___|| |_ _ __ ___  _ __   __ _
\___ \| __| '__/ _ \| '_ \ / _` |
 ___) | |_| | | (_) | | | | (_| |
|____/ \__|_|  \___/|_| |_|\__, |
                           |___/
 _____
|_   _|   _ _ __   ___  ___
  | || | | | '_ \ / _ \/ __|
  | || |_| | |_) |  __/\__ \
  |_| \__, | .__/ \___||___/
      |___/|_|
```

---

# Fundamental Problem

> Functions are accepting values they can't process

If we fix this, life gets much simpler

---

# Example

```scala
private def helper(i: PositiveInt, j: PositiveInt): Double = math.sqrt(i) * math.sqrt(j)
```

PositiveInt = 0, 1, 2, 3, ...

---

# Int vs PositiveInt

```
                                     PositiveInt
                           ------------------------->
 ...   -5  -4  -3  -2  -1  0  +1  +2  +3  +4  +5 ...
 <-------------------------------------------------->
                        Int
```

---

# How?

> How do you make such a type?

Deal with that later

For now, just roll with it

---

# Bouncer analogy

This "strong" type is a better bouncer

It only allows in values we can process

```scala
private def helper(i: PositiveInt, j: PositiveInt): Double = math.sqrt(i) * math.sqrt(j)
```

The function becomes complete

---

# Defensive checking?

None needed

```scala
private def helper(i: PositiveInt, j: PositiveInt): Double = math.sqrt(i) * math.sqrt(j)
```

Compiler makes sure it's impossible to pass an invalid value

---

# Tests?

Previously we used to have all these tests:

```scala
  def method4(j: Int): Double = helper(3, j)

  def method5(i: Int, j: Int): Double = helper(i, j) * 2

"method4" should {
  "throw an IllegalArgumentException if j < 0" in {
    assertThrows[IllegalArgumentException](Service.method4(-1))
  }
  ...
}

"method5" should {
  "throw an IllegalArgumentException if i < 0" in {
    assertThrows[IllegalArgumentException](Service.method5(-1, 10))
  }
  "throw an IllegalArgumentException if j < 0" in {
    assertThrows[IllegalArgumentException](Service.method4(1, -1))
  }
  ...
}
```

Noisy and duplicated

Let's make the methods receive a strong type...

---

# No error tests

Recall we used to have all these tests:

```scala
  def method4(j: PositiveInt): Double = helper(3, j)

  def method5(i: PositiveInt, j: PositiveInt): Double = helper(i, j) * 2

"method4" should {
  // "throw an IllegalArgumentException if j < 0" in {
  //   assertThrows[IllegalArgumentException](Service.method4(-1))
  // }
}

"method5" should {
  // "throw an IllegalArgumentException if i < 0" in {
  //   assertThrows[IllegalArgumentException](Service.method5(-1, 10))
  // }
  // "throw an IllegalArgumentException if j < 0" in {
  //   assertThrows[IllegalArgumentException](Service.method4(1, -1))
  // }
}
```

---

# Single responsibility

Our methods now just have the responsibility of performing a computation

They don't have to also validate the data

---

# Duplicate tests?

Because they're all duplicating the responsibility of validating input

---

# Move that responsibility

```scala
object PositiveInt {
  def fromInt(i: Int): Option[PositiveInt] = ...
}

"fromInt" should {
  "produce Some(3) from 3" in {
    PositiveInt.fromInt(3) mustBe Some(3)
  }
  "produce None from -1" in {
    PositiveInt.fromInt(-1) mustBe None
  }
}
```

---

# Recap - defensive approach

`Service` was responsible for:

- validating integers


- performing computations

Leads to duplicated logic and tests

---

# Recap - strong type approach

`PositiveInt` is responsible for validating integers

`Service` is responsible for computations

---

# Recap - bouncer

Strong types are like a bouncer that makes sure we only let in data that can be processed

---

# Simple and Safe

```scala
private def helper(i: PositiveInt, j: PositiveInt): Double = math.sqrt(i) * math.sqrt(j)
```

- no defensive checking


- complete


- simplifies testing

---

# "Push up"

We pushed the problem of invalid inputs up to the caller

```scala
// Previously
Service.method3(i) // possibly throws

// Now
PositiveInt.fromInt(i) match {
  case Some(p) => Service.method3(p)
  case None => // handle error appropriately
}
```

---

# "Push up"

- better error handling


- keeps the lower layers of your software more pure

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

# "Weak" types

Types for representing inputs are often "weak"

ie. they allow in values that can't be processed

```scala
private def helper(i: Int, j: Int): Double = math.sqrt(i) * math.sqrt(j)
//       weak         ^^^     ^^^
```

---

# Incomplete

Allowing values we can't process allows potential failures

```scala
helper(-1, 3)
```

---

# Strategies

- cross your fingers and just hope they don't pass bad values


- add defensive checking code (and corresponding tests)

Both have issues

---

# Instead

Just don't use a weak type

ie. only allow in data that we _can_ process

---

# Benefits

- simple code (removes checks)


- safe


- less tests to write

You can have your cake and eat it

---

# Next time

`NonEmptyList`

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
