---
author: Pranohan
date: 2022-07-19
title: Unsafe things
---

```
 _   _                  __
| | | |_ __  ___  __ _ / _| ___
| | | | '_ \/ __|/ _` | |_ / _ \
| |_| | | | \__ \ (_| |  _|  __/
 \___/|_| |_|___/\__,_|_|  \___|

 _____ _     _
|_   _| |__ (_)_ __   __ _ ___
  | | | '_ \| | '_ \ / _` / __|
  | | | | | | | | | | (_| \__ \
  |_| |_| |_|_|_| |_|\__, |___/
                     |___/
```

---

# What is today all about?

Common unsafe things in scala code

---

# Unsafe things?

ie. "incomplete" functions

---

# Recap

A function is "complete" if it gracefully returns for all input values

---

# Common example

Functions that throw exceptions for certain inputs are "incomplete"

---

# Caveat

If a function returns a non-sensical value (like `NaN`, `null`, `Infinity`),

we'll also consider it incomplete

---

# Today

About common unsafe/incomplete patterns that sneak into our code

Cause runtime exceptions and other nasty things

---

# Gotchas Format

Show you a function that is incomplete in some way

(e.g. throw an exception for some input)

You yell out the gotcha

---

# MVP

Award for effort

Prize: ride on Lee's motorcycle (pending MIS)

---

# Relationship to purity?

Helps us avoid one member of the Unholy Trinity of Impurity:

Incomplete functions

---

# Let's go!

---

# Gotcha 1

---

# Gotcha 1

```scala
def makeAussieHamburger(bread: Bread, meat: Meat, sauce: Option[Sauce]): Hamburger = {
  Hamburger(
    bread = bread,
    meat = meat,
    includeBeetroot = true,
    sauce = sauce.get
  )
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

# Gotcha 1

```scala
def makeAussieHamburger(bread: Bread, meat: Meat, sauce: Option[Sauce]): Hamburger = {
  Hamburger(
    bread = bread,
    meat = meat,
    includeBeetroot = true,
    sauce = sauce.get // <----- get is incomplete, what if sauce is None
  )
}
```

---

# General rule of thumb

If you see `Option.get`,

go to yellow alert

```
RED ALERT
BROWN ALERT
YELLOW ALERT <---
```

Shields up and phasers armed

---

# General rule of thumb

> If you see `Option.get`,
>
> go to yellow alert

It's often an indication of bad design

(even when it's safe)

---

# Example

```scala
val x = if (opt.isDefined) opt.get * 2 else 3

// improvement:
val x = opt.map(_ * 2).getOrElse(3)

val x = opt match {
  case Some(i) => i * 2
  case None => 3
}
```

Usually pattern matching or the Option api

will achieve your goal without needing a dangerous `.get`

---

# Gotcha 2

---

# Gotcha 2

```scala
def predictPriceOfGold(xFactor: Int, yFactor: Int): Double =
  xFactor * xFactor - math.sqrt(xFactor - yFactor) + 42
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Gotcha 2

```scala
def predictPriceOfGold(xFactor: Int, yFactor: Int): Double =
  xFactor * xFactor - math.sqrt(xFactor - yFactor) + 42
//                    ^^^^^^^^^
//                     dubious
```

If `xFactor` is smaller than `yFactor` the sqrt doesn't make sense

To the repl!

---

# Gotcha 3

---

# Gotcha 3

```scala
def processPeople(people: Seq[Person]): Seq[Person] = {
  log.info("Processing people")

  val results = people
    .filter(_.age > 18)
    .filter(_.connections < 4)
    .sortBy(_.age)

  log.info(s"Youngest person: ${results.head}")
  log.info(s"Oldest person: ${results.last}")

  results
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

# Gotcha 3

```scala
def processPeople(people: Seq[Person]): Seq[Person] = {
  log.info("Processing people")

  val results = people
    .filter(_.age > 18)
    .filter(_.connections < 4)
    .sortBy(_.age)

  log.info(s"Youngest person: ${results.head}")
  //                                    ^^^^ incomplete
  log.info(s"Oldest person: ${results.last}")
  //                                  ^^^^ incomplete

  results
}
```

What if `results` is empty?

---

# Beware head, last and tail

They implicitly assume a non-empty sequence

---

# Pattern matching approach

```scala
seq match {
  case head :: tail => // safe to use them
  case _ => // empty sequence
}
```

Forces us to consider the empty case and gracefully handle it

---

# Gotcha 4

---

# Gotcha 4

```scala
def reportFavoriteCookies(cookieDB: Map[UserId, CookieFlavor]): String = {
  s"""|There are ${cookieDB.size} entries in the db.
      |
      |The most popular cookie flavor is '${mostPopular(cookieDB)}'.
      |
      |VIP list:
      |- Feroz's favorite cookie: ${cookieDB("feroz")}
      |- Pannu's favorite cookie: ${cookieDB("pannu")}
      |- James's favorite cookie: ${cookieDB("james")}
      |- Yuhan's favorite cookie: ${cookieDB("yuhan")}
      |- Rohan's favorite cookie: ${cookieDB("rohan")}
      |""".stripMargin
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

# Gotcha 4

```scala
def reportFavoriteCookies(cookieDB: Map[UserId, CookieFlavor]): String = {
  s"""|There are ${cookieDB.size} entries in the db.
      |
      |The most popular cookie flavor is '${mostPopular(cookieDB)}'.
      |
      |VIP list:
      |- Feroz's favorite cookie: ${cookieDB("feroz")}
      |- Pannu's favorite cookie: ${cookieDB("pannu")}
      |- James's favorite cookie: ${cookieDB("james")}
      |- Yuhan's favorite cookie: ${cookieDB("yuhan")}
      |- Rohan's favorite cookie: ${cookieDB("rohan")}
      |""".stripMargin //           ^^^^^^^^^^^^^^^^^
           //                   What if the map is missing these keys?
}
```

(also `mostPopularCookie` _might_ be assuming a non-empty database)

---

# apply

```scala
cookieDB("feroz")

// is really

cookieDB.apply("feroz")
```

---

# Incomplete apply

```scala
cookieDB.apply("feroz")
```

`apply` throws an exception if the key is missing

---

# get

```scala
cookieDB.get("feroz") // returns Option
```

`get` is safe

Forces you to deal with the case that it's missing

---

# Gotcha 5

---

# Gotcha 5

```scala
def mostPopular(people: Seq[Person]): Person =
  people.maxBy(_.connections)
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Gotcha 5

```scala
def mostPopular(people: Seq[Person]): Person =
  people.maxBy(_.connections)
  //     incomplete
```

Max and Min are incomplete!

They only make sense for non-empty collections

---

# maxByOption, minByOption

Safe alternatives

Return an `Option`:

```scala
Seq("cat", "dog", "chimp", "rabbit", "fish").maxByOption(_.length)
// Some(value = "rabbit")

Seq.empty[String].maxByOption(_.length)
// None
```

The `Option` forces you to consider the empty case

---

# Gotcha 6

---

# Gotcha 6

```scala
def averageAge(people: Seq[Person]): Double =
  people.map(_.age).sum.toDouble / people.size
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Gotcha 6

```scala
def averageAge(people: Seq[Person]): Double =
  people.map(_.age).sum.toDouble / people.size
  //                             ^ incomplete
```

What if these's no people?

people.size would be zero and will throw exception

(Divide by zero) OR (`NaN`)

---

# Gotcha 7

```scala
/*
   "abc |dEf|g"   ---->   "abc"
   "H "                   "def"
   "ij|kl"                "g"
                          "h"
                          "ij"
                          "kl"
*/
def cleanAndSplitTokens(tokens: List[String]): List[String] = {
  tokens match {
    case Nil => Nil
    case token :: tail =>
      val subTokens = token.split('|').map(_.trim.toLowerCase)
      subTokens ++ cleanAndSplitTokens(tail)
  }
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

# Gotcha 7

```scala
/*
   "abc |dEf|g"   ---->   "abc"
   "H "                   "def"
   "ij|kl"                "g"
                          "h"
                          "ij"
                          "kl"
*/
def cleanAndSplitTokens(tokens: List[String]): List[String] = {
  tokens match {
    case Nil => Nil
    case token :: tail =>
      val subTokens = token.split('|').map(_.trim.toLowerCase)
      subTokens ++ cleanAndSplitTokens(tail)
  }
}
```

Not stack safe! (recursion is not tail recursive)

Will throw a SO exception for large lists

---

# flatMap

```scala
/*
   "abc |dEf|g"   ---->   "abc"
   "H "                   "def"
   "ij|kl"                "g"
                          "h"
                          "ij"
                          "kl"
*/
def cleanAndSplitTokens(tokens: List[String]): List[String] =
  tokens.flatMap(_.split('|').map(_.trim.toLowerCase))

// or with `for`
def cleanAndSplitTokens(tokens: List[String]): List[String] = for {
  token <- tokens
  subToken <- token.split('|')
} yield subToken.trim.toLowerCase
```

Here we can remove the SO and simplify the code using `flatMap`

---

# Gotcha 8

---

# Gotcha 8

```scala
def getSize(seq: Seq[Int]): Int = seq.size
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Gotcha 8

```scala
def getSize(seq: Seq[Int]): Int = seq.size
//                                    ^^^^ incomplete
```

Some sequences have a size too big for `Int`

Some sequences are infinite and don't have a concept of size

---

# Infinite sequences

There's no rule that says a `Seq` must be finite

---

# Example

If you want to play with this:

```scala
def infiniteIncreasing(start: Int): LazyList[Int] = start #:: infiniteIncreasing(start + 1)

val nats: Seq[Int] = infiniteIncreasing(0)

nats(0) // 0

nats(1) // 1

nats(2) // 2

nats.size // runs out of memory eventually and crashes
```

---

# Shock!

> But Pranali!

you say

> I use .size all the time on sequences!

---

# Shock!

> I use .size all the time on sequences!

_Technically_ what you're doing is unsafe

There's an implicit assumption that you're using a small finite collection

(which is usually reasonable)

---

# Note

Most concrete sequence types are finite:

- `List`


- `Vector`


- `Array`

---

# Seq

An abstract base class that can have many children

```
                     Seq
       /     /     /     \       \
   Range  List    Vector  Array   LazyList ...
```

Some of those children might not have a size

---

# Related gotchas

`last` is also technically unsafe

Infinite sequences don't have a last element!

---

# More on this another time

One day we'll look more into lazy sequences

---

# Gotcha 9

---

# Gotcha 9

```scala
def sum(values: Seq[Int]): Int = values.reduceLeft(_ + _)
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Gotcha 9

```scala
def sum(values: Seq[Int]): Int = values.reduceLeft(_ + _)
//                                      ^^^^^^^^^^ incomplete
```

What if `values` is empty?

(or infinite!)

---

# fold vs reduce

```scala
def sum(values: Seq[Int]): Int = values.reduceLeft(_ + _)

def sum(values: Seq[Int]): Int = values.foldLeft(0)(_ + _)
```

How do they bootstrap the aggregation?

---

# fold vs reduce

```scala
def sum(values: Seq[Int]): Int = values.reduceLeft(_ + _)

def sum(values: Seq[Int]): Int = values.foldLeft(0)(_ + _)
```

How do they bootstrap the aggregation?

- `reduceLeft` and `reduceRight` use the first/last element of the sequence respectively


- `foldLeft` and `foldRight` take a seed

---

# Dangerous reduce

> `reduceLeft` and `reduceRight` use the first/last element of the sequence respectively

This implicitly assumes the sequence is non-empty

---

# Prefer fold

Avoids issues when your sequence is empty

---

# Gotcha 10

---

# Gotcha 10

```scala
// "18,30" => (18,30), values should be positive
def parse(raw: String): (Int, Int) = {
  val tokens = raw.split(",")
  (tokens(0).toInt, tokens(1).toInt)
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

# Gotcha 10

```scala
// "18,30" => (18,30), values should be positive
def parse(raw: String): (Int, Int) = {
  val tokens = raw.split(",")
  (tokens(0).toInt, tokens(1).toInt)
//       ^^^ ^^^^^        ^^^ ^^^^^
}
```

Two issues:

- assumes it splits nicely into 2


- assumes the parts are integers

---

# Other minor issues

```scala
// "18,30" => (18,30), values should be positive
def parse(raw: String): (Int, Int) = {
  val tokens = raw.split(",")
  (tokens(0).toInt, tokens(1).toInt)
//       ^^^ ^^^^^        ^^^ ^^^^^
}
```

- would accept strings like "17,30,10" and ignore the 10


- not enforcing values are positive


- when the input is bad, the error message will be hard to understand higher up

e.g. `java.lang.IndexOutOfBoundsException: 1`

---

# Cleaner

```scala
val digits = "\\d+".r

// "18,30" => (18,30), values should be positive
def parse(raw: String): (Int, Int) = raw.split(",") match {
  case Array(digits(token1), digits(token2)) => (token1.toInt, token2.toInt)
  case _ => // gracefully handle case of there not being two parseable tokens
}
```

- makes sure there's exactly 2 tokens (not more)


- regex ensures they're parseable and not negative


- can gracefully handle bad inputs

(would fail for large inputs though)

---

# That's it!

---

# Closing thoughts

---

# Empty sequences

Many gotchas related to empty sequences:

- head/tail


- last


- max/min


- average


- reduce

---

# NonEmptyList

Later we'll introduce `NonEmptyList` from cats

It avoids these gotchas

---

# What about null?

A null input would blow up most functions:

```scala
def clean(s: String): String = s.toLowerCase.trim

clean(null) // throws `NullPointerException`
```

---

# What about null?

> A null input would blow up most functions

Doesn't this mean they're incomplete?

---

# What about null?

> A null input would blow up most functions
>
> Doesn't this mean they're incomplete?

Technically yes, _but_:

Conceptually: we don't consider `null` as a valid input

Practically: scala libraries never produce `null` return values

---

# Defensive checking

Seems like safety comes at the cost of being more defensive/careful

---

# Defensive checking

> Seems like safety comes at the cost of being more defensive/careful

Later we'll see how using stronger types avoids this

(e.g. `NonEmptyList`)

---

# MVP?

The MVP prize goes to...

---

# Wrapping up

---

# Traps

A lot of functionality in the standard library is not "complete"

e.g. throws exceptions, causes infinite loops, returns non-sensical values

---

# Purity

To keep our code pure,

we need to be mindful of these traps and work around them

Hopefully today helped!

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
