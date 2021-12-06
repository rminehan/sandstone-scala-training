---
author: Rohan
date: 2021-12-06
title: Implicit Parameters
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

Hard work the compiler does behind the scenes

---

# (Re)introducing implicits

Go more slowly this time

---

# Quite unique to scala

---

# Controversial

Can be useful

Don't overuse and abuse

(I will keep repeating this...)

---

# Next few sessions

- implicit parameters


- implicit classes


- implicit conversions


- implicit scope resolution

---

# Today

Implicit Parameters

---

# Already seen them

What is an example of an implicit parameter we've already seen?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Already seen them

> What is an example of an implicit parameter we've already seen?

`ExecutionContext`!

(when used with `Future`'s)

---

# Implicit execution context

```scala
Future {
  ...
}.map {
  ...
}.flatMap {
  ...
}.recover {
  ...
}.recoverWith {
  ...
}
```

is really

```scala
Future {
  ...
}(ec).map {
  ...
}(ec).flatMap {
  ...
}(ec).recover {
  ...
}(ec).recoverWith {
  ...
}(ec)
```

where the `ec` is being sourced from somewhere

---

# _Good_ use case

Removes noise

```scala
Future {
  ...
}(ec).map {
  ...
}(ec).flatMap {
  ...
}(ec).recover {
  ...
}(ec).recoverWith {
  ...
}(ec)
```

---

# "Context"

Implicit parameters are good for "context"

e.g. execution context, database connection information

- always the same


- don't want it distracting from our business logic

---

# Example

James' birthday is coming up soon

The committee has met to discuss birthday things

---

# Implicit

> The committee has met to discuss birthday things

In this context, it's implicit that we're talking about James

---

# Build a cake 

To the repl!

---

# Summary

The compiler will fill in missing implicit parameters for you:

```scala
implicit val birthdayBoy = "James"

def age(implicit name: String): Int = ...

val jamesAge = age

// Equivalent to

val jamesAge = age(birthdayBoy)
//                 ^^^^^^^^^^^ Inserted by compiler
```

---

# Not magic

The compiler doesn't magic values out of nothing

They have to come from somewhere

To the repl!

---

```
 __  __       _ _   _       _      
|  \/  |_   _| | |_(_)_ __ | | ___ 
| |\/| | | | | | __| | '_ \| |/ _ \
| |  | | |_| | | |_| | |_) | |  __/
|_|  |_|\__,_|_|\__|_| .__/|_|\___|
                     |_|           
 ___                 _ _      _ _       
|_ _|_ __ ___  _ __ | (_) ___(_) |_ ___ 
 | || '_ ` _ \| '_ \| | |/ __| | __/ __|
 | || | | | | | |_) | | | (__| | |_\__ \
|___|_| |_| |_| .__/|_|_|\___|_|\__|___/
              |_|                       
```

---

# Multiple?

Yes! We can define multiple implicit parameters

To the repl!

---

# Observations

- put `implicit` at the start of your parameters (just once)


- Can have multiple implicit parameters


- All or nothing
  - pass all explicitly or pass all implicitly
  - no mixing


- Can have two implicit parameters of the same type
  - values will be the same when implicit
  - can be different when explicit

---

```
 __  __ _      _             
|  \/  (_)_  _(_)_ __   __ _ 
| |\/| | \ \/ / | '_ \ / _` |
| |  | | |>  <| | | | | (_| |
|_|  |_|_/_/\_\_|_| |_|\__, |
                       |___/ 
```

---

# Recap

> pass all explicitly or pass all implicitly
>
> no mixing

But I want to mix!

---

# Future.map

```scala
getPersonById("000").map { person =>
  person.age
}
```

Two parameters for `map`:

- (explicit) callback: `person => person.age`


- (implicit) execution context

---

# What's going on?

```scala
getPersonById("000").map { person =>
  person.age
}
```

We clearly _can_ mix explicit and implicit...

---

# Clarification

> pass all explicitly or pass all implicitly

in the same parameter group

---

# `Future.map`

Look at `map` more closely:

```scala
def map(f: A => B)(implicit ec: ExecutionContext): Future[B] = ...

//      ^^^^^^^^^  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
```

Parameters are in different groups

---

# Play around with this

To the repl!

---

# Observations

- can have as many groups as you want


- groups are implicit, not individual parameters


- only the last group can be implicit

---

```
    _              _     _             _ _         
   / \   _ __ ___ | |__ (_) __ _ _   _(_) |_ _   _ 
  / _ \ | '_ ` _ \| '_ \| |/ _` | | | | | __| | | |
 / ___ \| | | | | | |_) | | (_| | |_| | | |_| |_| |
/_/   \_\_| |_| |_|_.__/|_|\__, |\__,_|_|\__|\__, |
                           |___/             |___/ 
```

---

# Ambiguity

What happens if we have two implicit values of the same type?

To the repl!

---

# Observations

Compiler doesn't like ambiguity

If it's not clear which to use, it will just fail

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

# Implicit parameters

Makes it so that you don't have to explicitly pass a parameter

---

# Good for "context"

Sometimes there is an implicitly understood context that will never change

Implicits can remove some noise from your code

---

# Multiple implicits

Your last parameter group can contain multiple parameters

---

# Careful children

Don't overuse it

Can lead to confusing code that will make you cringe in 6 months time

(if you're not sure, just don't use it)

---

```
  ___                  _   _                 
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___ 
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \ ?
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
                                             
```
