---
author: Rohan
date: 2022-06-13
title: Pure Functions
---

```
 ____
|  _ \ _   _ _ __ ___
| |_) | | | | '__/ _ \
|  __/| |_| | | |  __/
|_|    \__,_|_|  \___|

 _____                 _   _
|  ___|   _ _ __   ___| |_(_) ___  _ __  ___
| |_ | | | | '_ \ / __| __| |/ _ \| '_ \/ __|
|  _|| |_| | | | | (__| |_| | (_) | | | \__ \
|_|   \__,_|_| |_|\___|\__|_|\___/|_| |_|___/

```

---

# Pure Functions

An important part of your FP brain washing...

---

# Today

- what is a pure function?


- why is it useful?


- what stops functions being pure?


- composing pure functions


- dealing with impurity

---

# Why should I care?

Leads to stronger software:

- easier to test


- easier to reason about


- better designed, clearer abstraction boundaries

More on this during the talk

---

# Warning

Will be talking about "purity" a lot

Might sound a bit Nazi-ish...

---

```
__        ___           _     _       _ _  ___
\ \      / / |__   __ _| |_  (_)___  (_) ||__ \
 \ \ /\ / /| '_ \ / _` | __| | / __| | | __|/ /
  \ V  V / | | | | (_| | |_  | \__ \ | | |_|_|
   \_/\_/  |_| |_|\__,_|\__| |_|___/ |_|\__(_)

```

What is a pure function?

---

# Definition

A function is pure if you can always inline it without changing anything

---

# Example

> you can always inline it without changing anything

```scala
def cleanString(s: String): String = s.trim.toLowerCase

...

// Cached
val cleaned = cleanString(" BoBaN ")
val fullName = s"$cleaned jones"
println(cleaned)
```

---

# Example

> you can always inline it without changing anything

```scala
def cleanString(s: String): String = s.trim.toLowerCase

...

// Cached
val cleaned = cleanString(" BoBaN ")
val fullName = s"$cleaned jones"
println(cleaned)


// Inlined
val fullName = s"${cleanString(" BoBaN ")} jones"
println(cleanString(" BoBaN ")
```

---

# Example

This one is pure...

Inlining it will never change anything

```scala
def cleanString(s: String): String = s.trim.toLowerCase
```

---

# Another example

Think of one that's not pure...

ie. when would inlining matter...

---

# Side effect...

```scala
var counter = 0

def cleanString(s: String): String = {
  counter += 1
  s.trim.toLowerCase
}
```

Create a simple program that uses the cleaned string twice

---

# Hmmm...

```scala
var counter = 0

def cleanString(s: String): String = {
  counter += 1
  s.trim.toLowerCase
}

// Cached
val cleaned = cleanString(" BoBaN ")
val fullName = s"$cleaned jones"
println(cleaned)
// counter == 1
```

---

# Impure!

```scala
var counter = 0

def cleanString(s: String): String = {
  counter += 1
  s.trim.toLowerCase
}

// Cached
val cleaned = cleanString(" BoBaN ")
val fullName = s"$cleaned jones"
println(cleaned)
// counter == 1


// Inlined
val fullName = s"${cleanString(" BoBaN ")} jones"
println(cleanString(" BoBaN ")
// counter == 2
```

---

# Recap

> A function is pure, if inlining it will never change anything

A nice way of detecting anything odd the function does

---

# Lingo

Also called "referentially transparent"

---

```
__        ___             _
\ \      / / |__  _   _  (_)___
 \ \ /\ / /| '_ \| | | | | / __|
  \ V  V / | | | | |_| | | \__ \
   \_/\_/  |_| |_|\__, | |_|___/
                  |___/
 _ _                     __       _ ___
(_) |_   _   _ ___  ___ / _|_   _| |__ \
| | __| | | | / __|/ _ \ |_| | | | | / /
| | |_  | |_| \__ \  __/  _| |_| | ||_|
|_|\__|  \__,_|___/\___|_|  \__,_|_|(_)

```

---

# Easy refactoring

---

# Inlining example

```scala
val cleaned = cleanString(" BoBaN ")

... // 30 lines

val next = cleaned.toUpperCase
```

You say,

> it's only used once, I want to inline it

---

# Inlining example

```scala
val cleaned = cleanString(" BoBaN ")

... // 30 lines

val next = cleaned.toUpperCase
```

becomes

```scala
... // 30 lines

val next = cleanString(" BoBaN ").toUpperCase
```

---

# Moving example

```scala
val cleaned = cleanString(" BoBaN ")

... // 30 lines

val next = cleaned.toUpperCase
```

You say,

> why is it defined so far away from where it's used!

(reduce semantic distance)

---

# Moving example

```scala
val cleaned = cleanString(" BoBaN ")

... // 30 lines

val next = cleaned.toUpperCase
```

becomes

```scala
... // 30 lines

val cleaned = cleanString(" BoBaN ")
val next = cleaned.toUpperCase
```

---

# In both cases

If `cleanString` is pure,

your changes won't modify the behaviour of the program

---

# Refactoring

> Tidying up your code without changing the logic

Good fit for pure functions

---

# Performance example

```scala
def expensive(s: String): Option[Int] = ...

...

val x1 = expensive("Boban")

...

val x2 = expensive("Boban")
val y = x2 + 1
```

You say,

> Yikes! I'm doing the same expensive operation twice!

ie. reverse inline (outline?), cache

---

# Refactoring

```diff
 def expensive(s: String): Option[Int] = ...

 ...

 val x1 = expensive("Boban")

 ...

-val x2 = expensive("Boban")
-val y = x2 + 1
+val y = x1 + 1
```

Safe to do if `expensive` is pure

---

# If it's impure...

... still possible to optimise,

but will take more reasoning/sweating

---

# Scares devs

> but will take more reasoning/sweating

Less willingness to refactor

> I better not touch it in case I break something

More likely code will rot

---

# Moral

Pure functions make refactoring easier

More refactoring means less rot

---

# Easier to reason about

---

# No hidden traps

The signature tells you exactly what it does:

```scala
def cleanString(s: String): String = s.trim.toLowerCase
```

---

# What I see

```scala
def cleanString(s: String): String = s.trim.toLowerCase
```

- takes a `String`


- returns a `String`

---

# Exception?

```scala
def cleanString(s: String): String = {
  if (s.isEmpty)
    throw new InvalidArgumentException("Empty strings not supported")
  else
    s.trim.toLowerCase
}
```

The exception is not represented in the signature,

you only find out by peeping inside the code and playing with it

---

# Modify the example

```scala
def cleanString(s: String): String = {
  if (s.isEmpty)
    throw new InvalidArgumentException("Empty strings not supported")
  else
    s.trim.toLowerCase
}
```

(later we'll show throwing exceptions is impure)

---

# Side effect?

```scala
var counter = 0

def cleanString(s: String): String = {
  counter += 1
  s.trim.toLowerCase
}
```

Also not captured in the signature,

> it's a trap!

Thanks Admiral Ackbar!

---

# Simple functions

Simple functions make life simple

Inputs, outputs

No side effects and nasty exceptions

---

```
__        ___           _     _                    _
\ \      / / |__   __ _| |_  | |__  _ __ ___  __ _| | _____
 \ \ /\ / /| '_ \ / _` | __| | '_ \| '__/ _ \/ _` | |/ / __|
  \ V  V / | | | | (_| | |_  | |_) | | |  __/ (_| |   <\__ \
   \_/\_/  |_| |_|\__,_|\__| |_.__/|_|  \___|\__,_|_|\_\___/

                  _ _        ___
 _ __  _   _ _ __(_) |_ _   |__ \
| '_ \| | | | '__| | __| | | |/ /
| |_) | |_| | |  | | |_| |_| |_|
| .__/ \__,_|_|  |_|\__|\__, (_)
|_|                     |___/
```

---

# The trifecta of impurity

- side effects


- throwing exceptions


- non-determinism

---

# Proofs

For each,

I'll provide a little proof that they break purity

---

# Side effects

---

# Proof

Suppose `f` has a side effect,

then construct a program that uses the output of `f` twice

and inline it

---

# Example

> then construct a program that uses the output of `f` twice

```scala
// Has a side effect
def f(): Int = ...

val f1 = f()
println(f1)
println(f1)
```

Triggers the side effect once

---

# Example

> and inline it

```scala
// Has a side effect
def f(): Int = ...

// Cached
val f1 = f()
println(f1)
println(f1)

// Inlined
println(f())
println(f())
```

Inlined, it triggers the side effect twice

(which is different behaviour)

---

# Throwing exceptions

---

# Proof

Suppose `f` throws an exception,

then construct a program that uses the output of `f` in a `Future`

and inline it

---

# Example

> then construct a program that uses the output of `f` in a `Future`

```scala
def f(input: String): Int = {
 if (input == "bad") throw new Exception
 ...
}

val f1 = f("bad") // exception happens in the caller's thread

Future {
  println("Hi")
  f1
}
```

The `Future` is never started, "Hi" never prints

---

# Inline it

> and inline it

```scala
def f(input: String): Int = {
 if (input == "bad") throw new Exception
 ...
}

// val f1 = f("bad")

Future {
  println("Hi")
  // f1
  f("bad")
}
```

The `Future` _is_ started, "Hi" prints then the Future fails

---

# Interesting example

With impure code and `Future`,

we need to think a lot harder and be more careful about where it runs

---

# Note

It's not exceptions which are breaking purity...

---

# Note

It's not exceptions which are breaking purity...

it's _throwing_ exceptions

(You can gracefully _return_ exceptions in a pure way)

---

# Non-determinism

---

# Proof

Suppose `f` is non-deterministic,

then construct a program where `f` will produce a different value when inlined,

then inline it!

---

# Example

> then construct a program where `f` will produce a different value when inlined,

```scala
def f(): Long = {
  // return the seconds since the epoch (Jan 1st 1970)
  ...
}

val f1 = f() // Suppose this is 1,000,000

Thread.sleep(30_000)
println(f1) // prints 1,000,000
```

---

# Example

> then inline it!

```scala
def f(): Long = {
  // return the seconds since the epoch (Jan 1st 1970)
  ...
}

// val f1 = f() // Suppose this is 1,000,000

Thread.sleep(30_000)
println(f()) // prints 1,000,030
```

By evaluating the function _after_ the sleep, we print a different result

---

# Aside: parameter less functions

If a function has no inputs, but returns a meaningful output...

---

# Aside: parameter less functions

If a function has no inputs, but returns a meaningful output...

it's probably non-deterministic

```scala
ZonedDateTime.now()
Random.nextInt()
```

---

# Summing up

If your function has any of these, it can't be pure:

- side effects


- throwing exceptions


- non-determinism

---

# Contrapositive

> If your function has any of these, it can't be pure:

Flipping that around, if a function is pure, it must be:

- free of side effects


- "complete" (never throw exceptions)


- deterministic

(The trifecta of purity)

---

```
  ____                                _
 / ___|___  _ __ ___  _ __   ___  ___(_)_ __   __ _
| |   / _ \| '_ ` _ \| '_ \ / _ \/ __| | '_ \ / _` |
| |__| (_) | | | | | | |_) | (_) \__ \ | | | | (_| |
 \____\___/|_| |_| |_| .__/ \___/|___/_|_| |_|\__, |
                     |_|                      |___/
 ____
|  _ \ _   _ _ __ ___
| |_) | | | | '__/ _ \
|  __/| |_| | | |  __/
|_|    \__,_|_|  \___|

 _____                 _   _
|  ___|   _ _ __   ___| |_(_) ___  _ __  ___
| |_ | | | | '_ \ / __| __| |/ _ \| '_ \/ __|
|  _|| |_| | | | | (__| |_| | (_) | | | \__ \
|_|   \__,_|_| |_|\___|\__|_|\___/|_| |_|___/

```

---

# Pure babies

When two pure functions combine,

they make a pure baby

---

# Example

Two pure functions

```scala
def cleanString(s: String): String = s.trim.toLowerCase

def obscure(s: String): String = s.replaceAll("a", "")
```

---

# Example

Two pure functions

```scala
def cleanString(s: String): String = s.trim.toLowerCase

def obscure(s: String): String = s.replaceAll("a", "")

// baby
def cleanAndObscure(s: String): String = {
  val cleaned = cleanString(s)
  obscure(cleaned)
}
```

The baby is also pure

---

# Extend the example

We find another pure function

```scala
def duplicate(s: String): String = s + s
```

---

# Grand-baby

```scala
def cleanAndObscure(s: String): String = {
  val cleaned = cleanString(s)
  obscure(cleaned)
}

def duplicate(s: String): String = s + s

// grand-baby
def cleanAndObscureAndDuplicate(s: String): String = {
  val cleanedAndObscured = cleanedAndObscure(s)
  duplicate(cleanedAndObscured)
}
```

The grand-baby is also pure

---

# Summary

Composing pure functions creates more complex pure functions

Easy to reason about your program

---

# Pure + Impure = Impure

Suppose `f` is pure, but `g` is impure...

---

# Pure + Impure = Impure

Suppose `f` is pure, but `g` is impure...

then `f` composed with `g` is probably impure :(

---

# Example

```scala
// Side-effect
def cleanString(s: String): String = {
  println("Hi")
  s.trim.toLowerCase
}

def obscure(s: String): String = s.replaceAll("a", "")

// Inherits its Daddy's side-effect
def cleanAndObscure(s: String): String = {
  val cleaned = cleanString(s)
  obscure(cleaned)
}
```

---

# Example

```scala
// Side-effect
def cleanString(s: String): String = {
  println("Hi")
  s.trim.toLowerCase
}

def obscure(s: String): String = s.replaceAll("a", "")

// Inherits its Daddy's side-effect
def cleanAndObscure(s: String): String = {
  val cleaned = cleanString(s)
  obscure(cleaned)
}
```

Picture similar examples if `cleanString` threw an exception or was non-deterministic

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

# The harsh reality

For a program to do something useful,

it must be impure...

---

# Purity rules out

- updating records in a database


- most api calls


- caching


- most clock based logic


- errors


- logging


- producing and consuming kafka messages

---

# Impure world

The world is impure,

so to model it with software makes the software impure

---

# What to do?!

Our ivory tower of functional programming is under attack

from the cruel practical realities of our world

---

# What to do

Programs must have some impurity,

push it up higher and keep the core pure where possible

---

# Visually

```
-----------------------------------------
    |         |         |         |       impurities
-----------------------------------------
|         |         |         |         |
-----------------------------------------
    |         |         |         |
-----------------------------------------
|         |         |         |         | pure foundations
-----------------------------------------
```

---

# Simple example

```scala
def isAfternoon(): Boolean = ZonedDateTime.now().getHour() >= 12
```

---

# Writing tests

```scala
def isAfternoon(): Boolean = ZonedDateTime.now().getHour() >= 12

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

# In the wild

Currently:

```scala
def isAfternoon(): Boolean = ZonedDateTime.now().getHour() >= 12

if (isAfternoon())
  haveNap()
```

---

# "Push it up"

Currently:

```scala
def isAfternoon(): Boolean = ZonedDateTime.now().getHour() >= 12

if (isAfternoon())
  haveNap()
```

Push the impurity up a layer:

```scala
def isAfternoon(now: ZonedDateTime): Boolean = now.getHour() >= 12

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
def isAfternoon(now: ZonedDateTime): Boolean = now.getHour() >= 12

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

# Summary

Impurity is innevitable,

put you can often push it to the higher layer,

and keep your internal libraries and services more pure

---

# Practically speaking

The standard scala stack (`Future`) makes it hard to keep the core of your logic truly pure

---

# Practically speaking

The standard scala stack (`Future`) makes it hard to keep the core of your logic truly pure

There are more advanced frameworks like zio which make this easier

(Not on our roadmap though)

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

# Pure functions

Functions which can always be inlined

ie. referentially transparent

---

# Why are they useful?

- code is easier to reason about and refactor


- tests easier to write

---

# The trifecta of purity

Pure functions will be:

- free of side effects


- complete (not throw exceptions)


- deterministic

---

# Composition

Pure + Pure = Pure

If you combine small pure building blocks,

you get something complex,

but you know it's pure

---

# Purity and maths

Pure functions resemble mathematical functions

---

# Dealing with impurity

It's innevitable

But can be controlled and minimised

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
