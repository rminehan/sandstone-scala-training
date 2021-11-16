---
author: Rohan
date: 2021-11-16
title: Future Combinators
---

```
 _____      _                  
|  ___|   _| |_ _   _ _ __ ___ 
| |_ | | | | __| | | | '__/ _ \
|  _|| |_| | |_| |_| | | |  __/
|_|   \__,_|\__|\__,_|_|  \___|
                               
  ____                _     _             _                 
 / ___|___  _ __ ___ | |__ (_)_ __   __ _| |_ ___  _ __ ___ 
| |   / _ \| '_ ` _ \| '_ \| | '_ \ / _` | __/ _ \| '__/ __|
| |__| (_) | | | | | | |_) | | | | | (_| | || (_) | |  \__ \
 \____\___/|_| |_| |_|_.__/|_|_| |_|\__,_|\__\___/|_|  |___/
                                                            
```

ie. advanced tools for working with futures

---

# Recap

We're spending a few sessions on `Future`

as it's used everywhere and important to understand correctly

---

# Last time on scala training...

---

# Future

`Future[A]` is like a tracking id for a computation running somewhere else

that will eventually yield an `A` (or fail)

---

# When to use it

For "asynchronous" computations,

ie. slow things you don't want to be blocked waiting on

---

# When the data arrives

We need a notification to switch back to our earlier processing

We saw one mechanism `onComplete`

```scala
val personF: Future[Person] = getPersonById("131923123")

personF.onComplete {
  case Success(person) => println(s"Got a person! $person")
  case Failure(ex) => println(s"Failure encountered getting person from database: $ex")
}
```

---

# Today

Look at much more powerful ways to register callbacks ("combinators")

---

# Our previous problem

Find a person in the database and print something if they're 18+

```scala
val personF: Future[Person] = getPersonById("131923123")

personF.onComplete {
  case Success(person) => if (person.age >= 18) println("Got an adult!")
  case Failure(ex) => println(s"Failure encountered getting person from database: $ex")
}
```

---

# Modified problem

Write a method which looks up a user by id and returns if they're an adult

(Assume we already have a way to look up users from the database)

---

# Return type

> Write a method which looks up a user by id and returns if they're an adult

"looks up a user by id" is a db call (async) meaning `Future[...]`

"if they're an adult" means `Boolean`

---

# Contract

```scala
def isAdult(id: String): Future[Boolean] = ???
```

---

# Implementing it

We already have a way to fetch a person from the database:

```scala
def isAdult(id: String): Future[Boolean] = {
  personDatabase.getPersonById(id). ???  // Transform somehow using `person.age >= 18`
}
```

The bit we need is transforming that to a Boolean for whether they're an adult

---

# Conceptually

What we have:

```scala
personDatabase.getPersonById(id)    Future[Person]
person => person.age >= 18          Person => Boolean
```

What we want:

```scala
???                                 Future[Boolean]
```

What concept is this?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Mapping over the structure

> What concept is this?

Functor!

```
                      map f
F[A]                ------>     F[B]
f: A => B
```

Quick recap:

```
                      map f
List[String]        ------>     List[Int]
f: String => Int
   _.length
```

Example:

```scala
List("hi", "there", "you")      List(2, 5, 3)
  .map(_.length)
```

Applying `f` "inside the square brackets"

---

# Applying to Future

```
                      map f
F[A]                ------>     F[B]
f: A => B
```

Substitute:

- `F` = `Future`
- `A` = `Person`
- `B` = `Boolean`

```
Future[Person]      ----->      Future[Boolean]
f: Person => Boolean
```

Hopefully `Future` is a functor with a `map`!

---

# To the repl!

---

# Recap of demo

`Future` is a functor with a `map`

---

# The mapped future

`map` itself produces a `Future`

```
Future[A]    ----->   Future[B]
f: A => B
```

---

# The mapped future

The new `Future` represents the original computation plus an additional synchronous computation

ie. it will yield a value once:

- the original computation yields a value


- the mapping logic is applied

---

# Structure preserving

```
Success(a)   ----->      Success(f(a))


Failure(ex)  ----->      Failure(ex)
```

(Similar to Option)

---

# With one caveat

```
             nice f
Success(a)   ----->      Success(f(a))
             bad f
             ----->      Failure(...)

Failure(ex)  ----->      Failure(ex)
```

So it can "change" structure

(Usually `f` is pure though)

To the repl!

---

```
  ____ _           _                _
 / ___| |__   __ _(_)_ __   ___  __| |   __ _ ___ _   _ _ __   ___
| |   | '_ \ / _` | | '_ \ / _ \/ _` |  / _` / __| | | | '_ \ / __|
| |___| | | | (_| | | | | |  __/ (_| | | (_| \__ \ |_| | | | | (__
 \____|_| |_|\__,_|_|_| |_|\___|\__,_|  \__,_|___/\__, |_| |_|\___|
                                                  |___/
```

Nested futures

---

# Modify our example

Find age of person's next of kin

```scala
case class Person(id: String, name: String, age: Int, nextOfKinId: String)

def findNextOfKinAge(id: String): Future[Int] = ...
```

---

# General approach

```scala
case class Person(id: String, name: String, age: Int, nextOfKinId: String)

def findNextOfKinAge(id: String): Future[Int] = ...
```

> Find age of person's next of kin

- look up user from the database using id


- get the id of their next of kin


- look up next of kin from the database with that id


- yield the age of the next of kin

---

# First attempt

```scala
case class Person(id: String, name: String, age: Int, nextOfKinId: String)

def findNextOfKinAge(id: String): Future[Int] = {
  getPersonById(id).map { person =>
    getPersonById(person.nextOfKinId).map { nextOfKin =>
      nextOfKin.age
    }
  }
}
```

Try it in the repl!

---

# Recap

`map` didn't work

```
Future[Person]              ------->   Future[Future[Int]]
f: Person => Future[Int]
```

---

# Analogous to

```
                         |  |1 1|  |2 2|  |
                         |  |1 1|  |2 2|  |
            map f        |                |
            ---->        |  |3 3|  |4 4|  |
                         |  |3 3|  |4 4|  |
                         |                |
                         |  |5 5|  |6 6|  |
|  1  2  |               |  |5 5|  |6 6|  |
|  3  4  |
|  5  6  |


                         | 1 1 2 2 |
                         | 1 1 2 2 |
                         | 3 3 4 4 |
          flatMap f      | 3 3 4 4 |
           ---->         | 5 5 6 6 |
                         | 5 5 6 6 |


where

             |  a  a  |
 f: a  --->  |  a  a  |    Int => Matrix[Int]
```

---

# What does `Future[Future[...]]` mean?

The package arrives with a tracking id inside

---

# Aside: Similar structure

Remember our 2D example example:

```
          c ---->
 r  (0,0)  (1,0)  (2,0)  (3,0)
 |  (0,1)  (1,1)  (2,1)  (3,1)
\|/ (0,2)  (1,2)  (2,2)  (3,2)

     \/

    (0,0)
    (0,1)
    (0,2)
    (1,0)
    (1,1)
    (1,2)
    ...
    (3,2)
```

---

# First attempt with map

```scala
(0 until 4).map(c =>
  (0 until 3).map(r => (c, r))
)
```

```
|   0    |    1    |    2    |    3    |   List[...]

                  map(...)

| (0,0)  |  (1,0)  |  (2,0)  |  (3,0)  |
| (0,1)  |  (1,1)  |  (2,1)  |  (3,1)  |   List[List[...]]
| (0,2)  |  (1,2)  |  (2,2)  |  (3,2)  |
```

---

# Second attempt with flatMap

```scala
(0 until 4).flatMap(c =>
  (0 until 3).map(r => (c, r))
)
```

```
|   0    |    1    |    2    |    3    |   List[...]

                  flatMap(...)

| (0,0)  |  (1,0)  |  (2,0)  |  (3,0)  |
| (0,1)  |  (1,1)  |  (2,1)  |  (3,1)  |
| (0,2)  |  (1,2)  |  (2,2)  |  (3,2)  |


              |         |         |
  (0,0)       |         |         |       List[...]
  (0,1)       |         |         |
  (0,2)       |         |         |
  (1,0) <-----          |         |
  (1,1)                 |         |
  (1,2)                 |         |
  (2,0) <---------------          |
  (2,1)                           |
  (2,2)                           |
  (3,0) <-------------------------
  (3,1)
  (3,2)
```

---

# Why I mention this

Future and List examples are both "double nested"

The solution is to use `flatMap`

```scala
// Second attempt
foo.flatMap(...
  bar.map(...)
)
```

---

# Fix it!

To the repl!

---

# map vs flatMap

In the context of `Future`:

```scala
// Synchronous
getPersonById(id).map { _.age >= 18 }

// Asynchronous
getPersonById(id).flatMap { person =>
  getPersonById(person.nextOfKinId) ...
}
```

---

```
  __
 / _| ___  _ __
| |_ / _ \| '__|
|  _| (_) | |
|_|  \___/|_|

```

`Future` is a monad

We can use it with `for`!

---

# Our code

```scala
// New
def findNextOfKinAge(id: String): Future[Int] = {
  for {
    person <- getPersonById(id)
    nextOfKin <- getPersonById(person.nextOfKinId)
  } yield nextOfKin.age
}


// Old
def findNextOfKinAge(id: String): Future[Int] = {
  getPersonById(id).flatMap { person =>
    getPersonById(person.nextOfKinId).map { nextOfKin =>
      nextOfKin.age
    }
  }
}
```

(Difference is more pronounced as you add more and more steps)

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

# Functor and Monad

Future is a functor and a monad!

ie. has `map` and `flatMap`

---

# for

> Future is a functor and a monad!

Future integrates beautifully with the `for` comprehension

---

# map vs flatMap

Use:

- `map` for synchronous callbacks


- `flatMap` for asynchronous callbacks

---

# Next time

More combinators!

---

```
  ___                  _   _                 
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___ 
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \  ?
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
                                             
```
