---
author: Rohan
date: 2021-12-10
title: Implicit Conversions
---

```
 ___                 _ _      _ _
|_ _|_ __ ___  _ __ | (_) ___(_) |_
 | || '_ ` _ \| '_ \| | |/ __| | __|
 | || | | | | | |_) | | | (__| | |_
|___|_| |_| |_| .__/|_|_|\___|_|\__|
              |_|
  ____                              _
 / ___|___  _ ____   _____ _ __ ___(_) ___  _ __  ___
| |   / _ \| '_ \ \ / / _ \ '__/ __| |/ _ \| '_ \/ __|
| |__| (_) | | | \ V /  __/ |  \__ \ | (_) | | | \__ \
 \____\___/|_| |_|\_/ \___|_|  |___/_|\___/|_| |_|___/

```

Another way the compiler is working hard for us

(maybe too hard in this case...)

---

# Recap

3 implicit mechanisms:

- parameters

```scala
def age(implicit name: String): Int = ...

implicit val name: String = "James"
age
```


- classes

```scala
implicit class StringOps(val value: String) extends AnyVal {
  def clean: String = value.toLowerCase.trim
}

"abc DEF".clean
```


- conversions (coming up!)

---

# Today

Implicit conversions

---

# Recall the law of scala magic

> When something shouldn't work, but it magically does...

```scala
"12345".toInt
```

---

# Recall the law of scala magic

> When something shouldn't work, but it magically does...

```scala
"12345".toInt
```

> it's probably because of implicits

Scala _extended_ an existing class (`String`) with an implicit mechanism

---

# Two ways to extend a type

- implicit classes (last time)


- implicit conversions (this time)

---

# Demo time

Let's redo our `clean` extension,

using implicit conversions

---

# Recap

```scala
class StringOps(value: String) {
  def clean: String = value.trim.toLowerCase
}

implicit def string2StringOps(value: String): StringOps = new StringOps(value)

"abc DEF ".clean

// becomes

string2StringOps("abc DEF ").clean
```

---

# Compiler process

- sees you using a method that doesn't exist

```scala
"abc DEF".clean
```

---

# Compiler process

- sees you using a method that doesn't exist

```scala
"abc DEF".clean
```

- it searches for types where it would make sense

```scala
class StringOps(value: String) {
  def clean: String = value.trim.toLowerCase
}
```

---

# Compiler process

- sees you using a method that doesn't exist

```scala
"abc DEF".clean
```

- it searches for types where it would make sense

```scala
class StringOps(value: String) {
  def clean: String = value.trim.toLowerCase
}
```

- tries to find a mechanism to convert to that type

```scala
implicit def string2StringOps(value: String): StringOps = new StringOps(value)

string2StringOps("abc DEF ").clean
```

---

# Similarities

Implicit classes and conversions have a similar process

Only difference is mechanism in the last step

## Classes

```scala
implicit class StringOps(value: String) {
  def clean: String = s.toLowerCase.trim
}

// Not value class
(new StringOps("abc DEF ")).clean
// Value class
StringOps.clean$extension("abc DEF ")
```

## Conversions

```scala
implicit def string2StringOps(value: String): StringOps = new StringOps(value)

string2StringOps("abc DEF ").clean
```

---

# Can use explicitly

```scala
implicit def string2StringOps(value: String): StringOps = new StringOps(value)

string2StringOps("Hello")
```

Just a normal method with `implicit` slapped on the front

---

```
  ____                          _   _
 / ___|___  _ ____   _____ _ __| |_(_)_ __   __ _
| |   / _ \| '_ \ \ / / _ \ '__| __| | '_ \ / _` |
| |__| (_) | | | \ V /  __/ |  | |_| | | | | (_| |
 \____\___/|_| |_|\_/ \___|_|   \__|_|_| |_|\__, |
                                            |___/
 ____        _
|  _ \  __ _| |_ __ _
| | | |/ _` | __/ _` |
| |_| | (_| | || (_| |
|____/ \__,_|\__\__,_|
```

---

# Recap

Previous example was "extending" a type

```scala
implicit def string2StringOps(value: String): StringOps = new StringOps(value)

"abc DEF ".clean
// becomes
string2StringOps("abc DEF ").clean
```

---

# Another use case

Sometimes we want a "permanent" wrapper that we don't immediately throw away...

---

# Implicit conversions

Remember our `Meter` example

```scala
class Meter(value: Double)
```

Example of a "strong type"

(it's not inches, yards, km's, light years)

---

# Build a house

```scala
class Meter(value: Double)

def buildHouse(length: Meter, width: Meter, height: Meter): House = ...
```

---

# Build a house

Want to be able to pass doubles or maybe even int's:

```scala
class Meter(value: Double)

def buildHouse(length: Meter, width: Meter, height: Meter): House = ...
```

Want to build a house 3.5 x 4 x 2.5

```scala
// Have to do this...
buildHouse(length = new Meter(3.5d), width = new Meter(4.toDouble), height = new Meter(2.5d))
```

---

# What we'd like to do

```scala
class Meter(value: Double)

def buildHouse(length: Meter, width: Meter, height: Meter): House = ...
```

Want to build a house 3.5 x 4 x 2.5

```scala
// Instead of this:
buildHouse(length = new Meter(3.5d), width = new Meter(4.toDouble), height = new Meter(2.5d))

// Would be nice to do this:
buildHouse(length = 3.5d, width = 4, height = 2.5d)
```

---

# What we want

A way to convert `Double` or `Int` to `Meter` implicitly

```scala
buildHouse(length = new Meter(3.5d), width = new Meter(4.toDouble), height = new Meter(2.5d))

buildHouse(length = 3.5d, width = 4, height = 2.5d)
```

Not single use wrapper use case: `(new StringOps("abc DEF ")).clean`

We're converting for the sake of data, not functionality

---

# Demo time

To the repl!

---

# Summary

Implicit classes and conversions can:

- extend types


- convert data passed to methods

---

```
__        ___
\ \      / / |__  _   _
 \ \ /\ / /| '_ \| | | |
  \ V  V / | | | | |_| |
   \_/\_/  |_| |_|\__, |
                  |___/
 ____
|___ \
  __) |
 / __/  ?
|_____|

```

Two ways to achieve the same thing

---

# TLDR

If you want extensions, prefer implicit classes

---

# History

Originally scala just had implicit conversions

Implicit classes were added later

---

# Recap

They can both roughly do the same thing

---

# Why have two?

> They can both roughly do the same thing

Don't have a strong answer

Here is some things to consider

---

# Differences

Implicit classes syntax is "all in one place"

```scala
implicit class StringOps(value: String) {
  def clean: String = value.toLowerCase.trim
}
```

vs

```scala
class StringOps(value: String) {
  def clean: String = value.toLowerCase.trim
}

implicit def string2StringOps(value: String): StringOps = new StringOps(value)
```

---

# More focused

```scala
implicit class StringOps(value: String) {
  def clean: String = value.toLowerCase.trim
}
```

Only defines one sensible conversion from `String` to `StringOps`

vs

```scala
class StringOps(value: String) {
  def clean: String = value.toLowerCase.trim
}

implicit def string2StringOps(value: String): StringOps = new StringOps(value)
implicit def char2StringOps(value: Char): StringOps = new StringOps(value.toString)
```

---

# Practically

Compiler warns when using implicit conversions

To the compiler!

---

```
 ____
|  _ \  __ _ _ __   __ _  ___ _ __
| | | |/ _` | '_ \ / _` |/ _ \ '__| !
| |_| | (_| | | | | (_| |  __/ |
|____/ \__,_|_| |_|\__, |\___|_|
                   |___/
```

Beware of these silent conversions

---

# TLDR

These silent conversions cause a lot of headaches

Just use implicit classes for extensions

---

# Already seen unexpected example

Converted `Int -> Double -> Meter`

---

# Sometimes errors get silently "fixed"

Developer accidentally puts in the wrong type

```scala
def method(a: A): Unit = ...

method(b)
```

Implicit conversions would silently "fix" the error by converting `b` to an `A`

---

# Unintended behaviour

```scala
def method(a: A): Unit = ...

method(b)

// becomes

method(b2a(b))
```

- conversion might not be appropriate


- might have side effects


- developer doesn't even realise


- invisible, very hard to spot

---

# Can create ambiguity

```scala
def doSomething(a: A): Unit = ...

def doSomething(b: B): Unit = ...
```

---

# Which one?

```scala
def doSomething(a: A): Unit = ...

def doSomething(b: B): Unit = ...

implicit def a2B(a: A): B = ...

doSomething(a)
```

Which method will get called?

---

# From Scala's Daddy

> implicit conversions are evil

[Original context](https://contributors.scala-lang.org/t/can-we-wean-scala-off-implicit-conversions/4388)

---

# Summary

Stay away from these silent conversions (e.g. `Meter` example)

Just create classes designed for extensions (e.g. `StringOps` example)

---

```
 ____  _                  _               _
/ ___|| |_ __ _ _ __   __| | __ _ _ __ __| |
\___ \| __/ _` | '_ \ / _` |/ _` | '__/ _` |
 ___) | || (_| | | | | (_| | (_| | | | (_| |
|____/ \__\__,_|_| |_|\__,_|\__,_|_|  \__,_|

 _     _ _
| |   (_) |__  _ __ __ _ _ __ _   _
| |   | | '_ \| '__/ _` | '__| | | |
| |___| | |_) | | | (_| | |  | |_| |
|_____|_|_.__/|_|  \__,_|_|   \__, |
                              |___/
```

---

# Remember our examples

```scala
// Range
1 to 3

// Array
Array(1, 2, 3).map(_ * 2).filter(_ > 5)
```

Let's desugar these to see what's going on

To intellij!

---

# Results of desugaring

```scala
1 to 3
// is really
intWrapper(1).to(3)

Array(1, 2, 3).map(_ * 2)
// is really
intArrayOps(Array(1, 2, 3)).map(_ * 2)
```

and `intWrapper` and `intArrayOps` are implicit conversions

to richer types in the scala standard library

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

# Implicit conversions

Another mechanism to extend types

```scala
"abc DEF ".clean
```

---

# Also learnt

Implicit classes and conversions can transform data silently:

```scala
def buildHouse(length: Meter, width: Meter, height: Meter): House = ...

buildHouse(3.5d, 2, 2.5d)

// becomes

buildHouse(new Meter(3.5d), new Meter(2.toDouble), new Meter(2.5d))
```

---

# Silent conversions

Causes confusion and bugs

---

# Desugaring

You can use intellij to understand "magic" code

---

# Standard library

Hopefully the magic in the standard library makes more sense now

It relies heavily on implicit conversions

---

# Remember!

Use implicits with restraint (_particularly_ implicit conversions)

Is the hassle worth the characters you save? Sometimes it's helpful to be explicit

If in doubt, just don't use it

---

# Links

- [Official docs for implicit conversions](https://docs.scala-lang.org/tour/implicit-conversions.html)


- [SIP-13 which proposed implicit classes around 2009](https://docs.scala-lang.org/sips/implicit-classes.html)


- [Daddy Martin's rant about implicit conversions](https://contributors.scala-lang.org/t/can-we-wean-scala-off-implicit-conversions/4388)

---

# Next time

Implicit scope resolution

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \ ?
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
