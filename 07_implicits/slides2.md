---
author: Rohan
date: 2021-12-06
title: Implicit Extensions
---

```
 ___                 _ _      _ _
|_ _|_ __ ___  _ __ | (_) ___(_) |_
 | || '_ ` _ \| '_ \| | |/ __| | __|
 | || | | | | | |_) | | | (__| | |_
|___|_| |_| |_| .__/|_|_|\___|_|\__|
              |_|
  ____ _
 / ___| | __ _ ___ ___  ___  ___
| |   | |/ _` / __/ __|/ _ \/ __|
| |___| | (_| \__ \__ \  __/\__ \
 \____|_|\__,_|___/___/\___||___/

```

Another way the compiler is working hard for us

---

# Recap

Implicit parameters

```scala
def age(implicit name: String): Int = ...

// Implicit
implicit val name: String = "James"
age

// Explicit
age("James")
```

---

# Today

A different kind of implicit mechanism

---

# Get us thinking

Consider this scala code:

```scala
"12345".toInt
```

---

# Question

```scala
"12345".toInt
```

Java developers: In your java code did you ever use `.toInt`?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Try it out

```java
class JavaToInt {
    public static void computerSaysNo() {
        "123".toInt();
    }
}
```

To the shell!

---

# Result

```java
class JavaToInt {
    public static void computerSaysNo() {
        "123".toInt();
    }
}
```

Compile it:

```
$ javac JavaToInt.java
JavaToInt.java:3: error: cannot find symbol
        "123".toInt();
             ^
  symbol:   method toInt()
  location: class String
1 error
```

Computer says no

---

# But in scala it works

```scala
@ "123".toInt
// res0: Int = 123
```

---

# Multi-choice

Why does it work in scala but not java?

- A: Scala has cheat codes to unlock new features on the JVM


- B: Everyone knows scala is just better


- C: Java is mean


- D: I have a better answer

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# General rule of scala

> When something shouldn't work, but it magically does...

---

# General rule of scala

> When something shouldn't work, but it magically does,
>
> it's probably because of implicits

---

# Today

Implicit classes

> adding new functionality to an existing type

---

# Implicit classes

> adding new functionality to an existing type

Can modify classes we don't own

---

# Fill the gaps

> adding new functionality to an existing type

Can fill frustrating gaps in types that we don't control

---

# Let's build something

We'll define our own `clean` extension method on `String`:

- trim off boundary whitespace


- lowercase

---

# Example

```scala
"abc DEF ".clean

// "abc def"
```

To the repl!

---

# Observations

- just a normal wrapper class


- but it's marked as `implicit`


- class constructor takes in a `String`


- extension method(s) defined inside

---

# Under the hood?

```scala
"abc DEF ".clean
```

- compiler sees a method not defined on `String`


- searches for an implicit class that
    - is constructed around a `String`
    - has a `clean` method


- creates a wrapper object

```scala
(new StringOps("abc DEF ")).clean
```

---

# Summary

> Implicit classes are fancy wrapper classes

```scala
"abc DEF".clean

// is really

(new StringOps("abc DEF ")).clean
```

---

```
 ____  _                  _               _
/ ___|| |_ __ _ _ __   __| | __ _ _ __ __| |
\___ \| __/ _` | '_ \ / _` |/ _` | '__/ _` |
 ___) | || (_| | | | | (_| | (_| | | | (_| |
|____/ \__\__,_|_| |_|\__,_|\__,_|_|  \__,_|

 _     _ _
| |   (_) |__  _ __ __ _ _ __ _   _
| |   | | '_ \| '__/ _` | '__| | | |
| |___| | |_) | | | (_| | |  | |_| |
|_____|_|_.__/|_|  \__,_|_|   \__, |
                              |___/
```

---

# Common implicit extensions

Can you think of any new methods when using regular types in scala (compared to java)?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Examples

- ranges


- arrays

---

# Range

```scala
1 to 3

// really

1.to(3)
```

But java's int doesn't have a `to` method

(or any methods actually)

---

# Range

_Could_ implement this with a class like:

```scala
implicit class RangeOps(i: Int) {

  def to(end: Int): Range = ...

  def until(end: Int): Range = ...

}


1 to 3

// really

(new RangeOps(1)).to(3)
```

---

# Array

Typical Array code in Scala

```scala
val array = Array(1, 2, 3)

array.map(_ * 2).filter(_ > 3).foreach(println)
```

---

# Array

> Typical Array code in Scala

```scala
val array = Array(1, 2, 3)

array.map(_ * 2).filter(_ > 3).foreach(println)
```

But `Array` is not a scala sequence!

---

# Array

_Could_ implement this with a class like:

```scala
implicit class ArraySeqOps[A](array: Array[A]) {

  def map[B](f: A => B): Array[B] = ...

  def filter(pred: A => Boolean): Array[A] = ...

  ... // All the other standard combinators
}
```

---

# As it turns out

They don't use implicit classes

Instead they use implicit conversions (for next time)

---

```
__        __        _
\ \      / /_ _ ___| |_ ___
 \ \ /\ / / _` / __| __/ _ \
  \ V  V / (_| \__ \ ||  __/
   \_/\_/ \__,_|___/\__\___|

```

---

# Object creation

```scala
"abc DEF".clean

// becomes

(new StringOps(s)).clean
```

> So every time we call `clean`,
>
> we're creating a single-use throw away wrapper object?

Yep, not very environmentally friendly

---

# Usually

Not a big deal

---

# If that waste bothers you

Use the "value class" optimisation...

---

# Change it to a "value class"

```diff
-implicit class StringOps(s: String) {
+implicit class StringOps(val s: String) extends AnyVal {
   def clean: String = s.toLowerCase.trim
 }
```

- make it extend `AnyVal`


- make the constructor parameter public

---

# What does it do?

It's telling the compiler

> where possible, don't instantiate the class

---

# Example

```scala
implicit class StringOps(val value: String) extends AnyVal {
  def clean: String = value.toLowerCase.trim
}
```

also generates something like:

```scala
object StringOps {
  def clean$extension(value: String): String = value.toLowerCase.trim
}
```

---

# Example

```scala
implicit class StringOps(val value: String) extends AnyVal {
  def clean: String = value.toLowerCase.trim
}
```

also generates something like:

```scala
object StringOps {
  def clean$extension(value: String): String = value.toLowerCase.trim
}
```

and should translate:

```scala
val cleaned = "abc DEF".clean
```

to

```scala
val cleaned = StringOps.clean$extension("abc DEF")
```

---

# If you don't believe me

(There may be millenials in the audience...)

We can decompile some code - see `ValueClassDemo.scala`

---

# Decompiling

```scala
// Compile with: scalac ValueClassDemo.scala
object ValueClassDemo {

  def demo(): Unit = {
    "abc DEF".clean
  }

  implicit class StringOps(val value: String) extends AnyVal {
    def clean: String = value.toLowerCase.trim
  }

}
```

```java
// Decompile with: javap -c ValueClassDemo\$.scala
public void demo();
  ...
  4: ldc           #30  // String "abc DEF"
  ...
  9: invokevirtual #35  // Method clean$extension(String): String
  ...

// Decompile with: javap -c ValueClassDemo$StringOps$.class
public final String clean$extension(String);
  0: aload_1
  1: invokevirtual #29 // Method String.toLowerCase: String
  4: invokevirtual #32 // Method String.trim: String
  7: areturn
```

---

# Summary of decompiling

```scala
def demo(): Unit = {
  "abc DEF".clean
}

// as if we did

def demo(): Unit = {
  StringOps.clean$extension("abc DEF")
}

// and not

def demo(): Unit = {
  (new StringOps("abc DEF")).clean
}
```

---

```
 _     _           _ _        _   _
| |   (_)_ __ ___ (_) |_ __ _| |_(_) ___  _ __  ___
| |   | | '_ ` _ \| | __/ _` | __| |/ _ \| '_ \/ __|
| |___| | | | | | | | || (_| | |_| | (_) | | | \__ \
|_____|_|_| |_| |_|_|\__\__,_|\__|_|\___/|_| |_|___/

        __
  ___  / _|
 / _ \| |_
| (_) |  _|
 \___/|_|

            _                   _
__   ____ _| |_   _  ___    ___| | __ _ ___ ___  ___  ___
\ \ / / _` | | | | |/ _ \  / __| |/ _` / __/ __|/ _ \/ __|
 \ V / (_| | | |_| |  __/ | (__| | (_| \__ \__ \  __/\__ \
  \_/ \__,_|_|\__,_|\___|  \___|_|\__,_|___/___/\___||___/

```

---

# Can we add state?

To the repl!

---

# Stateless limitation

Using this:

```scala
implicit class StringOps(val value: String) extends AnyVal {
  private val _cleaned = value.toLowerCase.trim // what happens to me?
  def clean: String = _cleaned
}
```

to generate this?

```scala
object StringOps {
  def clean$extension(value: String): String = _cleaned // ?
}
```

Doesn't make sense

---

```
 _   _       _       _           _
| \ | | ___ | |_    (_)_   _ ___| |_
|  \| |/ _ \| __|   | | | | / __| __|
| |\  | (_) | |_    | | |_| \__ \ |_
|_| \_|\___/ \__|  _/ |\__,_|___/\__|
                  |__/
  __              _                 _ _      _ _
 / _| ___  _ __  (_)_ __ ___  _ __ | (_) ___(_) |_
| |_ / _ \| '__| | | '_ ` _ \| '_ \| | |/ __| | __|
|  _| (_) | |    | | | | | | | |_) | | | (__| | |_
|_|  \___/|_|    |_|_| |_| |_| .__/|_|_|\___|_|\__|
                             |_|
      _
  ___| | __ _ ___ ___  ___  ___
 / __| |/ _` / __/ __|/ _ \/ __|
| (__| | (_| \__ \__ \  __/\__ \
 \___|_|\__,_|___/___/\___||___/

```

---

# Wrappers generally

`AnyVal` is for any kind of wrapper class

e.g. `Meter` from the [docs](https://docs.scala-lang.org/overviews/core/value-classes.html)

To the docs!

---

# Wrap multiple values?

To the repl!

---

```
 _   _                        _     _       _     _
| | | |_ __   __ ___   _____ (_) __| | __ _| |__ | | ___
| | | | '_ \ / _` \ \ / / _ \| |/ _` |/ _` | '_ \| |/ _ \
| |_| | | | | (_| |\ V / (_) | | (_| | (_| | |_) | |  __/
 \___/|_| |_|\__,_| \_/ \___/|_|\__,_|\__,_|_.__/|_|\___|

                     _                    _
  _____   _____ _ __| |__   ___  __ _  __| |
 / _ \ \ / / _ \ '__| '_ \ / _ \/ _` |/ _` |
| (_) \ V /  __/ |  | | | |  __/ (_| | (_| |
 \___/ \_/ \___|_|  |_| |_|\___|\__,_|\__,_|

```

---

# Unavoidable overhead

Recall:

> where possible, don't instantiate the class

Sometimes it can't avoid it though

Back to the docs!

---

```
 ____                               __
|  _ \ ___  ___ __ _ _ __     ___  / _|
| |_) / _ \/ __/ _` | '_ \   / _ \| |_
|  _ <  __/ (_| (_| | |_) | | (_) |  _|
|_| \_\___|\___\__,_| .__/   \___/|_|
                    |_|
            _                   _
__   ____ _| |_   _  ___    ___| | __ _ ___ ___  ___  ___
\ \ / / _` | | | | |/ _ \  / __| |/ _` / __/ __|/ _ \/ __|
 \ V / (_| | | |_| |  __/ | (__| | (_| \__ \__ \  __/\__ \
  \_/ \__,_|_|\__,_|\___|  \___|_|\__,_|___/___/\___||___/

```

---

# Recap of "Value Classes"

- can often avoid creating throw away intermediate objects (not always)


- make your class extend `AnyVal` and have _one_ `val` parameter


- not just for implicit classes, but wrapper classes generally


- compiler is more strict about what's allowed


- read more in the [official scala docs](https://docs.scala-lang.org/overviews/core/value-classes.html)

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

# Glorified wrapper

```scala
s.clean

// becomes

(new StringOps(s)).clean
```

---

# Extend existing classes

Can extend stuff we don't own

---

# Waste?

If it bothers you, use `AnyVal`

Will add restrictions to your class (and is more work for the compiler)

---

# Handle with care

Implicits can be confusing

> Just because you can use an implicit, doesn't mean you should

Don't go crazy

---

# Having said that...

Implicit classes are the least implicit of the implicit tools

```scala
"abc DEF".clean
```

- more obvious what you're doing


- easy to jump to definition


- familiar concept from other languages (e.g. C#)

---

# My favourite of the 3

- simple to understand


- performs well

---

# Next time

Implicit conversions

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \ ?
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
