---
author: Rohan
date: 2021-12-15
title: Type Inference
---

```
 _____
|_   _|   _ _ __   ___
  | || | | | '_ \ / _ \
  | || |_| | |_) |  __/
  |_| \__, | .__/ \___|
      |___/|_|
 ___        __
|_ _|_ __  / _| ___ _ __ ___ _ __   ___ ___
 | || '_ \| |_ / _ \ '__/ _ \ '_ \ / __/ _ \
 | || | | |  _|  __/ | |  __/ | | | (_|  __/
|___|_| |_|_|  \___|_|  \___|_| |_|\___\___|

```

More hard work for our dear compiler

---

# Today

- get a basic understanding of type inference


- look at some weird examples


- think about some good practices

---

```
 _____
|_   _|   _ _ __   ___
  | || | | | '_ \ / _ \
  | || |_| | |_) |  __/
  |_| \__, | .__/ \___|
      |___/|_|
 ___        __
|_ _|_ __  / _| ___ _ __ ___ _ __   ___ ___
 | || '_ \| |_ / _ \ '__/ _ \ '_ \ / __/ _ \
 | || | | |  _|  __/ | |  __/ | | | (_|  __/
|___|_| |_|_|  \___|_|  \___|_| |_|\___\___|

```

---

# What is type inference?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Type inference

When the compiler "fills in" the type for you

---

# Examples

```scala
val i = 1
// is really
val i: Int = 1


def makeInt = 3
// is really
def makeInt: Int = 3


def eatList(list: List[Int]): Unit = ...
eatList(List.empty)
// is really
eatList(List.empty[Int])
```

---

# Context

The compiler infers it from context

```scala
val i = 1
// is really
val i: Int = 1


def makeInt = 3
// is really
def makeInt: Int = 3


def eatList(list: List[Int]): Unit = ...
eatList(List.empty)
// is really
eatList(List.empty[Int])
```

---

# Block example

```scala
def doSomething = {  // <--- missing type
  ...
  3 // <--- Int
}
```

Compiler infers `Int` as the return type of `doSomething`

---

# Why is type inference useful?

Can remove redundant noise from our code

```scala
val i: Int = 4

val i = 4
```

---

```
__        __   _         _
\ \      / /__(_)_ __ __| |
 \ \ /\ / / _ \ | '__/ _` |
  \ V  V /  __/ | | | (_| |
   \_/\_/ \___|_|_|  \__,_|

 _____                           _
| ____|_  ____ _ _ __ ___  _ __ | | ___  ___
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \/ __|
| |___ >  < (_| | | | | | | |_) | |  __/\__ \
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___||___/
                          |_|
```

---

# Weird examples

Mostly type inference does what you expect,

you don't need to think about it

---

# Weird examples

> Mostly type inference does what you expect,
>
> you don't need to think about it

_Sometimes_ it can surprise you though...

---

# Option

```scala
val option = Some(3)
```

What is the type of `option`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# To the repl!

---

# Option

```scala
val option = Some(3)
```

> What is the compile and runtime types of `option`?

`Some[Int]`

---

# Did you say Option?

(Usually someone does)

---

# Analogous example

```scala
case class Person(name: String, age: String)

val person = Person("Boban", 26)
```

What is the type of `person`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Analogous example

```scala
case class Person(name: String, age: String)

val person = Person("Boban", 26)
```

> What is the type of `person`?

`Person`

So why would you think this would be any different:

```scala
case class Some[A](a: A) extends Option[A]

val option = Some(3)
```

---

# Type hierarchy

```
           Any
            |
          Object
            |
         Option[A]
         /      \
  Some[A]        None
```

---

# If you said Option...

... you are pure of heart

---

# Pure of heart

Maybe you think of `Option` kind of like an enum with two values:

- Some


- None

(relates to "algrebraic data types" - for another day)

---

# Round 2

---

# Round 2

```scala
val optionInt = None
```

What is the type of `optionInt` here?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Round 2

```scala
val optionInt = None
```

> What is the type of `optionInt` here?

I'm happy as long as you didn't say `Option[Int]`

---

# Compiler isn't telepathic (yet)

```scala
val option = Some(3)

val optionInt = None
```

It doesn't understand our intention

```
           Any
            |
          Object
            |
         Option[A]    <---- "I want this one"
         /      \
  Some[A]        None
```

---

# Fixing it

```scala
val option = Some(3)
```

Want `option` to end up having type `Option[Int]`

Ideas?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# "Type hints"

```scala
val option: Option[Int] = Some(3)

val option: Option[Int] = None
```

The RHS is a subclass of the type we intend

```
           Any
            |
          Object
            |
         Option[A]    <---- "I want this one"
         /      \
  Some[A]        None
```

---

# Factory methods

```scala
def some[A](a: A): Option[A] = Some(a)

def none[A]: Option[A] = None
```

They represent how we really think of Some and None

```scala
val option1 = some(3) // Option[Int]

val option2 = none[Int] // Option[Int]
```

---

# Recap so far

Sometimes type inference will pick a stronger type than the one you intended

---

# Next weird example...

---

# if

```scala
val age = if (person.isBoban) 26
```

What is the type of `age`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# if

```scala
val age = if (person.isBoban) 26
```

> What is the type of `age`?

To the repl!

---

# Understanding this

```scala
val age = if (person.isBoban) 26
```

What about the `else` case?

What if the person isn't a Boban?

---

# Else

```scala
val age = if (person.isBoban) 26

// is really

val age = if (person.isBoban) 26 else ()
```

Remember that `()` is "the Unit"

---

# Branches

```scala
val age = if (person.isBoban) 26 else ()
```

Compiler has two branches:

- `if` produces `Int`


- `else` produces `Unit`

---

# Unifying branches

> `if` produces `Int`
>
> `else` produces `Unit`

Turns out the compiler unifies these with `AnyVal`

```
                    Any
                /         \
        AnyVal               AnyRef (Object)
       /   |  \                /  \
     Int Float Boolean     String Person
```

My guess: `Unit` is considered an `AnyVal`

Test with `String` - to the repl!

---

# Recap

An `if` without an `else` doesn't really make sense as an _expression_

(in contrast to a statement)

---

# Recap

When the compiler infers types from branches, it takes the strongest ancestor of both branches

---

# Pop Quiz 1

What will the compiler infer the type of this to be:

```scala
val age = if (person.isBoban) Some(30) else Some(20)
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Pop Quiz 1

> What will the compiler infer the type of this to be:

```scala
val age = if (person.isBoban) Some(30) else Some(20)

Some[Int]
```

That's the strongest ancestor of `Some[Int]` and `Some[Int]`

```
                    Any
                /         \
        AnyVal               AnyRef (Object)
       /   |  \                /  \
     Int Float Boolean     String Option[Int]
                                  |
                                 Some[Int]
```

---

# Pop Quiz 2

What will the compiler infer the type of this to be:

```scala
val age = if (person.isBoban) Some(30) else None
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Pop Quiz 2

> What will the compiler infer the type of this to be:

```scala
val age = if (person.isBoban) Some(30) else None

Option[Int]
```

That's the strongest ancestor of `Some[Int]` and `None`

```
                    Any
                /         \
        AnyVal               AnyRef (Object)
       /   |  \                /  \
     Int Float Boolean     String Option[Int]
                                  /    \
                            Some[Int]  None
```

---

# Going deeper into branching...

---

# Pattern matches

Pattern matches are another example of branching

The compiler needs to unify all branches

---

# Pop Quiz 3

What will the compiler infer the type of `age` to be:

```scala
val age = name match {
  case "Boban" => oldBobanAge + 1

  case "Bobanita" => if (birthday) Some(bobanitaAge - 8) else None

  case "Peter" => 25
}

...

val oldBobanAge = 30
val bobanitaAge = 30
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Pop Quiz 3

> What will the compiler infer the type of `age` to be:

```scala
val age = name match {
  case "Boban" => oldBobanAge + 1 // Int

  case "Bobanita" => if (birthday) Some(bobanitaAge - 8) else None // Option[Int]

  case "Peter" => 25 // Int
}

Any
```

Tricky...

```
                    Any
                /         \
        AnyVal               AnyRef (Object)
       /   |  \                /  \
     Int Float Boolean     String Option[Int]
                                  /    \
                            Some[Int]  None
```

---

# Tip:

If you're getting compiler errors involving `Any`, e.g.

```
required: Option[Int]
found: Any
```

```scala
def age(name: String): Option[Int] = name match {
  case "Boban" => oldBobanAge + 1 // Int

  case "Bobanita" => if (birthday) Some(bobanitaAge - 8) else None // Option[Int]

  case "Peter" => 25 // Int
}
```

often it's because one of your code paths is producing the wrong type

which causes type inference to produce the wrong type

---

# Pop Quiz 4

What will the compiler do with this code?

```scala
def name(person: Person): Option[String] =
  if (person.isVoldemort) None else person.name
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Pop Quiz 4

> What will the compiler do with this code?

```scala
def name(person: Person) =
  if (person.isVoldemort) None else person.name
```

Compiles

But infers the type to be `Object with Serializable`

---

# Quick detour

Imagine we defined this:

```scala
trait Edible

case class Cake(flavor: String) extends Edible

case class Bun(flavor: String) extends Edible
```

---

# Unify

Let's _unify_ them

```scala
trait Edible

case class Cake(flavor: String) extends Edible

case class Bun(flavor: String) extends Edible
```

To the repl!

---

# Visualising

```
                    Any
                /         \
        AnyVal               AnyRef (Object)
       /   |  \               /     \
     Int Float Boolean     Cake     Bun     <--- both also Edible
```

They unify as `Object` but they also implement `Edible`

hence you see `Object with Edible` (and also `Product` and `Serializable`)

---

# Serializable

From java:

```java
/*
 * Serializability of a class is enabled by the class implementing the
 * java.io.Serializable interface.
 *
 * Classes that do not implement this interface will not have any of their
 * state serialized or deserialized.
 *
 * The serialization interface has no methods or fields
 * and serves only to identify the semantics of being serializable.
 * ...
 */
public interface Serializable {
}
```

"marker" trait

---

# Back to our example

```scala
def name(person: Person) =
  if (person.isVoldemort) None else person.name
```

```
                    Any
                /         \
        AnyVal                 AnyRef (Object)
       /   |  \               /     \
     Int Float Boolean      Option  String (Serializable)
                              |
                            None (Serializable)
```

Both are `Serializable`, so we get `Object with Serializable`

---

# Hint

Similar to our `Any` hint,

if the compiler is giving errors like:

```
required: ...
found: Object with Serializable
```

then you are probably mixing incompatible reference types, e.g. `Option[String]` and `String`

---

# Exceptions...

---

# Exceptions

Some code paths throw exceptions

```scala
def name(person: Person): String =
  if (person.isVoldemort) throw new HeWhoShouldNotBeNamedException
  else person.name
```

What will the compiler do here?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Exceptions

Some code paths throw exceptions

```scala
def name(person: Person): String =
  if (person.isVoldemort) throw new HeWhoShouldNotBeNamedException
  else person.name
```

> What will the compiler do here?

It will compile just fine, to the repl!

---

# Pop Quiz 5

```scala
def name(person: Person): String =
  if (person.isVoldemort) throw new HeWhoShouldNotBeNamedException
  else person.name
```

What is the type of the `if` branch?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Pop Quiz 5

```scala
def name(person: Person): String =
  if (person.isVoldemort) throw new HeWhoShouldNotBeNamedException
  else person.name
```

> What is the type of the `if` branch?

`Nothing`

---

# Nothing

What do you remember about `Nothing`?

```scala
val whatWeRemember: Nothing = ...
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Quick recap

We like to live in the beautiful mathematical realm

where all functions gracefully return a value

---

# The grim truth

> all functions gracefully return a value

But they don't always:

- exceptions


- infinite loops (non-halting programs)


- JVM shutdown, e.g. `System.exit(1)`

---

# Expressions

Everything in scala is an expression,

expressions need a type

---

# So

We need a type for these expressions that never produce a value

```scala
val a = throw new Exception
```

---

# Nothing

> We need a type for these expressions that never produce a value

That is `Nothing`

---

# Quirky things about nothing...

Do you remember?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Uninhabited

A type that you can never have an instance of

---

# Bottom type

```
                    Any
                /         \
        AnyVal               AnyRef (Object)
       /   |  \               /     \
     Int Float Boolean     String   Cake

      \    \     \           /       /
                  Nothing
```

```scala
def doSomething(i: Int): Unit = ...

doSomething(throw new Exception)
```

---

# Putting together as puns

- uninhabited: nothing is `Nothing`


- bottom type: `Nothing` is everything

---

# Back to our example

```scala
def name(person: Person): String =
  if (person.isVoldemort) throw new HeWhoShouldNotBeNamedException
  else person.name
```

- if: `Nothing`


- else: `String`

What's the strongest ancestor of `Nothing` and `String`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# String!

```scala
def name(person: Person): String =
  if (person.isVoldemort) throw new HeWhoShouldNotBeNamedException
  else person.name
```

- if: `Nothing`


- else: `String`

> What's the strongest ancestor of `Nothing` and `String`?

`String`

```
                    Any
                /         \
        AnyVal               AnyRef (Object)
       /   |  \               /     \
     Int Float Boolean     String   Cake

      \    \     \           /       /
                  Nothing
```

---

# Nothing is the no fuss person

> Group: What do you want for dinner?
>
> Nothing: I'll have what you guys are having

---

# Example

Unifiying:

- `Some[Int]`


- `None`


- `Nothing`

```
                    Any
                /         \
        AnyVal               AnyRef (Object)
       /   |  \                |
     Int Float Boolean      Option
      \    \     \           /  \
                           Some  None
                           /     /
                  Nothing
```

Ends up being the same as just trying to unify `Some` and `None`

---

# Recap: nifty type trickery

By making `Nothing` a bottom type,

it makes type inference with exceptions work the way we'd expect

Analogy: `Nothing` always does what the group is doing (sheep)

---

```
 ____                 _   _
|  _ \ _ __ __ _  ___| |_(_) ___ ___  ___
| |_) | '__/ _` |/ __| __| |/ __/ _ \/ __|
|  __/| | | (_| | (__| |_| | (_|  __/\__ \
|_|   |_|  \__,_|\___|\__|_|\___\___||___/

```

---

# We said

> type inference removes noise from our code

```scala
val i = 3
// vs
val i: Int = 3


val person = Person("Boban", 26)
// vs
val person: Person = Person("Boban", 26)


val map = Map("boban" -> person1, "bobanita" -> person2)
// vs
val map: Map[String, Person] = Map[String, Person]("boban" -> person1, "bobanita" -> person2)
```

Simple cases where the type is obvious

---

# Complex cases

```scala
val map = // 50 lines of code with 7 branches of logic
```

And map is `Future[Map[String, List[(Int, Person)]]]`

Not so obvious just looking at `map`

---

# Not so obvious

Consider a type hint

```scala
val map: Future[Map[String, List[(Int, Person)]]] = ...
```

(Also helps the code reviewer)

---

# Debugging

Type hints can be helpful to ensure something has the type you think

Sometimes compiler errors are manifesting a few steps away from where the real issue is

---

# Example

Type inference is producing `Future[Seq[String]]`,

but you need `Future[List[String]]`

---

# Recap

Type inference helps reduce noise to make code more readable (remove obvious stuff)

But sometimes an explicit type hint makes the code more readable (add in non-obvious stuff)

---

# Method return types

---

# Type inference on public methods?

```scala
def getPerson = {  // <--- inferred
  ...
}
```

---

# Warning!

```scala
def getPerson = {  // <--- inferred
  ...
}
```

Sometimes the inferred type won't be what you think,

and you don't realise because the code compiles

---

# Examples

`Object with Serializable`

will probably notice and just waste some time with confusing debugging

---

# More dangerous

```scala
def age = Some(30)
```

---

# Also mixins...

You return some extra mixin

```scala
def getPerson = {  // <--- inferred `Person with This and That and SomethingElse`
  ...
}
```

That type is now burnt into your method definition

---

# Source/Binary compatibility

If this method is in a library used by others,

now they have compiled against that weird type

You may be stuck with it

(More on this another day)

---

# General rule of thumb

Put an explicit return type on public definitions,

particularly if they'll be published and depended on

No good reason why you wouldn't

The time saving of not typing those characters isn't worth the risk

The reality is intellij will use virtual text for them anyway

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

# Type inference

The compiler can "fill" in types where we leave them out using surrounding context

---

# Gotchas

Mostly works fine, but sometimes it surprises you

---

# Readability

Type inference can remove a lot of obvious noise,

but sometimes it removes non-obvious things,

so consider adding type hints for complex expressions

(Helps code reviewers too)

---

# Explicit return types on public methods

Do this

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
