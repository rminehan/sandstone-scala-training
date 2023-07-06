---
theme: seriph
class: text-center
highlighter: shiki
lineNumbers: false
info: |
  ## Basic Parsing
  A talk on basic parsers such as the interpolation
  parser and tips/gotchas related to splitting
drawings:
  persist: false
transition: slide-left
title: Basic Parsing
---

# Basic Parsing

---

# New series!

<div v-click class="text-xl p-2">
Parsing!
</div>

---

# Why?

<div v-click class="text-xl p-2">
A lot of parser heavy code in docanalyser
</div>

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

# Interpolation Parser

<div v-click class="text-xl p-2">
We all know about string interpolation

```scala
val greeting = "yo"
val name = "bro"

val sentence = s"$greeting $bro"
```
</div>
<arrow v-click="2" x1="600" y1="210" x2="320" y2="210" color="#564" width="3" arrowSize="1" />
<div v-click class="text-xl p-3">
but you can do it in reverse too
</div>
<div v-click class="text-xl p-5">
To the repl!
</div>

---

# Summary

```scala {all|4-5|7-9}
val greeting = "yo"
val name = "bro"

val sentence = s"$greeting $name"
// "yo bro"

val s"$greeting2 $name2" = sentence
// greeting2: String = "yo"
// name2: String = "bro"
```

Fast and simple but a bit brittle

More info in [scala 2.13 release notes](https://github.com/scala/scala/releases/tag/v2.13.0) and [PR 7387](https://github.com/scala/scala/pull/7387) 

---

# Splitting

<div v-click class="text-xl p-2">
Good old `String.split`
</div>

<div v-click class="text-xl p-3">
Let's get to know it better as there's a few gotchas and neat tricks

To the repl!
</div>

---

# Summary

```scala {all|1-2|4-5}
"173.2.243.1".split(".")  // regex
// Array()

"173.2.243.1".split('.')  // literal
// Array("173", "2", "243", "1")
```

---

# Splitting on whitespace

To the repl!

---

# Summary

```scala {all|1-3|5-6|8-9}
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

There is this obscure legacy OS called "Windows" which some people still use

```scala
val newline: String = System.getProperty("line.separator")

// Linux/Macos:     newline == "\n"

// Windows:         newline == "\r\n"
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

Good when the number of tokens is known ahead of time

---

# Summary

<div grid="~ cols-2 gap-2" m="-t-2">
<div>
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
</div>

<div>
Powered under the hood by `unapplySeq`

Succinct way to:

- express length conditions

- destructure specific elements directly into variables
</div>
</div>

---

# Summary

Wrapping up for today!

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

<div v-click class="text-xl p-2">
Many overloads
</div>

<div v-click class="text-xl p-3">
Be careful not to confuse the regex and literal one
</div>

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

# Questions?

<style>
h1 {
  font-size: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}
</style>
