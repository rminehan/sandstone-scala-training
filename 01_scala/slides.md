---
author: Rohan
date: 2021-11-01
title: Scala
---

```
 ____            _       
/ ___|  ___ __ _| | __ _ 
\___ \ / __/ _` | |/ _` |
 ___) | (_| (_| | | (_| |
|____/ \___\__,_|_|\__,_|
```

SCALA-01: A general introduction

---

# Course admin first!

---

# Why am I running these trainings?

---

# Why am I running these trainings?

Simon told me to

---

# The plan

Run a few trainings each week to fill in gaps

---

# Getting to know you guys

Different backgrounds (java, python, javascript)

Different levels of experience

---

# Finding a balance

> Different levels of experience

Tricky

Will tend to be more cautious

---

# The course overall

"Plug the gaps"

- advanced language features


- advanced concurrency concepts


- FP concepts


- deeper JVM concepts 

---

# Figure it out as I go

Based on feedback as I get to know you guys

---

# Having said that

Plan to cover topics like:

- type system


- validation and strong typing


- FP concepts of purity and referential transparency


- category theory concepts


- time libraries


- Future and friends


- advanced regex


- exceptions in detail

---

# Questions

Just interrupt me

Also time at the end

---

# "I want the slides!"

All on github [here](https://github.com/rminehan/sandstone-scala-training)

Can also look at slides from a [similar course](https://github.com/rminehan/scala-basic-training)
I did for juniors

---

# To scala!

```
 ____   ____    _    _        _    
/ ___| / ___|  / \  | |      / \   
\___ \| |     / _ \ | |     / _ \  
 ___) | |___ / ___ \| |___ / ___ \ 
|____/ \____/_/   \_\_____/_/   \_\
                                   
```

Today: Putting scala in perspective

---

# Scala - key points

- cousin of java


- statically typed


- designed for FP


- great for concurrency


- still a bit experimental

(Will become more concrete as time goes on)

---

# Scala - key points

- cousin of java


- statically typed


- designed for FP


- great for concurrency


- still a bit experimental

These ideas might be a bit abstract right now

Will become more concrete as time goes on

---

```
  ____                _       
 / ___|___  _   _ ___(_)_ __  
| |   / _ \| | | / __| | '_ \ 
| |__| (_) | |_| \__ \ | | | |
 \____\___/ \__,_|___/_|_| |_|
                              
        __ 
  ___  / _|
 / _ \| |_ 
| (_) |  _|
 \___/|_|  
           
     _                  
    | | __ ___   ____ _ 
 _  | |/ _` \ \ / / _` |
| |_| | (_| |\ V / (_| |
 \___/ \__,_| \_/ \__,_|
                        
```

You can use familiar things from the java standard library.

---

# Sample

## Java:

```java
import java.time.ZonedDateTime;

int a = 1;

String s = "abc".toUpperCase();
```

## Scala:

```scala
import java.time.ZonedDateTime

val a = 1

val s = "abc".toUpperCase
```

## Initial differences?

- packages are the same


- familiar friends are the same: `int`, `String` etc...


- drop the semi-colons


- can sometimes drop brackets when calling functions with no parameters


- type inference

---

# Application: Copying off stack overflow

You're trying to figure out how to do something in scala, e.g. you google:

> How to make cat gif of Simon using scala?

Might get no matches.

---

# Try it with java

> How to make cat gif of Simon using java?

Java is much more popular. Lots of libraries, blog posts, answers.

You can translate them to scala fairly directly.

---

# Caution though

Java code often uses an "imperative" style

Need to massage into "idiomatic" scala code

---

# Understanding the JVM

```
       Scala      Java      Kotlin 

          \        |        /

                  JVM
```

---

# Making this more concrete

Demo time!

---

# Scala: A better java?

scala == java++ ?

```
 ___ 
|__ \
  / /
 |_| 
 (_) 
     
```

---

# Scala: A better java?

Not really

Designed with different goals in mind

---

# Why I mention this

Worried that:

- you'll bring your java patterns into scala


- you'll just use scala for it's better syntax


- miss the point of what it's really about

---

```
 ____  _        _   _           _ _       
/ ___|| |_ __ _| |_(_) ___ __ _| | |_   _ 
\___ \| __/ _` | __| |/ __/ _` | | | | | |
 ___) | || (_| | |_| | (_| (_| | | | |_| |
|____/ \__\__,_|\__|_|\___\__,_|_|_|\__, |
                                    |___/ 
 _____                     _ 
|_   _|   _ _ __   ___  __| |
  | || | | | '_ \ / _ \/ _` |
  | || |_| | |_) |  __/ (_| |
  |_| \__, | .__/ \___|\__,_|
      |___/|_|               
```

---

# The spectrum

> How much of your logic can be checked without having to run the code?

More of a spectrum

```
          "DYNAMIC"         |          "STATIC"
                            |
                            |
           python           |   Java         Scala
           javascript       |    C#             Haskell
                            | Go      Rust
 <------------------------------------------------->
 Less                                           More
```

(Will become clearer as time goes on)

---

```
 _____           _
|_   _|__   ___ | |___
  | |/ _ \ / _ \| / __| ?
  | | (_) | (_) | \__ \
  |_|\___/ \___/|_|___/

```

---

# Text editor

Probably start out with intellij or VS code.

Most beginner friendly. Best scala support.

90% of our scala devs use intellj.

Code completion and other IDE features helps newbies navigate complex scala features.

(Then later of course you'll move to vim)

---

# Running code?

Scala statically typed => compiler

For big projects: sbt

For demo's and tiny prototypes: ammonite - https://ammonite.io/

---

# Ammonite

Great for mucking around in scala

Demo time!

---

# See also

Scala worksheets (for intellij users)

Codi worksheets (for vimbeciles)

---

# Scala versions?

In 2021, most likely you'll encounter

- 2.12


- 2.13

For our training, they're all the same

---


```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
