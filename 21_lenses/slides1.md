---
author: Rohan
date: 2022-11-14
title: Lenses
---

```
 _
| |    ___ _ __  ___  ___  ___
| |   / _ \ '_ \/ __|/ _ \/ __|
| |__|  __/ | | \__ \  __/\__ \
|_____\___|_| |_|___/\___||___/
```

---

# Lenses

A tool for updating deeply nested immutable data structures

---

# Recap

"Update" = make a modified copy

To the repl!

---

# Visualising the structure

```
                 User

          /       |       \

     name      address     age

              /       \

          number     street

                    /      \

                 name     streetType
```

Let's try and change the streetType

To the repl!

---

# Recap

```
                 User

          /       |       \

     name      address     age

              /       \

          number     street

                    /      \

                 name     streetType
```

```scala
user.copy(address =
  user.address.copy(street =
    user.address.street.copy(streetType =
      "Avenue"
    )
  )
)
```

Changing something deep in the tree causes an ugly ripple up the tree

---

# Introducing lenses

To the repl!

---

# Summary

```scala
import $ivy.`dev.optics::monocle-core:3.1.0`
import $ivy.`dev.optics::monocle-macro:3.1.0`

import monocle.syntax.all._

user.focus(_.address.street.streetType).replace("Avenue")
user.focus(_.address.number).modify(_ + 1)
```

---

# Recap

Lenses make it much easier to update complex immutable objects

---

# Introducing sequences

Life gets harder when sequences are involved

To the repl!

---

# Example: uppercase all filenames

```scala
case class LinkedDoc(id: String, filename: String)
case class Requirement(id: String, linkedDocs: List[LinkedDoc])
case class Job(id: String, requirements: List[Requirement])
```

## Traditional way

```scala
job.copy(requirements = job.requirements.map(
  req => req.copy(linkedDocs = req.linkedDocs.map(
    linkedDoc => linkedDoc.copy(filename =
      linkedDoc.filename.toUpperCase)
  ))
))
```

# Lens approach

```scala
import monocle.Focus

// Define these once in your companion objects
val requirements = Focus[Job](_.requirements)
val linkedDocs = Focus[Requirement](_.linkedDocs)
val filename = Focus[LinkedDoc](_.filename)
val id = Focus[LinkedDoc](_.id)

requirements.each.andThen(linkedDocs).each.andThen(filename).modify(_.toUpperCase)(job)
```

---

# Other examples

- uppercase filenames for docs with id "doc2"


- uppercase filenames on the 1'th doc for each requirement (if it exists)


- uppercase filenames on the 2'th requirement for the 0'th doc (if it exists)


- shorten each requirement to have just one linked doc


- uppercase filenames on all linked docs after the first one


- test if a job has at least one requirement linked to doc2

To the repl!

---

# Summary

```scala
// uppercase filenames for docs with id "doc2"
requirements.each.andThen(linkedDocs).each.filter(_.id == "doc2").andThen(filename).modify(_.toUpperCase)

// uppercase filenames on the 1'th doc for each requirement (if it exists)
requirements.each.andThen(linkedDocs).index(1).andThen(filename).modify(_.toUpperCase)

// shorten each requirement to have just one linked doc
requirements.index(2).andThen(linkedDocs).index(0).andThen(filename).modify(_.toUpperCase)

// shorten each requirement to have just one linked doc
requirements.each.andThen(linkedDocs).modify(_.take(1))

// uppercase filenames on all linked docs after the first one
requirements.each.andThen(linkedDocs).filterIndex((i: Int) => i >= 1).andThen(filename).modify(_.toUpperCase)

// test if a job has at least one requirement linked to doc2
requirements.each.andThen(linkedDocs).each.andThen(id).exist(_ == "doc2")
```

Each of the above generates a function which you can apply to a job

---

# Do you have any examples?

> uppercase filenames on the 2'th requirement for the 0'th doc (if it exists)

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# One-dollar-ionairre

How excited are you about lenses?

```
(A) It satisfies requirements             (C) Very excited


(B) Quite excited                         (D) Most exciting thing this week
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# One-dollar-ionairre

> How excited are you about lenses?

```
(A) It satisfies requirements             (C) Very excited


(B) Quite excited                         (D) Most exciting thing this week
                                              ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
```

---

# Summary

Lenses make it much easier to update complex data structures

They are like an xpath for your data

---

# Abstraction

Paths through our objects become first class citizens that can be reused (ie. a Lens)

---

# Mutable vs Immutable

Lenses allow us to use immutable objects,

but update them via a direct path as if they were mutable

It lets us go even further and make many alterations simultaneously

---

# New library

As usual, weigh up the pro's and con's of introducing a new library and new concepts to the team

(And you don't have to use monocle)

---

# Note: Seq vs List

To get monacle to work, I had to use `List` in my examples

`Seq` doesn't support those operations (not sure why)

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
