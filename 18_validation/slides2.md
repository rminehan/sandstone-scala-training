---
author: Rohan
date: 2022-09-20
title: Validated
---

```
__     __    _ _     _       _           _
\ \   / /_ _| (_) __| | __ _| |_ ___  __| |
 \ \ / / _` | | |/ _` |/ _` | __/ _ \/ _` |
  \ V / (_| | | | (_| | (_| | ||  __/ (_| |
   \_/ \__,_|_|_|\__,_|\__,_|\__\___|\__,_|
```

---

# Last time

Validation (generally)

---

# Working example

```
POST /user
```

```json
{
  "name": "Boban Jones",
  "age": 26,
  "email": "bobanjones@gmail.com",
  "password": "Boban4ever"
}
```

4 fields to validate

---

# Model

`Either[NonEmptyList[String], User]`

- `Left` means failure, `Right` means success


- `Left` has 1 or more errors (not 0)

---

# Dependent vs Independent

Validation is _independent_

---

# One-dollar-ionairre

For 23c, if you're doing independent computations,

the weapon of choice is:

```
(1) Definitely for                (3) Use for all the time for everything

(2) for, I just love it           (4) mapN
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# 4!

For 23c, if you're doing independent computations,

the weapon of choice is:

```
(1) Definitely for                (3) Use for all the time for everything

(2) for, I just love it           (4) mapN
                                      ^^^^
```

Answer is 4 (the number 4, not the word `for`)

---

# mapN recap

Option example

```scala
(Some(3), Some(2), Some(1)).mapN {
  case (a, b, c) => a + b + c
}
// Some(6)


(Some(3), None, Some(1)).mapN {
  case (a, b, c) => a + b + c
}
// None
```

---

# mapN recap

Either example

```scala
(Right(3), Right(2), Right(1)).mapN {
  case (a, b, c) => a + b + c
}
// Right(6)

(Right(3), Left(NonEmptyList("Invalid email")), Left(NonEmptyList("Invalid password"))).mapN {
  case (a, b, c) => a + b + c
}
// Left(NonEmptyList("Invalid email", "Invalid password"))
```

---

# Short circuiting

Validation that short-circuits creates a bad UX

Analogy: static vs dynamic language

---

# Today

Introduce `Validated` ADT from cats

---

# TLDR

`Validated` is like a specialised version of `Either` designed specifically for validation

---

# Agenda

- what is `Validated`?


- comparing with `Either`


- `ValidatedNel`


- useful syntax


- peak under the hood

---

```
    __        ___           _
    \ \      / / |__   __ _| |_
     \ \ /\ / /| '_ \ / _` | __|
      \ V  V / | | | | (_| | |_
       \_/\_/  |_| |_|\__,_|\__|

             _
            (_)___
            | / __|
            | \__ \
            |_|___/

            _ _     _       _           _
__   ____ _| (_) __| | __ _| |_ ___  __| |
\ \ / / _` | | |/ _` |/ _` | __/ _ \/ _` |
 \ V / (_| | | | (_| | (_| | ||  __/ (_| | ?
  \_/ \__,_|_|_|\__,_|\__,_|\__\___|\__,_|

```

---

# ADT

```scala
sealed trait Validated[+E, +A]

// Happy :]
case class Valid[A](a: A) extends Validated[Nothing, A]

// Sad :[
case class Invalid[E](e: E) extends Validated[E, Nothing]
```

`E` short for "error"

---

# Summary

An ADT with two concrete members:

- `Valid` for the happy path


- `Invalid` for the error path

---

# Simple example

To the repl!

---

# Summary

```scala
import $ivy.`org.typelevel::cats-core:2.7.0`

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}

def validateName(name: String): Validated[Exception, String] =
  if (name.isEmpty) Invalid(new IllegalArgumentException("Name can't be empty"))
  else Valid(name)

validateName("Boban")
// Valid("Boban")

validateName("")
// Invalid(new IllegalArgumentException("Name can't be empty"))
```

---

# Note

You don't have to use Exception's as your error type

(I like to use simple strings)

---

# Deja Vu?

`Validated` feels like `Either`

---

# Comparing ADT's

```
          Either           Validated
         /     \           /       \
      Left    Right    Invalid   Valid

    (error)   (happy)
```

Seems very similar...

---

```
         _____ _ _   _
        | ____(_) |_| |__   ___ _ __
        |  _| | | __| '_ \ / _ \ '__|
        | |___| | |_| | | |  __/ |
        |_____|_|\__|_| |_|\___|_|


            __   _____
            \ \ / / __|
             \ V /\__ \
              \_/ |___/

__     __    _ _     _       _           _
\ \   / /_ _| (_) __| | __ _| |_ ___  __| |
 \ \ / / _` | | |/ _` |/ _` | __/ _ \/ _` |
  \ V / (_| | | | (_| | (_| | ||  __/ (_| |
   \_/ \__,_|_|_|\__,_|\__,_|\__\___|\__,_|
```

---

# Why?

Why introduce `Validated` if `Either` does the job?

Is it worth it?

---

# Reasons

- interpretation is more clear


- not a monad


- nice api for specific validation tasks

---

# Interpretation is more clear

```scala
val a: Either[String, String] = ...

val a: Validated[String, String] = ...
```

---

# Interpretation is more clear

```scala
val a: Either[String, String] = ...

val a: Validated[String, String] = ...
```

Specific to validation

Meaning of left and right types a bit clearer

---

# Monad

`Either` is a monad

`Validated` is not

---

# One-dollar-ionairre

For 18c, which operation is associated with monads?

```
(A) map                (C) flatMap


(B) traverse           (D) mapN
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# flatMap

For 18c, which operation is associated with monads?

```
(A) map                (C) flatMap
                           ^^^^^^^

(B) traverse           (D) mapN
```

(and don't forget `pure` - the Tasmania of monads)

---

# Monad

> `Either` is a monad
>
> `Validated` is not

ie. `Validated` doesn't have `flatMap`

(so no `for`)

---

# Good?

> `Validated` doesn't have `flatMap`

Why is that good?

Seems bad...

---

# Good!

> `Validated` doesn't have `flatMap`

I think it's good

Removes temptation to use `for` in your validation logic

---

# Rich api

Lots of useful stuff specific to validation

To the repl!

---

# Summary

```scala
import cats.syntax.validated._

def validateName(name: String): Validated[String, String] =
  if (name.isEmpty) "Name can't be empty".invalid else name.valid
```

We'll cover more later, e.g. `andThen`, `ensure`

Type inference used above, otherwise put in types explicitly:

```scala
"Hiyo!".invalid[Int] // Validated[String, Int]

3.valid[String]      // Validated[String, Int]
```

---

```
__     __    _ _     _       _           _ _   _      _
\ \   / /_ _| (_) __| | __ _| |_ ___  __| | \ | | ___| |
 \ \ / / _` | | |/ _` |/ _` | __/ _ \/ _` |  \| |/ _ \ |
  \ V / (_| | | | (_| | (_| | ||  __/ (_| | |\  |  __/ |
   \_/ \__,_|_|_|\__,_|\__,_|\__\___|\__,_|_| \_|\___|_|
```

`ValidatedNel`

---

# Multiple errors

Our warm up examples used `Validated[E, A]`

Usually your models need to support multiple errors though

---

# NonEmptyList

Better model: `Validated[NonEmptyList[E], A]`

---

# NonEmptyList

Better model: `Validated[NonEmptyList[E], A]`

But quite a mouthful...

---

# ValidatedNel

```scala
type ValidatedNel[E, A] = Validated[NonEmptyList[E], A]
```

Demo time!

---

# Summary 1

```scala
import cats.data.{NonEmptyList, ValidatedNel}

// Place holders to make code easier
type NameError = String
type Name = String

def validateName(name: String): ValidatedNel[NameError, Name] =
  if (name.isEmpty) "Name can't be empty".invalidNel else name.validNel
```

---

# Summary 2

```scala
def validatePassword(password: String): ValidatedNel[PasswordError, Password] = {
  val lengthError = if (password.length < 8) Some("8 characters required") else None

  val digitError = if (!password.exists(_.isDigit)) Some("At least one digit required") else None

  val symbolError = if (!password.exists(c => ".,-_!@#$%".contains(c))) Some("At least one symbol required") else None

  // ...

  val errors = List(lengthError, digitError, symbolError).collect {
    case Some(error) => error
  }

  NonEmptyList.fromList(errors) match {
    case Some(nel) => nel.invalid
    case None => password.validNel
  }
}

validatePassword("Abc")
// Invalid(
//   NonEmptyList(
//     "8 characters required",
//     "At least one digit required",
//     "At least one symbol required"
//   )
// )
```

---

# Validated vs ValidatedNel

```scala
def validateName(name: String): Validated[NameError, Name] =

// vs

def validateName(name: String): ValidatedNel[NameError, Name] =
```

---

# Validated vs ValidatedNel

```scala
def validateName(name: String): Validated[NameError, Name] =

// vs

def validateName(name: String): ValidatedNel[NameError, Name] =
```

Often better to model with `ValidatedNel`

(even if you only anticipate one error)

---

# Why?

> Often better to model with `ValidatedNel`

- more future proof


- consistency across validators


- makes combining possible

---

# Demo time

> makes combining possible

To the repl!

---

# Summary

`Validated[E, A]` doesn't combine well

Only one "slot" for an error

`ValidatedNel` does

---

```
 _   _           _
| | | |_ __   __| | ___ _ __
| | | | '_ \ / _` |/ _ \ '__|
| |_| | | | | (_| |  __/ |
 \___/|_| |_|\__,_|\___|_|

 _   _
| |_| |__   ___
| __| '_ \ / _ \
| |_| | | |  __/
 \__|_| |_|\___|

 _                     _
| |__   ___   ___   __| |
| '_ \ / _ \ / _ \ / _` |
| | | | (_) | (_) | (_| |
|_| |_|\___/ \___/ \__,_|

```

---

# Disclaimer

Extra knowledge

Not crucial for day to day development

---

# Under the hood

`mapN` is using `Semigroup` to combine the errors

```scala
trait Semigroup[A] {
  def combine(left: A, right: A): A
}
```

---

# Under the hood

It found type class instances for `String` and `NonEmptyList[String]`

```scala
trait Semigroup[A] {
  def combine(left: A, right: A): A
}

object StringSemigroup extends Semigroup[String] {
  def combine(left: String, right: String): String = left + right // String concatenation
}

class NelSemigroup[T] extends Semigroup[NonEmptyList[T]] {
  def combine(left: NonEmptyList[T], right: NonEmptyList[T]): NonEmptyList[T] = left ++ right
}
```

---

# One-dollar-ionairre

For 27c, what type class does `Semigroup` remind you of?

```
(A) Falling asleep      (C) Monoid


(B) Functor             (D) Show
```

```scala
trait Semigroup[A] {
  def combine(left: A, right: A): A
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

# Monoid!

For 27c, what type class does `Semigroup` remind you of?

```
(A) Falling asleep      (C) Monoid
                            ^^^^^^

(B) Functor             (D) Show
```

```scala
trait Semigroup[A] {
  def combine(left: A, right: A): A
}

trait Monoid[A] {
  def identity: A

  def combine(left: A, right: A): A
}
```

Monoid = Semigroup + Identity

---

# One-dollar-ionairre

For 13c, what is the monoid identity for `String`?

```
(A) "Boban"             (C) Empty string


(B) 0                   (D) There isn't one
```

(and tell me why for 2c)

```scala
object StringMonoid extends Monoid[String] {
  def identity: String = ???

  def combine(left: String, right: String): String = left + right // String concatenation
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

# Empty string!

> For 13c, what is the monoid identity for `String`?

```
(A) "Boban"             (C) Empty string
                            ^^^^^^^^^^^^

(B) 0                   (D) There isn't one
```

```scala
object StringMonoid extends Monoid[String] {
  def identity: String = ""

  def combine(left: String, right: String): String = left + right // String concatenation
}
```

> (and tell me why for 2c)

Concatenating with empty string does nothing

---

# One-dollar-ionairre

For 32c, what is the monoid identity for `NonEmptyList`?

```
(A) null                (C) Empty list


(B) 0                   (D) There isn't one
```

(and tell me why for 2c)

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# There isn't one!

> For 32c, what is the monoid identity for `NonEmptyList`?

```
(A) null                (C) Empty list


(B) 0                   (D) There isn't one
                            ^^^^^^^^^^^^^^^
```

> (and tell me why for 2c)

Concatenating two non-empty lists always produces a bigger list

(and the empty list is not a non-empty list!)

---

# One-dollar-ionairre

For 56c, which of the following statements about `NonEmptyList` is true?

```
(A) It's a semigroup,           (C) It's a monoid,
    but not a monoid                but not a semigroup


(B) It's useless                (D) It's both a monoid
                                    and a semigroup
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# A!

For 56c, which of the following statements about `NonEmptyList` is true?

```
(A) It's a semigroup,           (C) It's a monoid,
    but not a monoid                but not a semigroup
    ^^^^^^^^^^^^^^^^^

(B) It's useless                (D) It's both a monoid
                                    and a semigroup
```

It has a concept of combining (semigroup)

It doesn't have an identity element (needed for monoid)

```scala
trait Semigroup[A] {
  def combine(left: A, right: A): A  // yep!
}

trait Monoid[A] {
  def identity: A                    // nope :(

  def combine(left: A, right: A): A  // yep!
}
```

---

# Aside

You might be wondering:

> Where do these weird names come from?
>
> Semigroup? Monoid?

From maths/category theory

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

# Introducing `Validated`

An ADT similar to `Either`

```
          Either           Validated
         /     \           /       \
      Left    Right    Invalid   Valid

    (error)   (happy)
```

---

# Validated vs Either

Validated:

- is easier to interpret


- is not a monad


- has a nice api specific to validation

---

# ValidatedNel

A type alias for `Validated[NonEmptyList[E], A]`

---

# Validated vs ValidatedNel

`ValidatedNel` is generally better (I think)

---

# Semigroup

New type class!

Represents the concept of combining

Used when `mapN` combines `Validated`'s errors together

---

# Monoid vs Semigroup

Monoid = Semigroup + Identity

---

# NonEmptyList

Is a semigroup, but not a monoid

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
