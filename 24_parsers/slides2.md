---
author: Rohan
date: 2023-05-29
title: Unapply Parsing
---

```
 _   _                         _
| | | |_ __   __ _ _ __  _ __ | |_   _
| | | | '_ \ / _` | '_ \| '_ \| | | | |
| |_| | | | | (_| | |_) | |_) | | |_| |
 \___/|_| |_|\__,_| .__/| .__/|_|\__, |
                  |_|   |_|      |___/
 ____                _
|  _ \ __ _ _ __ ___(_)_ __   __ _
| |_) / _` | '__/ __| | '_ \ / _` |
|  __/ (_| | |  \__ \ | | | | (_| |
|_|   \__,_|_|  |___/_|_| |_|\__, |
                             |___/
```

---

# Today

Explore the mysterious `unapply`

---

# Today

Explore the mysterious `unapply`

Understand it's a great fit for parsing

---

# Example 1

```scala
case class Person(name: String, age: Int)

def gimmePerson(person: Person): Unit = person match {
  case Person("Boban", _) => println("No Bobans!")
  case Person(_, 0) => println("No babies!")
  case Person("Bobanita", 26) => println("Yo it's Bobanita")
  case _ => println("Default case")
}
```

---

# Example 2

```scala
case class Person(name: String, age: Int)

def gimmePerson(person: Person): Unit = person match {
  case Person("Boban", _) => println("No Bobans!")
  case Person(_, 0) => println("No babies!")
  case Person("Bobanita", 26) => println("Yo it's Bobanita")
  case _ => println("Default case")
}

def gimmePeople(people: Seq[Person]): Unit = people match {
  case Seq(Person("Boban", _), Person("Bobanita", _)) => println("Yo it's Boban and Bobanita!")
  case Seq(Person(_, 0)) => println("There's a baby all by itself!")
  case _ => println("Default case")
}
```

---

# Some questions to think about?

```scala
case class Person(name: String, age: Int)

def gimmePerson(person: Person): Unit = person match {
  case Person("Boban", _) => println("No Bobans!")
  case Person(_, 0) => println("No babies!")
  case Person("Bobanita", 26) => println("Yo it's Bobanita")
  case _ => println("Default case")
}

def gimmePeople(people: Seq[Person]): Unit = people match {
  case Seq(Person("Boban", _), Person("Bobanita", _)) => println("Yo it's Boban and Bobanita!")
  case Seq(Person(_, 0)) => println("There's a baby all by itself!")
  case _ => println("Default case")
}
```

- how does it magically do that?


- is it just for certain types?


- can I modify it myself?

---

# unapply

---

# Recap

We've actually covered this waaaaaaaaaay back - section 3 (pattern matching), talk 2

---

# Id example

```
anz--20210923--ac423f32
     ^^^^^^^^  ^^^^^^^^
       date      hash

comm-2394222933.basic
     ^^^^^^^^^^ ^^^^^
       code     plan (basic/premium)
```

---

# Parsing!

```scala
def parseAnzId(anzText: String): Option[(LocalDate, String)] = {
  // TODO - validate date before converting
  val format = DateTimeFormatter.ofPattern("yyyyMMdd")
  anzText.split("--", 3) match {
    case Array("anz", dateStr, hash) if hash.matches("[a-f0-9]{8}") => Some((LocalDate.parse(dateStr, format), hash))
    case _ => None
  }
}

def parseCommbankId(commbankText: String): Option[(String, Plan)] = {
  commbankText match {
    case s"commbank-$code.$planStr" if code.matches("\\d{10}") =>
      val planPF: PartialFunction[String, Plan] = {
        case "basic" => Basic
        case "premium" => Premium
      }
      planPF.lift(planStr).map { plan =>
        (code, plan)
      }
    case _ => None
  }
}

parseAnzId("anz--20210923--ac423f32")
// Some(value = (2021-09-23, "ac423f32"))

parseCommbankId("commbank-2394222933.basic")
// Some(value = ("2394222933", Basic))

parseCommbankId("yo")
// None
```

---

# General pattern

Parser:

```scala
String => Option[...]

// E.g.
def parseAnzId(anzText: String): Option[(LocalDate, String)] = ...
def parseCommbankId(commbankText: String): Option[(String, Plan)] = ...
```

---

# Example use case

> lookup user by id
>
> if id is anz, lookup using anz api
>
> else if id is commbank, lookup using commbank api
>
> else ...

To the repl!

---

# Summary

```scala
object AnzId {
  def unapply(text: String): Option[(LocalDate, String)] = parseAnzId(text)
}

object CommbankId {
  def unapply(text: String): Option[(String, Plan)] = parseCommbankId(text)
}

def doStuff(id: String): Unit = id match {
  case AnzId(date, hash) => println(s"Got anz id with date: $date and hash: $hash")
  case CommbankId(code, plan) => println(s"Got commbank id with code: $code and plan: $plan")
  case _ => println(s"Default case: $id")
}

doStuff("anz--20210923--ac423f32")
// Got anz id with date: 2021-09-23 and hash: ac423f32

doStuff("commbank-2394222933.basic")
// Got commbank id with code: 2394222933 and plan: Basic

doStuff("bro")
// Default case: bro
```

---

# Model change

Suppose we introduce a stronger type to model id's

---

# Model change

Suppose we introduce a stronger type to model id's

For now, let's pretend we don't have case classes

---

```scala
object AnzIdParse {
  def unapply(text: String): Option[AnzId] = parseAnzId(text)
}

object CommbankIdParse {
  def unapply(text: String): Option[CommbankId] = parseCommbankId(text)
}

class AnzId(val date: LocalDate, val hash: String)
object AnzId {
  def unapply(anzId: AnzId): Option[(LocalDate, String)] = Some((anzId.date, anzId.hash))
}

class CommbankId(val code: String, val plan: Plan)
object CommbankId {
  def unapply(commbankId: CommbankId): Option[(String, Plan)] = Some((commbankId.code, commbankId.plan))
}

def doStuff(id: String): Unit = id match {
  case AnzIdParse(AnzId(date, hash)) => println(s"Got anz id with date: $date and hash: $hash")
  case CommbankIdParse(CommbankId(code, plan)) => println(s"Got commbank id with code: $code and plan: $plan")
  case _ => println(s"Default case: $id")
}
```

---

# Fixing those classes

To the repl!

---

# Summary

```scala
case class AnzId(date: LocalDate, hash: String)
case class CommbankId(code: String, plan: Plan)

// No need to explicitly define companion objects, compiler does this for us

def doStuff(id: String): Unit = id match {
  case AnzIdParse(AnzId(date, hash)) => println(s"Got anz id with date: $date and hash: $hash")
  case CommbankIdParse(CommbankId(code, plan)) => println(s"Got commbank id with code: $code and plan: $plan")
  case _ => println(s"Default case: $id")
}
```

Case classes have built in extractors (and apply methods)

---

# That annoying date validation

Recall:

```scala
parseAnzId("anz--20210950--ac423f32")
//                     ^^
// java.time.format.DateTimeParseException:
//     Text '20210950' could not be parsed:
//          Invalid value for DayOfMonth: 50
```

Was expecting `None`

---

# That annoying date validation

Recall:

```scala
parseAnzId("anz--20210950--ac423f32")
//                     ^^
// java.time.format.DateTimeParseException:
//     Text '20210950' could not be parsed:
//          Invalid value for DayOfMonth: 50
```

Was expecting `None`

---

# Problem

Problem was there wasn't a nice `Boolean` check in the api

```scala
def parseAnzId(anzText: String): Option[AnzId] = {
  // TODO - validate date before converting
  val format = DateTimeFormatter.ofPattern("yyyyMMdd")
  anzText.split("--", 3) match {
    case Array("anz", dateStr, hash) if hash.matches("[a-f0-9]{8}") && format.matches(dateStr) => ...
    //                                                                 ^^^^^^^^^^^^^^^^^^^^^^^ ???
    case _ => None
  }
}
```

You can "try and see what happens"

---

# Solve with extractor

To the repl!

---

# Summary

```scala
object LocalDateParse {
  val compactLocalDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
  def unapply(text: String): Option[LocalDate] = Try(LocalDate.parse(text, compactLocalDateFormat)).toOption
}

def parseAnzId(anzText: String): Option[AnzId] = {
  anzText.split("--", 3) match {
    case Array("anz", LocalDateParse(date), hash) if hash.matches("[a-f0-9]{8}") => Some(AnzId(date, hash))
    case _ => None
  }
}
```

Our extractor transforms the parsing to: `String => Option[LocalDate]`

It can be used seemlessly with the pattern match

Avoids "check then parse" duplication

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

# Extractors

The language provides a mechanism to extend pattern matching syntax (`unapply`)

---

# Extractors and Parsing

The essence of a parser: `String => Option[...]`

That fits beautifully with `unapply`

---

# The point

If you capture parsing logic as an extractor,

you can write very slick code

---

# Next time

Regexes

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
