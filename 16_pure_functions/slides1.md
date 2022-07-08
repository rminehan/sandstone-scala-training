---
author: Rohan
date: 2022-07-05
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

Big concept

Touches everything we do

An important part of your FP brain washing...

---

# Today

- what is a pure function?


- what stops functions being pure?


- why is purity useful?

---

# Why should I care?

You'll see it has a lot of very practical uses

---

# Training roadmap

Purity relates to testing, error handling and modelling

Made sense to cover first

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

An expression is pure if you can always inline it without changing your program's behaviour

---

# Counter-examples

> An expression is pure if you can always inline it without changing your program's behaviour

Easiest to explain with impure expressions

---

# Side effects...

We'll see that functions with side effects aren't pure...

---

# Example

Function with a side effect

```scala
var counter = 0

def cleanString(s: String): String = {
  counter += 1
  s.trim.toLowerCase
}
```

Create a simple program that cleans a string,

then uses that value twice

---

# Hmmm...

> Create a simple program that cleans a string,
>
> then uses that value twice

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

> An expression is pure if you can always inline it without changing anything

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
println(cleanString(" BoBaN "))
// counter == 2
```

---

# Next impurity...

Non-deterministic logic

---

# Example

Consider this program:

```scala
val now = ZonedDateTime.now()

Thread.sleep(5000)

println(now)
```

---

# Inlining?

Would it change behaviour if we inlined `now`?

## Before

```scala
val now = ZonedDateTime.now()

Thread.sleep(5000)

println(now)
```

## After

```scala
Thread.sleep(5000)

println(ZonedDateTime.now())
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Yep

> Would it change behaviour if we inlined `now`?

## Before

```scala
val now = ZonedDateTime.now()

Thread.sleep(5000)

println(now)
```

## After

```scala
Thread.sleep(5000)

println(ZonedDateTime.now())
```

It will print out a timestamp 5 seconds different

---

# Next impurity...

Throwing exceptions

---

# Example

Consider this program:

```scala
def tantrum: Int = throw new Exception("Wah!")

val i = tantrum // exception happens in the caller's thread

Future {
  println("Hi")
  i
}
```

The `Future` is never started, "Hi" never prints

---

# Inline it

```scala
def tantrum: Int = throw new Exception("Wah!")

// val i = tantrum // exception happens in the caller's thread

Future {
  println("Hi")
  tantrum
}
```

The `Future` _is_ started, "Hi" prints then the Future fails

---

# Impure!

You get different behaviour depending _where_ an exception is thrown

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

---

# Pure exception example

You can gracefully _return_ exceptions in a pure way

```scala
def createException(error: String): Exception = new ComputationException(s"Computation failed: $error")
```

---

# Recap

---

# Recap

> A function is pure, if inlining it will never change anything

A nice rule for detecting anything odd the function does

---

# Lingo

Also called "referentially transparent"

---

# Expanding to expressions

Purity is not limited to functions

> An expression is pure if you can always inline it without changing anything

---

# Example 1

```scala
val x = 30 * 50

println(x)
```

---

# Example 1

```scala
val x = 30 * 50

println(x)
```

inlines to:

```scala
println(30 * 50)
```

---

# Example 2

```scala
val x = if (s.nonEmpty) Some(s) else None

val y = process(x)
```

inlines to:

```scala
val y = process(if (s.nonEmpty) Some(s) else None)
```

---

# So..

Purity is a concept for expressions

Functions are just one example of that

(but often the most common one we talk about)

---

# Holy Trinity of Purity

- no side effects


- complete: always returns gracefully (no exceptions, infinite loops, jvm shutdowns)


- deterministic

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

Why is it nice to work with pure functions?

---

# Easy refactoring

---

# Refactoring

> Tidying up your code without changing the behaviour

Good fit for pure functions

---

# Inlining example

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

# Suppose...

`cleanString` is pure...

```scala
val cleaned = cleanString(" BoBaN ") // pure

... // 30 lines

val next = cleaned.toUpperCase
```

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

Easy peasy lemon squeezy

---

# Suppose...

`cleanString` is _not_ pure...

---

# Hmmm....

```scala
val cleaned = cleanString(" BoBaN ") // impure

... // 30 lines

val next = cleaned.toUpperCase
```

:(

Now we have understand:

- what happens in that function


- any possible interactions with those 30 lines of code

---

# Extraction example

---

# Extraction example

```scala
val x1 = expensive("Boban")

...

val x2 = expensive("Boban")
val y = x2 + 1
```

---

# Extraction example

```scala
val x1 = expensive("Boban")

...

val x2 = expensive("Boban")
val y = x2 + 1
```

You say,

> Yikes! I'm doing the same expensive operation twice!

---

# Refactoring

```diff
 val x1 = expensive("Boban")

 ...

-val x2 = expensive("Boban")
+val x2 = x1
 val y = x2 + 1
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
def cleanString(s: String): String = ...
```

---

# What I see

```scala
def cleanString(s: String): String = ...
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

# Simple

Pure functions make life simple

"It does what it says on the label"

Inputs, outputs

No side effects and nasty exceptions

---

# Memoization

---

# Example

Consider factorial:

```scala
def fac(n: BigInt): BigInt = ...

fac(5) = 5 * 4 * 3 * 2 * 1
```

O(n) time complexity

---

# Service example

FE sends the BE a number

BE computes the factorial and returns it

What optimisation can you see?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Memoization

> FE sends the BE a number
>
> BE computes the factorial and returns it
>
> What optimisation can you see?

Memoization!

Suppose James sends 20_000 and Pranali does too

Why recompute `fac(20_000)`?

---

# Algorithm

Each time a request arrives:

- check if we've already computed it


- if yes, return that


- if not, compute, store and return

---

# What prevents memoization?

---

# Cookie example

FE sends BE a user id,

BE replies with that user's favourite flavour of cookie

---

# Cookie example

> FE sends BE a user id,
>
> BE replies with that user's favourite flavour of cookie

Can you memoize this?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Cookie example

> FE sends BE a user id,
>
> BE replies with that user's favourite flavour of cookie
>
> Can you memoize this?

Not permanently

People's taste in cookies change

---

# Cookie service

```scala
// Monday
favouriteCookieFlavor("yuhan") // returns "choc-chip"

// Tuesday
favouriteCookieFlavor("yuhan") // returns "vegemite"
```

What member of the holy trinity of purity are we talking about here?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Cookie service

```scala
// Monday
favouriteCookieFlavor("yuhan") // returns "choc-chip"

// Tuesday
favouriteCookieFlavor("yuhan") // returns "vegemite"
```

> What member of the holy trinity of purity are we talking about here?

Determinism

This function is not deterministic,

so memoizing is trickier

---

# Side effects

Functions with side-effects are tricky to memoize as well

```scala
def favouriteCookieFlavor(userId: UserId): String =
  // Side effect in here
  // e.g. analytics, events, incrementing a counter
```

Memoizing will cause the counter to not get incremented on later requests

---

# What hinders memoization?

- non-determinism


- side effects

---

# What hinders memoization?

- non-determinism


- side effects

Pure functions don't have this!

Very simple to memoize, easy peasy lemon squeezy

---

# Equational reasoning

---

# Equational reasoning?

Pure functions are like mathematical functions

---

# Example

Unrolling a computation

---

# Summing

```scala
def sum(top: Int): Int = top match {
  case 0 => 0
  case _ => top + sum(top - 1)
}
```

---

# Evaluate

```scala
def sum(top: Int): Int = top match {
  case 0 => 0
  case _ => top + sum(top - 1)
}

val total = sum(5)
```

---

# Evaluate

```scala
def sum(top: Int): Int = top match {
  case 0 => 0
  case _ => top + sum(top - 1)
}

val total = top(5)

val total = 5 match {
  case 0 => 0
  case _ => 5 + sum(5 - 1)
}
```

---

# Evaluate

```scala
def sum(top: Int): Int = top match {
  case 0 => 0
  case _ => top + sum(top - 1)
}

val total = 5 + sum(4)
```

---

# Evaluate

```scala
def sum(top: Int): Int = top match {
  case 0 => 0
  case _ => top + sum(top - 1)
}

val total = 5 + (4 match {
  ...
})
```

---

# Evaluate

```scala
def sum(top: Int): Int = top match {
  case 0 => 0
  case _ => top + sum(top - 1)
}

val total = 5 + 4 + sum(3)
```

---

# Equational reasoning

```scala
def sum(top: Int): Int = top match {
  case 0 => 0
  case _ =>
    counter += 1 // side effect
    top + sum(top - 1)
}

val total = sum(5)

          = 5 + sum(4)

          = 5 + 4 + sum(3)

          = 5 + 4 + 3 + sum(2)

          ...

          = 5 + 4 + 3 + 2 + 1 + 0

          = 15
```

Just like high school maths

Feels like maths, because it's a pure function

---

# Impure?

Suppose `sum` had a side effect though:

```scala
def sum(top: Int): Int = top match {
  case 0 => 0
  case _ =>
    counter += 1 // side effect
    top + sum(top - 1)
}
```

Can you still apply this equational reasoning?

---

# Trickier

Becomes

```scala
def sum(top: Int): Int = top match {
  case 0 => 0
  case _ =>
    counter += 1 // side effect
    top + sum(top - 1)
}

val total = sum(5)

         ?= 5 + sum(4)

         ?= 5 + 4 + sum(3)

         ?= 5 + 4 + 3 + sum(2)

          ...

         ?= 5 + 4 + 3 + 2 + 1 + 0

         ?= 15
```

Our equational reasoning doesn't have a nice way to capture the side effect

---

# Equational reasoning

> Pure functions are like mathematical functions

That unlocks mathematical processes of reasoning

---

# Recap

Pure functions make it easy to use "equational reasoning"

You can unwind functions to understand how they work

Allows simplification/refactoring

---

# Testing

In upcoming sessions we'll see that pure functions make testing easier

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

# Pure

An expression is pure if you can inline it without changing program behaviour

```scala
val cleaned = name.trim.toLowerCase
process(cleaned)

// becomes
process(name.trim.toLowerCase)
```

---

# Pure functions and mathematics

Pure functions are like mathematical functions

We can port mathematical concepts into our software engineering

---

# Very useful

- makes refactoring easier


- easier to reason about (no hidden traps)


- easy to memoize


- allows equational reasoning

---

# And more...

Other reasons we couldn't cover today

---

# Holy Trinity of Purity

- no side effects


- no exceptions ("complete")


- deterministic

---

# My hope

We'll start designing libraries and services with purity in mind

---

# Next time

Dealing with impurity

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
