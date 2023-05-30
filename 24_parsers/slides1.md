---
author: Rohan
date: 2023-05-29
title: Basic Parsing
---

```
 ____            _
| __ )  __ _ ___(_) ___
|  _ \ / _` / __| |/ __|
| |_) | (_| \__ \ | (__
|____/ \__,_|___/_|\___|

 ____                _
|  _ \ __ _ _ __ ___(_)_ __   __ _
| |_) / _` | '__/ __| | '_ \ / _` |
|  __/ (_| | |  \__ \ | | | | (_| |
|_|   \__,_|_|  |___/_|_| |_|\__, |
                             |___/
```

---

# New series!

---

# New series!

Parsing!

---

# Why?

A lot of parser heavy code in docanalyser

---

# Goals

- increase awareness of existing tools


- fill knowledge gaps in existing tools


- understand what tools best suit a problem

---

# Outline

- basic parsing


- unapply


- regex


- parser combinators

---

# Today

- interpolation parser


- splitting

---

```
 ___       _                        _       _   _
|_ _|_ __ | |_ ___ _ __ _ __   ___ | | __ _| |_(_) ___  _ __
 | || '_ \| __/ _ \ '__| '_ \ / _ \| |/ _` | __| |/ _ \| '_ \
 | || | | | ||  __/ |  | |_) | (_) | | (_| | |_| | (_) | | | |
|___|_| |_|\__\___|_|  | .__/ \___/|_|\__,_|\__|_|\___/|_| |_|
                       |_|
 ____
|  _ \ __ _ _ __ ___  ___ _ __
| |_) / _` | '__/ __|/ _ \ '__|
|  __/ (_| | |  \__ \  __/ |
|_|   \__,_|_|  |___/\___|_|
```

---

# Interpolation Parser

We all know about string interpolation

```scala
val greeting = "yo"
val name = "bro"

val sentence = s"$greeting $bro"
```

---

# Interpolation Parser

We all know about string interpolation

```scala
val greeting = "yo"
val name = "bro"

val sentence = s"$greeting $bro"
```

but you can do it in reverse too

To the repl!

---

# Summary

```scala
val greeting = "yo"
val name = "bro"

val sentence = s"$greeting $name"
// "yo bro"

val s"$greeting2 $name2" = sentence
// greeting2: String = "yo"
// name2: String = "bro"
```

Fast and simple but a bit brittle

---

```
 ____        _ _ _   _   _
/ ___| _ __ | (_) |_| |_(_)_ __   __ _
\___ \| '_ \| | | __| __| | '_ \ / _` |
 ___) | |_) | | | |_| |_| | | | | (_| |
|____/| .__/|_|_|\__|\__|_|_| |_|\__, |
      |_|                        |___/
```

Let's get to know `split` better

---

# Tokenising code

Very common to split text in basic parsing

---

# Gotchas

There's many split methods...

To the repl!

---

# Summary

```scala
"173.2.243.1".split(".")
// Array()

"173.2.243.1".split('.')
// Array("173", "2", "243", "1")
```

The string one is a _regex_ (and '.' is a special character)

---

# Splitting on whitespace

To the repl!

---

# Summary

```scala
"Yo bro  anna".split(' ')
// Array("Yo", "bro", "", "anna")
//                    ^^  <------- careful

"Yo bro  anna".split(' ').filter(_.nonEmpty)
// Array("Yo", "bro", "anna")

"Yo bro  anna".split("\\s+")
// Array("Yo", "bro", "anna")
```

The regex one is a bit cleaner and probably performs better

---

# Many split methods!

Let's see all the overloads

To the repl!

---

# Summary

```scala
def split(separator: Char): Array[String]
def split(separators: Array[Char]): Array[String]

def split(pattern: String): Array[String]
def split(pattern: String, limit: Int): Array[String]
```

---

# Separators overload

```scala
def split(separators: Array[Char]): Array[String]
```

To the repl!

---

# Summary

```scala
"abc,def.ghi|jkl,mno".split(Array('.', ',', '|'))
// Array("abc", "def", "ghi", "jkl", "mno")

// Equivalent to
"abc,def.ghi|jkl,mno".split("[,.|,]")
// Array("abc", "def", "ghi", "jkl", "mno")
```

The first one would probably perform faster and is easier to read

---

# Limit overload

```scala
def split(pattern: String, limit: Int): Array[String]
```

To the repl!

---

# Summary

```scala
",abc,def,,ghi,,,,".split(',')
// Array("", "abc", "def", "", "ghi")
```

Drops all empty trailing strings

Keeps the ones at the start or middle

---

# Exploring the limit parameter

Back to the example

---

# Summary

```scala
def split(pattern: String, limit: Int): Array[String]
```

When `limit` is:

- 0 - behaves like regular split, ie. splits to the end and drops trailing empties


- positive - splits only as much as it needs to


- negative - behaves like regular split but doesn't drop trailing empties

---

# Extracting Lines

To the repl!

---

# Summary

Don't assume line endings are always '\n'

There is this obscure OS called "Windows" which some people still use

```scala
val newline: String = System.getProperty("line.separator")

// Linux/Macos:    newline == "\n"

// Window:         newline == "\r\n"
```

Using this system property will make your code more portable

---

# Splitting and `lift`

Recall `lift` is helpful when you're not sure how many tokens you'll get

To the repl!

---

# Summary

```scala
val tokens = text.split(',').lift
// Int => Option[String]

tokens(0)
// Some(value = "abc")

tokens(10)
// None
```

---

# Another approach - unapplySeq

Good when there number of tokens is known ahead of time

---

# Summary

```scala
text.split(',') match {
  case Array(tok0, tok1, tok2, tok3, tok4) =>
    // Exactly 5 tokens
    ...
  case Array(tok0, tok1, tok2, tok3) =>
    // Exactly 4 tokens
    ...
  case Array(tok0, tok1, tok2, tok3, tok4, _*) =>
    // 5+ tokens
    ...
  case Array("yo", tok1, tok2) =>
    // Exactly 3 tokens and the first is "yo"
  case _ =>
    // Every other case
}
```

Powered under the hood by `unapplySeq`

Succinct way to:

- express length conditions

- destructure specific elements directly into variables

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

# Reverse string interpolation

```scala
val s"$greeting $name" = sentence
```

Can quickly parse text out of strings into variables

Great for simple cases

Fast, simple, brittle

---

# split

Many overloads

---

# split

Be careful not to confuse the regex and literal one

---

# split - empty tokens

Beware: By default, empty trailing strings get dropped

Pass -1 limit to prevent this

---

# split - efficient splitting

If you only need the first n tokens, you can avoid needless parsing with the limit

---

# split - newlines

When splitting understand where the data is coming from

This platform independent system property can be useful:

```scala
System.getProperty("line.separator")
```

---

# split

`lift` and `unapplySeq` can make tokenising code much more elegant

---

# Don't overdo it

`split` can get pushed beyond what it's really meant for

Pick a tool that matches the complexity of the data

---

# Next time

Using `unapply` to build parsers

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
