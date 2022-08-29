---
author: Pranohan
date: 2022-08-30
title: DIY Strong Types
---

```
 ____ _____   __
|  _ \_ _\ \ / /
| | | | | \ V /
| |_| | |  | |
|____/___| |_|

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

# Today

Sometimes we need a specific strong type

---

# Today

Sometimes we need a specific strong type

Nothing in our libraries does the job

---

# Today

Sometimes we need a specific strong type

Nothing in our libraries does the job

Time to roll up the sleeves and DIY it

---

# Agenda

- considerations


- introducing a framework


- examples


- how it works

----

```
  ____                _     _                _   _
 / ___|___  _ __  ___(_) __| | ___ _ __ __ _| |_(_) ___  _ __  ___
| |   / _ \| '_ \/ __| |/ _` |/ _ \ '__/ _` | __| |/ _ \| '_ \/ __|
| |__| (_) | | | \__ \ | (_| |  __/ | | (_| | |_| | (_) | | | \__ \
 \____\___/|_| |_|___/_|\__,_|\___|_|  \__,_|\__|_|\___/|_| |_|___/

```

---

# Example

Natural

```
                                     Natural
                           ------------------------->
 ...   -5  -4  -3  -2  -1  0  +1  +2  +3  +4  +5 ...
 <-------------------------------------------------->
                        Int
```

---

# Natural

0, 1, 2, ..., Int.MaxValue

Use cases:

- counters


- age


- safe mathematical operations (e.g. sqrt)

---

# Example Approach

Use a "wrapper" approach

Wrap around a regular `Int` and limit it

To the repl!

---

# Summary

```scala
case class Natural(value: Int)

def safeSqrt(natural: Natural): Double = math.sqrt(nat.value)
```

---

# Problems?

```scala
case class Natural(value: Int)

def safeSqrt(natural: Natural): Double = math.sqrt(nat.value)
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Useless

```scala
case class Natural(value: Int)

def safeSqrt(natural: Natural): Double = math.sqrt(nat.value)
```

Nothing actually enforces the value is non-negative

```scala
val naughtyNatural = Natural(-1)

safeSqrt(naughtyNatural) // NaN
```

---

# Add fromInt method

To the repl!

---

# Summary

```scala
case class Natural(value: Int)

object Natural {
  def fromInt(value: Int): Option[Natural] = if (value >= 0) Some(Natural(value)) else None
}

Natural.fromInt(3)
// Some(Natural(3))

Natural.fromInt(-1)
// None
```

---

# Problem?

```scala
case class Natural(value: Int)

object Natural {
  def fromInt(value: Int): Option[Natural] = if (value >= 0) Some(Natural(value)) else None
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

# Still insecure

```scala
case class Natural(value: Int)

object Natural {
  def fromInt(value: Int): Option[Natural] = if (value >= 0) Some(Natural(value)) else None
}
```

Can still just do this:

```scala
val naughtyNatural = Natural(-1)
safeSqrt(naughtyNatural) // NaN

val newNaughtyNatural = new Natural(-2)
safeSqrt(newNaughtyNatural) // NaN
```

---

# Night Club Analogy

Imagine Natural as a night club:

```
          --------------
         |              | fromInt
         |              | bouncer
         |              | <------  3 (allowed)
         |              | <------ -1 (rejected)
         |              |
          --------------
```

We want everyone to come in through the front door where the bouncer is

---

# Back doors!

There's two back doors

```
          --------------
         |              |
-2 -->  new             | bouncer
         |              | <------  3 (allowed)
-1 --> apply            |
         |              |
          --------------
```

Adding a front door doesn't remove the back doors

---

# Fixing it?

There are ways you can close off those back doors

Not the focus of today

Point is that you need to consider security

---

# Interop

---

# Interop

A `Natural` is an `Int` (conceptually)

```
                                     Natural
                           ------------------------->
 ...   -5  -4  -3  -2  -1  0  +1  +2  +3  +4  +5 ...
 <-------------------------------------------------->
                        Int
```

ie. `Natural` is stronger than `Int`

---

# Parent / Child

> A `Natural` is an `Int` (conceptually)

Analogy: `Natural` is like a child of `Int`

---

# Interop

Anything that expects an `Int`, should accept a `Natural`

```scala
def gimmeInt(int: Int): Unit = ...
gimmeInt(natural)      // Compiles?
```

---

# Wrapper approach?

```scala
case class Natural(value: Int)

def gimmeInt(int: Int): Unit = ...
gimmeInt(natural)            // Doesn't compile
```

---

# Wrapper approach

```scala
case class Natural(value: Int)

def gimmeInt(int: Int): Unit = ...
gimmeInt(natural)            // Doesn't compile
gimmeInt(natural.value)      // Compiles
```

Not completely smooth, but workable

---

# Member methods

```scala
int.toFloat

natural.toFloat // compiles?
```

---

# Member methods

```scala
case class Natural(value: Int)

natural.toFloat       // doesn't compile
natural.value.toFloat // compiles, but a little icky
```

---

# Performance

---

# Wrapper overhead

```scala
case class Natural(value: Int)
//         class          primitive
```

---

# Boxing

Wrapper classes incur the same kind of overhead as boxing

---

# Aside: AnyVal

```scala
case class Natural(value: Int) extends AnyVal
//         class          primitive
```

Can avoid this overhead in some situations

Not for today!

---

# Summary

We've seen potential issues you can hit making with DIY strong types

---

# Summary

We've seen potential issues you can hit making with DIY strong types

- security


- interop


- performance

---

# Security

When making a strong type,

you want to prevent accidental mistakes

```scala
def processMetric(value: Int): Double = {
  ...
  val root = safeSqrt(Natural(value))  // <--- accidental mistake
  ...
}

def safeSqrt(natural: Natural): Double = ...
```

---

# Interop

It's nice when strong types work like their weak types

---

# Performance

Often strong types introduce overhead

---

```
  ___
 / _ \ _   _ _ __
| | | | | | | '__|
| |_| | |_| | |
 \___/ \__,_|_|

 _____                                            _
|  ___| __ __ _ _ __ ___   _____      _____  _ __| | __
| |_ | '__/ _` | '_ ` _ \ / _ \ \ /\ / / _ \| '__| |/ /
|  _|| | | (_| | | | | | |  __/\ V  V / (_) | |  |   <
|_|  |_|  \__,_|_| |_| |_|\___| \_/\_/ \___/|_|  |_|\_\
```

---

# strongtypes

New library added to `docserviceparent`

Makes it easy to create your own strong type

To intellij!

---

# Natural?

What would `Natural` look like using our framework?

To the repl!

---

# Summary

```scala
object Natural extends Strong[Int] {
  def validate(value: Int): Boolean = value >= 0
}

// Defines Natural.Type (our strong type)

Natural.from(3)  // Some(3)
Natural.from(-1) // None

Natural.fromUnsafe(7)  // 7
Natural.fromUnsafe(-3) // throws exception

def safeSqrt(natural: Natural): Double = math.sqrt(natural)
gimmeInt(natural) // compiles
```

---

# DIY

When making a strong type, what are the crucial ingredients?

```scala
object Natural extends Strong[Int] {
  def validate(value: Int): Boolean = value >= 0
}

object NonEmptyString extends Strong[String] {
  def validate(value: String): Boolean = value.nonEmpty
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

# Ingredients

```scala
//     name                   weak type
object Natural extends Strong[Int] {
  def validate(value: Int): Boolean = value >= 0
  //                                  validation logic
}

//     name                          weak type
object NonEmptyString extends Strong[String] {
  def validate(value: String): Boolean = value.nonEmpty
  //                                     validation logic
}
```

---

# Framework

## Developer supplies

- name for the strong type


- weak type


- validation logic: `Weak => Boolean`

## Framework supplies:

- `Type`


- `from`


- `fromUnsafe`

---

# Introducing `Strong`

Our mini-framework

```scala
trait Strong[Weak] {
  ...
  def validate(value: Weak): Boolean
}

//     name                   weak
object Natural extends Strong[Int] {
  def validate(value: Int): Boolean = value >= 0
  //                                  validation
}
```

---

# More examples!

Back to the repl!

`NonEmptyString` and `Name`

---

# Summary

```scala
trait Strong[Weak] {
  ...
}

type NonEmptyString = NonEmptyString.Value
object NonEmptyString extends Strong[String] {
  def validate(s: String): Boolean = s.nonEmpty
}

def capitalize(s: NonEmptyString): NonEmptyString = NonEmptyString.fromUnsafe(s.head.toString.toUpperCase + s.tail)

type Name = Name.Value
object Name extends Strong[String] {
  def validate(s: String): Boolean = {
    val trimmed = s.trim
    s == trimmed && s.nonEmpty
  }
}
```

---

# Considerations

## Wrapper approach

- secure? (no, needs work)


- interop? (no)


- performant? (no)

## Our mini-framework

- secure? (yep*)


- interop? (yep!)


- performant? (yep!)

---

# Design Notes

Framework is simple and depends only on cats

Designed to minimise boilerplate

---

# strongtypes library recap

Contains:

- `Strong` trait


- some very common strong types

---

# Adding a strong type?

Where do I put it?

---

# Adding a strong type?

> Where do I put it?

If it's general purpose: add to strongtypes library `common` package

If it's specific to my service: add it there

---

# Note

The framework demo'd today is a simplified version of what's in our library

It uses some validation concepts we'll cover later

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

How does `Strong` work?

---

# Usual disclaimer

Extra knowledge, but not essential for using `Strong`

---

# Tagging

Analogy: tagging a fish

---

# Differentiating

Demo time!

---

# Summary

```scala
Int

Int with Tag
```

Allows the compiler to differentiate between them

---

# Alias

```scala
Int

type Natural = Int with Tag
```

---

# Type safety

```scala
Int

type Natural = Int with Tag  // Still an `Int`, has extra bits

def gimmeInt(int: Int): Unit = ...
gimmeInt(natural) // compiles

def gimmeNatural(natural: Natural): Unit = ...
gimmeNatural(3) // won't compile
```

---

# Compile time vs Runtime

We want:

- `Tag` just exists for the compiler


- disappears at runtime (leaving just `Int`)

---

# trait

Make the tag an abstract trait with no members

```scala
trait Tag
type Natural = Int with Tag
```

---

# trait

> Make the tag an abstract trait with no members

```scala
trait Tag
type Natural = Int with Tag
```

From the compiler's perspective: `Int` and `Int with Tag` are different types

At runtime, `Int with Tag` is just `Int`

---

# Performance

> At runtime, `Int with Tag` is just `Int`

There is no wrapper overhead

---

# Security

Understanding why we seal the tag

To the repl!

---

# Summary

```scala
// Legacy model
case class Person(name: String, age: Int)

type StrongPerson = StrongPerson.Type
object StrongPerson extends Strong[Person] {
  def validate(person: Person): Boolean = person.name.nonEmpty && person.age >= 18
}

// Without `sealed`, this will compile.
// From the compiler's perspective it's a StrongPerson because it's been tagged.
// It would be able to sneak into places only StrongPerson's are meant to be allowed.
object HackerPerson extends Person("", -1) with StrongPerson.Tag
```

Marking the `Tag` as sealed closes off a security vulnerability

It's just to be extra safe, developers wouldn't accidentally do this

---

# Aside

There is a way to hack our framework using casting

(see strong_type_framework.sc)

That won't happen "accidentally" though

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

# DIY

There are times we need to create our own strong types from weak types

---

# Techniques

- wrappers


- tagging approach

---

# Considerations

- security


- interop


- performance

---

# strongtypes library

We can start using this in our projects

---

# Next time

Strong type examples

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
