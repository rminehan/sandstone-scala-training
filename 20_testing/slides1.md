---
author: Rohan
date: 2022-12-13
title: Testing Case Study
---

```
 _____         _   _
|_   _|__  ___| |_(_)_ __   __ _
  | |/ _ \/ __| __| | '_ \ / _` |
  | |  __/\__ \ |_| | | | | (_| |
  |_|\___||___/\__|_|_| |_|\__, |
                           |___/
  ____
 / ___|__ _ ___  ___
| |   / _` / __|/ _ \
| |__| (_| \__ \  __/
 \____\__,_|___/\___|

 ____  _             _
/ ___|| |_ _   _  __| |_   _
\___ \| __| | | |/ _` | | | |
 ___) | |_| |_| | (_| | |_| |
|____/ \__|\__,_|\__,_|\__, |
                       |___/
```

---

# New topic

Testing

---

# Defining "testing"

Automated tests

Part of the build

Not human QA

---

# Why is it important?

- reduces long term maintenance costs


- improves code quality


- reduces dependency on human QA

---

# Today

Explore _how_ you write tests

Understand the benefits of writing tests during the development process

---

# Case study

`docanalyser AmountFinderService.firstPotentials`

docanalyser is a "renovator's dream"

---

```
 _____ _
|_   _| |__   ___
  | | | '_ \ / _ \
  | | | | | |  __/
  |_| |_| |_|\___|

 __  __      _   _               _
|  \/  | ___| |_| |__   ___   __| |
| |\/| |/ _ \ __| '_ \ / _ \ / _` |
| |  | |  __/ |_| | | | (_) | (_| |
|_|  |_|\___|\__|_| |_|\___/ \__,_|
```

---

# Contract

```scala
def firstPotentials(iWords: Seq[(Word, Int)], n: Int, ccode: String): Seq[WordSpan]
```

It extracts "amounts" from page words

---

# Playing with it

To the repl!

---

# Recap

``` scala
def firstPotentials(iWords: Seq[(Word, Int)], n: Int, ccode: String): Seq[WordSpan]
```
```
                  page words:    ["3", "45", "Bill", "$3", "+120.50", "-40.34", "100", "0", "House", "192.168.0.1", "123--", ".", "yo"]
                                   0    1     2       3     4          5         6      7    8        9              10      11    12

                                   /           |             |              \
                                  /            |             |               \
           group            length=1       length=2      length=3         length=4+
            by                  /              |             |                 \
          length               /               |             |                  \
                          ["3","0","."] ["45","$3","yo"]  ["100"]         ["Bill", "+120.50", "-40.34", "House", "192.168.0.1", "123--"]
                            0   7   11    1    3    12      6               2       4          5         8        9              10
                              |                |             |                   |
                              |                |             |                   |
          filter              |                |             |                   |
            +             ["3","0",   ] ["45","$3"     ]  ["100"]         [         "120.50", "-40.34"                          "123--"]
        transform           0   7         1    3            6                        4         5                                 10


                                 \             |             |                /
                                  \            |             |               /
                                   \           |             |              /

        recombine         ["3", "0", "45", "$3", "100", "120.50", "-40.34", "123--"]
                            0    7    1     3     6      4         5         10                                   Note words have to be
                                                                                                                  kept with their original
                                                      |                                                            indexes
                                                      |

        reorder           ["3", "45", "$3", "120.50", "-40.34", "100", "0", "123--"]
                            0    1     3     4         5         6      7    10
```

---

# Zooming out

```scala
// Primary method of this service
def findAmounts(p: Page): Seq[CurrencyAmount] = {
  val ccode = p.countryCode
  val currencyMap: Map[String, String] = pageCurrencies(ccode)
  val iWords = p.words.filter(_.text.trim.nonEmpty).zipWithIndex
  val n = iWords.size

  val potentials = firstPotentials(iWords, n, ccode) // <---

  finalPotentials(potentials).map(createAmount)
}
```

`firstPotentials` is the first of 2 steps

---

# What tests exist already?

To the code!

---

# Findings

- the tests aren't very comprehensive


- often it's not clear what aspect of the logic is being tested


- often it's not clear what the input and output data is


- the presence of debugging code shows developers found the code hard to test

---

# How did this happen?

My strong suspicion:

> the tests were written at the end of the development cycle or after it, not during it

---

# Mindset

Many developers just think of tests as a tool to make sure your code is correct

---

# Mindset

Many developers just think of tests as a tool to make sure your code is correct

Tests can _also_ be used as a mechanism to improve design/architecture and document your specification

---

# Mindset

Many developers just think of tests as a tool to make sure your code is correct

Tests can _also_ be used as a mechanism to improve design/architecture and document your specification

But this usually requires writing the tests _alongside_ the production code

---

# Reluctance

Writing tests take work

```
Cost         |         Benefit
-------------------------------
             |
Time/effort  |   Higher confidence
             |
             |
```

> "Ooh, I'm already finished and sick of this task, I'll just manually test it. QA can cover the rest"

---

# Mindset change

```
Cost         |         Benefit
-------------------------------
             |
Time/effort  |   Higher confidence
             |
             |   Stronger architecture
             |
             |   Stronger specification
             |
             |   Easier to consume
```

---

# Enough brain washing

Back to the code

---

```
 _____         _   _               _ _
|_   _|__  ___| |_(_)_ __   __ _  (_) |_
  | |/ _ \/ __| __| | '_ \ / _` | | | __|
  | |  __/\__ \ |_| | | | | (_| | | | |_
  |_|\___||___/\__|_|_| |_|\__, | |_|\__|
                           |___/
```

How would we test this?

What test cases would you want?

---

# Discussion time: What do you think?

``` scala
def firstPotentials(iWords: Seq[(Word, Int)], n: Int, ccode: String): Seq[WordSpan]
```
```
                  page words:    ["3", "45", "Bill", "$3", "+120.50", "-40.34", "100", "0", "House", "192.168.0.1", "123--", ".", "yo"]
                                   0    1     2       3     4          5         6      7    8        9              10      11    12

                                   /           |             |              \
                                  /            |             |               \
           group            length=1       length=2      length=3         length=4+
            by                  /              |             |                 \
          length               /               |             |                  \
                          ["3","0","."] ["45","$3","yo"]  ["100"]         ["Bill", "+120.50", "-40.34", "House", "192.168.0.1", "123--"]
                            0   7   11    1    3    12      6               2       4          5         8        9              10
                              |                |             |                   |
                              |                |             |                   |
          filter              |                |             |                   |
            +             ["3","0",   ] ["45","$3"     ]  ["100"]         [         "120.50", "-40.34"                          "123--"]
        transform           0   7         1    3            6                        4         5                                 10


                                 \             |             |                /
                                  \            |             |               /
                                   \           |             |              /

        recombine         ["3", "0", "45", "$3", "100", "120.50", "-40.34", "123--"]
                            0    7    1     3     6      4         5         10                                   Note words have to be
                                                                                                                  kept with their original
                                                      |                                                            indexes
                                                      |

        reorder           ["3", "45", "$3", "120.50", "-40.34", "100", "0", "123--"]
                            0    1     3     4         5         6      7    10
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

```
    _                                _
   / \   _ __ ___   ___  _   _ _ __ | |_
  / _ \ | '_ ` _ \ / _ \| | | | '_ \| __| ?
 / ___ \| | | | | | (_) | |_| | | | | |_
/_/   \_\_| |_| |_|\___/ \__,_|_| |_|\__|
```

Thinking about specification

---

# "Amount"?

What is an "amount" exactly?

- can they have dollar signs and pound signs?


- do those symbols have to be at the front?


- is there an upper limit? (e.g. 1,000,000)


- are negatives okay?


- is there a limit on decimal places? (e.g. 1.18342397523423498234)


- is "000" a legitimate amount? (amount vs number)


- are numbers from non-English languages allowed? (e.g. `四`)

---

# Clear spec

> What is an "amount" exactly?

If you can't answer these questions, how do you communicate to your customers?

How do you know if something is a bug or intended behaviour?

---

# Later on

> What is an "amount" exactly?
>
> The code says it's...

---

# Later on

> What is an "amount" exactly?
>
> The code says it's...

That's reverse engineering a specification from an implementation

The original implementers probably didn't clearly define this anywhere, it will be quite arbitrary

Now we don't know what is intentional and what is coincidental

---

# Late Testing

It's common to have badly defined concepts if you wait until the end to write your tests

The implementation drives the specification

---

# Early Testing

You think about the specification more clearly during development

Easier to change things before the concrete sets

---

```
    _    _         _                  _   _
   / \  | |__  ___| |_ _ __ __ _  ___| |_(_) ___  _ __
  / _ \ | '_ \/ __| __| '__/ _` |/ __| __| |/ _ \| '_ \
 / ___ \| |_) \__ \ |_| | | (_| | (__| |_| | (_) | | | |
/_/   \_\_.__/|___/\__|_|  \__,_|\___|\__|_|\___/|_| |_|

 ____                        _            _
| __ )  ___  _   _ _ __   __| | __ _ _ __(_) ___  ___
|  _ \ / _ \| | | | '_ \ / _` |/ _` | '__| |/ _ \/ __|
| |_) | (_) | |_| | | | | (_| | (_| | |  | |  __/\__ \
|____/ \___/ \__,_|_| |_|\__,_|\__,_|_|  |_|\___||___/
```

---

# Mixed concepts

Our method consumes `Word`'s and returns `WordSpan`'s

```scala
def firstPotentials(iWords: Seq[(Word, Int)], n: Int, ccode: String): Seq[WordSpan] = {
```

but the interior logic just uses text

To the code!

---

# Recap

``` scala
def firstPotentials(iWords: Seq[(Word, Int)], n: Int, ccode: String): Seq[WordSpan] = {
  val itext = iWords.map { t => (t._1.text, t._2) } // Word -> Text

  ... // logic uses text

  (mix1 ++ mix2 ++ mix3 ++ mix4)
    .map { case (text, index) =>
      val (originalWord, _) = iWords(index) // Text -> Word
      (originalWord.copy(text), index)
    }
    .sortBy { case (_, index) => index }
    .map(WordSpan(_))                       // Word -> WordSpan
}
```

```
   Word, WordSpan   --                                         ---
                      |                                       |
   ===================|=======================================|=========
                      |                                       |
   Text                ---------------------------------------
```

---

# Testing this

```scala
// Requires constructing `Word` objects
def firstPotentials(iWords: Seq[(Word, Int)], n: Int, ccode: String): Seq[WordSpan] = ...

// Just requires constructing pairs
def firstPotentials(itexts: Seq[(String, Int)], n: Int, ccode: String): Seq[(String, Int)] = ...
```

Push the wordy stuff up a level

---

# Observation

A decision about our method contract was influenced by testing

---

# Hmmm...

We made the method more general and easier to consume

```scala
// Requires constructing `Word` objects
def firstPotentials(iWords: Seq[(Word, Int)], n: Int, ccode: String): Seq[WordSpan] = ...

// Just requires constructing pairs
def firstPotentials(itexts: Seq[(String, Int)], n: Int, ccode: String): Seq[(String, Int)] = ...
```

Second version could be used in a different context (without `Word`)

---

# Dog-fooding your own code

> We made the method more general and easier to consume

Writing tests forces you to consume your own code

It gives you the perspective of the author and also the user

---

# Dog-fooding your own code

> We made the method more general and easier to consume

Writing tests forces you to consume your own code

It gives you the perspective of the author and also the user

You notice much earlier if your contract doesn't make sense or is difficult to consume

---

```
 _____ _       _     _             _
|_   _(_) __ _| |__ | |_ ___ _ __ (_)_ __   __ _
  | | | |/ _` | '_ \| __/ _ \ '_ \| | '_ \ / _` |
  | | | | (_| | | | | ||  __/ | | | | | | | (_| |
  |_| |_|\__, |_| |_|\__\___|_| |_|_|_| |_|\__, |
         |___/                             |___/
 _   _
| | | |_ __
| | | | '_ \
| |_| | |_) |
 \___/| .__/
      |_|
```

---

# ccode?

```scala
def firstPotentials(iWords: Seq[(Word, Int)], n: Int, ccode: String): Seq[WordSpan] = ...
```

Is it actually used?

---

# ccode?

```scala
def firstPotentials(iWords: Seq[(Word, Int)], n: Int, ccode: String): Seq[WordSpan] = ...
```

Is it actually used?

No! (I think)

This gets noticed when you write tests and think to yourself:

> what do I pass for ccode?

---

# n?

It's always just the same as `iWords.size`

So why pass it separately?

---

# Annoying edge cases

You have to define how you handle edge cases:

```scala
def firstPotentials(itexts: Seq[(String, Int)], n: Int) ...

firstPotentials(
  itexts = Seq(
    ("", 4),   // wrong index
    ("", 1),
    ("", 1),   // duplicate
    ("", -10), // negative
  ),
  n = 5 // doesn't match sequence length
)
```

---

# Annoying edge cases

Our method contract is very weak and invites many edge cases

We either:

- cross our fingers and trust users to do the right thing (leaving the behaviour undefined)


- define the behaviour, add tests and ugly error handling code

OR...

---

# Use stronger modelling

```scala
// firstPotentials(Seq(("yo", 0), ("man", 1)))
def firstPotentials(itexts: Seq[(String, Int)], n: Int) ...


// firstPotentials(Seq("yo", "man"))
def firstPotentials(texts: Seq[String]) ... {
  val itexts = texts.zipWithIndex
  ...
  // replace `n` with `texts.size`
}
```

The old model has duplication/redundancy which makes it easy to put it in an undefined state

You could derive the indices and `n` off the underlying sequence of text

The new model has no duplication

---

```
 ____                                 _ _     _ _ _ _   _
|  _ \ ___  ___ _ __   ___  _ __  ___(_) |__ (_) (_) |_(_) ___  ___
| |_) / _ \/ __| '_ \ / _ \| '_ \/ __| | '_ \| | | | __| |/ _ \/ __|
|  _ <  __/\__ \ |_) | (_) | | | \__ \ | |_) | | | | |_| |  __/\__ \
|_| \_\___||___/ .__/ \___/|_| |_|___/_|_.__/|_|_|_|\__|_|\___||___/
               |_|
```

---

# Peeking inside

```scala
def firstPotentials(texts: Seq[String]) ... {
  val itexts = texts.zipWithIndex

  // Original code
  val (itextn1, itext1) = itexts.partition { case (text, _) => text.length > 1 }
  val (itextn2, itext2) = itextn1.partition { case (text, _) => text.length > 2 }
  val (itext4, itext3) = itextn2.partition { case (text, _) => text.length > 3 }

  // Our refactored version from our backroom unofficial blackmarket fight club refactoring session
  val itext1 = itexts.filter { case (text, _) => text.length == 1 }
  val itext2 = itexts.filter { case (text, _) => text.length == 2 }
  val itext3 = itexts.filter { case (text, _) => text.length == 3 }
  val itext4 = itexts.filter { case (text, _) => text.length > 3 }
```

Are they the same though?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Peeking inside

```scala
def firstPotentials(texts: Seq[String]) ... {
  val itexts = texts.zipWithIndex

  // Original code
  val (itextn1, itext1) = itexts.partition { case (text, _) => text.length > 1 }
  val (itextn2, itext2) = itextn1.partition { case (text, _) => text.length > 2 }
  val (itext4, itext3) = itextn2.partition { case (text, _) => text.length > 3 }
  // itext1 is equivalent to:
  val itext1 = itexts.filter { case (text, _) => text.length <= 1 }

  // Our modified version from our backroom unofficial blackmarket fight club refactoring session
  val itext1 = itexts.filter { case (text, _) => text.length == 1 }
  val itext2 = itexts.filter { case (text, _) => text.length == 2 }
  val itext3 = itexts.filter { case (text, _) => text.length == 3 }
  val itext4 = itexts.filter { case (text, _) => text.length > 3 }
```

> Are they the same though?

Not quite

In the old code, `itext1` includes empty strings

Should be called `itext0or1`

---

# Peeking further

```scala
  val itext1 = itexts.filter { case (text, _) => text.length <= 1 }
  ...

  val mix1 = itext1.filter { case (text, _) => text.head.isDigit }
  //                                                ^^^^
  //                                               DANGER!
```

Wouldn't it explode if there's an empty string in the input?

---

# Zooming out

Zoom out to where this is called

```scala
def findAmounts(p: Page): Seq[CurrencyAmount] = {
  ...
  val iWords = p.words.filter(_.text.trim.nonEmpty).zipWithIndex
  //                          ^^^^^^^^^^^^^^^^^^^^

  val potentials = firstPotentials(iWords)

  finalPotentials(potentials).map(createAmount)
}
```

There's an implicit contract that the caller will remove empty strings

---

# Fuzzy and inconsistent

Responsibilities are randomly assigned between caller and method

"abc" -> (excluded)

"" -> (explodes)

---

# Spec vs Implementation

If the specification was thought out and explored with tests,

this would never have happened

---

# Improvements

## Make `itext1` just be words length 1

```scala
  val itext1 = itexts.filter { case (text, _) => text.length == 1 }

  val mix1 = itext1.filter { case (text, _) => text.head.isDigit }
  //                                                safer now
```

Now the specification for empty strings is well defined (they're not amounts)

---

# Improvements

## Map then filter

```diff
- // Let's through "abc " unchanged
- val iWords = p.words.filter(_.text.trim.nonEmpty).zipWithIndex
+ // Transforms "abc " to "abc" then lets it through
+ val iWords = p.words.map(_.trim).filter(_.nonEmpty).zipWithIndex
```

I've seen this bug elsewhere in docanalyser

(And map + filter is a soft validation process - relates to debt smashers campaign 2, level 2)

---

# Accidental Specification

Imagine docanalyser was a published library used by other teams

---

# Accidental Specification

A client realises that it throws an exception whenever there's an empty string:

```scala
try {
  val potentials = firstPotentials(iWords)
  handleNonEmptyPotentials(potentials)
}
catch {
  case ex: Exception =>
    handleEmptyStrings(iWords)
}
```

Now they're coupled to that behaviour

We can't fix it without breaking someone else's code

---

# Grown up pants

Part of putting on your grown up pants as an engineer is:

- explicitly define the behaviour of your public services/libraries


- enforce that with tests


- carefully manage backwards compatibility to not hurt your clients

This is one difference between "coding" and "engineering"

---

```
    _                                _
   / \   _ __ ___   ___  _   _ _ __ | |_
  / _ \ | '_ ` _ \ / _ \| | | | '_ \| __|
 / ___ \| | | | | | (_) | |_| | | | | |_
/_/   \_\_| |_| |_|\___/ \__,_|_| |_|\__|
```

Identifying an abstraction

---

# Testing amount logic

Suppose we wanted to write some unit tests to capture our specification of an "amount"

---

# Examples

## Amounts

"123"

"-1"

"0"

"$1.30"

"+100" -> "100"

## Not amounts

""

"abc"

"000"

"192.168.1.210"

"0401 223 255"

"四"

---

# Test cases

```scala
"firstPotentials" should {
  "consider strings of digits as amounts" in {
    firstPotentials(Seq("123")) mustEqual Seq(("123", 0))
  }
}
```

---

# Etc...

```scala
"firstPotentials" should {

  // "affirmative" tests

  "consider strings of digits as amounts" in {
    firstPotentials(Seq("123")) mustEqual Seq(("123", 0))
  }

  "consider strings of digits following a single negative sign as amounts" in {
    firstPotentials(Seq("-123")) mustEqual Seq(("-123", 0))
  }

  "consider '0' an amount" in {
    firstPotentials(Seq("0")) mustEqual Seq(("0", 0))
  }

  ...

  // "negative" tests

  "not consider alphabetical characters as an amount" in {
    firstPotentials(Seq("abc")) mustEqual Seq.empty
  }

  "not consider strings of multiple zeroes only as an amount" in {
    firstPotentials(Seq("000")) mustEqual Seq.empty
  }

  ...
}
```

---

# Observations

- a lot of scaffolding needed, even though we just want to test a single value


- the expected value is always a sequence of 1 or 0 elements

```scala
  "consider strings of digits as amounts" in {
    firstPotentials(Seq("123")) mustEqual Seq(("123", 0))
    //              ^^^^     ^            ^^^^      ^^^    <--- scaffolding
  }

  ...

  "not consider strings of multiple zeroes only as an amount" in {
    firstPotentials(Seq("000")) mustEqual Seq.empty
  }
```

Our tests are telling us something...

---

# Simplifying

Imagine a simpler method:

```scala
def isAmount(text: String): Boolean = ...
```

The tests would look like:

```scala
"isAmount" should {
  "consider strings of digits as amounts" in {
    isAmount("123") mustBe true
  }
  ...

  "not consider alphabetical characters as an amount" in {
    isAmount("abc") mustBe false
  }
}
```

No scaffolding required...

---

# But...

```scala
def isAmount(text: String): Boolean = ...
```

Doesn't capture the idea of transforming data:

```
"+123" -> "123"
```

Has an Option-y vibe...

---

# Option

```scala
def validateAmount(text: String): Option[String] = ...
```

Tests

```scala
"isAmount" should {
  "consider strings of digits as amounts" in {
    validateAmount("123") mustBe Some("123")
  }

  "allow '+' characters on amounts, but remove them" in {
    validateAmount("+123") mustBe Some("123")
  }

  ...

  "not consider alphabetical characters as an amount" in {
    isAmount("abc") mustBe None
  }
}
```

---

# Going even further

You could even capture _why_ something failed validation as a string (maybe for logging purposes)

---

# Validated

```scala
def validateAmount(text: String): Validated[String, String] = ...
```

Tests

```scala
"isAmount" should {
  "consider strings of digits as amounts" in {
    validateAmount("123") mustBe Valid("123")
  }

  "allow '+' characters on amounts, but remove them" in {
    validateAmount("+123") mustBe Valid("123")
  }

  ...

  "not consider alphabetical characters as an amount" in {
    isAmount("abc").isInvalid mustBe true
  }
}
```

---

# Going even further

In fact there could be multiple reasons why validation failed

e.g. boundary whitespace, non-digit characters etc...

---

# ValidatedNel

```diff
-def validateAmount(text: String): Validated[String, String] = ...
+def validateAmount(text: String): ValidatedNel[String, String] = ...
```

---

# Deja vu

```scala
def validateAmount(text: String): ValidatedNel[String, String] = ...
```

What does this remind you of?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Strong types!

```scala
def validateAmount(text: String): ValidatedNel[String, String] = ...
```

> What does this remind you of?

Strong types

---

# Amount as a type

We've discovered that the "amount" concept could be modelled as a strong type

It's just a string restricted to certain values

---

# Amount

```scala
object Amount extends Strong[String] {
  // Move that validation logic into your strong type:
  def validate(value: String): Validated[String, String] = ...
}
```

Transform the tests:

```scala
"Amount validation" should {
  "pass strings of digits" in {
    Amount.from("123") mustBe Valid("123")
  }

  "allow '+' characters on amounts, but remove them" in {
    Amount.from("+123") mustBe Valid("123")
  }

  ...

  "fail alphabetical characters" in {
    Amount.from("abc").isInvalid mustBe true
  }
}
```

---

# Recap

Buried in this problem is the concept of an amount

---

# Recap

We can use that strong type in our contract

```diff
-def firstPotentials(texts: Seq[String]): Seq[(String, Int)] = ...
+def firstPotentials(texts: Seq[String]): Seq[(Amount, Int)] = ...
//                                             ^^^^^^
```

Now consumers can more confidence we're really returning an amount

---

# Recap

The _tests_ made us notice that it was awkward to test the concept of an "amount"

They helped us decouple and isolate this concept into an independent type

---

# Other responsibilities

`firstPotentials` doesn't take a single text though

It's job is to receive multiple texts and filter/map them down to just the amounts

paired with their original index

---

# Other responsibilities

`firstPotentials` doesn't take a single text though

> It's job is to receive multiple texts and filter/map them down to just the amounts
>
> paired with their original index

Isn't that just this:

```scala
texts
  .zipWithIndex
  .map { case (text, index) => (Amount.from(text), index)
  .collect {
    case (Valid(amount), index) => (amount, index)
  }
```

?

---

# Individual vs Grouped processing

The code in stash breaks the text into groups by size,

then processes each group in a mini pipeline

To the code!

---

# Alternative

Process each individually

```scala
object Amount extends Strong[String] {
  // Move that validation logic into your strong type:
  def validate(value: String): Validated[String, String] = {
    value.length match {
      case 0 => "Empty text can't be an amount".invalidNel
      case 1 => validateLength1(value)
      case 2 => validateLength2(value)
      case 3 => validateLength3(value)
      case _ => validateLength4Up(value)
    }
  }

  private def validateLength1(value: String): ValidatedNel[String, String] = ...

  private def validateLength2(value: String): ValidatedNel[String, String] = ...

  private def validateLength3(value: String): ValidatedNel[String, String] = ...

  private def validateLength4Up(value: String): ValidatedNel[String, String] = ...
}
```

---

# Comparing - Length 3 Example

```scala
// Code on stash - processes a group
val mix3 = itext3
  .filter { case (text, _) => isSignSymbolDotDigit(text) }
  .filter { case (text, _) => text.nonEmpty }
  .map { case (text, _) => removeTail(text) }
  .filter { case (text, _) => isMultiSymbol(text) }
  .filterNot { case (text, _) => max1Sign(text) }
  ...

// Strong type code
private def validateLength3(value: String): ValidatedNel[String, String] = {
  if (isSignSymbolDotDigit(text) && text.nonEmpty) {
    val cleaned = removeTail(text)
    isMultiSymbol(text) && max1Sign(text)
  }
  else false
}
```

New code doesn't need the constant destructuring

---

# Aside

Individual processing preserves the original order, no need to sort!

```scala
texts
  .zipWithIndex
  .map { case (text, index) => (Amount.from(text), index)
  .collect {
    case (Valid(amount), index) => (amount, index)
  }
```

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

# Decisions


During a PR, a software developer makes hundreds of little decisions

```
                                   /       \
                                 /  \     /  \
                                /\  /\   /\  /\
                               /\/\/\/\ /\/\/\/\
```

When tests are present, they affect how those decisions are made

That affect accumulates

---

# Mindset change

Testing doesn't just help you write more "correct" code

Writing tests whilst you code makes your code better in other ways

---

# Examples

> Writing tests whilst you code makes your code better in other ways

- the single responsibility principle


- modelling and strong types


- abstraction boundaries


- is my function/api easy to consume


- how should this behave?

---

# Testing itself becomes easier

Compare these:

```scala
"Amount validation" should {
  "accept and clean a degree character" in {
    Amount.from("°20,627.17") mustBe Valid("20,627.17")
  }
}



private val word1 = Word("°20,627.17", Rectangle(1616, 292, 377, 83), 96)

"AmountFinderTest POT-01" must {
  "find amount portion from the word" in {
    val pot = amountFinder.firstPotentials(Seq((word1, 0)), 1, "au")
    pot(0).words(0).text mustBe "20,627.17"
  }
}
```

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
