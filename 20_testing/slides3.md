---
author: Rohan
date: 2023-02-07
title: Property Testing
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
```

---

# Recap

Bug hunting

To the repl!

---

# Bug Hunting

Unit tests: Like finding a needle in a haystack

"Fuzzing": Finds bugs very quickly

---

# Property Testing

## Last time

Motivating property testing

## Today

More foundational/theoretical

---

# Agenda

- what are properties?


- why is property testing useful?


- diva example

---

```
 ____                            _   _
|  _ \ _ __ ___  _ __   ___ _ __| |_(_) ___  ___
| |_) | '__/ _ \| '_ \ / _ \ '__| __| |/ _ \/ __|
|  __/| | | (_) | |_) |  __/ |  | |_| |  __/\__ \
|_|   |_|  \___/| .__/ \___|_|   \__|_|\___||___/
                |_|
```

---

# Properties

Universal statements we can test on our function

---

# Recall

```scala
/* Uppercases the characters a-z, everything else left the same
 *
 * Examples:
 *
 *   "abc" -> "ABC"
 *   "a#"  -> "A#"
 *   ""    -> ""
 *   "XYZ" -> "XYZ"
 */
def toUpperCase(s: String): String
```

---

# Example

```scala
// "length is not changed"
s.length == toUpperCase(s).length
```

---

# Recall bug

```scala
// PROPERTY
// "length is not changed"
s.length == toUpperCase(s).length // for all strings s

// Testing it:
"q".length // 1
toUpperCase("q").length // 0
// FAIL!
```

---

# Other properties

Can you think of some other properties for `toUpperCase`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Other properties

> Can you think of some other properties for `toUpperCase`?

- idempotent


- preserved by concatenation


- preserves whitespace

---

# Idempotent

```scala
// "toUpperCase is idempotent"
toUpperCase(toUpperCase(s)) == toUpperCase(s)
```

---

# Scenario

`toUpperCase` accidentally removes the last character from the string in some code paths

```scala
// PROPERTY
// "toUpperCase is idempotent"
toUpperCase(toUpperCase(s)) == toUpperCase(s)

// Testing it:
s.length = 200
toUpperCase(s).length = 199
toUpperCase(toUpperCase(s)).length = 198

toUpperCase(toUpperCase(s)) == toUpperCase(s) // must be false as they're different lengths
```

---

# Preserved by concatenation

```scala
// "toUpperCase is linear with respect to concatenation"
toUpperCase(s + t) == toUpperCase(s) + toUpperCase(t) // for all strings s,t

// Example
toUpperCase("abc" + "Def")              // "ABCDEF"
toUpperCase("abc") + toUpperCase("Def") // "ABC" + "DEF" = "ABCDEF"
```

---

# Preserves whitespace

```scala
// "whitespace will be unchanged"
def whitespaceCharsAndPositions(s: String): Seq[(Char, Int)] =
  s.toUpperCase.zipWithIndex.filter { case (c, _) => c.toString.matches("\\s") }

whitespaceCharsAndPositions(s) == whitespaceCharsAndPositions(toUpperCase(s))

// Example
whitespaceCharsAndPositions("abc def\nghi")
// Seq((' ', 3), ('\n', 7))

whitespaceCharsAndPositions(toUpperCase("abc def\nghi"))
// Seq((' ', 3), ('\n', 7))
```

---

# isUpperCase

---

# isUpperCase

```scala
/* A string is considered uppercase if it doesn't contain a-z
 *
 * Examples:
 *
 * UPPER CASE == true
 *   "ABC"
 *   "A#"
 *   "#"
 *   ""
 *
 * UPPER CASE == false
 *   "a"
 *   "aA"
 */
def isUpperCase(s: String): Boolean = ???
```

---

# Properties?

```scala
def isUpperCase(s: String): Boolean = ???
```

Can you think of any properties?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Properties?

```scala
def isUpperCase(s: String): Boolean = ???
```

Concatenation is linear with AND

> isUpperCase(s + t) will only be true, if isUpperCase(s) and isUpperCase(t) are both true

---

# Concatenation and AND

```scala
// concatenation is linear with AND
isUpperCase(s + t) == isUpperCase(s) && isUpperCase(t)

// Examples
isUpperCase("ABC" + "DEF") // true
isUpperCase("ABC") && isUpperCase("DEF") // true && true == true

isUpperCase("ABC" + "def") // false
isUpperCase("ABC") && isUpperCase("def") // true && false == false

isUpperCase("abc" + "def") // false
isUpperCase("abc") && isUpperCase("def") // false && false == false
```

---

# Recap

Property testing involves:

- identifying a property for your function (e.g. uppercasing a string doesn't change its length)


- writing a test which "fuzzes" that property over your function


- fuzzing requires generating a huge number of inputs

---

```
__        ___             _
\ \      / / |__  _   _  (_)___
 \ \ /\ / /| '_ \| | | | | / __|
  \ V  V / | | | | |_| | | \__ \
   \_/\_/  |_| |_|\__, | |_|___/
                  |___/
 _ _                       __       _
(_) |_     _   _ ___  ___ / _|_   _| |
| | __|   | | | / __|/ _ \ |_| | | | | ?
| | |_    | |_| \__ \  __/  _| |_| | |
|_|\__|    \__,_|___/\___|_|  \__,_|_|
```

---

# No outputs needed

You don't need to know the inputs and outputs ahead of time

---

# Unit testing

- developer comes up with an `(input, output)` pair


- does `f(input)`


- validates that it equals `output`

---

# Fuzzing

You can rely on something else to generate thousands of random inputs

---

# Bias and Blindspots

The developer writing the tests is usually the developer writing the code

---

# Bias

> The developer writing the tests is usually the developer writing the code

The developer is biased towards the implementation they're testing

---

# Bias

What if the implementation changes?

---

# Blindspots

> The developer writing the tests is usually the developer writing the code

You don't know what you don't know

The tester and implementer have the same blindspots

---

# Independent validation?

You're trusting the author to audit their own work

---

# Property testing

Quite resistant to bias and blindspots

---

# Property Testing

Quite resistant to bias and blindspots

- it's not influenced by your implementation


- a good input generator will create a wide variety of inputs


- it will catch stuff you didn't think about

---

# Efficient use of build resources

---

# Scenario

- Pranali writes a utility in docanalyser in Jan 2022 (with 30 unit tests)


- the utility isn't touched for a whole year


- docanalyser gets built in jenkins 2000 times over that year from unrelated changes

---

# Re-running the same tests

> Pranali writes a utility in docanalyser in Jan 2022 (with 30 unit tests)
>
> the utility isn't touched for a whole year
>
> docanalyser gets built in jenkins 2000 times over that year from unrelated changes

Those same 30 tests were run 2000 times

But nothing changed in that utility

---

# Build server economy

Running a build on jenkins costs money

---

# Scenario

Suppose running those 30 tests costs 2c

---

# Scenario

Suppose running those 30 tests costs 2c

We run it 2000 times -> $40

It cost us $40 to run 30 unique tests (~$1.30/test)

---

# Alternative

- Pranali writes a utility in docanalyser in Jan 2022 (with some *property tests*)


- the utility isn't touched for a whole year


- docanalyser gets built in jenkins 2000 times over that year from unrelated changes


- each property test runs about 100 random inputs

---

# Alternative

> Pranali writes a utility in docanalyser in Jan 2022 (with some *property tests*)
>
> the utility isn't touched for a whole year
>
> docanalyser gets built in jenkins 2000 times over that year from unrelated changes
>
> each property test runs about 100 random inputs

Suppose running those 100 tests costs 6c

---

# Cost

> docanalyser gets built in jenkins 2000 times over that year from unrelated changes
>
> Suppose running those 100 tests costs 6c

But each test is running 100 different inputs

2000 builds = $120

Number of unique values tested = 2000 x 100 = 200,000

Annual cost per test = 12,000c / 200,000 = 0.06c

---

# My point

It's an imperfect analysis,

but property testing clearly makes better use of your build resources

You're not rerunning the same tests over and over

---

# Clarification

It's not: Property testing vs Unit testing

Use both where they make sense

e.g. unit tests for known edge cases and regressions

---

# Recap

Why is property testing useful?

- can get high test coverage without needing to generate many (input, output) pairs


- avoids developer bias and blindspots


- utilises build resources more efficiently

---

# Other benefits

Property testing frameworks can drill into bugs

More on that another day

---

# Property testing and Unit testing

Property testing doesn't replace unit tests

There are scenarios where unit tests makes more sense

But some places where devs have been using unit tests would make more sense with property tests

---

```
 ____  _
|  _ \(_)_   ____ _
| | | | \ \ / / _` |
| |_| | |\ V / (_| |
|____/|_| \_/ \__,_|

 _____                           _
| ____|_  ____ _ _ __ ___  _ __ | | ___
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \
| |___ >  < (_| | | | | | | |_) | |  __/
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___|
                          |_|
```

---

# PhoneNumberFinderService

To the repl!

---

# Example properties

- inserting an empty or blank word has no effect


- inserting an "a" anywhere should make it fail


- prepending an area code to a landline doesn't change success/failure


- changing word boundaries has no effect

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

# Property testing

Identifying properties of your functions,

then fuzzing those properties

---

# Why is it useful?

High coverage

Less bias and blind spots

Better utilisation of build resources

---

# Does it replace unit testing?

No

Unit testing makes more sense in some cases

You can use both for the same function

---

# Diva example

Parsing phone numbers from words

Was surprisingly easy to come up with properties for that

---

# Coming Up

- ScalaCheck


- property testing and FP - love at first sight

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```

What do you guys think of property testing?
