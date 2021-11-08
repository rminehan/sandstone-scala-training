---
author: Rohan
date: 2021-11-08
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

continued...

---

# Previously on scala training...

- basic syntax


- default cases


- guards (`if`)


- destructuring complex structures

```scala
myPair match {
  case (x, _) if x == 3 => ...
  //    ^^^^     ^^^^^^
  //  destru-     guard
  //  cturing

  // Catch all
  case (x, y) =>
}
```

---

# Today

- more advanced syntax


- pattern matching sequences


- understanding extractors

---

```
    _    _ _                        _            
   / \  | | |_ ___ _ __ _ __   __ _| |_ ___  ___ 
  / _ \ | | __/ _ \ '__| '_ \ / _` | __/ _ \/ __|
 / ___ \| | ||  __/ |  | | | | (_| | ||  __/\__ \
/_/   \_\_|\__\___|_|  |_| |_|\__,_|\__\___||___/
                                                 
```

Using `|` like an "or"

but for _patterns_

---

# Already seen this

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
  case (_, 0) | (0, _) => "axis"
  //          ^
  case (x, y) if x > 0 && y > 0 => "top-right"
  case (x, y) if x > 0 && y < 0 => "bottom-right"
  case (x, y) if x < 0 && y > 0 => "top-left"
  case (x, y) if x < 0 && y < 0 => "bottom-left"
}
```

---

# More typical example

```scala
def favouriteLanguage(name: String): String = name match {
  case "tansel" => "small talk"
  case "denis" | "rohan" | "martin" | "pranali" | "sheethal" => "scala"
  case "feroz" | "yuhan" => "javascript"
  case "simon" => "yaml"
  case "lanie" => "cobol"
}
```

(Did I get those right?)

---

# Binding?

Back to our example:

```scala
def categorize(coord: (Int, Int)): String = coord match {
  case (_, 0) | (0, _) => "axis"
  case (x, y) if x > 0 && y > 0 => "top-right"
  case (x, y) if x > 0 && y < 0 => "bottom-right"
  case (x, y) if x < 0 && y > 0 => "top-left"
  case (x, y) if x < 0 && y < 0 => "bottom-left"
}
```

Imagine we wanted to print the x, y values so we tried this:

```scala
  case (x, 0) | (0, y) =>
    println(s"x=$x, y=$y")
    "axis"
```

Can you see a problem with this?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Binding issue

Imagine (3, 0) being evaluated:

```scala
  case (x, 0) | (0, y) =>   // (3, 0) matches left case, binds x = 3
    println(s"x=$x, y=$y")  // what is y?
    "axis"
```

---

# Overall

Binding doesn't conceptually make sense when you have alternate patterns

Because only one pattern will match

The scala compiler doesn't allow it

---

# Aside: Work around

```scala
  case (x, y) if x == 0 || y == 0 =>
    println(s"x=$x, y=$y")
    "axis"
```

---

# `|` vs `||`

Two versions of our axis check:

```scala
  // v1
  case (_, 0) | (0, _) => "axis"
  //          ^

  // v2
  case (x, y) if x == 0 || y == 0 => "axis"
  //                    ^^
}
```

Why is there one pipe in the first and two pipes in the second?

---

# `|` vs `||`

```scala
  case (_, 0) | (0, _) => "axis"
  //          ^
  //   <------------->
  //        pattern


  case (x, y) if x == 0 || y == 0 => "axis"
  //                    ^^
  //   <---->    <-------------->
  //   pattern        regular
}
```

> Why is there one pipe in the first and two pipes in the second?

The `|` is "pattern syntax"       (before the guard)

The `||` is regular scala syntax  (after the guard)

They are different things

Will make more sense when we define our own extractors

---

# Summary of alternatives

Useful when many cases have the same handling logic

ie. "or"-ing patterns

Can't bind data though

---

```
 _          _          _ _ _             
| |    __ _| |__   ___| | (_)_ __   __ _ 
| |   / _` | '_ \ / _ \ | | | '_ \ / _` |
| |__| (_| | |_) |  __/ | | | | | | (_| |
|_____\__,_|_.__/ \___|_|_|_|_| |_|\__, |
                                   |___/ 
```

with `@`

---

# Recall our person example

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

# Example pattern match

```scala
def describe(person: Person): String = person match {
  case Person(_, Name("penny", _, _), Some(Address(_, _, "lane", _)) => "penny lane"
  case Person(_, Name("simon", _, _), None) => "simon says: buy me a house"
  case _ => "(none)"
}

case class Person(
  id: UUID,
  name: Name,
  addressOpt: Option[Address]
)
```

---

# Modify it

```scala
def describe(person: Person): String = person match {
  case Person(_, Name("penny", _, _), Some(Address(_, _, "lane", _)) => printAddress(???)
  ...
}

def printAddress(address: Address): String = ...
```

What if we wanted to bind to the entire address for penny?

---

# Inner vs Outer

```scala
def describe(person: Person): String = person match {
  case Person(_, Name("penny", _, _), Some(Address(number, name, "lane", postcode)) => printAddress(???)
  ...
}

def printAddress(address: Address): String = ...
```

It's easy to get references to the elements _inside_ the address, but not the _outer_ `Address` itself

---

# Solution 1: guards

```scala
def describe(person: Person): String = person match {
//case Person(_, Name("penny", _, _), Some(Address(number, name, "lane", postcode)) => printAddress(???)
  case Person(_, Name("penny", _, _), Some(address)) if address.streetType == "lane" => printAddress(address)
  ...
}
```

By not destructuring, we were able to bind the entire address to a value `address`

But what if we want to destructure?

---

# Solution 2: label

```scala
def describe(person: Person): String = person match {
  case Person(_, Name("penny", _, _), Some(address @ Address(number, name, "lane", postcode)) => printAddress(address)
//case Person(_, Name("penny", _, _), Some(          Address(number, name, "lane", postcode)) => printAddress(???)
  ...
}
```

We can have our cake and eat it:

- (inner) destructure our address into smaller bits


- (outer) bind the top level address itself

---

# Hyper labelling example

```scala
person match {
  case person @ Person(id, name @ Name(...), addressOpt @ Some(address @ Address(...))) => ...
}
```

Shows labels being used at different depths

---

# Summary of labels

Useful for when you need to want to break some data into smaller pieces,

but still hold a reference to the data as a whole

---

```
 ____                                            
/ ___|  ___  __ _ _   _  ___ _ __   ___ ___  ___ 
\___ \ / _ \/ _` | | | |/ _ \ '_ \ / __/ _ \/ __|
 ___) |  __/ (_| | |_| |  __/ | | | (_|  __/\__ \
|____/ \___|\__, |\__,_|\___|_| |_|\___\___||___/
               |_|                               
```

Scala has great pattern matching syntax for "variable length" concepts like collections

---

# Examples

```scala
def processList(list: List[Int]): Unit = list match {
  case List(1, 2, 3) => println("Got exact List(1, 2, 3)")

  case List(1, _, 3) => println("Got list length 3 starting with 1 and ending with 3")

  case List(1, _*) => println("Got a list starting with 1, length 1 or more")

  case List(x, y, z) if x <= y && y <= z => println("Got an ordered list length 3")

  case List(_, _, _) => println("Got a list length 3")

  case List(_, _, _*) => println("Got a list length 2 or more")

  case _ => println("Default case")
}

processList(List(1, 2, 3, 4, 5))
processList(List(1, 2, 3))
processList(List(1))
processList(List(1, 4, 3, 4, 5))
processList(List(2, 3, 4))
processList(List(2, 3, 4, 5))
```

---

# Practical example: Splitting strings

We're often tokenizing strings:

```scala
val array = "123 456 789".split(' ')
// Array("123", "456", "789")

val first = array(0)
val second = array(1)
val third = array(2)
```

---

# Imagine that as a method

```scala
def tokenize(id: String): (String, String, String) = {
  val array = id.split(' ')
  (array(0), array(1), array(2))
}
```

A bit icky:

- what if there's more than 3 tokens? e.g. "123 456 789 000"


- what if there's less than 3 tokens? e.g. "123 456"

---

# Improve with pattern matching

```scala
// Old
def tokenize(id: String): (String, String, String) = {
  val array = id.split(' ')
  (array(0), array(1), array(2))
}

// New
def tokenize(id: String): (String, String, String) = id.split(' ') match {
  case Array(first, second, third) => (first, second, third)
  case _ => throw new IllegalArgumentException(s"Unable to tokenize id, exactly 3 tokens required: '$id'") 
}
```

Enforces there are exactly 3

---

# Make it more complex

Suppose the third token is optional and we'll default it "other"

And we have a special "admin" id which becomes ("admin", "admin", "admin")

---

# Not too hard

> Suppose the third token is optional and we'll default it "other"
>
> And we have a special "admin" id which becomes ("admin", "admin", "admin")

```scala
def tokenize(id: String): (String, String, String) = id.split(' ') match {
  case Array("admin")              => ("admin", "admin", "admin")
  case Array(first, second)        => (first, second, "other")
  case Array(first, second, third) => (first, second, third)
  case _ => throw new IllegalArgumentException(s"Unable to tokenize id: '$id'") 
}
```

---

# Finally a complex example

```scala
val coordinates = List(
  (0, 0),
  (1, 3),
  ...
)

coordinates match {
  case List(c1, c2, c3@(x, y), c4) => ...
  case List(_, _, (x3, _), (x4, _)) if x3 > x4 => ...
  case List((0, 0), _*) => ...
}
```

---

# Summary of sequence patterns

Great for simultaneously expressing conditions about:

- the length of a sequence


- the values in the sequence

---

```
 ___       _                        _       _   _             
|_ _|_ __ | |_ ___ _ __ _ __   ___ | | __ _| |_(_) ___  _ __  
 | || '_ \| __/ _ \ '__| '_ \ / _ \| |/ _` | __| |/ _ \| '_ \ 
 | || | | | ||  __/ |  | |_) | (_) | | (_| | |_| | (_) | | | |
|___|_| |_|\__\___|_|  | .__/ \___/|_|\__,_|\__|_|\___/|_| |_|
                       |_|                                    
 ____       _   _                      
|  _ \ __ _| |_| |_ ___ _ __ _ __  ___ 
| |_) / _` | __| __/ _ \ '__| '_ \/ __|
|  __/ (_| | |_| ||  __/ |  | | | \__ \
|_|   \__,_|\__|\__\___|_|  |_| |_|___/
                                       
```

Great for string parsing

New in scala 2.13!

---

# Demo time!

To the repl!

---

# Summary

You can invert your string interpolation to _parse_ strings rather than build them

Great for simple parsing logic

```scala
@ def matchRandomFormats(s: String): (String, String, String) = s match {
    case s"$x--$y--$z" => (x, y, z)
    case s"($x)($y)($z)" => (x, y, z)
    case _ => ???
  }

@ matchRandomFormats("123--456--789")
// ("123", "456", "789")

@ matchRandomFormats("(1)(2)(345)")
// ("1", "2", "345")
```

Mwah!

---

```
 ____        __ _            
|  _ \  ___ / _(_)_ __   ___ 
| | | |/ _ \ |_| | '_ \ / _ \
| |_| |  __/  _| | | | |  __/
|____/ \___|_| |_|_| |_|\___|
                             
                                              
 _   _  ___  _   _ _ __    _____      ___ __  
| | | |/ _ \| | | | '__|  / _ \ \ /\ / / '_ \ 
| |_| | (_) | |_| | |    | (_) \ V  V /| | | |
 \__, |\___/ \__,_|_|     \___/ \_/\_/ |_| |_|
 |___/                                        
           _                  _                 
  _____  _| |_ _ __ __ _  ___| |_ ___  _ __ ___ 
 / _ \ \/ / __| '__/ _` |/ __| __/ _ \| '__/ __|
|  __/>  <| |_| | | (_| | (__| || (_) | |  \__ \
 \___/_/\_\\__|_|  \__,_|\___|\__\___/|_|  |___/
                                                
```

---

# Time to peek under the covers

How does pattern matching actually work?

What _is_ a pattern?

---

# Build one ourselves

Easiest to understand with an example

We have two databases of customers: ANZ and Commbank

---

# Find by id 

Each database uses its own id convention

## ANZ

> anz--<yyyyMMdd>--<8 hexadecimal characters>

```scala
"anz--20210923--ac423f32"
"anz--20210803--df423334"
"anz--20210130--3349affe"
```

## Commbank

> <10 digits>.<category>

```scala
"2394222933.basic"
"5596003921.premium"
```

---

# Our problem

An id comes from the front end and we need to return that user

---

# Basic logic

```scala
def findUser(id: String): User = {
  if (...) // is anz
    findAnzUser(id)
  else if (...) // is commbank
    findCommbankUser(id)
  else
    throw new IllegalArgumentException(s"Invalid id format: '$id'")
}

...

// Existing methods
def findAnzUser(id: String): User = ...
def findCommbankUser(id: String): User = ...
```

ie. we're unifying the two databases

---

# `if-else` chain

```scala
def findUser(id: String): User = {
  if (...) // is anz
    findAnzUser(id)
  else if (...) // is commbank
    findCommbankUser(id)
  else
    throw new IllegalArgumentException(s"Invalid id format: '$id'")
}
```

Pattern match?

---

# As a pattern match?

```scala
def findUser(id: String): User = id match {
  case ... => findAnzUser(id)
  case ... => findCommbankUser(id)
  case _ => throw new IllegalArgumentException(s"Invalid id format: '$id'")
}
```

We would need to define patterns for our two formats

> "anz--20210923--ac423f32"
>
> "2394222933.basic"

---

# Demo time!

---

# What it will look like

```scala
def findUser(id: String): User = id match {
  case AnzId() => findAnzUser(id)
  case CommbankId() => findCommbankUser(id)
  case _ => throw new IllegalArgumentException(s"Invalid id format: '$id'")
}
```

---

# Structure

Our id's have structure

Let's model that:

```
"anz--20210923--ac423f32"
      ^^^^^^^^  ^^^^^^^^
        date      hash

"2394222933.basic"
 ^^^^^^^^^^ ^^^^^
    code     plan
```

---

# Extraction

```
"anz--20210923--ac423f32"
      ^^^^^^^^  ^^^^^^^^
        date      hash

"2394222933.basic"
 ^^^^^^^^^^ ^^^^^
    code     plan
```

What if we also wanted to destructure them?

```scala
def demo(id: String): Unit = id match {
  case AnzId(date, hash) if date.getYear == 2019 => ...
  case CommbankId(code, "basic") => ...
  case _ => ...
}
```

This is no longer a simple yes/no question (boolean),

it's an extraction question

---

# Changing unapply

> This is no longer a simple yes/no question (boolean),
>
> it's an extraction question

```
"anz--20210923--ac423f32"
      ^^^^^^^^  ^^^^^^^^
        date      hash
```

```diff
 object AnzId {
-  def unapply(raw: String): Boolean = ...
+  def unapply(raw: String): Option[(LocalDate, String)] = ...
 }
```

---

# Demo time!

---

# Summary

You can build your own extractors

ScaLa = SCAlable LAnguage

The built in extractors are mostly defined in the standard library

---

# Summary - building your own

- define an `object` whose name will be your extractor

```scala
object AnzId {
}
```

---

# Summary - building your own

- define an `unapply` method that returns either `Boolean` or `Option`

```scala
object AnzId {
  def unapply(raw: String): Boolean = ...

  // or

  def unapply(raw: String): Option[(...)] = ...
}
```

The input is the data you want to extract _from_

---

# Summary - building your own

- import it if necessary and use it in pattern matches!

```scala
import my.useful.AnzId

id match {
  // Boolean yes/no extractor
  case AnzId() =>

  // Option extractor
  case AnzId(....) =>
}
```

---

```
                               _       ____             
 _   _ _ __   __ _ _ __  _ __ | |_   _/ ___|  ___  __ _ 
| | | | '_ \ / _` | '_ \| '_ \| | | | \___ \ / _ \/ _` |
| |_| | | | | (_| | |_) | |_) | | |_| |___) |  __/ (_| |
 \__,_|_| |_|\__,_| .__/| .__/|_|\__, |____/ \___|\__, |
                  |_|   |_|      |___/               |_|
```

Variable length patterns

---

# Scenario

We also just inherited a database of NAB customers (National Australia Bank)

They have variable length id's like:

```scala
"nab--448--993--558--330--119"
"nab--330--119"
"nab--519--339--441--332"
"nab--519--339--441--332--001--338--145--339"
```

---

# Rules:

- starts with "nab"


- uses "--" as delimiters


- each token is 3 digits


- at least 2 tokens


- no upper limit

```scala
// Valid
"nab--448--993--558--330--119"
"nab--330--119"
"nab--519--339--441--332"
"nab--519--339--441--332--001--338--145--339"

// Invalid
"noob--123--789"
"nab--003"
"nab--1234"
"nab--113--39e"
```

---

# Token meaning

Suppose the number of tokens is significant (e.g. more means more important customer)

and there are some tokens with special meaning

---

# Pattern matching

> Suppose the number of tokens is significant (e.g. more means more important customer)
>
> and there are some tokens with special meaning

We want to be able to do something like:

```scala
val id: String = ...

id match {
  case NabId("000", token2) => ...
  case NabId(token1, token2) => ...
  case NabId(token1, token2, token3) => ...
  case NabId(token1, token2, token3, token4, _*) => ...
}
```

---

# How would you do this?

`unapply` won't be good enough:

```scala
def unapply(raw: String): Option[(....)] = ...
//                               ^^^^^^  Fixed size tuple


// Doesn't fit this:
id match {
  case NabId("000", token2) => ... // admin
  case NabId(token1, token2) => ... // lowest tier
  case NabId(token1, token2, token3) => ... // medium tier
  case NabId(token1, token2, token3, token4, _*) => ... // highest tier
}
```

We'll need a new weapon

---

# unapplySeq

```scala
def unapplySeq(raw: String): Option[Seq[String]] = ...
//         ^^^
```

---

# Demo time!

---

# Summary of unapplySeq

Use it for "variadic" pattern matches

This is what the scala collections library uses under the hood

---

# How

Like `unapply` before, but define an `unapplySeq` that returns `Option[Seq[...]]`

---

# Last section

```
 ____            _                 _   _                 
|  _ \  ___  ___| | __ _ _ __ __ _| |_(_) ___  _ __  ___ 
| | | |/ _ \/ __| |/ _` | '__/ _` | __| |/ _ \| '_ \/ __|
| |_| |  __/ (__| | (_| | | | (_| | |_| | (_) | | | \__ \
|____/ \___|\___|_|\__,_|_|  \__,_|\__|_|\___/|_| |_|___/
                                                         
```

You can apply your destructuring knowledge to declarations

---

# Simple example

```scala
val coordinate = (3, 10)

// Pattern match
coordinate match {
  case (x, y) => ...
}

// Declaration
val (x, y) = coordinate
```

---

# Principle:

Declarations use the same destructuring rules as pattern matches

---

# Complex example

```scala
// Pattern match
person match {
  case Person(id, Name(first, _, _), _) => ...
}

// Declarations
val Person(id, Name(first, _, _), _) = person
println(id)
println(first)
```

---

# But be careful

What if we did this:

```scala
val Person(id, Name(first, Some(middle), _), _) = person
println(id)
println(first)
println(middle)
```

What issue can you see?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Hint

Under the hood this code:

```scala
val Person(id, Name(first, Some(middle), _), _) = person
println(id)
println(first)
println(middle)
```

is analagous to:

```scala
person match {
  case Person(id, Name(first, Some(middle), _), _) =>
    println(id)
    println(first)
    println(middle)
}
```

So what's the issue?

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
person match {
  case Person(id, Name(first, Some(middle), _), _) =>
    println(id)
    println(first)
    println(middle)
}
```

Will throw a `MatchError` if your person is missing a middle name

---

# Aside: the "trivial" pattern

```scala
val x = someExpression(...)
println(x)
```

is essentially the trivial top level pattern:

```scala
someExpression(...) match {
  case x => println(x)
}
```

Matches all and binds a name to the data

Just a special case of a more general syntax

---

# Summary

You can leverage the powerful pattern matching syntax in your declarations

---

# Summary

But be careful not to create incomplete matches that would throw exceptions

Picture what your code would look like as a pattern match and ask yourself if it's complete

---

```
 ____                                             
/ ___| _   _ _ __ ___  _ __ ___   __ _ _ __ _   _ 
\___ \| | | | '_ ` _ \| '_ ` _ \ / _` | '__| | | |
 ___) | |_| | | | | | | | | | | | (_| | |  | |_| |
|____/ \__,_|_| |_| |_|_| |_| |_|\__,_|_|   \__, |
                                            |___/ 
```

That's it for today

---

# What we covered...

---

# Alternatives

The `|` syntax for "or"-ing patterns

Compiler won't let you bind data as it doesn't make sense

---

# Labelling

Use `@` when you need to put a name on the top level thing you're destructuring

---

# Matching sequences

The standard library has built in extractors that allow for expressive logic:

```scala
def processList(list: List[Int]): Unit = list match {
  case List(1, 2, 3) => println("Got exact List(1, 2, 3)")

  case List(1, _, 3) => println("Got list length 3 starting with 1 and ending with 3")

  case List(1, _*) => println("Got a list starting with 1, length 1 or more")

  case List(x, y, z) if x <= y && y <= z => println("Got an ordered list length 3")

  case List(_, _, _) => println("Got a list length 3")

  case List(_, _, _*) => println("Got a list length 2 or more")

  case _ => println("Default case")
}
```

---

# Interpolation patterns

Flip the `s` interpolation around into an extractor

```scala
def matchRandomFormats(s: String): (String, String, String) = s match {
  case s"$x--$y--$z" => (x, y, z)
  case s"($x)($y)($z)" => (x, y, z)                                    ---
  ...
}
```

Good for simple parsing logic

---

# Defining your own extractors

Scala the scalable language

Create compantion objects with `unapply` or `unapplySeq` methods

---

# Declarations are also pattern matches

```scala
val coordinate = (3, 10)

// Pattern match
coordinate match {
  case (x, y) => ...
}

// Declaration
val (x, y) = coordinate
```

---

# If we had more time...

- type erasure


- dead code 


- algebraic data types


- regex pattern matching

Another time!

---

# Final note: don't overuse

```scala
myBoolean match {
  case true => ...
  case false => ...
}
```

Just use an `if` here

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
