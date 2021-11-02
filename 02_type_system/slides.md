---
author: Rohan
date: 2021-11-03
title: Type systems
---

```
 _____                 
|_   _|   _ _ __   ___ 
  | || | | | '_ \ / _ \
  | || |_| | |_) |  __/
  |_| \__, | .__/ \___|
      |___/|_|         
 ____            _                     
/ ___| _   _ ___| |_ ___ _ __ ___  ___ 
\___ \| | | / __| __/ _ \ '_ ` _ \/ __|
 ___) | |_| \__ \ ||  __/ | | | | \__ \
|____/ \__, |___/\__\___|_| |_| |_|___/
       |___/                           
```

SCALA-02

Understanding the quirks of scala's type system

---

# Reminder

Slides [here](https://github.com/rminehan/sandstone-scala-training)

---

# What is today about?

Understand the "quirky" aspects of scala's type system

Understand how it relates to java and the JVM

---

# And answer the question:

> Why is Int uppercase in scala ...

```scala
val i: Int = 1
```

> but lowercase in java?

```java
int i = 1;
```

> Aren't they the same thing?

---

# For java developers

Scala's type system is familiar yet different

Can be unsettling

---

# Today

- what is a type system?


- the JVM's type system


- scala's type system
    - Unit
    - Any
    - Nothing

---

# Type system?

```
__        ___           _     _       _ _  ___ 
\ \      / / |__   __ _| |_  (_)___  (_) ||__ \
 \ \ /\ / /| '_ \ / _` | __| | / __| | | __|/ /
  \ V  V / | | | | (_| | |_  | \__ \ | | |_|_| 
   \_/\_/  |_| |_|\__,_|\__| |_|___/ |_|\__(_) 
                                               
```

---

# My rough definition

- a group of built in types you can use to represent data


- (optional) a mechanism to make new types


- (optional) relationships between types (e.g. inheritance)

---

# Example: json

- null


- numeric (e.g. `2`, `3.4`)


- string (e.g. `"hello"`)


- boolean (`true` or `false`)


- array (e.g. `[0, true, "hi"]`)


- object (e.g. `{ "x": 1, "y": 2 }`)

---

# Applying our definition

> a group of built in types you can use to represent data

null, numeric, string, etc...

> (optional) a mechanism to make new types

nope

> (optional) relationships between types (e.g. inheritance)

nope

---

# Json

- very limited type system


- types aren't fine grained

---

# Example: the JVM

---

# Primitives

> a group of built in types you can use to represent data

Numerics:

- int (32 bit signed integral type)


- long (64 bit signed integral type)


- float (32 bit signed floating point type)


- double (64 bit signed floating point type)

(More granular than json)

---

# Primitives

> a group of built in types you can use to represent data

- boolean


- char (16 bits)


- byte (8 bits)

---

# New types

> (optional) a mechanism to make new types

Can define your own:

- classes


- interfaces

Building new types from old types

---

# Inheriting and implementing

> (optional) relationships between types

Examples:

- classes can inherit other classes


- interfaces can inherit other interfaces


- classes can implement interfaces


- can't inherit from primitives

---

# The divided universe of the JVM

```
                   JVM type system

----------------------------------------------------------
      Primitives        |     Reference Types (Objects)
----------------------------------------------------------
                        |
        int             |            Object
                        |         /          \
        float           |  ZonedDateTime  MyParentClass
                        |                     |
        double          |                 MyChildClass
                        |
----------------------------------------------------------
```

---

# Pop quiz

```
                   JVM type system

----------------------------------------------------------
      Primitives        |     Reference Types (Objects)
----------------------------------------------------------
                        |
        int             |            Object
                        |         /          \
        float           |  ZonedDateTime  MyParentClass
                        |                     |
        double          |                 MyChildClass
                        |
----------------------------------------------------------
```

Which side does `String` go on?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# String

Pseudo-primitive

- technically a reference type


- built into the JVM's logic, e.g. application entry point:

```java
public void main(String[] args) {
  ...
}
```

---

# Array?

```
                   JVM type system

----------------------------------------------------------
      Primitives        |     Reference Types (Objects)
----------------------------------------------------------
                        |
        int             |            Object
                        |         /          \
        float           |  ZonedDateTime  MyParentClass
                        |                     |
        double          |                 MyChildClass
                        |
----------------------------------------------------------
```

Which side does `Array` go on?

---

# Array?

Odd case

- `Objects` in the sense that they inherit from `java.lang.Object`


- special treatment from the JVM (e.g. `int[]`)


- not a class or an interface


- not initialised with constructors ("dynamically created")

(left them off my diagram to keep things simple)

---

> a group of built in types you can use to represent data

One array type for each simple primitive and `Object`:

- `int[]`


- `float[]`


- `long[]`

etc...

- `Object[]`

---


---

# What makes a "primitive" primitive?

ie. what's the essence of a primitive

---

# What makes a "primitive" primitive?

- how they're created at runtime


- specialised byte code instructions

---

# Quick demo

We'll look at some byte code instructions

To the terminal!

---

# Summary of demo

```java
  // Many 'i' instructions
  public int intDemo();
       0: iconst_1
       1: istore_1
       2: bipush        100
       4: istore_2
       5: iload_1
       6: iload_2
       7: iadd
       8: ireturn

  // Many 'f' instructions
  public float floatDemo();
       0: fconst_1
       1: fstore_1
       2: ldc           #2                  // float 100.0f
       4: fstore_2
       5: fload_1
       6: fload_2
       7: fadd
       8: freturn

  // Many 'a' instructions ('a' = reference)
  // A bit more complex
  public java.lang.String stringDemo();
    Code:
       0: ldc           #3                  // String hey
       2: astore_1
       3: ldc           #4                  // String you
       5: astore_2
       6: new           #5                  // class java/lang/StringBuilder
       9: dup
      10: invokespecial #6                  // Method java/lang/StringBuilder."<init>":()V
      13: aload_1
      14: invokevirtual #7                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
      17: aload_2
      18: invokevirtual #7                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
      21: invokevirtual #8                  // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
      24: areturn
```

---

# Summarizing the JVM type system

---

# Divided universe

```
                   JVM type system

----------------------------------------------------------
      Primitives        |     Reference Types (Objects)
----------------------------------------------------------
                        |
        int             |            Object
                        |         /          \
        float           |  ZonedDateTime  MyParentClass
                        |                     |
        double          |                 MyChildClass
                        |
----------------------------------------------------------
```

---

# Type system

Pretty sophisticated

- fairly granular primitives


- reference types


- inheritance concepts

---

```
 ____            _       
/ ___|  ___ __ _| | __ _ 
\___ \ / __/ _` | |/ _` |
 ___) | (_| (_| | | (_| |
|____/ \___\__,_|_|\__,_|
                         
 _____                 
|_   _|   _ _ __   ___ 
  | || | | | '_ \ / _ \
  | || |_| | |_) |  __/
  |_| \__, | .__/ \___|
      |___/|_|         
 ____            _                 
/ ___| _   _ ___| |_ ___ _ __ ___  
\___ \| | | / __| __/ _ \ '_ ` _ \ 
 ___) | |_| \__ \ ||  __/ | | | | |
|____/ \__, |___/\__\___|_| |_| |_|
       |___/                       
```

---

# Scala Type System?

What do we mean by this?

---

# Clarifications

## Scala

A _language_

Has a compiler which produces byte code

## JVM

A machine

Runs byte code

---

# Pop Quiz

True or false

> Scala code is run

---

# False!

> Scala code is run

Scala code is compiled

Byte code is run

---

# False!

> Scala code is run

Scala code is compiled

Byte code is run

(Grammar police alert!)

---

# Back to our question

What do we mean by the "scala type system"?

---

# Scala type system

> What do we mean by the "scala type system"?

It's all in the imagination of the compiler

---

# Two worlds

```
                   JVM type system

----------------------------------------------------------
      Primitives        |     Reference Types (Objects)
----------------------------------------------------------
                        |
        int             |            Object
                        |         /          \
        float           |  ZonedDateTime  MyParentClass
                        |                     |
        double          |                 MyChildClass
                        |
----------------------------------------------------------




                Scala's World View

----------------------------------------------------------
                      Any
                    /     \
             AnyVal         AnyRef
              / \          /      \
            Int Boolean  String   ZonedDateTime
----------------------------------------------------------
```

---

# Observations

```
                   JVM type system                              - divided

----------------------------------------------------------
      Primitives        |     Reference Types (Objects)
----------------------------------------------------------
                        |
        int             |            Object
                        |         /          \
        float           |  ZonedDateTime  MyParentClass
                        |                     |
        double          |                 MyChildClass
                        |
----------------------------------------------------------




                Scala's World View                              - unified

----------------------------------------------------------
                      Any
                    /     \
             AnyVal         AnyRef
              / \          /      \
            Int Boolean  String   ZonedDateTime
----------------------------------------------------------
```

---

# Observations

```
                   JVM type system                              - divided

----------------------------------------------------------
      Primitives        |     Reference Types (Objects)
----------------------------------------------------------
                        |
        int             |            Object
                        |         /          \
        float           |  ZonedDateTime  MyParentClass
                        |                     |
        double          |                 MyChildClass
                        |
----------------------------------------------------------




                Scala's World View                              - unified
                                                                - all uppercase
----------------------------------------------------------
                      Any
                    /     \
             AnyVal         AnyRef
              / \          /      \
            Int Boolean  String   ZonedDateTime
----------------------------------------------------------
```

---

# Observations

```
                   JVM type system                              - divided
                                                                - primitives don't have parents
----------------------------------------------------------
      Primitives        |     Reference Types (Objects)
----------------------------------------------------------
                        |
        int             |            Object
                        |         /          \
        float           |  ZonedDateTime  MyParentClass
                        |                     |
        double          |                 MyChildClass
                        |
----------------------------------------------------------




                Scala's World View                              - unified
                                                                - all uppercase
----------------------------------------------------------      - where's Object?
                      Any                                       - Int has a parent!
                    /     \
             AnyVal         AnyRef
              / \          /      \
            Int Boolean  String   ZonedDateTime
----------------------------------------------------------
```

---

# Existential crisis

The world of Java developer has just been shattered

---

# How does this make sense?

Remember this is in the mind of the compiler

A lot of these concepts disappear in the final `.class` file

---

```
 ____            _       _     
/ ___|  ___ __ _| | __ _( )___ 
\___ \ / __/ _` | |/ _` |// __|
 ___) | (_| (_| | | (_| | \__ \
|____/ \___\__,_|_|\__,_| |___/
                               
  ___        _      _          
 / _ \ _   _(_)_ __| | ___   _ 
| | | | | | | | '__| |/ / | | |
| |_| | |_| | | |  |   <| |_| |
 \__\_\\__,_|_|_|  |_|\_\\__, |
                         |___/ 
 _____                      
|_   _|   _ _ __   ___  ___ 
  | || | | | '_ \ / _ \/ __|
  | || |_| | |_) |  __/\__ \
  |_| \__, | .__/ \___||___/
      |___/|_|              
```

Some "special" types

---

# Quirky Types

- `Unit`


- `Any`


- `Nothing`

---

# Unit

Simplified TLDR: Usually corresponds to `void` on the JVM

---

# Scala is void of void

Have you ever seen `void` in scala code?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# What is `void` in java/JVM?

Is it a type?

Can you pass voids into methods?

---

# void

Represents when a method doesn't produce a value

---

# At the byte code level?

Quick demo

---

# void?

Means that nothing gets pushed onto the stack before quitting the function

---

# Scala and void

Why doesn't scala have a `void` keyword like java?

---

# FP mindset

> Why doesn't scala have a `void` keyword like java?

Go back to your high school maths days...

---

# In the class room

Teacher is describing some function `f` on the blackboard

Asks the class:

> What is f(3)?

---

# In the class room

> What is f(3)?

Someone says:

> f doesn't produce a value at 3

---

# In the class room

> f doesn't produce a value at 3

Hmmm...

Sounds ridiculous...

---

# Not produce a value?

A function can be _not defined_ at an input,

but if it is defined, it must produce a value at that input

---

# Back to scala

Designed around FP/mathematical philosophy

Everything is an expression

---

# Expressions

> Everything is an expression

ie. everything produces a value

ie. everything can be assigned to a value

Can put this in front of everything:

```scala
val x = ...
```

---

# Statements vs Expressions

Statements "do" something

Expressions "produce" something

---

# Java

Has both

## Statements

"Do" something

```java
println("Hi");

if (removeUser) {
  user.delete();
}
```

Can't do:

```java
void x = println("Hi");
```

## Expressions

"Produce" something

```java
int i = j + 10;
//      ^^^^^^

removeById(user.getId());
//         ^^^^^^^^^^^^
```

---

# Scala

Everything is an expression

---

# Hmm?

> Everything is an expression

But what if I just replicate the java code?

```scala
println("Hi")

if (removeUser) {
  user.delete()
}
```

---

# In the compiler's mind

```scala
// Produces a Unit
println("Hi")
```

just like:

```scala
// Produces an Int
1 + 3
```

---

# Let's assign it to stuff

To the repl!

---

# So: Unit

The type equivalent of `void`

A trick to make everything an expression in the type system

---

# Wasteful?

> The type equivalent of `void`

Does this mean that scala is making little units everywhere?

---

# Wasteful?

> Does this mean that scala is making little units everywhere?

Mostly no

Just exists in the mind of the compiler

Becomes `void` in byte code

---

# Demo to prove it

To the editor!

---

# Summary

This java method:

```java
public void returnVoid() {
    System.out.println("Not returning anything");
}
```

and this scala method:

```scala
def returnUnit(): Unit = {
  System.out.println("Not returning anything")
}
```

produce identical byte code:

```java
0: getstatic     #16  // Field java/lang/System.out:Ljava/io/PrintStream;
3: ldc           #18  // String Not returning anything
5: invokevirtual #24  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
8: return
```

---

# "The" Unit

There is only one instance of `Unit`

Syntax is `()`

```scala
def showMeUnit(): Unit = {
  doSomething
  ()       // Explicitly returning the unit
}
```

---

# Summary of Unit

---

# Expressions

In scala everything is an expression

Unit is a trick to make `void` computations fit that concept 

---

# Runtime

Generally has no representation in the output byte code 

Just exists in the mind of the compiler

---

```
    _                
   / \   _ __  _   _ 
  / _ \ | '_ \| | | |
 / ___ \| | | | |_| |
/_/   \_\_| |_|\__, |
               |___/ 
```

One type to rule them all

---

# Recap

```
                Scala's World View

----------------------------------------------------------
                      Any
                    /     \
             AnyVal         AnyRef
              / \          /      \
            Int Boolean  String   ZonedDateTime
----------------------------------------------------------
```

`Any` is the "top type"

---

# Object?

So is Any just Object rebranded for scala?

---

# Any != Object

> So is Any just Object rebranded for scala?

No

It's a parent of `Object` and everything else

```
                Scala's World View

----------------------------------------------------------
                      Any
                    /     \
             AnyVal         AnyRef  (Object)
              / \          /      \
            Int Boolean  String   ZonedDateTime
----------------------------------------------------------
```

---

# The point of Any

It unifies the type system into one big family

---

# Example

Suppose you wanted to write a method that could take _any_ type as an input:

For scala:

```scala
def printThing(thing: ???): Unit = {
  ...
}
```

For java:

```java
public void printThing(??? thing) {
  ...
}
```

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Example

Suppose you wanted to write a method that could take _any_ type as an input:

For scala: (Any)

```scala
def printThing(thing: Any): Unit = {
  ...
}
```

For java: (best you can do is Object)

```java
public void printThing(Object thing) {
  ...
}
```

---

# Confused java developer

```scala
def printThing(thing: Any): Unit = {
  ...
}
```

You're thinking:

> But how could that work? 
>
> The JVM doesn't have a "top" type that works for primitives and objects...

---

# Compile it and see what happens!

To the terminal!

---

# Demo Summary

```java
public void printThing(java.lang.Object);
   0: getstatic     #17  // Field scala/Predef$.MODULE$:Lscala/Predef$;
   3: aload_1
   4: invokevirtual #20  // Method scala/Predef$.println:(Ljava/lang/Object;)V
   7: return

public void demo();
   0: aload_0
   1: ldc           #27  // String String
   3: invokevirtual #29  // Method printThing:(Ljava/lang/Object;)V
   6: aload_0
   7: iconst_1
   // Sneaky boxing
   8: invokestatic  #35  // Method scala/runtime/BoxesRunTime.boxToInteger:(I)Ljava/lang/Integer;
  11: invokevirtual #29  // Method printThing:(Ljava/lang/Object;)V
  14: return
```

---

# Summary of Any

---

# Scala type system

- top type which unifies the entire type system


- everything is an `Any`


- it only exists in the mind of the compiler

---

# Translating to the JVM

- the JVM doesn't have a concept of a "top" type


- `Any` disappears


- trickery like boxing is used to create the effect of a top type

---

# Last one

```
 _   _       _   _     _             
| \ | | ___ | |_| |__ (_)_ __   __ _ 
|  \| |/ _ \| __| '_ \| | '_ \ / _` |
| |\  | (_) | |_| | | | | | | | (_| |
|_| \_|\___/ \__|_| |_|_|_| |_|\__, |
                               |___/ 
```

The bad boy of the type system

---

# Recall

Everything in scala is an expression.

ie. everything can be assigned back to a variable.

That variable will always have a type.

---

# What about exceptions

```scala
val x = throw new IllegalArgumentException("No Bobans!")
```

This compiles

What should we make the type of `x` here?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Nothing

> What should we make the type of `x` here?

`Nothing`

---

# Nothing

Scala's way of representing code paths that will never gracefully terminate.

- infinite loops


- exceptions

---

# Nothing and Unit

Tricks to make impure concepts fit into a pure FP framework

---

# Unleash the puns!

---

# nothing is Nothing

---

# nothing is Nothing

What does that mean?

"uninhabited"

ie. impossible to create an instance of Nothing (hence its name)

---

# Nothing is everything

---

# Nothing is everything

What does that mean?

It's a bottom type

```
                Scala's World View

----------------------------------------------------------
                      Any
                    /     \
             AnyVal         AnyRef
              / \          /      \
            Int Boolean  String   ZonedDateTime
               \    \       /    /

                    Nothing
----------------------------------------------------------
```

---

# Why make it a bottom type?

Will talk more about that when we do type inference

---

# Summary

---

# Nothing

Probably the most confusing type

---

# For code that doesn't terminate gracefully

- exceptions


- infinite loops

---

# Characteristics

- uninhabited: nothing is Nothing


- bottom type: Nothing is everything

---

```
 ____                                             
/ ___| _   _ _ __ ___  _ __ ___   __ _ _ __ _   _ 
\___ \| | | | '_ ` _ \| '_ ` _ \ / _` | '__| | | |
 ___) | |_| | | | | | | | | | | | (_| | |  | |_| |
|____/ \__,_|_| |_| |_|_| |_| |_|\__,_|_|   \__, |
                                            |___/ 
```

Overall

---

# What we covered

- what is a type system?


- the JVM's type system


- scala's type system
    - Unit
    - Any
    - Nothing

---

# JVM vs Scala

- the JVM's type system is very different to scala's


- many scala type system concepts exist only in the mind of the compiler

---

# Quirky Scala types

- Unit (functional equivalent to `void`)


- Any (top type)


- Nothing (bottom type)

---

```
  ___                  _   _                ___ 
 / _ \ _   _  ___  ___| |_(_) ___  _ __  __|__ \
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|/ /
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \_| 
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___(_) 
                                                
  ____                                     _      ___ 
 / ___|___  _ __ ___  _ __ ___   ___ _ __ | |_ __|__ \
| |   / _ \| '_ ` _ \| '_ ` _ \ / _ \ '_ \| __/ __|/ /
| |__| (_) | | | | | | | | | | |  __/ | | | |_\__ \_| 
 \____\___/|_| |_| |_|_| |_| |_|\___|_| |_|\__|___(_) 
                                                      
```
