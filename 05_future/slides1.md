---
author: Rohan
date: 2021-11-15
title: Future Intro
---

```
 _____      _
|  ___|   _| |_ _   _ _ __ ___
| |_ | | | | __| | | | '__/ _ \
|  _|| |_| | |_| |_| | | |  __/
|_|   \__,_|\__|\__,_|_|  \___|

 ___       _
|_ _|_ __ | |_ _ __ ___
 | || '_ \| __| '__/ _ \
 | || | | | |_| | | (_) |
|___|_| |_|\__|_|  \___/

```

---

# Futures

Scala library we use for asynchronous programming

Nice tool from the scala standard library

---

# Rough plan

Focus of the next few talks

- general intro


- advanced usage


- traps and gotcha's


- understanding blocking concepts more deeply

---

# Today

- what is a `Future`?


- basic usage


- understanding synchronous vs asynchronous style

---

Future:

```
__        ___           _     _       _ _
\ \      / / |__   __ _| |_  (_)___  (_) |_
 \ \ /\ / /| '_ \ / _` | __| | / __| | | __|  ?
  \ V  V / | | | | (_| | |_  | \__ \ | | |_
   \_/\_/  |_| |_|\__,_|\__| |_|___/ |_|\__|

```

---

# Rough definition

> A Future[A] represents a computation that will eventually give you an A

---

# Let's play with one

To the repl!

---

# Life cycle of a `Future`

- starts "incomplete"


- eventually "completes"
  - success
  - failure

---

# Life cycle of a `Future`

Using types

- starts "incomplete": `None`


- eventually "completes": `Some(...)`
  - `Success(a)`
  - `Failure(ex)`

---

# Observation 1

`Future` is _mutable_

It changes state during its lifetime

(from incomplete to complete)

---

# Observation 1

> `Future` is _mutable_

It is not "pure"

(more on that another day)

---

# Observation 2

`Future` is "non-blocking" (asynchronous)

---

# Observation 2

> `Future` is "non-blocking" (asynchronous)

When we create it,

it runs somewhere else and we're free to do something else

---

# Observation 3

Like a tracking id for a package

The `Future` isn't the computation itself,

it's a handle to help you understand how it's going

---

```
 ____                   _
/ ___| _   _ _ __   ___| |__  _ __ ___  _ __   ___  _   _ ___
\___ \| | | | '_ \ / __| '_ \| '__/ _ \| '_ \ / _ \| | | / __|
 ___) | |_| | | | | (__| | | | | | (_) | | | | (_) | |_| \__ \
|____/ \__, |_| |_|\___|_| |_|_|  \___/|_| |_|\___/ \__,_|___/
       |___/


                            vs

    _                         _
   / \   ___ _   _ _ __   ___| |__  _ __ ___  _ __   ___  _   _ ___
  / _ \ / __| | | | '_ \ / __| '_ \| '__/ _ \| '_ \ / _ \| | | / __|
 / ___ \\__ \ |_| | | | | (__| | | | | | (_) | | | | (_) | |_| \__ \
/_/   \_\___/\__, |_| |_|\___|_| |_|_|  \___/|_| |_|\___/ \__,_|___/
             |___/
```

---

# Synchronous

Example: phone call

```
Speaker 1  ----------         ----------   ----------

Speaker 2            ---------          ---


--- = who's talking
```

---

# Synchronous

- orderly (often has the idea of waiting for your turn)


- only one thing happening at a time


- has a deterministic vibe

---

# Asynchronous

Example: email

---

# Email

When you send an email, you usually don't sit around waiting for the reply

You do something else

---

# Asynchronous

- no concept of taking turns


- some notification/callback mechanism

---

# Pop Quiz

You bought a TV and it turned out to have a fault

You call customer support and get put on hold waiting for a human

Is this synchronous or asynchronous?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Pop Quiz

> You call customer support and get put on hold waiting for a human
>
> Is this synchronous or asynchronous?

Synchronous

You're stuck waiting on the line (ie. "blocked")

waiting for your turn

You can't go have a shower or play tennis

---

# Pop Quiz

> Synchronous

How could the customer support make it asynchronous/non-blocking?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Pop Quiz

> How could the customer support make it asynchronous/non-blocking?

You could leave your phone number and have them call you back once a human is free

---

# Synchronous vs Asynchronous

## Synchronous

Simpler to implement

Tends to be less efficient

## Asynchronous

More complex to implement correctly

Tends to be more efficient

---

# Customer service example

## Synchronous

Easier to design a workflow where customers are forced to wait

## Asynchronous

Requires additional concepts, e.g.

- a callback queue for phone numbers


- requeueing people they couldn't get hold of


- answering in realtime if the queue is empty

---

```
 _____         _
|  ___|_ _ ___| |_
| |_ / _` / __| __|
|  _| (_| \__ \ |_
|_|  \__,_|___/\__|


         vs

 ____  _
/ ___|| | _____      __
\___ \| |/ _ \ \ /\ / /
 ___) | | (_) \ V  V /
|____/|_|\___/ \_/\_/

```

Understanding orders of magnitude with computers

---

# Our weak intuition for speed

Everything a computer does seems "fast" when you're a human

---

# Our weak intuition for speed

> Everything a computer does seems "fast" when you're a human

But some operations are millions of times slower than others

---

# Useful diagram

[Latency Numbers Every Programmer Should Know](https://colin-scott.github.io/personal_website/research/interactive_latency.html)

---

# Simplifying it

```
                "Fast"                                                "Slow"


                                               |
               Working with the                |                  accessing the disk
              processor and memory             |                     network call
            (working inside the JVM)           |                       "IO"
                                               |

                                             time -->
```

---

# Waiting on someone else

```
                "Fast"                                                "Slow"


                                               |
               Working with the                |                  accessing the disk
              processor and memory             |                     network call
            (working inside the JVM)           |                       "IO"
                                               |             (usually waiting for something else)

                                             time -->
```

---

# Synchronous vs Asynchronous

```
                "Fast"                                                "Slow"


                                               |
               Working with the                |                  accessing the disk
              processor and memory             |                     network call
            (working inside the JVM)           |                       "IO"
                                               |
                                               |
              "CPU intensive"                  |             (usually waiting for something else)
            Compute synchronously                               Compute asynchronously
                                             time -->
```

We don't want to sit around waiting for these "slow" operations to finish

We could do millions of useful instructions in that time

---

# Summary of this section

Our services do many "slow" tasks which involve waiting on something outside the JVM:

- database call


- http call


- putting a message on a queue

---

# Non-blocking

> Our services do many "slow" tasks which involve waiting on something outside the JVM

While those operations are going on,

we don't want to be sitting there waiting

---

# Non-blocking

> we don't want to be sitting there waiting

ie. run them somewhere else as `Future`'s

and get on with other CPU intensive work

---

# Next question

We've got the basic concepts,

but how do asynchronous libraries work?

---

```
  ____      _ _ _                _
 / ___|__ _| | | |__   __ _  ___| | _____
| |   / _` | | | '_ \ / _` |/ __| |/ / __|
| |__| (_| | | | |_) | (_| | (__|   <\__ \
 \____\__,_|_|_|_.__/ \__,_|\___|_|\_\___/

```

---

# Under the hood

Building efficient asynchronous systems requires some callback concept

e.g. customer support example

---

# Callback

> Building efficient asynchronous systems requires some callback concept

Do this calculation for me somewhere else,

and let me know when you're done

---

# Example

We want to retrieve someone's age from the database,

then print something if they're 18+ (adult)

---

# Database interface

Suppose we already have a way to fetch from the database:

```scala
class PersonDatabase {
  def getPersonById(id: String): Future[Person] = ...
}
```

---

# Fetching our person

```scala
val personF: Future[Person] = personDatabase.getPersonById("0293923423")
```

When that `Future` completes, we want to print something if they're an adult

---

# Solution?

How would you print a value that is _eventually_ returned?

```scala
val personF: Future[Person] = personDatabase.getPersonById("0293923423")


...


// Control has left the original call
```

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Solution?

> How would you print a value that is _eventually_ returned?

Callback!

```scala
val personF: Future[Person] = personDatabase.getPersonById("0293923423")

personF.onComplete {
  ...
}
```

Back when you created the future, leave a callback

To the repl!

---

# Recap of `onComplete`

```scala
val personF: Future[Person] = personDatabase.getPersonById("0293923423")

personF.onComplete {
  case Success(person) => if (person.age >= 18) println("Person is an adult")
  case Failure(ex) => println(s"Failure fetching person: $ex")
}
```

---

```
__        __    _ _   _
\ \      / /_ _(_) |_(_)_ __   __ _
 \ \ /\ / / _` | | __| | '_ \ / _` |
  \ V  V / (_| | | |_| | | | | (_| |
   \_/\_/ \__,_|_|\__|_|_| |_|\__, |
                              |___/
```

---

# Conceptually

```scala
// Thread 1
val x = 1
getPersonById("004293423") // Thread 2
val y = 1
```

```
            x=1  start    y=1  ...
                 Future
Thread 1    ----------------------
                    \
Thread 2             -----------------
                        (going to db)
```

---

# Testing

To simplify things, in our tests it's okay to wait until a future is done

```scala
// Testing a person is 19
val personF = getPersonById("0342934")

val person = Await.result(personF, 10.seconds)

person.age mustBe 19
```

(rather than use a callback)

---

# Waiting = Blocking

```scala
// Thread 1
val x = 1
val personF = getPersonById("004293423") // Thread 2
val y = 1
val person = Await.result(personF, 10.seconds)
```

```
            x=1  start    y=1   Await.result  (waiting...)
                 Future
Thread 1    -----------------------------------------------
                    \                                     /
Thread 2             -------------------------------------
                              (going to db)
```

---

# Quick demo

To the repl!

---

# Danger!

Blocking futures in prod is very dangerous, particularly on the play framework

(Tests are usually okay)

Make sure you know what you're doing

(More on this later)

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

# Future

A library for representing asynchronous computations

---

# Asynchronous = Complexity

But the `Future` library can minimise a lot of the pain you usually have with these libraries

---

# Callbacks

Under the hood async systems work by leaving callbacks

---

# Blocking

Makes sense in tests

Generally doesn't make sense in prod code - very dangerous

---

# Just scratching the surface

There is much more to the `Future` api!

---

# Coming up

- advanced features


- common traps


- futures and the play framework

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \  ?
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
