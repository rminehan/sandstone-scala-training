---
author: Rohan
date: 2021-12-17
title: Operators
---

```
  ___                       _
 / _ \ _ __   ___ _ __ __ _| |_ ___  _ __ ___
| | | | '_ \ / _ \ '__/ _` | __/ _ \| '__/ __|
| |_| | |_) |  __/ | | (_| | || (_) | |  \__ \
 \___/| .__/ \___|_|  \__,_|\__\___/|_|  |___/
      |_|
```

Advanced scala syntax

---

# Today

Demystify scala's operator syntax

```scala
val thing = x |+| y

val thingy = x :: y
```

---

# Agenda

- it's all just functions


- colon operations


- not going crazy

---

```
 ___ _   _             _ _
|_ _| |_( )___    __ _| | |
 | || __|// __|  / _` | | |
 | || |_  \__ \ | (_| | | |
|___|\__| |___/  \__,_|_|_|

   _           _
  (_)_   _ ___| |_
  | | | | / __| __|
  | | |_| \__ \ |_
 _/ |\__,_|___/\__|
|__/
  __                  _   _
 / _|_   _ _ __   ___| |_(_) ___  _ __  ___
| |_| | | | '_ \ / __| __| |/ _ \| '_ \/ __|
|  _| |_| | | | | (__| |_| | (_) | | | \__ \
|_|  \__,_|_| |_|\___|\__|_|\___/|_| |_|___/

```

---

# Traditional languages

C++ java python

Clear divide between "operators" and "functions"

```
    Operators         |          Functions
===================================================
                      |
      +               |
                      |       doSomething()
      -               |
                      |        println
      +=              |
                      |
      *               |
                      |
      >>              |
```

---

# Predefined operators

Languages usually have predefined lists of operators

e.g. C++

- `+`
- `++`
- `+=`
- `-`
- `--`
- `-=`
- `>`
- `>>`
- ...

with special syntax to override them for your type:

```c++
// Okay
void operator ++ (int) {
    ...
}

// Not okay
void operator |+| (int) {
    ...
}
```

---

# Scala

Doesn't have this clear distinction

Operators are just functions with funny names

Demo time - to the repl!

---

# Summary

We use the same syntax to define "operator" style functions as regular functions

```scala
def funnyAdd(s: String, i: Int): Int = ...

def |+|(s: String, i: Int): Int = ...
```

---

# Not limited

Not limited to some predefined set of operators

```scala
def <|<||*^^^^*||>|>(i: Int): Int = i + 1
```

---

# Member methods on classes

Add `+` to `Meter`

Show off infix notation

To the repl!

---

# Summary

Can define operator member functions

```scala
case class Meter(value: Double) {
  def plus(other: Meter): Meter = Meter(this.value + other.value)
  def +(other: Meter): Meter = plus(other)
}
```

and use the "operator/infix" syntax

```scala
Meter(3).plus(Meter(4))
Meter(3) plus Meter(4)

Meter(3).+(Meter(4))
Meter(3) + Meter(4)
```

---

# Recapping so far

Scala doesn't have the typical sharp distinction between "methods" and "operators"

Operators are just methods with funny names

---

```
  ____      _
 / ___|___ | | ___  _ __
| |   / _ \| |/ _ \| '_ \
| |__| (_) | | (_) | | | |
 \____\___/|_|\___/|_| |_|

  ___                       _   _
 / _ \ _ __   ___ _ __ __ _| |_(_) ___  _ __  ___
| | | | '_ \ / _ \ '__/ _` | __| |/ _ \| '_ \/ __|
| |_| | |_) |  __/ | | (_| | |_| | (_) | | | \__ \
 \___/| .__/ \___|_|  \__,_|\__|_|\___/|_| |_|___/
      |_|
```

---

# Look at this List code

```scala
val list = List(1, 2, 3)

val newList = 0 :: List(1, 2, 3)
```

Test it on the repl

---

# Pop Quiz

Who's doing the real work here? Where's the logic located?

```scala
val list = List(1, 2, 3)

val newList = 0 :: List(1, 2, 3)  // List(0, 1, 2, 3)
```

Multi-choice:

- A: The Int is appending the List


- B: The List is prepending the Int

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Syntactically

```scala
val newList = 0 :: List(1, 2, 3)  // List(0, 1, 2, 3)
```

> A: The Int is appending the List
>
> B: The List is prepending the Int

Looks like the `Int` is doing it (because it's on the left)

```scala
0.::(List(1, 2, 3))
```

Syntax is suggesting `A`

---

# Conceptually

```scala
val newList = 0 :: List(1, 2, 3)  // List(0, 1, 2, 3)
```

> A: The Int is appending the List
>
> B: The List is prepending the Int

`A` sounds crazy

What does an `Int` know about appending lists?

Sounds like the `List` would know about prepending

Also scala lists are prepend data structures (more on this later)

---

# Answer

> A: The Int is appending the List
>
> B: The List is prepending the Int

B

```scala
0 :: List(1, 2, 3)

// is really

List(1, 2, 3).::(0)
```

i.e calling the `::` on `List`

Try it in the repl!

---

# What's going on?

Methods that end with `:` have the caller on the right in infix mode:

```scala
a :: b

// is actually

b.::(a)
```

---

# Demo with `Meter`

Will add `+:`

---

# "Colon towards caller"

The colon points towards the caller when using infix syntax

```scala
3.4d +: Meter(3d)

// is actually

Meter(3d).+:(3.4d)
```

---

# When to have a colon operation?

When your data isn't sitting right

---

# Good use case

Making your code reflect your data structure

```scala
0 :: List(1, 2, 3)

// vs

List(1, 2, 3).::(0)
```

Doesn't make sense for `Meter`

```scala
3.4d +: Meter(3d)
```

---

# Recapping colon operations

When a method ends with `:`, the caller is on the right in infix mode:

```scala
a :: b

// equivalent to

b.::(a)
```

---

# Can help readability

```scala
0 :: List(1, 2, 3)
```

---

```
 _   _       _
| \ | | ___ | |_
|  \| |/ _ \| __|
| |\  | (_) | |_
|_| \_|\___/ \__|

             _
  __ _  ___ (_)_ __   __ _
 / _` |/ _ \| | '_ \ / _` |
| (_| | (_) | | | | | (_| |
 \__, |\___/|_|_| |_|\__, |
 |___/               |___/

  ___ _ __ __ _ _____   _
 / __| '__/ _` |_  / | | |
| (__| | | (_| |/ /| |_| |
 \___|_|  \__,_/___|\__, |
                    |___/
```

My usual "old man" style warnings

---

# Back in the early days of scala

Overused

Confusing

---

# Can be hard to read

```scala
1 <<*>> 2 |+| 3
```

---

# Prudence

Just because you _can_ define these operators,

doesn't mean you should

---

# Prudence

If in doubt, just use a normal method

---

# Established conventions?

e.g. `++`

---

# Two versions

If you do decide to define an operator,

it's helpful to have a "normal" version too:

```scala
case class Meter(value: Double) {
  def plus(other: Meter): Meter = ...

  // Just delegates to the other one
  def +(other: Meter): Meter = plus(other)
}
```

---

# Why is this helpful?

Any thoughts?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Choice

```scala
case class Meter(value: Double) {
  def plus(other: Meter): Meter = ...

  // Just delegates to the other one
  def +(other: Meter): Meter = plus(other)
}
```

> Why is this helpful?

Some people aren't comfortable with operators or just don't like them

This gives them a "normal" alternative

---

# Interop

> Why is this helpful?

It's _really_ helpful for non-scala developers using your library

(e.g. java and kotlin developers)

---

# Detour

What signature will this have:

```scala
def +(other: Meter): Meter = ...
```

when it's compiled to byte code?

---

# Detour

What signature will this have:

```scala
def +(other: Meter): Meter = ...
```

when it's compiled to byte code?

Maybe this:

```java
public Meter +(Meter other) {
  ... // byte code
}
```

What do you think?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# JVM says no

```scala
def +(other: Meter): Meter = ...
```

This isn't a valid byte code signature

```java
public Meter +(Meter) {
  ... // byte code
}
```

---

# Decompile it


The compiler will turn it into something more icky looking

```java
public Meter ???(Meter) {
  ... // byte code
}
```

Let's decompile some code

`Meter.scala`

To the shell!

---

# Results

```scala
def +(other: Meter): Meter = ...
```

became

```java
public Meter $plus(Meter)
```

The scala compiler had to generate a method with a valid name

---

# In a java developer's shoes

What does a java developer see?

(Sorry if this causes you nightmare flashbacks)

---

# Pop Quiz

How do we share libraries?

- as source


- as compiled byte code

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Sharing code

We don't share source code,

we share _byte_ code

---

# Typical process

- `sbt` uses `scalac` to compile code into `.class` files


- `sbt` bundles them into a `.jar`


- publish the jar


- other devs pull that jar


- that jar is what the `javac`, `scalac` and `kotlinc` see

---

# Demo

Compile `Meter.scala`

Use `Meter.class` from a java file

---

# Recap

```java
Meter meter1 = new Meter(3.0d);

Meter meter2 = meter1 + meter1; // No
Meter meter2 = meter1.+(meter1); // No
Meter meter2 = meter1.$plus(meter1); // Yes bug ugly
Meter meter2 = meter1.plus(meter1); // Yes - the most "java"y

System.out.println(meter2);
```

---

# Interop

So if you plan to have "polyglot" code,

have a sensibly named alias for operators

(Will make java developers hate you a bit less)

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

# Operators == Methods

In scala syntax, there's no real difference

```scala
def funnyAdd(s: String, i: Int): Int = ...

def |+|(s: String, i: Int): Int = ...
```

---

# Infix

"Operators" are intended for infix position

```scala
Meter(3.0d) + Meter(4.5d)

Meter(3.0d).+(Meter(4.5d))
```

---

# Colon Operations

"Colon towards caller" in infix mode

```scala
0 +: Seq(1, 2, 3)

Seq(1, 2, 3).+:(0)
```

---

# Don't go crazy with operators

Similar to implicits

Often not as clear as a simple descriptive method name

Ideally there's an established understanding for that operator

---

# Interop

If you do define operators and plan to share your code,

provide an alternative method with a simple name

---

# More training

Next week

Survey

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
