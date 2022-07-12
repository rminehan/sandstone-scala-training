---
author: Rohan
date: 2022-08-16
title: Modelling Time
---

```
 __  __           _      _ _ _
|  \/  | ___   __| | ___| | (_)_ __   __ _
| |\/| |/ _ \ / _` |/ _ \ | | | '_ \ / _` |
| |  | | (_) | (_| |  __/ | | | | | | (_| |
|_|  |_|\___/ \__,_|\___|_|_|_|_| |_|\__, |
                                     |___/
 _____ _
|_   _(_)_ __ ___   ___
  | | | | '_ ` _ \ / _ \
  | | | | | | | | |  __/
  |_| |_|_| |_| |_|\___|
```

---

# Time

We have to deal with it all the time

Quite easy to introduce subtle bugs

Confusing number of options

---

# Goals for today

- explain common mistakes


- introduce best practices

---

# Common issues I've seen

- weak/incorrect representations of time


- using legacy time libraries


- confusing local and absolute time


- dangerous serialisation practices


- inconsistent representations of time in api's


- precision bugs

---

# Best practices doc and DoD

To the browser!

---

# Agenda

- a brief history of java.time


- modelling time with java.time (majority of talk)


- mongodb


- serialisation/deserialisation


- miscellaneous bugs

Along the way we'll encounter common errors

---

# Big talk

Split over 2 sessions

---

# Let's go!

---

```
 ____  _
/ ___|| |_ ___  _ __ _   _
\___ \| __/ _ \| '__| | | |
 ___) | || (_) | |  | |_| |
|____/ \__\___/|_|   \__, |
                     |___/
 _   _
| |_(_)_ __ ___   ___
| __| | '_ ` _ \ / _ \
| |_| | | | | | |  __/
 \__|_|_| |_| |_|\___|
```

A brief history of java.time

---

# Three generations of libraries

- java.util.Date


- joda time


- java.time

---

# java.util.Date

There at the birth of java

Not a great library

Can't remove it due to backwards compatibility

---

# joda time

Third party library

Fixes the deficiencies of java.util.Date

---

# java.time

Joda time integrated into the standard library

(some small changes)

---

# The rule

Use java.time where possible

---

# Scenarios

- brand new service/class: use java.time

---

# Scenarios

- brand new service/class: use java.time


- older logic: migrate to java.time if practical

---

# Scenarios

- brand new service/class: use java.time


- older logic: migrate to java.time if practical


- blindly copy-pasting from SO: make sure it's a java.time solution

---

```
 __  __           _      _ _ _
|  \/  | ___   __| | ___| | (_)_ __   __ _
| |\/| |/ _ \ / _` |/ _ \ | | | '_ \ / _` |
| |  | | (_) | (_| |  __/ | | | | | | (_| |
|_|  |_|\___/ \__,_|\___|_|_|_|_| |_|\__, |
                                     |___/
 _____ _
|_   _(_)_ __ ___   ___
  | | | | '_ ` _ \ / _ \
  | | | | | | | | |  __/
  |_| |_|_| |_| |_|\___|
```

Useful types from java.time

---

# Modelling Time

We'll take an inventory of the common types from java.time

Understand how they work and what they're intended for

---

# TLDR

For timestamps, use `Instant`

---

# Instant

java.time.Instant

---

# java.time.Instant

Peeking inside:

```java
/**
 * An instantaneous point on the time-line.
 * ...
 *
 * @since 1.8
 */
public final class Instant ... {

    ...

    /**
     * The number of seconds from the epoch of 1970-01-01T00:00:00Z.
     */
    private final long seconds;
    /**
     * The number of nanoseconds, later along the time-line, from the seconds field.
     * This is always positive, and never exceeds 999,999,999.
     */
    private final int nanos;
}
```

---

# Instant under the hood

Two fields:

- seconds since 1970 UTC (Long)


- nanos into that second (Int)

---

# Notes

- times before 1970 would use negative seconds


- nano-second precision (10^-9) - very precise!

---

# Working with instants

To the repl!

---

# Summary

```scala
import java.time.Instant

val instant = Instant.now
// 2022-07-22T06:49:35.369Z

instant.getEpochSecond
// 1658472575L

instant.getNano // millisecond precision
// Int: 369000000

val secondStart = Instant.ofEpochSecond(instant.getEpochSecond)
// 2022-07-22T06:49:35Z

val oneNanoLater = secondStart.plusNanos(1)
// 2022-07-22T06:49:35.000000001Z

val earlyInstant = Instant.ofEpochSecond(100, 3)
// 1970-01-01T00:01:40.000000003Z

earlyInstant.toEpochMilli
// Long: 100000L

Instant.MAX
// +1000000000-12-31T23:59:59.999999999Z

Instant.MIN
// -1000000000-01-01T00:00:00Z
```

---

# Use cases

Absolute timestamps!

e.g.

- created at


- updated at

---

# Recap

- rich api


- absolute (UTC concept built in)


- very precise


- cheap


- simple immutable

---

# Recap

Usually the best way to represent a timestamp

---

# "Machine" representation

Designed for efficient machine processing, e.g.

- adding seconds


- comparison

---

# "Machine" representation

> Designed for efficient machine processing

Not so useful for humans:

> Pranali: Yooo, what time is scala training?
>
> James: 1660631400 seconds after the epoch, lol
>
> Pranali: Not helpful James

---

# How humans think of time

"Next Wednesday"

"2021-03-10"

"5 minutes ago"

(This is what you'll see in UI's)

---

# "Structured" representations

More complex human friendly representations

---

# Example: Structured Dates

We think of a date like:

```scala
case class LocalDate(year: Int, month: Int, day: Int)
```

More intuitive, but more complex

---

# Concepts we need to be clear on

- absolute vs contextual (local)


- precision (day vs time)

---

# LocalDate

```java
public final class LocalDate ... {
    ...

    private final int year;

    private final short month;

    private final short day;

    ...
}
```

"Local" - implicit context

Day precision

---

# LocalTime

```java
public final class LocalTime ... {
    ...

    private final byte hour;

    private final byte minute;

    private final byte second;

    private final int nano;

    ...
}
```

"Local" - implicit day context

Nanosecond precision

Good for daily schedules, e.g. "run everyday at 12:30:00"

---

# LocalDateTime

LocalDateTime = LocalDate + LocalTime

```java
public final class LocalDateTime ... {

    ...

    private final LocalDate date;

    private final LocalTime time;

    ...
}
```

Represents a point in time on a particular day

But context is still needed

---

# Context dependent

```scala
val meteoriteLanding =
  LocalDateTime.of(
    LocalDate.of(2021, 4, 21),
    LocalTime.of(13, 2, 15)
  )
```

Implicit knowledge required to get an `Instant`

---

# Zone concepts

> Implicit knowledge required to get an `Instant`

- ZoneId


- ZoneOffset

---

# Timezone (e.g. zone id)

Examples:

- Australia/Sydney


- Asia/Singapore

To the repl!

---

# Summary

```scala
import java.time.ZoneId

ZoneId.of("Australia/Sydney")

ZoneId.of("Asia/Singapore")
```

See more [here](https://en.wikipedia.org/wiki/List_of_tz_database_time_zones)

---

# ZoneOffset

A representation of the time difference between somewhere and Greenwich/UTC.

To the repl!

---

# Summary

```scala
import java.time.ZoneOffset

ZoneOffset.of("+02:00")
// +02:00

ZoneOffset.UTC
// Z

ZoneOffset.UTC.getTotalSeconds
// 0

val singaporeOffset = ZoneOffset.of("+08:00")
// +08:00

singaporeOffset.getTotalSeconds / 3600
// 8

singaporeOffset.getId
// "+08:00"
```

---

# Recapping so far

- many `Local-` style classes (`LocalDate`, `LocalTime`, `LocalDateTime`)


- can't convert these to an `Instant` directly


- `ZoneId` and `ZoneOffset` will help pin this down

---

# ZonedDateTime

```java
public final class ZonedDateTime ... {

    ...

    private final LocalDateTime dateTime;

    private final ZoneOffset offset;

    private final ZoneId zone;

    ...

}
```

Basically `LocalDateTime` plus the extra context needed to zone it

---

# OffsetDateTime

```java
public final class OffsetDateTime ... {

    ...

    private final LocalDateTime dateTime;

    private final ZoneOffset offset;

    ...
}
```

(Just `ZonedDateTime` without a `ZoneId`)

```scala
import java.time.OffsetDateTime

OffsetDateTime.now
// 2021-06-24T15:08:40.659+10:00
//                              ^^^^ no zone id
```

---

# Recapping ZonedDateTime

`ZonedDateTime` is essentially a `LocalDateTime` with extra timezone information

(offset + zone)

---

# Maybe you were wondering:

> When should I use OffsetDateTime or ZonedDateTime?

---

# Example

We want a timestamp to represent 3pm, Feb 1st 2022 Sydney time

```scala
// 3pm, Feb 1st 2022
// No zone info though
val localTime = LocalDateTime.parse("2022-02-01T15:00")
```

---

# Add a zone info

```scala
// 3pm, Feb 1st 2022
// No zone info though
val localTime = LocalDateTime.parse("2022-02-01T15:00")

val offsetDateTime = OffsetDateTime.of(localTime, ZoneOffset.of("+10:00"))

val zonedDateTime = ZonedDateTime.of(localTime, ZoneId.of("Australia/Sydney"))
```

Which approach do you prefer?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Compare them:

```scala
// 3pm, Feb 1st 2022
// No zone info though
val localTime = LocalDateTime.parse("2022-02-01T15:00")

val offsetDateTime = OffsetDateTime.of(localTime, ZoneOffset.of("+10:00"))

val zonedDateTime = ZonedDateTime.of(localTime, ZoneId.of("Australia/Sydney"))

offsetDateTime.toInstant == zonedDateTime.toInstant
//                       ^^
```

Will it give true or false?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Different!

```scala
// 3pm, Feb 1st 2022
// No zone info though
val localTime = LocalDateTime.parse("2022-02-01T15:00")

val offsetDateTime = OffsetDateTime.of(localTime, ZoneOffset.of("+10:00"))

val zonedDateTime = ZonedDateTime.of(localTime, ZoneId.of("Australia/Sydney"))

offsetDateTime.toInstant == zonedDateTime.toInstant
// false
```

---

# How different?

```scala
// 3pm, Feb 1st 2022
// No zone info though
val localTime = LocalDateTime.parse("2022-02-01T15:00")

val offsetDateTime = OffsetDateTime.of(localTime, ZoneOffset.of("+10:00"))

val zonedDateTime = ZonedDateTime.of(localTime, ZoneId.of("Australia/Sydney"))

Duration.between(offsetDateTime, zonedDateTime).getSeconds
```

Any guesses?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# One hour!

```scala
// 3pm, Feb 1st 2022
// No zone info though
val localTime = LocalDateTime.parse("2022-02-01T15:00")

val offsetDateTime = OffsetDateTime.of(localTime, ZoneOffset.of("+10:00"))

val zonedDateTime = ZonedDateTime.of(localTime, ZoneId.of("Australia/Sydney"))

Duration.between(offsetDateTime, zonedDateTime).getSeconds
// -3600L seconds
// ie. 1 hour
```

Why are they different by an hour!

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Daylight Savings!

```scala
// 3pm, Feb 1st 2022
// No zone info though
val localTime = LocalDateTime.parse("2022-02-01T15:00")

val offsetDateTime = OffsetDateTime.of(localTime, ZoneOffset.of("+10:00"))

val zonedDateTime = ZonedDateTime.of(localTime, ZoneId.of("Australia/Sydney"))

Duration.between(offsetDateTime, zonedDateTime).getSeconds
// -3600L seconds
// ie. 1 hour
```

Feb 1st is during daylight savings which is UTC+11

---

# Back to our question

> When should I use OffsetDateTime or ZonedDateTime?

Use `Instant` in your model

---

# Calculations

For some calculations though it's easier to temporarily work with `OffsetDateTime` or `ZonedDateTime`

- is this timestamp on a Tuesday?


- at what instant does February 2022 start?


- I want to present this timestamp to a person in their timezone?

---

# Back to our question

> When should I use OffsetDateTime or ZonedDateTime?

Will depend on context, but mostly `ZonedDateTime` is better:

- more specific timezone information


- less chance of being stung by daylight savings (particularly in Sydney!)

---


# Recap

```
      Machine           |       Structured
   Representations      |    Representations
                        |
  ----------------------|--------------------------
                        |      Local    |  Absolute
       Instant          | ----------------------
                        |               |
                        | LocalDate     |
                        |               |  ZonedDateTime
                        | LocalTime     |
                        |               |  OffsetDateTime
                        | LocalDateTime |
```

---

# More obscure time concepts

---

# Credit card expiry

We need to model a credit card:

```scala
case class CreditCard(
  name: String,
  number: CreditCardNumber,
  expiry: ??? // e.g. 2023/01
)
```

---

# Credit card expiry

It's a year and a month

(no day resolution)

---

# Model?

How would you model a year and a month?

```scala
case class CreditCard(
  name: String,
  number: CreditCardNumber,
  expiry: ??? // e.g. 2023/01
)
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Model?

> How would you model a year and a month?

`java.time.YearMonth`

To the repl!

---

# Summary

`YearMonth` is a strong type

Factory methods like `YearMonth.of` won't allow invalid values

No need to build own one

---

# Aside: Don't use LocalDate!

---

# Next example

---

# Billing Cycle

Our customer configures an annual billing date (e.g. March 15th)

How do we model that?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# MonthDay!

To the repl!

---

# Summary

`MonthDay` is a strong type

Factory methods like `MonthDay.of` won't allow invalid values

Note that it allows Feb 29th

---

# Recap

java.time has lots of useful strong types for modelling time concepts

---

```
                                       _ _
 _ __ ___   ___  _ __   __ _  ___   __| | |__
| '_ ` _ \ / _ \| '_ \ / _` |/ _ \ / _` | '_ \
| | | | | | (_) | | | | (_| | (_) | (_| | |_) |
|_| |_| |_|\___/|_| |_|\__, |\___/ \__,_|_.__/
                       |___/
```

---

# mongodb

How to represent timestamps in mongo?

---

# bson

> How to represent timestamps in mongo?

Mongo stores data in the bson format, so we're really asking:

> How to represent timestamps in bson?

---

# bson

Quick overview

---

# bson

"binary javascript object notation"

ie. "binary json"

---

# json recap

```json
{
  "array": [1, 2, 3],
  "boolean": true,
  "null": null,
  "nested": {
    "more_nested": {
      "even_more_nested": {
        ...
      }
    }
  }
}
```

Finite type system:

- object (recursive)
- array (recursive)
- number
- string
- boolean
- null

Can't create new types

---

# json

Transmitted as text

```scala
val json = """{ "key": 63 }"""
```

That's "63" string, not a 4 byte signed integer

---

# bson

Like a superset of json

Similar recursive types

Finite type system

Sent over the network and stored in binary format

---

# Type system comparison

## json

- object (recursive)
- array (recursive)
- number
- string
- boolean
- null

## bson

[See docs](https://www.mongodb.com/docs/manual/reference/bson-types/)

Has all the json types, plus:

- "number" is split into
    - 64-bit int (ie. long)
    - double
    - 128 bit decimal
- regex type
- binary data
- Date

(and many more)

---

# If you got lost...

Just think of bson as json with extra types (e.g. timestamp types)

---

# bson Date

64-bit signed int representing milliseconds since the epoch UTC

aka "UTC datetime"

---

# bson Date

> 64-bit signed int representing milliseconds since the epoch UTC

Machine representation

Analogous to `java.time.Instant`

---

# No timezone

> 64-bit signed int representing milliseconds since the epoch UTC

It's absolute because it's counted from the epoch (UTC)

Mongo doesn't store any timezone information

---

# Timezone confusion

> Mongo doesn't store any timezone information

You say:

> But in robo3T/studio3T it shows the dates with timezones! You lie!

e.g. "2022-08-16T09:03:12 [Sydney/Australia]"

---

# Timezone confusion

> Mongo doesn't store any timezone information

You say:

> But in robo3T/studio3T it shows the dates with timezones! You lie!

The client is just presenting the data to you in a human friendly way based on your computer's timezone

That data itself is a 64 bit signed int

---

# Quiz time

---

# PersonService

```scala
class PersonService {
  def save(person: Person): Unit = ...

  def load(id: Id): Person = ...
}
```

Want to test that saving and loading gives us back the same person

---

# Find the issue

This test makes sure we save and load a model correctly:

```scala
"PersonService" should {
  "load an identical person to the one saved" in {
    val id = ...

    val savedPerson = Person(
      id = id,
      name = "Boban",
      age = 26,
      createdAt = Instant.now
    )

    personService.save(savedPerson)

    val loadedPerson = personService.load(id)

    savedPerson mustEqual loadedPerson
  }
}
```

---

# Find the issue

This test makes sure we save and load a model correctly:

```scala
"PersonService" should {
  "load an identical person to the one saved" in {
    val id = ...

    val savedPerson = Person(
      id = id,
      name = "Boban",
      age = 26,
      createdAt = Instant.now
    )

    personService.save(savedPerson)

    val loadedPerson = personService.load(id)

    savedPerson mustEqual loadedPerson
  }
}
```

The test will probably pass, but it's a bit weak/ambiguous around timestamps

---

# Pass or fail?

```scala
"PersonService" should {
  "load an identical person to the one saved" in {
    val id = ...

    val savedPerson = Person(
      id = id,
      name = "Boban",
      age = 26,
      createdAt = Instant.ofEpochSecond(0L, 1) // 1 nanosecond after the epoch
    )

    personService.save(savedPerson)

    val loadedPerson = personService.load(id)

    savedPerson mustEqual loadedPerson
  }
}
```

Will this test pass or fail?

---

# Fail!

```scala
"PersonService" should {
  "load and identical person to the one saved" in {
    val id = ...

    val savedPerson = Person(
      id = id,
      name = "Boban",
      age = 26,
      createdAt = Instant.ofEpochSecond(0L, 1) // 1 nanosecond after the epoch
    )

    personService.save(savedPerson)

    val loadedPerson = personService.load(id)

    savedPerson mustEqual loadedPerson
  }
}
```

> Will this test pass or fail?

Fail

```scala
savedPerson.createdAt  // 1 nanosecond after the epoch
loadedPerson.createdAt // 0 nanoseconds after the epoch
```

---

# Precision

## java.time.Instant

Nanoseconds

10^-9

## bson Date

Milliseconds

10^-3

---

# Aside: sql

Microseconds

10^-6

---

# First test passes

```scala
"PersonService" should {
  "load and identical person to the one saved" in {
    val id = ...

    val savedPerson = Person(
      id = id,
      name = "Boban",
      age = 26,
      createdAt = Instant.now
    )

    personService.save(savedPerson)

    val loadedPerson = personService.load(id)

    savedPerson mustEqual loadedPerson
  }
}
```

`Instant.now` happens to generate millisecond precision

If it didn't, the test would be non-deterministic

---

# Aside

When writing tests involving time,

- don't just copy-paste the same times over and over, mix them up a bit, more likely to expose some bug


- don't just stick to "safe" timestamps - try to break it, don't be a scaredy cat - take pride in breaking something

---

# Funny sandstone code

Some of our code uses strings or numbers to represent timestamps in mongo

---

# Funny sandstone code

> Some of our code uses strings or numbers to represent timestamps in mongo

Much better to use Date

- easier queries


- integrates better with indexes


- more efficient storage and processing


- more straightforward serialisation/deserialisation

---

# Unusual cases

e.g. "LocalDate" and analytics

Probably a bson date doesn't make sense here

---

# Recap

```
      Machine           |       Structured
   Representations      |    Representations
                        |
  ----------------------|--------------------------
                        |      Local    |  Absolute
       Instant          | ----------------------
        10^-9           |               |
                        | LocalDate     |
                        |               |  ZonedDateTime
      bson Date         | LocalTime     |
        10^-3           |               |  OffsetDateTime
                        | LocalDateTime |
                        |               |
                        |   YearMonth   |
                        |               |
                        |   MonthDay    |
```

---

```
     _
    | |___  ___  _ __
 _  | / __|/ _ \| '_ \
| |_| \__ \ (_) | | | |
 \___/|___/\___/|_| |_|

```

---

# Json

How to represent timestamps in json?

e.g. BE sending data to the FE

---

# Fundamental Problem

Json doesn't have a native timestamp type

---

# Fundamental Problem

> Json doesn't have a native timestamp type

We have to use other types, e.g.

- string: "2022-07-22T06:49:35Z"


- string: "10/11/2021 14:33:09.119 +10:00"


- number: 1203230423423 (milliseconds since the epoch)

---

# Many ways!

Law of developer entropy

> Give developers freedom and they'll somehow always end up choosing to be different

---

# Consistency

Whatever format you pick, please be consistent across your api's

---

# ISO-8601 format

Industry standard for representing time as text

e.g. "2021-06-24T04:13:01.993Z"

See [wikipedia](https://en.wikipedia.org/wiki/ISO_8601)

---

# java.time Support

Contains built in formatters for formatting to ISO-8601

Demo time!

---

# My recommendation

Use the textual representation spelled out by the ISO-8601 standard:

```scala
import java.time.format.DateTimeFormatter

val zonedDateTime = ZonedDateTime.now

zonedDateTime.format(DateTimeFormatter.ISO_INSTANT)
// "2022-08-03T05:04:24.818Z"
```

---

# Why use it?

Industry standard

Unambiguous (e.g. month vs day of month)

Encourages explicit encoding of timezone

Most languages will have support for ISO-8601

---

# Aside

When formatting timestamps to string, ask yourself:

> What is consuming this? A machine or a human?

---

# Example

```scala
def getUsersCreatedAfter(zonedDateTime: ZonedDateTime): Seq[User] = {
  val sqlQuery = s"db.users.find({'createdAt': { $$gt: '$zonedDateTime' }})"
  ...
}
```

Hmmm...

---

# Example

```scala
def getUsersCreatedAfter(zonedDateTime: ZonedDateTime): Seq[User] = {
  val sqlQuery = s"db.users.find({'createdAt': { $$gt: '$zonedDateTime' }})"
  ... //                                                 ^^^^^^^^^^^^^ zonedDateTime.toString
}
```

`toString` is for humans

It might not do what you expect

---

# Float example

> It might not do what you expect

To the repl!

---

# Float.toString

Repl is using .toString to print the data we enter:

```scala
@ 143F
// 143.0F

@ 143000000F
// 1.43E8F
```

Unexpectantly flips to exponential notation

Handy for humans (we have intuition and adapt quickly)

---

# General rule

`.toString` is usually designed for a human

Don't use it for serialisation

Explicitly define a format

---

# Timestamp serialisation

Define an explicit format, e.g. `DateTimeFormatter.ISO_INSTANT`

Use for both directions

```
             formatter

               GET
    --------  .format
   |        | ------> "2022-07-22T06:49:35.369Z"
   |        |
   |        |  POST
   |        | .parse
   |        | <------ "2022-07-22T06:50:53.349"
    --------
```

Don't use `.toString`!

---

```
 __  __ _
|  \/  (_)___  ___
| |\/| | / __|/ __|
| |  | | \__ \ (__
|_|  |_|_|___/\___|

 ____
| __ ) _   _  __ _ ___
|  _ \| | | |/ _` / __|
| |_) | |_| | (_| \__ \
|____/ \__,_|\__, |___/
             |___/
```

Left over bits and pieces

---

# Comparing ZonedDateTime's

```scala
if (zonedDateTime1 == zonedDateTime2)
  println(s"These timestamps are the same point in time!")
```

What's the bug?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Comparing ZonedDateTime's

```scala
if (zonedDateTime1 == zonedDateTime2)
  println(s"These timestamps are the same point in time!")
```

> What's the bug?

```scala
val instant = Instant.now

val zonedDateTime1 = instant.atZone(ZoneOffset.UTC)
// 2022-08-03T05:48:34.121Z

val zonedDateTime2 = instant.atZone(ZoneId.of("Australia/Sydney"))
// 2022-08-03T15:48:34.121+10:00[Australia/Sydney]

zonedDateTime1 == zonedDateTime2
// false
```

Equality requires zone info to match too...

---

# Aside

Using `Instant` instead of `ZonedDateTime` will help avoid this bug

Two `Instant`'s that represent the same point in time must be equal

---

# now

```scala
val newUser = User(
  name = "Boban Jones",
  age = 26,
  createdAt = Instant.now,
  updatedAt = Instant.now,
)
```

Bug?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# now

```scala
val newUser = User(
  name = "Boban Jones",
  age = 26,
  createdAt = Instant.now, // potentially different
  updatedAt = Instant.now, //
)
```

Timestamps may not match

Could cause issues

Depending on order of execution, `updatedAt` might be earlier than `createdAt`

---

# Fix

```scala
val now = Instant.now

val newUser = User(
  name = "Boban Jones",
  age = 26,
  createdAt = now
  updatedAt = now
)
```

The previous code is the inlined version of this

Root cause of the issue is that `Instant.now` is not pure

---

# Get current date

```scala
def getCurrentDate(zonedDateTime: ZonedDateTime): LocalDate = zonedDateTime.toLocalDate
```

Issue?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Get current date

```scala
def getCurrentDate(zonedDateTime: ZonedDateTime): LocalDate = zonedDateTime.toLocalDate
```

```scala
val au = ZonedDateTime.parse("2020-08-01T05:00:00+10:00[Australia/Sydney]")
// 5am

val uk = au.withZoneSameInstant(ZoneOffset.UTC)
// 7pm previous day

au.toLocalDate
// 2020-08-01

uk.toLocalDate
// 2020-07-31
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

# Time is tricky

More tricky than you might first think

---

# Strong Types

java.time contains lots of strong types for modelling common time concepts

- `Instant`
- `LocalDate`
- `LocalTime`
- `LocalDateTime`
- `OffsetDateTime`
- `ZonedDateTime`
- `YearMonth`
- `MonthDay`

---

# Most common type

For modelling timestamps, use: `Instant`

---

# Gotchas

- implicit timezones


- daylight savings


- precision mismatches


- passing human string representations where a machine representation is expected

---

# Deprecated libraries

- java.util.Date


- joda time

Use java.time where possible

---

# Internal docs - DoD

Hopefully makes sense now!

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
