---
author: Rohan
date: 2022-09-20
title: Introducing Validation
---

```
 ___       _                 _            _
|_ _|_ __ | |_ _ __ ___   __| |_   _  ___(_)_ __   __ _
 | || '_ \| __| '__/ _ \ / _` | | | |/ __| | '_ \ / _` |
 | || | | | |_| | | (_) | (_| | |_| | (__| | | | | (_| |
|___|_| |_|\__|_|  \___/ \__,_|\__,_|\___|_|_| |_|\__, |
                                                  |___/
__     __    _ _     _       _   _
\ \   / /_ _| (_) __| | __ _| |_(_) ___  _ __
 \ \ / / _` | | |/ _` |/ _` | __| |/ _ \| '_ \
  \ V / (_| | | | (_| | (_| | |_| | (_) | | | |
   \_/ \__,_|_|_|\__,_|\__,_|\__|_|\___/|_| |_|

```

---

# New series!

Validation concepts

---

# Roadmap

- purity


- strong types/modelling


- validation (you are here)


- testing

---

# General aims

- reduce runtime errors


- clearer error handling


- code easier to test

---

# Ad-hoc validation

Validation is usually done in an inconsistent, fuzzy way

Developers don't have a principled approach to it

---

# Today

- what is validation


- how to model and think about validation

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

            _ _     _       _   _
__   ____ _| (_) __| | __ _| |_(_) ___  _ __
\ \ / / _` | | |/ _` |/ _` | __| |/ _ \| '_ \
 \ V / (_| | | | (_| | (_| | |_| | (_) | | | | ?
  \_/ \__,_|_|_|\__,_|\__,_|\__|_|\___/|_| |_|
```

---

# Clarifying terminology

Making sure data from "outside" can be processed

```
              ----------------
             |                |
             |                |
     ----->  |                | <-----
             |                |
             |                |
             |                |
              ----------------

                    / \
                     |
                     |
                     |
```

---

# Examples

- service receiving a `POST` with json payload


- kafka consumer receiving a message with json payload


- function receiving a weak type

---

# "Wild west"

Data from outside your system is not trusted

```
              ----------------
             |                |
             |      SAFE      |
     ----->  |                | <-----
             |   POLICED BY   |            WILD
             | Sheriff Scalac |            WEST
             |                |
              ----------------

                    / \
                     |
                     |
                     |
```

e.g. clients can send whatever json they want

---

# Recap

Weak/untrusted data comes in,

we check it before processing it

---

```
__        __         _    _
\ \      / /__  _ __| | _(_)_ __   __ _
 \ \ /\ / / _ \| '__| |/ / | '_ \ / _` |
  \ V  V / (_) | |  |   <| | | | | (_| |
   \_/\_/ \___/|_|  |_|\_\_|_| |_|\__, |
                                  |___/
 _____                           _
| ____|_  ____ _ _ __ ___  _ __ | | ___
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \
| |___ >  < (_| | | | | | | |_) | |  __/
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___|
                          |_|
```

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

---

# Deserialisation

```json
{
  "name": "Boban Jones",
  "age": 26,
  "email": "bobanjones@gmail.com",
  "password": "Boban4ever"
}
```

```scala
case class Input(name: String, age: Int, email: String, password: String)

object Input {
  implicit val format: Format[Input] = ...
}
```

---

# Weak

```scala
case class Input(name: String, age: Int, email: String, password: String)

object Service {
  def create(user: Input): Future[UserId]
}
```

---

# Validation

```json
{
  "name": "Boban Jones",
  "age": 26,
  "email": "bobanjones@gmail.com",
  "password": "Boban4ever"
}
```

Suppose we make strong types for each field

```scala
object Name extends Strong[String] {
  ...
}

object Age extends Strong[Int] {
  ...
}

object Email extends Strong[String] {
  ...
}

...
```

---

# Weak to strong model

```scala
// Representation of weak json
case class Input(name: String, age: Int, email: String, password: String)

// Representation of validated input
case class User(name: Name, age: Age, email: Email, password: Password)

object Service {
  def create(user: User): Future[UserId]
}
```

If all weak fields can be converted to strong fields, process the payload

Otherwise return 400 with error messages

---

# What would it look like

```scala
case class Input(name: String, age: Int, email: String, password: String)

// ???

case class User(name: Name, age: Age, email: Email, password: Password)
```

---

# Recall

```scala
trait Strong[Weak] {
  def validate(value: Weak): Boolean

  def from(value: Weak): Option[Type] = if (validate(value)) Some(...) else None
}


object Name extends Strong[String] {
  def validate(value: String): Boolean = value.nonEmpty && value.trim == value
}

Name.from("Boban") // Some
Name.from("") // None
```

---

# Zooming out

```scala
val input: Input = ... // parse from json

val userOpt: Option[User] =
  for {
    name     <- Name.from(input.name)
    age      <- Age.from(input.age)
    email    <- Email.from(input.email)
    password <- Password.from(input.password)
  } yield User(name, age, email, password)

userOpt match {
  case Some(user) => service.create(user).map(Created) // 201
  case None => Future.successful(Invalid) // 400
}
```

---

```
 __  __           _      _ _ _
|  \/  | ___   __| | ___| | (_)_ __   __ _
| |\/| |/ _ \ / _` |/ _ \ | | | '_ \ / _` |
| |  | | (_) | (_| |  __/ | | | | | | (_| |
|_|  |_|\___/ \__,_|\___|_|_|_|_| |_|\__, |
                                     |___/
 _____
| ____|_ __ _ __ ___  _ __ ___
|  _| | '__| '__/ _ \| '__/ __|
| |___| |  | | | (_) | |  \__ \
|_____|_|  |_|  \___/|_|  |___/
```

---

# First problem

```scala
userOpt match {
  case Some(user) => service.create(user).map(Created) // 201
  case None => Future.successful(Invalid) // 400
                            //   ^^^^^^^
}
```

Validation error has no details

Any of the four fields could have failed for any reason

---

# Why no details?

Using `Option` to represent validation

```scala
val userOpt: Option[User] =
  for {
    name     <- Name.from(input.name)         // Option[Name]
    age      <- Age.from(input.age)           // Option[Age]
    email    <- Email.from(input.email)       // Option[Email]
    password <- Password.from(input.password) // Option[Password]
  } yield User(name, age, email, password)    // Option[User]
```

We are working in the "context" of `Option`

---

# Option

Useful when it's obvious why we got None

```scala
val people: List[Person] = ...

NonEmptyList.fromList(people) // None => list was empty

people.find(_.age == 18) // None => no one was 18
```

---

# Option

Less useful when there's multiple reasons for getting None

```scala
Password.from(input.password)
```

- too few characters?


- no upper case?


- no lower case?


- no symbol?

---

# Computer says no

`None` is like "computer says no"

Gives you no details to correct it

---

# Recap

```scala
val userOpt: Option[User] =
  for {

    name     <- Name.from(input.name)         // Option[Name]
    age      <- Age.from(input.age)           // Option[Age]
    email    <- Email.from(input.email)       // Option[Email]
    password <- Password.from(input.password) // Option[Password]

  } yield User(name, age, email, password)    // Option[User]
```

The individual failures are just `None`'s

The final error has nothing meaningful to report

---

# Switch to Either

---

# Either vs Option

Either is like `Option` + error message

- `Some(password) => Right(password)`


- `None => Left(error)`

---

# Either

```diff
 trait Strong[Weak] {
-  def validate(value: Weak): Boolean
+  def validate(value: Weak): Either[String, Weak]
 }
```

Use `String` instead of `Exception` to hold error messages

---

# Example

```scala
object Password {
  def validate(value: String): Either[String, String] = {
    if (value.length < 8) Left("Password must be at least 8 characters")
    else if (!value.exists(isDigit)) Left("Password must contain at least one digit")
    ... // other validation checks
    else Right(value)
  }
}
```

---

# Option -> Either

Imagine all the validators use `Either` now

---

# for: Option -> Either

```scala
val userEither: Either[String, User] =
  for {
    name     <- Name.from(input.name)         // Either[String, Name]
    age      <- Age.from(input.age)           // Either[String, Age]
    email    <- Email.from(input.email)       // Either[String, Email]
    password <- Password.from(input.password) // Either[String, Password]
  } yield User(name, age, email, password)    // Either[String, User]

userEither match {
  case Right(user) => service.create(user).map(Created) // 201
  case Left(error) => Future.successful(Invalid(error)) // 400
//                                              ^^^^^   // hoorah!
}
```

---

# Broken

```scala
val userEither: Either[String, User] =
  for {
    name     <- Name.fromString(input.name)         // Either[String, Name]
    age      <- Age.fromInt(input.age)              // Either[String, Age]
    email    <- Email.fromString(input.email)       // Either[String, Email]
    password <- Password.fromString(input.password) // Either[String, Password]
  } yield User(name, age, email, password)          // Either[String, User]

userEither match {
  case Right(user) => service.create(user).map(Created) // 201
  case Left(error) => Future.successful(Invalid(error)) // 400
//                                              ^^^^^   // hoorah!
}
```

Why is it broken?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Multiple errors across fields?

```scala
val userEither: Either[String, User] =
                    // ^^^^^^ one error
  for {
    name     <- Name.from(input.name)         // Either[String, Name]
    age      <- Age.from(input.age)           // Either[String, Age]
    email    <- Email.from(input.email)       // Either[String, Email]
    password <- Password.from(input.password) // Either[String, Password]
  } yield User(name, age, email, password)    // Either[String, User]
```

Modelling our validation as `Either[String, User]` assumes only one thing can go wrong

---

# Optimistic

> assumes only one thing can go wrong

There is no limit on how bad users can be

They could stuff up all 4 fields

---

# Multiple errors per field?

```scala
  for {
    name     <- Name.from(input.name)         // Either[String, Name]
    age      <- Age.from(input.age)           // Either[String, Age]
    email    <- Email.from(input.email)       // Either[String, Email]
    password <- Password.from(input.password) // Either[String, Password]
  } yield User(name, age, email, password)    // Either[String, User]
  //                                                    ^^^^^^
```

e.g. a password could have multiple issues

---

# Bad modelling

`Either[String, User]` only has one slot for errors

---

# Model first approach

Forget about the implementation

How would you model the result of our validation?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Seq?

Did you say:

```scala
Either[Seq[String], User]
```

---

# Issue

This is _better_

```scala
Either[Seq[String], User]
```

but there's an issue...

---

# Issue

This is _better_

```scala
Either[Seq[String], User]
```

Is it possible for validation to ever produce `Left(Seq.empty)`?

ie. fail with no errors?

---

# Issue

This is _better_

```scala
Either[Seq[String], User]
//     empty? No!
```

> Is it possible for validation to ever produce `Left(Seq.empty)`?

No, it makes no sense

If validation failed, you should have _at least one_ error string

---

# At least one error

```scala
Either[???[String], User]
```

Our sequence can't be empty...

How to model that?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# NonEmptyList!

```scala
Either[NonEmptyList[String], User]
```

---

# Aside

When modelling things,

don't let the implementation drive the modelling

---

# Aside

When modelling things,

don't let the implementation drive the modelling

Independently think about what the data should be,

let that drive the implementation

---

# Models drive implementation

Like the bones/architecture

Implementation like the muscles and skin

---

# Models drive implementation

Good modelling guides you towards a correct implementation

Bad modelling guides you away from a correct implementation

---

```
     ____                            _            _
    |  _ \  ___ _ __   ___ _ __   __| | ___ _ __ | |_
    | | | |/ _ \ '_ \ / _ \ '_ \ / _` |/ _ \ '_ \| __|
    | |_| |  __/ |_) |  __/ | | | (_| |  __/ | | | |_
    |____/ \___| .__/ \___|_| |_|\__,_|\___|_| |_|\__|
               |_|

                      __   _____
                      \ \ / / __|
                       \ V /\__ \
                        \_/ |___/

 ___           _                           _            _
|_ _|_ __   __| | ___ _ __   ___ _ __   __| | ___ _ __ | |_
 | || '_ \ / _` |/ _ \ '_ \ / _ \ '_ \ / _` |/ _ \ '_ \| __|
 | || | | | (_| |  __/ |_) |  __/ | | | (_| |  __/ | | | |_
|___|_| |_|\__,_|\___| .__/ \___|_| |_|\__,_|\___|_| |_|\__|
                     |_|
```

---

# Back to our controller

```scala
val userEither: Either[String, User] =
  for {
    name     <- Name.from(input.name)         // Either[String, Name]
    age      <- Age.from(input.age)           // Either[String, Age]
    email    <- Email.from(input.email)       // Either[String, Email]
    password <- Password.from(input.password) // Either[String, Password]
  } yield User(name, age, email, password)    // Either[String, User]
```

What makes a `for` comprehension a true monadic computation?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Non-monadic for

```scala
val userEither: Either[String, User] =
  for {
    name     <- Name.from(input.name)         // Either[String, Name]
    age      <- Age.from(input.age)           // Either[String, Age]
    email    <- Email.from(input.email)       // Either[String, Email]
    password <- Password.from(input.password) // Either[String, Password]
  } yield User(name, age, email, password)    // Either[String, User]
```

> What makes a `for` comprehension a true monadic computation?

Each step depends on the previous step

Can't re-order steps

(But we clearly can here)

---

# User experience

```scala
val userEither: Either[String, User] =
  for {
    name     <- Name.from(input.name)         // Either[String, Name]
    age      <- Age.from(input.age)           // Either[String, Age]
    email    <- Email.from(input.email)       // Either[String, Email]
    password <- Password.from(input.password) // Either[String, Password]
  } yield User(name, age, email, password)    // Either[String, User]
```

Suppose the FE submitted a bad name and a bad password

What would the service return to the FE?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# User experience

```scala
val userEither: Either[String, User] =
  for {
    name     <- Name.from(input.name)      // <--- short circuits
    age      <- Age.from(input.age)
    email    <- Email.from(input.email)
    password <- Password.from(input.password)
  } yield User(name, age, email, password)
```

> Suppose the FE submitted a bad name and a bad password
>
> What would the service return to the FE?

Just the Name error

Age, email and password are never validated

---

# Fix and try again

```scala
val userEither: Either[String, User] =
  for {
    name     <- Name.from(input.name)
    age      <- Age.from(input.age)
    email    <- Email.from(input.email)
    password <- Password.from(input.password) // <--- fails
  } yield User(name, age, email, password)
```

User fixes the name and resubmits

Service returns a password error

---

# Bad user experience

The user has to fix issues one at a time,

resubmitting after each fix

---

# Independent validation

Would be nice if the service _independently_ validated all the fields,

and sent back all the errors in one go

---

# Monadic computation recap

```scala
for {
  a <- getA
  b <- getB(a)
  c <- getC(b)
  d <- getD(c)
} yield d
```

Each step depends on the previous

Short-circuiting makes sense, e.g. if `b` fails, then you can't get `c` or `d`

---

# Validation

Often you're sent independent pieces of data to validate

Validation should be done in "parallel" or "independently"

---

# Analogous to Futures

```scala
// Sequential
for {
  a <- futA
  b <- futB
} yield (a, b)
```

Want to start them in "parallel" or "independently"

---

# Ultimate cookie question

Think hard back to what we've learnt...

---

# Combining parts

Assume we've validated each part independently

```scala
type Errors = NonEmptyList[String]

val nameEither: Either[Errors, Name] = Name.from(input.name)

val ageEither: Either[Errors, Age] = Age.from(input.age)

val emailEither: Either[Errors, Email] = Email.from(input.email)

val passwordEither: Either[Errors, Password] = Password.from(input.password)

// Somehow combine them into:

val userEither: Either[Errors, User] = ???
```

---

# Combining parts

```scala
Either[Errors, Name]
Either[Errors, Age]
Either[Errors, Email]
Either[Errors, Password]

// Logic to combine them if all successful
case (name, age, email, password) => User(name, age, email, password)

Either[Errors, User]
```

If all 4 inputs are happy, we'll get `Right(user)`

If at least one input is unhappy, we'll get `Left(errors)`

What tool does this?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# mapN

```scala
type Errors = NonEmptyList[String]

val nameEither: Either[Errors, Name] = Name.fromString(input.name)
val ageEither: Either[Errors, Age] = Age.fromString(input.age)
val emailEither: Either[Errors, Email] = Email.fromString(input.email)
val passwordEither: Either[Errors, Password] = Password.fromString(input.password)

val userEither: Either[Errors, Password] = (nameEither, ageEither, emailEither, passwordEither).mapN {
  case (name, age, email, password) => User(name, age, email, password)
}
```

---

# Upsized cookie

For extra cookie points, which typeclass powers `mapN`?

```
A: Functor                  C: Traverse


B: Applicative              D: Show
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Applicative!

> For extra cookie points, which typeclass powers `mapN`?

```
A: Functor                  C: Traverse


B: Applicative              D: Show
   ^^^^^^^^^^^
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

That was a lot of big concepts!

---

# Many old friends visiting

- `for`


- `Either`


- strong types


- `mapN/Applicative`


- `NonEmptyList`


- `Monoid`

All starting to come together...

---

# Option for validation?

Option is good when there's only one obvious cause of failure

"Computer says no"

---

# Either?

Either can do everything Option can do,

and also capture an error

---

# Multiple errors

`Either[String, User]` can't model multiple errors

---

# Modelling with Seq?

`Either[Seq[String], User]` is better, but allows `Left(Seq.empty)`

---

# NonEmptyList!

`Either[NonEmptyList[String], User]`

That guy pops up everywhere!

---

# Independent Validation

Validation of multiple fields is _independent_

---

# Short-circuiting for

> Validation of multiple fields is _independent_

Don't use `for`

`for` represents dependent computations so it short-circuits

---

# Bad UX

> `for` is powered by `flatMap` which short-circuits

Users get notified of errors one by one

---

# mapN

> Validation of multiple fields is _independent_

Validate each field independently

Then combine with `mapN`

---

# Dependent vs Independent

When you hear:

- "sequential/dependent" - think "flatMap/monad/for"


- "parallel/independent" - think "mapN/applicative"

---

# Next time

Either -> Validated

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
