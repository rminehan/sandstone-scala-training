---
author: Pranohan
date: 2022-09-06
title: Modelling Examples
---

```
 __  __           _      _ _ _
|  \/  | ___   __| | ___| | (_)_ __   __ _
| |\/| |/ _ \ / _` |/ _ \ | | | '_ \ / _` |
| |  | | (_) | (_| |  __/ | | | | | | (_| |
|_|  |_|\___/ \__,_|\___|_|_|_|_| |_|\__, |
                                     |___/

 _____                           _
| ____|_  ____ _ _ __ ___  _ __ | | ___  ___
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \/ __|
| |___ >  < (_| | | | | | | |_) | |  __/\__ \
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___||___/
                          |_|
```

---

# Recap

`strongtypes` demo

```scala
object Natural extends Strong[Int] {
  def validate(i: Int): Boolean = i >= 0
}
```

---

# Recap

`strongtypes` demo

```scala
type Natural = Natural.Type // makes things smoother
object Natural extends Strong[Int] {
  def validate(i: Int): Boolean = i >= 0
}
```

---

# Narrowing approach

Create strong type by narrowing weak type

e.g. `Natural` is `Int` narrowed to non-negative values

---

# Today

Lots of juicy examples

Get us thinking more deeply about modelling

---

# Format

I'll throw a concept at you

You tell me things like:

- what weak type you'd use


- how you'd restrict it

```scala
object Example extends Strong[???] {
  def validate(i: ???): Boolean = ???
}
```

---

# Example

Me: "Age of a user"


You: "Represent with an Int, restrict it to non-negative values."


Me: "Correct!"

---

# MVP

Get's Violet's North Sydney parking spot for a month

Can auction it

---

# Analysis

Not about getting the right or wrong answer

But in thinking through the problem

So throw out ideas and get the discussion going

---

# Ready?

---

# Example 1

Name of a user

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Name

Weak type: String

Conditions:

- non-empty usually


- no boundary whitespace?


- possibly some character restrictions (e.g. no newline, carriage return)

---

# Example 2

Digit (0-9)

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Digit (Char)

Weak type: Char

Conditions:

- '0' to '9'

---

# Digit (Int/Short)

Weak type: Int/Short

Conditions:

- 0 to 9

---

# Which one?

Depends what's it's used for...

---

# Which one?

Depends what's it's used for...

Numerical processing: Int

Symbols/id's/regex: Char

---

# Example 3

Email address

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Email Address

Weak type: String

Conditions:

- satisfies a complex regex? (not all emails do though)

Or:

- contains 1 '@'


- non-empty tokens on both sides


- tokens restricted to certain characters


- tokens can't start or end with certain symbols (e.g. '.')


- no boundary whitespace

---

# Example 4

Monetary amount (AUD)

e.g. `30.67`

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Monetary amount (AUD)

Depends...

e.g.

- can it go beyond 2 d.p?


- capped?


- negative?

---

# Float or Double?

No

Potential precision issues

---

# BigDecimal

Better for precision

---

# Pair

- dollar part (`Long` or `BigInt`)


- cent part (`Int` 0-99)

Captures the 2 d.p concept

---

# Pair

```scala
type Dollar = Long

type Cent = Cent.Type
object Cent extends Strong[Int] {
  def validate(i: Int): Boolean = i >= 0 && i < 100
}

type Currency = (Dollar, Cent)

// 3.67
(3L, Cent.fromUnsafe(67))
```

---

# Pair - tricky addition

```
 (3, 50)     $3.50
 +
 (9, 60)     $9.60
-------------------
(13, 10)    $13.10
```

Carry the dollar

---

# Pair - tricky addition

Negative value represented dollar part

```
 (-3, 50)   -$3.50
 +
  (0, 10)    $0.10
-------------------
 (-3, 60)   -$3.60 ?
```

Addition is tricky...

---

# Thinking more abstractly

Cents is our indivisible unit

ie. a dollar is really just 100 cents

$3.50 is just 350c

---

# Cents

```scala
type Cents = Long
```

e.g. $3.27 is `327L`

---

# Addition?

Just add them as `Long`'s

```
 350         $3.50
 +
 960         $9.60
-------------------
1310        $13.10
```

Handles negatives too

---

# Analogous to Instant

Instant = nanoseconds since the epoch

Cents = cents since $0

---

# Danger

Not intuitive

```scala
type Cents = Long

// Potential units bug
val currentBalance = 300L // $3.00

val newBalance = currentBalance + 6L // Thinks they're adding $6
```

---

# Abstraction

Hide away the details

```scala
class Currency(totalCents: Long) {
  val dollars: Long = totalCents / 100L
  val cents: Int = (totalCents % 100).toInt

  def addDollars(dollars: Int): Currency = addCents(dollars * 100)
  def subtractDollars(dollars: Int): Currency = subtractCents(dollars * 100)

  def addCents(cents: Int): Currency = new Currency(totalCents + cents)
  def subtractCents(cents: Int): Currency = new Currency(totalCents - cents)
}

object Currency {
  def from(dollars: Int, cents: Int = 0): Currency = new Currency(dollars * 100L + cents)
}

// TODO think about overflow
```

(Internally uses `Long` but public interface tries to use `Int` where possible to reduce chance of overflow)

---

# Aside: Opaque

Scala 3 introduced a feature "opaque types"

Makes hiding the underlying type much simpler and avoids boxing overhead

---

# Data vs Representation

When modelling, think more abstractly about what the data _is_ (and how it's used)

Don't be influenced by what it looks like: "$1.30"

---

# Example 5

Australian Mobile Phone Number

e.g. 0401 995 453

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Australian Mobile Phone Number

Weak type: String

Conditions:

- length 10?


- contains only digits (no spaces)


- starts with a '0'? Require "+61"?

---

# Did you say:

> Weak type: Int or Long

---

# Ambiguous "number"

In English we often use "number" when we really mean "id":

---

# Ambiguous "number"

In English we often use "number" when we really mean "id":

- telephone number


- TFN (tax file number)


- ABN (Australian business number)


- bank account number


- medicare number

---

# What is a number?

Mathematical number

vs

Identification number

---

# Conversations you'll never have

> What is the square root of your phone number?
>
> Our phone numbers are both even, let's be friends!
>
> Who in this room has the biggest phone number?

---

# Conversations you'll never have

> What is the square root of your phone number?

sqrt concept

> Our phone numbers are both even, let's be friends!

division by 2 concept

> Who in this room has the biggest phone number?

ordering concept

---

# Maths vs Id

You'll never have these conversations because a phone "number" is not a mathematical number

It's a _unique string of characters_ (that happen to be digits)

---

# How is it used?

Thinking about the questions asked of it helps

---

# Common questions:

- "are these the same numbers or different?"


- "find the bson document with this number"


- "what's the id?" (for printing)

Not mathematical, just `==` and `!=` logic

---

# Smell test

If your "number" used letters instead, would it limit you in any way?

e.g. phone number: "ABAD FFC EEA"

---

# Common modelling mistake

Developers often hear "number" and then model these concepts with an `Int` or `Long`

---

# Conflating data with representation

"0401238332"

> Ooh, I see digits, therefore it's a number

---

# Maybe you're still not convinced

---

# Problems

Suppose we model a bank account number with `Int`, what's the issue?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Problems

> Suppose we model a bank account number with `Int`, what's the issue?

- overflow

`Int` is limited to 2.1 billion (10 digits)

`3_000_000_000` overflows

---

# Problems

> Suppose we model a bank account number with `Int`, what's the issue?

- 0 truncation

These bank account numbers will all become the same int:

```
"00559234"  --->  559234

 "0559234"  --->  559234

  "559234"  --->  559234
```

Colliding!

---

# Why problems?

Because the abstract concept you're trying to model isn't a mathematical number

`Int` and `Long` aren't designed to model strings of digits

---

# Example questions

- does my number start with "0401"?


- how many digits in my number?

---

# Example questions

- does my number start with "0401"?


- how many digits in my number?

Is this easier to answer with an `Int` or a `String`?

(For `Int`, you'll end up just turning it into a `String`)

---

# Aside

Common issue with excel

It interprets id fields as numerical and chops off the leading 0's

---

# Recap

Many "numbers" are really just id's

ie. unique string of characters

Digits are commonly used characters because they're familiar

---

# Example 6

TFN

e.g. 983 273 139

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Hmm...

(Hopefully you didn't say "Int")

---

# TFN

Weak type: String

Restrictions:

- 9 or 10 characters


- digits only


- checksum (ABN has this too)

---

# Example 7

Version number

e.g. 1.9.2

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Version number

Weak type: String

Restrictions:

- contains 1 or 2 periods? (`1.9` vs `1.9.0`)


- numbers are capped (e.g. under 256)


- numbers non-negative

---

# Checking requires parsing

> numbers are capped (e.g. under 256)
>
> numbers non-negative

```
"1.355.25"  ->  (1, 355, 25)
```

That information gets thrown away...

---

# Edge cases

- "1"


- "1."


- "1.2"


- "1.2"


- "1.2.0"

---

# What is a version?

Hmmm...

---

# What is a version?

It's a triplet of numbers

---

# Data vs Representation

Don't conflate these:

- what the data is (3 numbers)


- how to present it ("1.9.1")

---

# Modelling

Choose an internal representation that makes data processing easy

---

# Easy processing?

How hard would it be to answer these questions:

```scala
"1.8.2"
```

- what is the major version number?


- is the patch version 0?

---

# Parsing

```scala
"1.8.2"
```

> is the patch version 0?

Requires re-parsing

---

# Alternative

```scala
case class Version(major: VersionPart, minor: VersionPart, patch: VersionPart)

type VersionPart = VersionPart.Type
object VersionPart extends Strong[Int] {
  def validate(value: Int): Boolean = i >= 0 && i < 256 // Some upper limit
}
```

---

# Answering questions

```scala
case class Version(major: VersionPart, minor: VersionPart, patch: VersionPart)
```

> is the patch version 0?

```scala
patch == 0
```

Easy to answer because we have captured the conceptual structure

---

# Fancy show

You can still have fancy logic for stringy representations:

```scala
def show(version: Version): String = version match {
  case Version(major, 0, 0) => s"$major"
  case Version(major, minor, 0) => s"$major.$minor"
  case Version(major, minor, patch) => s"$major.$minor.$patch"
}
```

It's easy to go from structured to string, but not vice-versa

---

# Recap

Some models have an inherent structure

Capture that structure using case classes or tuples,

and use strong values for the atoms of the structure

---

# Example 8

IPv4 address

e.g. "192.168.0.3"

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# IPv4 address

A structure of 4 strong values

```scala
case class IPv4(_1: IPSection, _2: IPSection, _3: IPSection, _4: IPSection)

type IPSection = IPSection.Type
object IPSection extends Strong[Int] {
  def validate(value: Int): Boolean = i >= 0 && i < 256
}
```

Deja Vu...

---

# Better type?

> Int's restricted to 0-255

What's a type we could use that is already limited to 0-255?

```scala
case class IPv4(_1: ?, _2: ?, _3: ?, _4: ?)
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Better type?

> Int's restricted to 0-255
>
> What's a type we could use that is already limited to 0-255?

`Byte`

```scala
case class IPv4(_1: Byte, _2: Byte, _3: Byte, _4: Byte)
```

A perfect strong type

(But less intuitive to developers)

---

# Even better type?

An IPv4 address is four bytes

What's a tighter way to represent that?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Int

> An IPv4 address is four bytes
>
> What's a tighter way to represent that?

`Int`

```scala
type IPv4 = Int
```

---

# Easy to work with?

Harder to answer questions like:

- what is the second number?


- is the first number 196?


- find all ip's in the database starting with 196

---

# Example 9

A year and month concept

e.g. "Feb 2020"

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Year and month

`java.time.YearMonth`

Just checking you've been paying attention

---

# That's it!

---

# MVP?

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

# Thinking session

Hopefully got you engaged and thinking

Modelling needs careful thought

---

# The importance of modelling

Getting your models right makes life much simpler later

---

# Think abstractly

When picking models, think more abstractly about what the data _is_

- does it have structure?


- what values should we exclude?


- what are we doing with this? Will this model be easy for a downstream user to process it?

---

# Common gotchas

- modelling the representation and not the data


- using numerical types for string id's

---

# Next time

Validation!

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
