---
author: Rohan
date: 2022-10-11
title: Validating Strong Types
---

```
__     __    _ _     _       _   _
\ \   / /_ _| (_) __| | __ _| |_(_)_ __   __ _
 \ \ / / _` | | |/ _` |/ _` | __| | '_ \ / _` |
  \ V / (_| | | | (_| | (_| | |_| | | | | (_| |
   \_/ \__,_|_|_|\__,_|\__,_|\__|_|_| |_|\__, |
                                         |___/
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

# Recap

(it's been a while)

---

# `Validated`

An ADT similar to `Either`

```
          Either           Validated
         /     \           /       \
      Left    Right    Invalid   Valid

    (error)   (happy)
```

---

# ValidatedNel

```scala
type ValidatedNel[E, A] = Validated[NonEmptyList[E], A]
```

Less verbose

---

# Strong type pattern

```scala
object Natural extends Strong[Int] {
  def validate(value: Int): Boolean = value >= 0
}

Natural.from(-1) // None

Natural.from(0)  // Some(0)
```

---

# Today

- write validators that spit out strong types

```scala
def validateName(name: String): ValidatedNel[E, Name] = ...
```


- discuss healing data during validation


- explore the validated api a little

---

```
  ___        _   _
 / _ \ _ __ | |_(_) ___  _ __
| | | | '_ \| __| |/ _ \| '_ \
| |_| | |_) | |_| | (_) | | | |
 \___/| .__/ \__|_|\___/|_| |_|
      |_|
 _
| |_ ___
| __/ _ \
| || (_) |
 \__\___/

__     __    _ _     _       _           _
\ \   / /_ _| (_) __| | __ _| |_ ___  __| |
 \ \ / / _` | | |/ _` |/ _` | __/ _ \/ _` |
  \ V / (_| | | | (_| | (_| | ||  __/ (_| |
   \_/ \__,_|_|_|\__,_|\__,_|\__\___|\__,_|

```

---

# Strong type pattern

```scala
object Natural extends Strong[Int] {
  def validate(value: Int): Boolean = value >= 0
  //                        ^^^^^^^
}

Natural.from(-1) // None

Natural.from(0)  // Some(0)
```

Previously the validation was represented by an `Option`

---

# Recall

> Previously the validation was represented by an `Option`

`Option` makes sense when it's obvious why a failure happened

---

# Recall

> Previously the validation was represented by an `Option`
>
> `Option` makes sense when it's obvious why a failure happened

For complex validation we'll use `ValidatedNel[String, A]` to describe what went wrong

```scala
object Natural extends Strong[Int] {
  def validate(value: Int): ValidatedNel[String, Int] =
    if (value >= 0) value.validNel else "Value must be non-negative".invalidNel
}

trait Strong[Weak] {
  ...

  // Framework provides this
  def from(value: Weak): ValidatedNel[String, Type] = ...
}
```

---

# Demo time!

Build the new version of `Strong`

---

# Summary

```scala
import $ivy.`org.typelevel::cats-core:2.7.0`

import cats.data.ValidatedNel
import cats.syntax.validated._

trait Strong[Weak] {
  sealed trait Tag
  type Type = Weak with Tag

  def validate(value: Weak): ValidatedNel[String, Weak]

  def from(value: Weak): ValidatedNel[String, Type] = validate(value).map(_.asInstanceOf[Type])
}

type Natural = Natural.Type
object Natural extends Strong[Int] {
  def validate(value: Int): ValidatedNel[String, Int] =
    if (value >= 0) value.validNel else s"Value $value is negative".invalidNel
}

Natural.from(3)  // Valid(3)

Natural.from(-1) // Invalid(NonEmptyList("Value -1 is negative"))
```

---

# Summary

```scala
trait Strong[Weak] {
  sealed trait Tag
  type Type = Weak with Tag

  def validate(value: Weak): ValidatedNel[String, Weak]

  def from(value: Weak): ValidatedNel[String, Type] = validate(value).map(_.asInstanceOf[Type])
}
```

We provide the `validate` logic

The `from` method just wraps it and upcasts the type

---

# Add `fromUnsafe`

Back to our demo!

---

# Summary

```scala
trait Strong[Weak] {
  ...

  def fromUnsafe(value: Weak): Type = from(value) match {
    case Valid(t) => t
    case Invalid(errors) => throw new IllegalArgumentException(s"Got ${errors.size} errors... TODO")
  }
}


Natural.fromUnsafe(1) // 1

Natural.fromUnsafe(0) // 0

Natural.fromUnsafe(-1) // Kaboom!
```

Just wraps around `from` and explodes if it fails

---

# strongtype library

Now you're up to speed with how our strongtype library works

---

```
 _   _            _ _
| | | | ___  __ _| (_)_ __   __ _
| |_| |/ _ \/ _` | | | '_ \ / _` |
|  _  |  __/ (_| | | | | | | (_| |
|_| |_|\___|\__,_|_|_|_| |_|\__, |
                            |___/
 ____        _
|  _ \  __ _| |_ __ _
| | | |/ _` | __/ _` |
| |_| | (_| | || (_| |
|____/ \__,_|\__\__,_|
```

---

# Fussy validators

Sometimes the validator can help get to valid data

---

# Name example

Names can't have boundary whitespace

To the repl!

---

# Summary

```scala
object Name extends Strong[String] {
  def validate(value: String): ValidatedNel[String, String] =
    if (value.trim == value) value.validNel else "Names can't have boundary whitespace".invalidNel
}

Name.from(" Boban") // Invalid

Name.from("Boban")  // Valid

// Heals the data
object Name extends Strong[String] {
  def validate(value: String): ValidatedNel[String, String] = value.trim.validNel
}

Name.from(" Boban") // Valid
```

---

# Caution

Sometimes when we try to be helpful, we end up being _too_ helpful

Can end up hiding subtle bugs

Depends on context

---

```
__     __    _ _     _       _           _
\ \   / /_ _| (_) __| | __ _| |_ ___  __| |
 \ \ / / _` | | |/ _` |/ _` | __/ _ \/ _` |
  \ V / (_| | | | (_| | (_| | ||  __/ (_| |
   \_/ \__,_|_|_|\__,_|\__,_|\__\___|\__,_|

    _          _
   / \   _ __ (_)
  / _ \ | '_ \| |
 / ___ \| |_) | |
/_/   \_\ .__/|_|
        |_|
```

---

# Validated Api

Have a look at some specialised validation methods

---

# ensure

Allows us to add post-validation checks

---

# Example

We model a user's age using `Natural`,

but for a particular endpoint, there's an additional check that they're 18+

To the repl!

---

# Summary

```scala
import cats.data.NonEmptyList

// ensure
def validateAdultAge(age: Int): ValidatedNel[String, Natural] =
  Natural.from(age).ensure(NonEmptyList.of(s"Only adults can do this thing"))(_ >= 18)

validateAdultAge(1) // Invalid(NonEmptyList("Only adults can do this thing"))

validateAdultAge(30) // Valid(30)

// ensureOr
def validateAdultAge(age: Int): ValidatedNel[String, Natural] =
  Natural.from(age).ensureOr(age => NonEmptyList.of(s"Only adults can do this thing, you're $age years old"))(_ >= 18)

validateAdultAge(2) // Invalid(NonEmptyList("Only adults can do this thing, you're 2 years old"))
```

---

# andThen

Allows us to compose different validators together

---

# Example

We validate an integer specified on the command line

```
./run 3
      ^
```

---

# Example

We validate an integer specified on the command line

```
./run 3
      ^
```

(we have an existing validator for that)

```scala
def validateInt(arg: String): ValidatedNel[String, Int] = ...
```

---

# Example

We validate an integer specified on the command line

```
./run 3
      ^
```

(we have an existing validator for that)

```scala
def validateInt(arg: String): ValidatedNel[String, Int] = ...
```

---

# Example

We also have a validator for converting `Int` to `Natural` (Natural.from)

---

# Lego pieces

```scala
def validateInt(arg: String): ValidatedNel[String, Int] = ...

Natural.from
```

```
"123"     --->      123      --->       123
      validateInt        Natural.from

                "andThen"
```

To the repl!

---

# Summary

```scala
def validateInt(arg: String): ValidatedNel[String, Int] = {
  if (arg.matches("-?\\d{1,4}")) arg.toInt.validNel
  else "Integer can be an optional minus sign followed by 1 to 4 digits".invalidNel
}

validateInt("345") // Valid(345)

validateInt("-5") // Valid(-5)

def validateNatural(arg: String): ValidatedNel[String, Natural] =
  validateInt(arg).andThen { int => Natural.from(int) }

validateNatural("abc") // Invalid(NonEmptyList("Integer can be an optional minus sign followed by 1 to 4 digits"))

validateNatural("-1") // Invalid(NonEmptyList("Value -1 is negative"))

validateNatural("0") // Valid(0)
```

---

# Composing validation logic

`andThen` is a tool to compose smaller validators together to make bigger ones

```
"123"     --->      123      --->       123
      validateInt        Natural.from

                "andThen"
```

---

# Note

`andThen` is like `flatMap`

Has that "dependent" vibe:

```scala
for {
  i <- validateInt("123")
  n <- Natural.from(i)
} yield n
```

---

# Recap

`Validated` isn't a monad, it doesn't have `flatMap`

But sometimes you have to do dependent validation logic - use `andThen` for that

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

# strongtypes library

Uses `ValidatedNel` and not `Option`:

```scala
object Natural extends Strong[Int] {
  def validate(value: Int): ValidatedNel[String, Int] = ...
}
```

---

# Healing data

In some contexts your validators can repair broken data

Beware of being "too helpful"

---

# Validated Api

`ensure` and `ensureOr` - applies post-validation checking

`andThen` - allows composing of different validators

---

# Next time

Single Responsibility Models

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
