---
author: Rohan
date: 2022-11-22
title: Literal Singletons
---

```
 _     _ _                 _
| |   (_) |_ ___ _ __ __ _| |
| |   | | __/ _ \ '__/ _` | |
| |___| | ||  __/ | | (_| | |
|_____|_|\__\___|_|  \__,_|_|

 ____  _             _      _
/ ___|(_)_ __   __ _| | ___| |_ ___  _ __  ___
\___ \| | '_ \ / _` | |/ _ \ __/ _ \| '_ \/ __|
 ___) | | | | | (_| | |  __/ || (_) | | | \__ \
|____/|_|_| |_|\__, |_|\___|\__\___/|_| |_|___/
               |___/
```

---

# Today

Using up another backup training...

---

# Today

- singletons


- literal singletons


- search for your socks

---

# Warning

Some very advanced type concepts today

Showing you the extremes you can go to with type safety

---

# Warning

Some very advanced type concepts today

I'm not suggesting we use this in our code

---

```
 ____  _             _      _
/ ___|(_)_ __   __ _| | ___| |_ ___  _ __  ___
\___ \| | '_ \ / _` | |/ _ \ __/ _ \| '_ \/ __|
 ___) | | | | | (_| | |  __/ || (_) | | | \__ \
|____/|_|_| |_|\__, |_|\___|\__\___/|_| |_|___/
               |___/
```

---

# What is a singleton type?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# What is a singleton type?

A type with one instance

---

# Java

How is this done in java?

To the demo!

---

# Summary

```java
public class Singleton {
    public static Singleton INSTANCE = new Singleton();

    private Singleton() {
    }

    public int doStuff() {
        return 5;
    }
}
```

```scala
// Client code
Singleton.INSTANCE.doStuff
```

---

# Scala

How is this done in scala?

To the demo!

---

# Summary

```scala
object Singleton {
  def doStuff: Int = 6
}

// Client code
Singleton.doStuff
```

(A bit more concise than java :P)

---

# Singletons in scala

Trusty `object` _is_ the singleton pattern

It both defines a class with behaviour,

and also the only instance of that

---

# Recap

Singleton - there can be only one

(like Highlander)

---

# Literal Singletons

New in scala 2.13

Demo time!

---

# Recap

```scala
def gimmeYuman(yuman: "Yuman"): Unit = {
  println(s"I got a yuman: $yuman")
}

gimmeYuman("Yuman")
// I got a yuman: Yuman

gimmeYuman("Pranali")
/*
type mismatch;
 found   : String("Pranali")
 required: "Yuman"
*/

def gimme4(i: 4): Unit = {
  println(s"I got: $i")
}

gimme4(4)
// I got: 4

gimme4(3)
/*
type mismatch;
 found   : Int(3)
 required: 4
*/
```

---

# What's the point?

What's the point of a type that can only have one value?

You know in advance what the value must be...

---

# Recall MinString from other demo

To the repl!

---

# Recap - MinString2

```scala
type MinString2 = MinString2.Type
object MinString2 extends Strong[String] {
  def validate(weak: String): ValidatedNel[String, String] =
    if (weak.length >= 2) weak.validNel else s"String requires at least 2 characters: '$weak'".invalidNel
}

// Complete function
def stripBoundary(s: MinString2): String = s.substring(1, s.length - 1)

stripBoundary("") // Won't compile
```

---

# Recap - MinString4

```scala
type MinString2 = MinString2.Type
object MinString2 extends Strong[String] {
  def validate(weak: String): ValidatedNel[String, String] =
    if (weak.length >= 2) weak.validNel else s"String requires at least 2 characters: '$weak'".invalidNel
}

// Complete function
def stripBoundary(s: MinString2): String = s.substring(1, s.length - 1)


type MinString4 = MinString4.Type
object MinString4 extends Strong[String] {
  def validate(weak: String): ValidatedNel[String, String] =
    if (weak.length >= 4) weak.validNel else s"String requires at least 4 characters: '$weak'".invalidNel
}

def doubleStripBoundary(s: MinString4): String = s.substring(2, s.length - 2)

doubleStripBoundary("abc") // Won't compile
```

---

# Will this compile?

```scala
def stripBoundary(s: MinString2): String = s.substring(1, s.length - 1)

val minString4 = MinString4.fromUnsafe("1234")

stripBoundary(minString4)
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Will this compile?

```scala
def stripBoundary(s: MinString2): String = s.substring(1, s.length - 1)

val minString4 = MinString4.fromUnsafe("1234")

stripBoundary(minString4)
```

Let's see

---

# MinString2 vs MinString4

```scala
def stripBoundary(s: MinString2): String = s.substring(1, s.length - 1)

val minString4 = MinString4.fromUnsafe("1234")

stripBoundary(minString4)
/*
type mismatch;
 found   : MinString4
 required: MinString2
*/
```

---

# The problem

We _know_ mathematically that a string with at least 4 characters

must also have at least 2 characters

---

# The problem

> We _know_ mathematically that a string with at least 4 characters
>
> must also have at least 2 characters

ie. MinString4 is a stronger condition than MinString2

---

# MinString2 vs MinString4

Our understanding

```
                String                 (parent)

                  |

              MinString2               (child)

                  |

              MinString4               (grandchild)


```

---

# MinString2 vs MinString4

Compiler's understanding

```
                String                (parent)

              /        \

       MinString2   MinString4        (siblings)
```

---

# MinString2 vs MinString3

```
              mathematically           compiler

                String                  String

                  |                   /        \

              MinString2       MinString2   MinString4

                  |

              MinString4
```

---

# Why am I showing you this?

Recall this question:

> What's the point of a type that can only have one value?

We'll see that literal singletons is a great use case for this problem

Be patient...

---

# Ideally

We could pass a MinString4 wherever a MinString2 is expected

(or have a quick way to convert from MinString4 to MinString2)

---

# Aside

Also the duplication of `MinString2` and `MinString4` wasn't nice

```scala
type MinString2 = MinString2.Type
object MinString2 extends Strong[String] {
  def validate(weak: String): ValidatedNel[String, String] =
    if (weak.length >= 2) weak.validNel else s"String requires at least 2 characters: '$weak'".invalidNel
}

type MinString4 = MinString4.Type
object MinString4 extends Strong[String] {
  def validate(weak: String): ValidatedNel[String, String] =
    if (weak.length >= 4) weak.validNel else s"String requires at least 4 characters: '$weak'".invalidNel
}
```

What if we wanted `MinString1`, `MinString10` and many more?

---

# Aside

Would be nice to be able to quickly generate `MinStringN` for any `N`

---

# Demo time!

Singleton int's to the rescue!

---

# Recap

```scala
class MinString[Min <: Int](implicit min: ValueOf[Min]) extends Strong[String] {
  def validate(weak: String): ValidatedNel[String, String] =
    if (weak.length >= min.value) weak.validNel else s"String requires at least ${min.value} characters: '$weak'".invalidNel
}

val MinString1 = new MinString[1]
type MinString1 = MinString1.Type

val MinString2 = new MinString[2]
type MinString2 = MinString2.Type

val MinString3 = new MinString[3]
type MinString3 = MinString3.Type

val MinString4 = new MinString[4]
type MinString4 = MinString4.Type

// etc...
```

Code duplication removed

---

# Weaken method

```scala
import $ivy.`eu.timepit::singleton-ops:0.5.0`
import singleton.ops.{Require, >=}

def weaken[MinHigher <: Int, MinLower <: Int](minStringHigher: MinString[MinHigher], minStringLower: MinString[MinLower])
                                             (value: minStringHigher.Type)
                                             (implicit requirement: Require[MinHigher >= MinLower]): minStringLower.Type = {
  minStringLower.fromUnsafe(value)
}
```

All the real work is done at compile time

---

# Weaken method

```scala
import $ivy.`eu.timepit::singleton-ops:0.5.0`
import singleton.ops.{Require, >=}

def weaken[MinHigher <: Int, MinLower <: Int](minStringHigher: MinString[MinHigher], minStringLower: MinString[MinLower])
           ^^^^^^^^^         ^^^^^^^^        (value: minStringHigher.Type)
                                             (implicit requirement: Require[MinHigher >= MinLower]): minStringLower.Type = {
  minStringLower.fromUnsafe(value)                                  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
}

weaken(MinString3, MinString1)(minString3) // compiles
weaken(MinString3, MinString2)(minString3) // compiles
weaken(MinString3, MinString4)(minString3) // doesn't compile
/*
Cannot prove requirement Require[...]
*/
```

The compiler will only let us go from higher to lower values

---

# Zooming out

By using integers as types,

we can do integer logic at compile time

```scala
def weaken[MinHigher <: Int, MinLower <: Int](minStringHigher: MinString[MinHigher], minStringLower: MinString[MinLower])
           ^^^^^^^^^         ^^^^^^^^        (value: minStringHigher.Type)
                                             (implicit requirement: Require[MinHigher >= MinLower]): minStringLower.Type = {
                                                                    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                                                            //        compile time integer logic
```

---

# Capturing this relationship

```
                String                 (parent)

                  |

              MinString1               (child)

  / \             |
   |
   |          MinString2               (grandchild)
weaken
                  |

              MinString3               (great grandchild)

                  |

              MinString4               (great great grandchild)


               etc...
```

Didn't solve it using traditional inheritance

---

# Overwhelming...

---

# Overwhelming...

I'm not proposing we start doing this in our code

Just giving you a glimpse of what you can do with the compiler

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

# Singleton

A type with one instance

e.g. `object` in scala

---

# Literal Singletons

New feature in scala 2.13 that allows types to expressed as literal values

---

# Practical Use

- more type safe code, ie. capture requirements in your types and enforce at compile time, not runtime


- code reuse becomes easier

----

# Now go find your socks

:)

---

# Next time

Refactoring or testing stuff, we'll see

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
