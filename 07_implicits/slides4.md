---
author: Rohan
date: 2021-12-10
title: Implicit Scope Resolution
---

```
 ___                 _ _      _ _
|_ _|_ __ ___  _ __ | (_) ___(_) |_
 | || '_ ` _ \| '_ \| | |/ __| | __|
 | || | | | | | |_) | | | (__| | |_
|___|_| |_| |_| .__/|_|_|\___|_|\__|
              |_|
 ____
/ ___|  ___ ___  _ __   ___
\___ \ / __/ _ \| '_ \ / _ \
 ___) | (_| (_) | |_) |  __/
|____/ \___\___/| .__/ \___|
                |_|
 ____                 _       _   _
|  _ \ ___  ___  ___ | |_   _| |_(_) ___  _ __
| |_) / _ \/ __|/ _ \| | | | | __| |/ _ \| '_ \
|  _ <  __/\__ \ (_) | | |_| | |_| | (_) | | | |
|_| \_\___||___/\___/|_|\__,_|\__|_|\___/|_| |_|

```

Our compiler works hard searching for values for us

---

# "Implicit Scope Resolution"?

ie. how the compiler looks for implicit values

---

# Recap

Sometimes we rely on the compiler to fill in our code

```scala
def age(implicit name: String): Int = ...

age
```

---

# Rules

There are rules to make this process deterministic and sensible

---

# Already seen a rule

```scala
def age(implicit name: String): Int = ...

implicit val s1: String = "Simon"
implicit val s2: String = "James"

age
```

Won't compile because it's ambiguous

---

# Hierarchy

Some implicit values have higher precendence than others

---

```
 ___       _                 _            _
|_ _|_ __ | |_ _ __ ___   __| |_   _  ___(_)_ __   __ _
 | || '_ \| __| '__/ _ \ / _` | | | |/ __| | '_ \ / _` |
 | || | | | |_| | | (_) | (_| | |_| | (__| | | | | (_| |
|___|_| |_|\__|_|  \___/ \__,_|\__,_|\___|_|_| |_|\__, |
                                                  |___/
                        _
  ___  _   _ _ __    __| | ___ _ __ ___   ___
 / _ \| | | | '__|  / _` |/ _ \ '_ ` _ \ / _ \
| (_) | |_| | |    | (_| |  __/ | | | | | (_) |
 \___/ \__,_|_|     \__,_|\___|_| |_| |_|\___/

```

---

# Today

Use an sbt project to explore resolution for:

- implicit parameters


- implicit conversions

(and a few bits and pieces)

---

# High level

Just to get the general gist

No point getting too deeply technical

---

```
 ___                 _ _      _ _
|_ _|_ __ ___  _ __ | (_) ___(_) |_
 | || '_ ` _ \| '_ \| | |/ __| | __|
 | || | | | | | |_) | | | (__| | |_
|___|_| |_| |_| .__/|_|_|\___|_|\__|
              |_|
 ____                                _
|  _ \ __ _ _ __ __ _ _ __ ___   ___| |_ ___ _ __ ___
| |_) / _` | '__/ _` | '_ ` _ \ / _ \ __/ _ \ '__/ __|
|  __/ (_| | | | (_| | | | | | |  __/ ||  __/ |  \__ \
|_|   \__,_|_|  \__,_|_| |_| |_|\___|\__\___|_|  |___/

```

---

# Implicit parameters!

```scala
doSomething   // <--- how does the compiler search?

def doSomething(implicit name: Name): Unit = ...
```

To the project!

---

# Summary of implicit parameters

Where does the compiler look?

- imports


- locally


- companion object of the type it's searching for

(and maybe elsewhere)

---

# Precedence?

"Specific" definitions override "background" definitions (e.g. companion object)

Can't have multiple "specific" definitions

(and there may be other rules)

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

---

# Scope resolution?

A bit different and fairly complex

---

# Repurpose the demo

```diff
+case class CompoundName(first: String, second: String)

-doSomething
+doSomething(CompoundName("Bobanita", "Hayworth"))

-def doSomething(implicit name: Name)
+def doSomething(name: Name): Unit = ...
```

Explicilty pass data

But the data is the wrong type

---

# Conversion logic

```scala
doSomething(CompoundName("Bobanita", "Hayworth"))

case class CompoundName(first: String, second: String)
//             |
//             | ???
//             |
//            \|/
case class Name(value: String)

def doSomething(name: Name): Unit = ...

// Can just concatenate first and second name
implicit def compoundName2Name(value: CompoundName): Name =
  new Name(s"${value.first} ${value.second}")
```

---

# Demo time

```scala
implicit def compoundName2Name(value: CompoundName): Name =
  new Name(s"${value.first} ${value.second}")
```

We'll define logic like this at various locations

See how the compiler treats them

To the editor!

---

# Findings

Complex precedence:

- local


- class level


- explicit import


- wildcard import


- companion objects of types involved (`Name`, `CompoundName`)

---

# Plus some quirky things

e.g. ambiguous wildcard imports cause companion objects to be used instead

---

# Simplification

Just a simple demo to get the general idea across

The formal rules are a bit more involved

---

# Now for leftover bits

---

```
 _                 _ _      _ _   _
(_)_ __ ___  _ __ | (_) ___(_) |_| |_   _
| | '_ ` _ \| '_ \| | |/ __| | __| | | | |
| | | | | | | |_) | | | (__| | |_| | |_| |
|_|_| |_| |_| .__/|_|_|\___|_|\__|_|\__, |
            |_|                     |___/
```

Summoning implicit values

---

# Example

Sorting a list

```scala
List(3, 5, 2, 1).sorted
```

---

# How does it know?

How does a generic type like `List[A]` magically know how to sort integers?

```scala
List(3, 5, 2, 1).sorted
```

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Implicit magic

> How does a generic type like `List[A]` magically know how to sort integers?

```scala
List(3, 5, 2, 1).sorted
// is really
List(3, 5, 2, 1).sorted(ordering)
```

There's an implicit parameter telling it how to order integers

---

# Ordering

List uses `Ordering` to sort:

```scala
trait List[A] {
  ...
  def sorted(implicit ordering: Ordering[A]): List[A] = ...
  ...
}

trait Ordering[A] {
 /** Returns an integer whose sign communicates how x compares to y.
   *
   * The result sign has the following meaning:
   *
   *  - negative if x < y
   *  - positive if x > y
   *  - zero otherwise (if x == y)
   */
  def compare(x: A, y: A): Int

  ...
}
```

---

# From the standard library

```scala
List(3, 5, 2, 1).sorted
// becomes
List(3, 5, 2, 1).sorted(ordering)
```

The compiler found an implicit `Ordering[Int]`

somewhere in the standard library

---

# Scenario

> The compiler found an implicit `Ordering[Int]`
>
> somewhere in the standard library

I want to use it

e.g. I have two integers I need to compare

Or investigate it on the repl

---

# Using compare

> e.g. I have two integers I need to compare

```scala
def compareInts(x: Int, y: Int): Int = {
  val ordering: Ordering[Int] = ... // Get from somewhere
  ordering.compare(x, y)
}

trait Ordering[A] {
 /** Returns an integer whose sign communicates how x compares to y.
   *
   * The result sign has the following meaning:
   *
   *  - negative if x < y
   *  - positive if x > y
   *  - zero otherwise (if x == y)
   */
  def compare(x: A, y: A): Int
}
```

---

# "Summoning"

```scala
def compareInts(x: Int, y: Int): Int = {
  val ordering: Ordering[Int] = ... // Get from somewhere
  //                            ^^^
  ordering.compare(x, y)
}
```

How do I get a reference to the one in implicit scope?

Any ideas?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Demo time

To the repl!

---

# Summoning with `implicitly`

```scala
def implicitly[T](implicit t: T): T = t
```

---

```
 ____            _
/ ___|  ___ __ _| | __ _
\___ \ / __/ _` | |/ _` |
 ___) | (_| (_| | | (_| |
|____/ \___\__,_|_|\__,_|

 _____
|___ /
  |_ \
 ___) |
|____/

```

This training has been for scala 2

---

# Scala 3

Implicits will be overhauled

Learning from mistakes and trying to start again

---

# Scala 3

> Implicits will be overhauled

Will look very different in scala 3

New keywords like `given` and `provided`

---

# Scala 2 vs 3

> Will look very different in scala 3

Some scala 2 concepts might be there,

but just look a bit different

Still worth understanding scala 2's implicits

---

```
 ____                               __
|  _ \ ___  ___ __ _ _ __     ___  / _|
| |_) / _ \/ __/ _` | '_ \   / _ \| |_
|  _ <  __/ (_| (_| | |_) | | (_) |  _|
|_| \_\___|\___\__,_| .__/   \___/|_|
                    |_|
 _            _
| |_ ___   __| | __ _ _   _
| __/ _ \ / _` |/ _` | | | |
| || (_) | (_| | (_| | |_| |
 \__\___/ \__,_|\__,_|\__, |
                      |___/
```

---

# Implicit scope resolution

The compiler doesn't look _everywhere_ to find values

---

# Search pattern

It has sensible predefined places it will look, e.g.

- companion objects of data


- imports


- class level


- locally

---

# Today just high level

It's okay if you don't remember all the details

You'll know where to look when it becomes important

---

# Summoning implicits

Use `implicitly` when you need a handle to an implicit value

---

# Scala 3

Will overhaul implicits

---

# Further reading

Good [SO post "Where does Scala look for implicits?"](https://stackoverflow.com/a/5598107)

---

```
 ____
/ ___| _   _ _ __ ___  _ __ ___   __ _ _ __ _   _
\___ \| | | | '_ ` _ \| '_ ` _ \ / _` | '__| | | |
 ___) | |_| | | | | | | | | | | | (_| | |  | |_| |
|____/ \__,_|_| |_| |_|_| |_| |_|\__,_|_|   \__, |
                                            |___/
```

That's it for implicits

---

# 3 mechanisms

- implicit parameters

```scala
def age(implicit name: String): Int

age
```

- implicit classes

```scala
implicit class StringOps(value: String) {
  ...
}
```

- implicit conversions

```scala
implicit def compoundName2Name(value: CompoundName): Name = ...
```

---

# In practice...

Most likely to come across implicit parameters

e.g. `ExecutionContext`

---

# In practice...

Might find implicit classes here and there

e.g. found this in `docservicemodel`

```scala
implicit class TrimPun(s: String) {
  def trimPunc: String = TextUtil.trimPunc(s)
}
```

---

# In practice...

Will find implicit conversions in the standard library,

but hopefully not in our code

---

# And remember

Don't go crazy with implicits

They can cause head aches

Be conservative: if you're not sure, don't use it

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
