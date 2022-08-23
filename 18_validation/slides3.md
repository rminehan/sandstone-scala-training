---
author: Rohan
date: 2022-09-20
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

# Today

Write validators that spit out strong types

```scala
def validateName(name: String): ValidatedNel[E, Name] = ...
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
case class Natural private(value: Int)

object Natural {
  def fromInt(value: Int): Option[Natural] = if (value >= 0) Some(new Natural(value)) else None
  private def apply(value: Int): Natural = new Natural(value)
}

// Fails to compile
new Natural(-1)

// Fails to compile
Natural.apply(-1)
```

Close off any back doors that sneak in bad data

---

# Note

We would use something more streamlined and performant in our real code

---

# For today

Just use as placeholders

We'll pretend they're secure

```scala
case class Natural(value: Int)
```

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
case class Natural(value: Int)

object Natural {
  def fromInt(value: Int): Option[Natural] = ...
  //                       ^^^^^^
}
```

Currently the validation was represented by an Option

---

# Recall

`Option` makes sense when it's obvious why a failure happened

---

# Recall

`Option` makes sense when it's obvious why a failure happened

For complex validation we'll use `ValidatedNel[String, A]` to describe what went wrong

---

```

```
