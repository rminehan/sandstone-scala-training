---
author: Rohan
date: 2023-05-31
title: Parser Combinators
---

```
 ____
|  _ \ __ _ _ __ ___  ___ _ __
| |_) / _` | '__/ __|/ _ \ '__|
|  __/ (_| | |  \__ \  __/ |
|_|   \__,_|_|  |___/\___|_|

  ____                _     _             _
 / ___|___  _ __ ___ | |__ (_)_ __   __ _| |_ ___  _ __ ___
| |   / _ \| '_ ` _ \| '_ \| | '_ \ / _` | __/ _ \| '__/ __|
| |__| (_) | | | | | | |_) | | | | | (_| | || (_) | |  \__ \
 \____\___/|_| |_| |_|_.__/|_|_| |_|\__,_|\__\___/|_|  |___/
```

The big daddy/mumma of parsing tools

---

# Today

- background


- concepts


- basic examples


- json parser


- technology direction

---

```
 ____             _                                   _
| __ )  __ _  ___| | ____ _ _ __ ___  _   _ _ __   __| |
|  _ \ / _` |/ __| |/ / _` | '__/ _ \| | | | '_ \ / _` |
| |_) | (_| | (__|   < (_| | | | (_) | |_| | | | | (_| |
|____/ \__,_|\___|_|\_\__, |_|  \___/ \__,_|_| |_|\__,_|
                      |___/
```

---

# Current uses

docanalyser:

- `AddressParser`


- `PageNumberParser`

---

# Current library

`scala-parser-combinators` library

---

# Existing libraries

```
 ------------------------------------------------------------------------------------------------------------
| Library                  | Maturity | Maintained | Understandable | Docs     | Performance | Supports Cuts |
 ------------------------------------------------------------------------------------------------------------
| scala-parser-combinators | Mature   | Yes        | Medium         | Lacking  | Poor        | No            |
| atto                     | Mature   | Maybe      | Medium         | Okay     | Unsure      | No            |
| fastparse                | Mature   | Yes        | Low            | Detailed | Decent      | Yes           |
 ------------------------------------------------------------------------------------------------------------

 --------------------------------------------------------------------------------------------------------------
| Library                  | Whitespace handling       | Capture behaviour| Errors   | Notes                   |
 --------------------------------------------------------------------------------------------------------------
| scala-parser-combinators | Basic                     | Opt out          | Standard | Booted from stdlib      |
| atto                     | None                      | Opt out          | Unsure   | Integrates with refined |
| fastparse                | Advanced and customisable | Opt in           | Good     | Battle hardened         |
 --------------------------------------------------------------------------------------------------------------

 --------------------------------------------------------
| Library                  | Input notes                 |
 --------------------------------------------------------
| scala-parser-combinators | None                        |
| atto                     | Support feeding             |
| fastparse                | Supports many input formats |
 --------------------------------------------------------
```

Will come back to this later

---

# My feelings

Migrate from `scala-parser-combinators` to `fastparse`

Today's demo will use `fastparse` (but the basic concepts are the same)

---

```
  ____                           _
 / ___|___  _ __   ___ ___ _ __ | |_ ___
| |   / _ \| '_ \ / __/ _ \ '_ \| __/ __|
| |__| (_) | | | | (_|  __/ |_) | |_\__ \
 \____\___/|_| |_|\___\___| .__/ \__|___/
                          |_|
```

---

# What is it?

What is a parser combinator?

---

# What is it?

> What is a parser combinator?

Abstractly, just another parsing tool:

```
String => Option[...]
```

(or `Either` or some other kind of effect)

---

```
 ____            _
| __ )  __ _ ___(_) ___
|  _ \ / _` / __| |/ __|
| |_) | (_| \__ \ | (__
|____/ \__,_|___/_|\___|

 _____                           _
| ____|_  ____ _ _ __ ___  _ __ | | ___
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \
| |___ >  < (_| | | | | | | |_) | |  __/
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___|
                          |_|
```

---

# Example

We'll parse addresses

```
221b Baker Street, London
```

---

# Example

We'll parse addresses

```
221b Baker Street, London

Number              221
Optional letter     b
Whitespace
Word                Baker
Whitespace
Word                Street
Comma               ,
Optional Whitespace
Word                London
```

---

# Regex?

We'll parse addresses

```
221b Baker Street, London

Number              221
Optional letter     b
Whitespace
Word                Baker
Whitespace
Word                Street
Comma               ,
Optional Whitespace
Word                London
```

Regex _might_ suit a simple example like this

We're just getting started though

---

# Aside

Regex often gets stretched past its limit

Parser combinators "scale" better as requirements get more complex

(in terms of readability)

---

# Demo time!

---

# Summary

```scala
def addressParser[_: P]: P[Address] = (
  P(CharsWhileIn("0-9").!).map(_.toInt) ~ // street number
  P(CharIn("a-zA-Z").!.?) ~ whitespace ~ // street letter
  upperCaseWord ~ whitespace ~ // street name
  upperCaseWord ~ "," ~ whitespace.? ~ // street type
  upperCaseWord // city
).map(Address.tupled)

parse("221b Baker Street, London", addressParser(_))
// Parsed[Address] = Success(
//  value = Address(streetNumber = 221, streetLetter = Some(value = "b"), streetName = "Baker", streetType = "Street", city = "London"),
//  index = 25
//)
```

---

# Recap

- parsers don't have to consume all text, they're leaving some for the next parser


- parsers don't capture by default, use `!` to capture


- use `rep` for repetition


- use `?` to make optional


- we used strict whitespace settings


- there's some cryptic scala magic

---

```
     _
    | |___  ___  _ __
 _  | / __|/ _ \| '_ \
| |_| \__ \ (_) | | | |
 \___/|___/\___/|_| |_|
```

Build a json parser

---

# Why json?

It's nested

Could have also used:

- yaml
- html
- source code

---

# Note

Demo is using scala 3

But not using anything fancy

---

# Approach

Implement simple primitive types first (null, boolean, numeric, string)

Then move onto the nested ones (array and object)

Lots of unit tests to cover all my screwups

---

# Demo time

Let's go!

---

# Phase 1

`null`

To the repl!

---

# Resources

## scala-parser-combinator

[Github](https://github.com/scala/scala-parser-combinators)

[Getting started guide](https://github.com/scala/scala-parser-combinators/blob/main/docs/Getting_Started.md)

## atto

[Microsite](https://tpolecat.github.io/atto/)

[Github](https://github.com/tpolecat/atto)

## fastparse

[Github](https://github.com/com-lihaoyi/fastparse)

[Docs](https://com-lihaoyi.github.io/fastparse/)

[Tutorial](http://www.lihaoyi.com/post/EasyParsingwithParserCombinators.html)

## json

[Json spec](https://www.json.org/json-en.html)

---

# Better parser combinator libraries?

TODO Investigate and discuss

---

Write tests!

UPTO - got a basic demo working

It might be better to start with a simpler example before jumping into such a hard problem
Json is easy easy easy super hard

Or split it across a couple of sessions

I think you should do a practice run on Pranali and Varun as this is pretty involved


---

UPTO

Redo this demo with fastparse

Before diving into json parsing, do some basic demo's first, maybe ones from the codebase

Before diving in, explain how there's 3 parser combinator libraries I know of and give basic pro's and con's
Then loop back to it later when it's more clear

Have a discussion section and explain that I recommend we consider changing library
It's a good time to do it because we only use it in two places,
but we should be using them more

Whitespace handling
Come back to this at the end of the demo

Go through the little numbered checklist on Li Haoyi's blog
http://www.lihaoyi.com/post/EasyParsingwithParserCombinators.html
Gives a good insight into his approach to which tool to pick

Deal with the funny macro stuff and syntax with fastparse

Show how to use for comp's with parsers.

Have a flatMap example that actually cares about what was parsed previously.
E.g. n letters followed by n digits

Need slides for how the P/ParsingRun is mutable
