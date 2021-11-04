---
author: Rohan
date: 2021-11-05
title: Pattern Matching
---

SCALA-03

```
 ____       _   _
|  _ \ __ _| |_| |_ ___ _ __ _ __
| |_) / _` | __| __/ _ \ '__| '_ \
|  __/ (_| | |_| ||  __/ |  | | | |
|_|   \__,_|\__|\__\___|_|  |_| |_|

 __  __       _       _     _
|  \/  | __ _| |_ ___| |__ (_)_ __   __ _
| |\/| |/ _` | __/ __| '_ \| | '_ \ / _` |
| |  | | (_| | || (__| | | | | | | | (_| |
|_|  |_|\__,_|\__\___|_| |_|_|_| |_|\__, |
                                    |___/
```

(and destructuring)

A lovely language feature of scala

---

# Today?

- basic syntax


- advanced features

---

# Two parter

Too much for 1 session

---

# Why should you care?

Can make your code much more readable

Helps avoid common code smells

---

```
__        ___           _     _       _ _  ___
\ \      / / |__   __ _| |_  (_)___  (_) ||__ \
 \ \ /\ / /| '_ \ / _` | __| | / __| | | __|/ /
  \ V  V / | | | | (_| | |_  | \__ \ | | |_|_|
   \_/\_/  |_| |_|\__,_|\__| |_|___/ |_|\__(_)

```

What is pattern matching?

---

# Pattern matching

> What is pattern matching?

This kind of thing:

```scala
x match {
  case 1 => ...
  case "foo" => ...
  case List(1, 2, 3) => ...
  case Array(x, _, z) if x > z => ...
}
```

An `if` on steroids

---

# What is destructuring?

---

# Destructuring

> What is destructuring?

Pulling data apart and assigning names to the parts inside

```scala
val xyCoordinate = (10, 30)

...

val (x, y) = xyCoordinate
```

---

```
 __  __       _   _            _   _               _ _
|  \/  | ___ | |_(_)_   ____ _| |_(_)_ __   __ _  (_) |_
| |\/| |/ _ \| __| \ \ / / _` | __| | '_ \ / _` | | | __|
| |  | | (_) | |_| |\ V / (_| | |_| | | | | (_| | | | |_
|_|  |_|\___/ \__|_| \_/ \__,_|\__|_|_| |_|\__, | |_|\__|
                                           |___/
```

Compare with approaches from java

---

# Motivating pattern matching

Consider this java code:

```java
String team = null;

switch (name) {

    case "feroz":
        team = "DiVA";
        break;

    case "boban":
        team = "Bobanware";
        break;

    ...

    default:
        team = "Unknown";

}
```

Purpose is to set `team`

---

# Or using if-else chain

```java
String team = null;

if (name == "feroz")
    team = "DiVA";
else if (name == "boban")
    team = "Bobanware";
...
else
    team = "Unknown";
```

---

# In essence

- some data being tested


- sequential series of tests


- compute something when a match is hit


- bail out

```java
String team = null;

if (name == "feroz")
    team = "DiVA";
else if (name == "boban")
    team = "Bobanware";
...
else
    team = "Unknown";
```

---

# In scala

Pattern matching is the tool you'd use for this pattern

---

# Scala

```scala
val team = name match {
  case "feroz" => "DiVA"
  case "boban" => "Bobanware"
  ...
  case _ => "Unknown"
}
```

---

# Observations

---

# Expression vs Statement

## Scala

Expression

```scala
val team = name match {
  case "feroz" => "DiVA"
  case "boban" => "Bobanware"
  ...
  case _ => "Unknown"
}
```

## Java

Statement

```java
String team = null;

if (name == "feroz")
    team = "DiVA";
else if (name == "boban")
    team = "Bobanware";
...
else
    team = "Unknown";
```

---

# Default case

## Scala

Uses `_`

```scala
val team = name match {
  case "feroz" => "DiVA"
  case "boban" => "Bobanware"
  ...
  case _ => "Unknown"
}
```

Note this isn't "special" syntax

(More on this later)

## Java

Uses `default`

```java
String team = null;

switch (name) {

    case "feroz":
        team = "DiVA";
        break;

    case "boban":
        team = "Bobanware";
        break;

    ...

    default:
        team = "Unknown";

}
```

---

# Advanced features of pattern matching

- destructuring (binding names to your data)


- guards (`if` in your pattern)

Examples coming later in talk!

Most languages are missing these features

---

# Summary so far

Pattern matching is good where you'd typically use an if-else chain

---

```
 ____        __             _ _
|  _ \  ___ / _| __ _ _   _| | |_
| | | |/ _ \ |_ / _` | | | | | __|
| |_| |  __/  _| (_| | |_| | | |_
|____/ \___|_|  \__,_|\__,_|_|\__|

  ____
 / ___|__ _ ___  ___
| |   / _` / __|/ _ \
| |__| (_| \__ \  __/
 \____\__,_|___/\___|

```

---

# Consider this method

```scala
def describeNumber(int: Int): String = int match {
  case 0 => "zero"
  case 3 => "strong"
  case 4 => "unlucky"
  case 12 => "a nice round number"
}
```

What problems can you see?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Incomplete pattern

```scala
def describeNumber(int: Int): String = int match {
  case 0 => "zero"
  case 3 => "strong"
  case 4 => "unlucky"
  case 12 => "a nice round number"
}
```

> What problems can you see?

Not defined for some inputs

What will happen if we call say `describeNumber(2)`?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Incomplete pattern

> What will happen if we call say `describeNumber(2)`?

Let's test it out

To ammonite!

---

# Result?

Generates

```
scala.MatchError: 2 (of class java.lang.Integer)
```

ie. throws an exception called a `MatchError`

---

# How to fix this

Depends on the context...

Could add a default case, but maybe that doesn't make sense in context

---

# Suppose a default case makes sense

```scala
def describeNumber(int: Int): String = int match {
  case 0 => "zero"
  case 3 => "strong"
  case 4 => "unlucky"
  case 12 => "a nice round number"
  case _ => "unknown"
}
```

---

# If a default value doesn't make sense

## Use `Option`

```scala
def describeNumber(int: Int): Option[String] = int match {
  case 0 => Some("zero")
  case 3 => Some("strong")
  case 4 => Some("unlucky")
  case 12 => Some("a nice round number")
  case _ => None
}
```

## Throw an exception

```scala
def describeNumber(int: Int): String = int match {
  case 0 => "zero"
  case 3 => "strong"
  case 4 => "unlucky"
  case 12 => "a nice round number"
  case _ => throw new IllegalArgumentException(s"Unable to describe number: $int")
}
```

---

```
  ____                     _
 / ___|_   _  __ _ _ __ __| |___
| |  _| | | |/ _` | '__/ _` / __|
| |_| | |_| | (_| | | | (_| \__ \
 \____|\__,_|\__,_|_|  \__,_|___/

```

Conditional logic in your match

---

# Example

We have an x,y space of numbers which we want to categorise:

```
               / \ y
                |
  "top-left"    |   "top-right"
                |
                |
<---------------|--------------> x    "axis"
                |
                |
  "bottom-left" |  "bottom-right"
                |
               \ /
```

Numbers are pairs of integers in (x, y) form

---

# Scala code

```
               / \ y
                |
  "top-left"    |   "top-right"
                |
                |
<---------------|--------------> x    "axis"
                |
                |
  "bottom-left" |  "bottom-right"
                |
               \ /
```

```scala
def categorize(coord: (Int, Int)): String = coord match {
  case (x, y) if x > 0 && y > 0 => "top-right"
  case (x, y) if x > 0 && y < 0 => "bottom-right"
  case (x, y) if x < 0 && y > 0 => "top-left"
  case (x, y) if x < 0 && y < 0 => "bottom-left"
  case _ => "axis"
}
```

---

# Modify to print

```
               / \ y
                |
  "top-left"    |   "top-right"
                |
                |
<---------------|--------------> x    "axis"
                |
                |
  "bottom-left" |  "bottom-right"
                |
               \ /
```

```scala
def categorize(coord: (Int, Int)): String = coord match {
  case (x, y) if x > 0 && y > 0 => "top-right"
  case (x, y) if x > 0 && y < 0 => "bottom-right"
  case (x, y) if x < 0 && y > 0 => "top-left"
  case (x, y) if x < 0 && y < 0 => "bottom-left"
  case _ => "axis"
}
```

_But_ we want to print the x and y values when it's "axis"

Thoughts?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Thoughts

> _But_ we want to print the x and y values when it's "axis"

To print them, we can bind them to names

---

# Bindings needed

```scala
def categorize(coord: (Int, Int)): String = coord match {
  case (x, y) if x > 0 && y > 0 => "top-right"
  case (x, y) if x > 0 && y < 0 => "bottom-right"
  case (x, y) if x < 0 && y > 0 => "top-left"
  case (x, y) if x < 0 && y < 0 => "bottom-left"
  // case _ => "axis"
  case (x, y) =>
    println(s"x=$x, y=$y")
    "axis"
}
```

---

# Wait a minute!

Is "axis" still the default case?

```scala
def categorize(coord: (Int, Int)): String = coord match {
  case (x, y) if x > 0 && y > 0 => "top-right"
  case (x, y) if x > 0 && y < 0 => "bottom-right"
  case (x, y) if x < 0 && y > 0 => "top-left"
  case (x, y) if x < 0 && y < 0 => "bottom-left"
  // case _ => "axis"
  case (x, y) =>
    println(s"x=$x, y=$y")
    "axis"
}
```

Can any values slip past it and cause `MatchError`'s?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Default = Match all

```scala
def categorize(coord: (Int, Int)): String = coord match {
  case (x, y) if x > 0 && y > 0 => "top-right"
  case (x, y) if x > 0 && y < 0 => "bottom-right"
  case (x, y) if x < 0 && y > 0 => "top-left"
  case (x, y) if x < 0 && y < 0 => "bottom-left"
  // case _ => "axis"
  case (x, y) =>
    println(s"x=$x, y=$y")
    "axis"
}
```

> Can any values slip past it and cause `MatchError`'s?

No

It will match everything

Recall earlier when talking about `_` I said:

> Note this isn't "special" syntax

---

# The catch all pattern

Just needs to be something that will catch everything that slipped through

```scala
def categorize(coord: (Int, Int)): String = coord match {
  case (x, y) if x > 0 && y > 0 => "top-right"
  case (x, y) if x > 0 && y < 0 => "bottom-right"
  case (x, y) if x < 0 && y > 0 => "top-left"
  case (x, y) if x < 0 && y < 0 => "bottom-left"

  // Pattern that will catch everything
  case _ => ...
  case pair => ...
  case (x, y) => ...
}
```

The point is none of them have filters which would exclude data

---

# So what is underscore then?

- matches everything


- doesn't bind a name to the data (throws it away)

e.g.

```scala
def categorize(coord: (Int, Int)): String = coord match {
  case (x, y) if x > 0 && y > 0 => "top-right"
  case (x, y) if x > 0 && y < 0 => "bottom-right"
  case (x, y) if x < 0 && y > 0 => "top-left"
  case (x, y) if x < 0 && y < 0 => "bottom-left"

  case _ => "axis"
  // vs
  case (x, y) =>
    println(s"x=$x, y=$y")
    "axis"
}
```

---

```
  ____                      _
 / ___|___  _ __ ___  _ __ | | _____  __
| |   / _ \| '_ ` _ \| '_ \| |/ _ \ \/ /
| |__| (_) | | | | | | |_) | |  __/>  <
 \____\___/|_| |_| |_| .__/|_|\___/_/\_\
                     |_|
 ____            _                   _              _
|  _ \  ___  ___| |_ _ __ _   _  ___| |_ _   _ _ __(_)_ __   __ _
| | | |/ _ \/ __| __| '__| | | |/ __| __| | | | '__| | '_ \ / _` |
| |_| |  __/\__ \ |_| |  | |_| | (__| |_| |_| | |  | | | | | (_| |
|____/ \___||___/\__|_|   \__,_|\___|\__|\__,_|_|  |_|_| |_|\__, |
                                                            |___/
```

---

# Back to our problem

```
               / \ y
                |
  "top-left"    |   "top-right"
                |
                |
<---------------|--------------> x    "axis"
                |
                |
  "bottom-left" |  "bottom-right"
                |
               \ /
```

But we'll put the "axis" case first

---

# Axis first

```scala
def categorize(coord: (Int, Int)): String = coord match {
  case (_, 0) | (0, _) => "axis"
  case (x, y) if x > 0 && y > 0 => "top-right"
  case (x, y) if x > 0 && y < 0 => "bottom-right"
  case (x, y) if x < 0 && y > 0 => "top-left"
  case (x, y) if x < 0 && y < 0 => "bottom-left"
}
```

---

# Zooming in

```scala
def categorize(coord: (Int, Int)): String = coord match {

  case (_, 0) | (0, _) => "axis"
  //   ^^^^^^

  case (x, y) if x > 0 && y > 0 => "top-right"
  case (x, y) if x > 0 && y < 0 => "bottom-right"
  case (x, y) if x < 0 && y > 0 => "top-left"
  case (x, y) if x < 0 && y < 0 => "bottom-left"
}
```

`(_, 0)` means:

> Anything can be in the x position
>
> 0 must be in the y position

ie. the x-axis

```
               / \ y
                |
                |
                |
                |
<---------------|--------------> x  y=0
 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                |
                |
                |
               \ /
```

---

# Zooming in

```scala
def categorize(coord: (Int, Int)): String = coord match {

  case (_, 0) | (0, _) => "axis"
  //            ^^^^^^

  case (x, y) if x > 0 && y > 0 => "top-right"
  case (x, y) if x > 0 && y < 0 => "bottom-right"
  case (x, y) if x < 0 && y > 0 => "top-left"
  case (x, y) if x < 0 && y < 0 => "bottom-left"
}
```

`(0, _)` means:

> 0 must be in the x position

> Anything can be in the y position
>

ie. the y-axis

```            x=0
               / \ y
                |  <-
                |  <-
                |  <-
                |  <-
<---------------|  <- ---------> x
                |  <-
                |  <-
                |  <-
                |  <-
               \ /
```

---

# Aside: Underscore

Again we see that underscore isn't just scala's version of `default`

It can go anywhere in patterns and means:

- matches everything (at that level)


- doesn't bind the data to a meaningful name

---

```
 ____
|  _ \  ___  ___ _ __
| | | |/ _ \/ _ \ '_ \
| |_| |  __/  __/ |_) |
|____/ \___|\___| .__/
                |_|
 ____            _                   _              _
|  _ \  ___  ___| |_ _ __ _   _  ___| |_ _   _ _ __(_)_ __   __ _
| | | |/ _ \/ __| __| '__| | | |/ __| __| | | | '__| | '_ \ / _` |
| |_| |  __/\__ \ |_| |  | |_| | (__| |_| |_| | |  | | | | | (_| |
|____/ \___||___/\__|_|   \__,_|\___|\__|\__,_|_|  |_|_| |_|\__, |
                                                            |___/
```

We'll make a complex structure and elegantly destructure out the bits we want

---

# Scenario

A person has:

- an id


- name (complex: first, optional middle, last)


- an optional address (complex: number, street, street type, postcode)

---

# Modelling that

```scala
case class Name(
  first: String,
  middleOpt: Option[String],
  last: String
)

case class Address(
  number: Int,
  street: String,
  streetType: String,
  postcode: Int
)

case class Person(
  id: UUID,
  name: Name,
  addressOpt: Option[Address]
)
```

---

# Problem

We have a database of people (`Seq[Person]`).

Extract the id's, first names and postcodes of everyone who has a middle name and address and lives in a "lane"

---

# Thoughts

> Extract the id's, first names and postcodes of everyone who has a middle name and address and lives in a "lane"

Feels like a "filter then map" kind of problem

---

# Breaking it down

> Extract the id's, first names and postcodes of everyone who has a middle name and address and lives in a "lane"

## Filtering

Keep them if they:

- have an address


- have a middle name


- live in a lane

## Mapping

Extract out the id's, first names and postcode into a tuple say

---

# Filter part

> Keep them if they:
>
> - have an address
>
> - have a middle name
>
> - live in a lane

```scala
people.filter { person =>
  person.addressOpt.isDefined &&
  person.name.middleOpt.isDefined &&
  person.addressOpt.get.streetType == "lane"
}
```

---

# A bit smelly:

```scala
people.filter { person =>
  person.addressOpt.isDefined &&
  //                ^^^^^^^^^
  person.name.middleOpt.isDefined &&
  //                    ^^^^^^^^^
  person.addressOpt.get.streetType == "lane"
  //                ^^^
}
```

- `.isDefined` on an `Option` is _often_ (not always) a signal of bad design


- `.get` on an `Option` is almost always a signal of bad design (and is unsafe)

(Will fix this later)

---

# Map part

> Extract out the id's, first names and postcode into a tuple say

```scala
people.filter { person =>
  ...
}.map { person =>
  (person.id, person.name.first, person.addressOpt.get.postcode)
  //                                       smelly  ^^^
}
```

---

# Putting it together

```scala
val tuples: Seq[(UUID, String, Int)] = people.filter { person =>
  person.addressOpt.isDefined &&
  person.name.middleOpt.isDefined &&
  person.addressOpt.get.streetType == "lane"
}.map { person =>
  (person.id, person.name.first, person.addressOpt.get.postcode)
}
```

Works, but not very nice

---

# What's not nice

```scala
val tuples: Seq[(UUID, String, Int)] = people.filter { person =>
  person.addressOpt.isDefined &&
  person.name.middleOpt.isDefined &&
  person.addressOpt.get.streetType == "lane"
}.map { person =>
  (person.id, person.name.first, person.addressOpt.get.postcode)
}
```

- two passes over your database (filter, map)


- implicit assumptions between expressions


- a bit noisy and hard to read

---

# Introducing `collect`

Great for this use case:

- filter followed by map


- mapping logic depends on filter (e.g. postcode can only be accessed if they have an address)


- can better utilise pattern matching which is great for deeply structured data

---

# What it would look like

## Old

```scala
val tuples: Seq[(UUID, String, Int)] = people.filter { person =>
  person.addressOpt.isDefined &&
  person.name.middleOpt.isDefined &&
  person.addressOpt.get.streetType == "lane"
}.map { person =>
  (person.id, person.name.first, person.addressOpt.get.postcode)
}
```

## collect

```scala
val tuples: Seq[(UUID, String, Int)] = people.collect {
  case Person(id, Name(first, Some(_), _), Some(Address(_, _, "lane", postcode))) => (id, first, postcode)
}
```

## For reference

```scala
case class Name(
  first: String,
  middleOpt: Option[String],
  last: String
)

case class Address(
  number: Int,
  street: String,
  streetType: String,
  postcode: Int
)
```

---

# Breaking it down

```scala
val tuples: Seq[(UUID, String, Int)] = people.collect {
  case Person(id, Name(first, Some(_), _), Some(Address(_, _, "lane", postcode))) => (id, first, postcode)
  // Binding  ^^       ^^^^^                                          ^^^^^^^^

  // Filtering                ^^^^^^       ^^^^               ^^^^^^
  //                          must have    must have          street
  //                        middle name     address         type must be
  //                       but don't care                     "lane"
  //                        what it is
}
```

---

# Comparing them

## Old

```scala
val tuples: Seq[(UUID, String, Int)] = people.filter { person =>
  person.addressOpt.isDefined &&
  person.name.middleOpt.isDefined &&
  person.addressOpt.get.streetType == "lane"
}.map { person =>
  (person.id, person.name.first, person.addressOpt.get.postcode)
}
```

- two passes over your data


- implicit relationships between data


## collect

```scala
val tuples: Seq[(UUID, String, Int)] = people.collect {
  case Person(id, Name(first, Some(_), _), Some(Address(_, _, "lane", postcode))) => (id, first, postcode)
}
```

- one pass over the data


- uses pattern matching to simultaneously filter data and destructure it when it's there

(no `get` or `isDefined`)


- so concise! Mwah!

---

# Summary of that section

Pattern matching is great for tunnelling down into complex data structures

---

```
 ____                                                __
/ ___| _   _ _ __ ___  _ __ ___   __ _ _ __ _   _   / _| ___  _ __
\___ \| | | | '_ ` _ \| '_ ` _ \ / _` | '__| | | | | |_ / _ \| '__|
 ___) | |_| | | | | | | | | | | | (_| | |  | |_| | |  _| (_) | |
|____/ \__,_|_| |_| |_|_| |_| |_|\__,_|_|   \__, | |_|  \___/|_|
                                            |___/
                  _
 _ __   __ _ _ __| |_
| '_ \ / _` | '__| __|
| |_) | (_| | |  | |_    1
| .__/ \__,_|_|   \__|
|_|
```

That's it for today

Will continue next time

---

# Pattern matching

Like a switch statement on 'roids

---

# Incomplete patterns

If you miss a case, you'll get a `MatchError`

---

# Guards

Can put an `if` with regular scala code after your pattern:

```scala
case (x, y) if x > 0 && y > 0 => ...
//             --------------
```

---

# Alternatives `|`

```scala
case (0, _) | (_, 0) => ...
```

More on these next time

---

# Destructuring

Very useful for complex structures:

```scala
case Person(id, Name(first, Some(_), _), Some(Address(_, _, "lane", postcode))) => ...
```

Simultaneously filter it and extract data out of it

---

# Coming up in part 2

- more advanced syntax


- pattern matching sequences


- understanding extractors

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

  ____                                     _
 / ___|___  _ __ ___  _ __ ___   ___ _ __ | |_ ___
| |   / _ \| '_ ` _ \| '_ ` _ \ / _ \ '_ \| __/ __|
| |__| (_) | | | | | | | | | | |  __/ | | | |_\__ \
 \____\___/|_| |_| |_|_| |_| |_|\___|_| |_|\__|___/


                      ?
```
