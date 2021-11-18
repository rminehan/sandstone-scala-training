---
author: Rohan
date: 2021-11-17
title: More Future Combinators
---

```
 __  __                
|  \/  | ___  _ __ ___ 
| |\/| |/ _ \| '__/ _ \
| |  | | (_) | | |  __/
|_|  |_|\___/|_|  \___|
                       
  ____                _     _             _                 
 / ___|___  _ __ ___ | |__ (_)_ __   __ _| |_ ___  _ __ ___ 
| |   / _ \| '_ ` _ \| '_ \| | '_ \ / _` | __/ _ \| '__/ __|
| |__| (_) | | | | | | |_) | | | | | (_| | || (_) | |  \__ \ !
 \____\___/|_| |_| |_|_.__/|_|_| |_|\__,_|\__\___/|_|  |___/
                                                            
```

---

# Recap

Saw `map` and `flatMap`

---

# map

For synchronous continuations/callbacks

```scala
getPersonById("000").map(_.age)
```

---

# flatMap

For asynchronous continuations/callbacks

```scala
getPersonById("000").flatMap { person =>
  getPersonById(person.nextOfKinId)
}
```

---

# Today

- recovering from failure


- lifting


- sequencing

---

```
 ____                              _             
|  _ \ ___  ___ _____   _____ _ __(_)_ __   __ _ 
| |_) / _ \/ __/ _ \ \ / / _ \ '__| | '_ \ / _` |
|  _ <  __/ (_| (_) \ V /  __/ |  | | | | | (_| |
|_| \_\___|\___\___/ \_/ \___|_|  |_|_| |_|\__, |
                                           |___/ 
  __                     
 / _|_ __ ___  _ __ ___  
| |_| '__/ _ \| '_ ` _ \ 
|  _| | | (_) | | | | | |
|_| |_|  \___/|_| |_| |_|
                         
  __       _ _                
 / _| __ _(_) |_   _ _ __ ___ 
| |_ / _` | | | | | | '__/ _ \
|  _| (_| | | | |_| | | |  __/
|_|  \__,_|_|_|\__,_|_|  \___|
                              
```

---

# map structure

```
Failure(ex) --->  Failure(ex)

Success(a)  --->  Success(f(a))
```

(Assuming `f` is pure)

---

# flatMap?

Second future could fail

```scala
getPersonById("000").flatMap { person =>
  getPersonById(person.nextOfKinId) // Might fail
}
```

---

# flatMap

```
Failure(ex1) --->  Failure(ex1)

Success(a)   --->  Success(b)

             --->  Failure(ex2)
```

---

# So far

## map

```
Failure(ex) --->  Failure(ex)

Success(a)  --->  Success(f(a))
```

## flatMap

```
Failure(ex1) --->  Failure(ex1)

Success(a)   --->  Success(b)

             --->  Failure(ex2)
```

## Observation

No way to go from a `Failure` to a `Success`

---

# Scenario

We have a fast (but unreliable) library we use for fetching transactions

```scala
def fetchTransactionsUnsafe(userId: String): Future[Seq[Transaction]] = ...
```

It frequently encounters errors like time outs and network issues etc...

---

# Problem 1

Write a function to get an updated version of the user's transactions,

but fallback to the transactions passed if there's a failure

```scala
def getUpdatedTransactions(userId: String, currentTransactions: Seq[Transaction]): Future[Seq[Transaction]] = ???
```

(perhaps in an analytics context)

---

# Introducing `recover`

```scala
def getUpdatedTransactions(userId: String, currentTransactions: Seq[Transaction]): Future[Seq[Transaction]] = {
  fetchTransactionsUnsafe(userId).recover {
    case ex: Exception =>
      logger.warn(s"[userId=$userId] Failure fetching up to date transactions")
      currentTransactions
  }
}
```

```
Success(freshTransactions)   --->    Success(freshTransactions)

Failure(ex)                  --->    Success(currentTransactions)
```

---

# Failure recovering

But recover might fail

```
Success(freshTransactions)   --->    Success(freshTransactions)

Failure(ex1)                 --->    Success(currentTransactions)

                             --->    Failure(ex2)
```

---

# Recap of `recover`

---

# Happy path

Gets your chain back on the "happy path"

```
Success(freshTransactions)   --->    Success(freshTransactions)

Failure(ex1)                 --->    Success(currentTransactions)

                             --->    Failure(ex2)
```

---

# Combinator

`recover` returns a `Future`

Can chain on the back of that

---

# Synchronous

Use with synchronous logic

```scala
def getUpdatedTransactions(userId: String, currentTransactions: Seq[Transaction]): Future[Seq[Transaction]] = {
  fetchTransactionsUnsafe(userId).recover {
    case ex: Exception =>
      logger.warn(s"[userId=$userId] Failure fetching up to date transactions")
      currentTransactions
  }
}
```

---

# Problem 2

We just built a new library for fetching transactions

```scala
def fetchTransactionsSafe(userId: String): Future[Seq[Transaction]] = ...
//                   ^^^^
```

It is many times slower, but is very reliable (99.9%)

We can reimplement our method...

---

# New Strategy

- Try the fast unstable library first (asynchronous)


- If it fails, fallback to the slow reliable library (also asynchronous)

---

# Introducing recoverWith

```diff
-def getUpdatedTransactions(userId: String, currentTransactions: Seq[Transaction]): Future[Seq[Transaction]] = {
+def getUpdatedTransactions(userId: String): Future[Seq[Transaction]] = {
-  fetchTransactionsUnsafe(userId).recover {
+  fetchTransactionsUnsafe(userId).recoverWith {
     case ex: Exception =>
       logger.warn(s"[userId=$userId] Failure fetching up to date transactions")
-      currentTransactions
+      fetchTransactionsSafe(userId)
   }
 }
```

---

# recoverWith

```scala
def getUpdatedTransactions(userId: String): Future[Seq[Transaction]] = {
  fetchTransactionsUnsafe(userId).recoverWith {
    case ex: Exception =>
      logger.warn(s"[userId=$userId] Failure fetching up to date transactions")
      fetchTransactionsSafe(userId)
  }
}

def fetchTransactionsSafe(userId: String): Future[Seq[Transaction]] = ...
```

Use when recovery requires another async call (ie. a `Future`)

`recoverWith` is like the flatMap of recovering

---

# Comparing them

## recover

Use with synchronous logic

e.g.

- you know the recovery value in advance


- you can compute the recovery value using what you already have available in memory

## recoverWith

Use with asynchronous logic

e.g.

- need to use another http call


- need another database lookup

---

# Partial recovering

You don't have to recover from every single kind of error

---

# Modify the scenario

If the fast unstable library fails, only recover if it's an arithmetic error

---

# Partial recovering

> If the fast unstable library fails, only recover if it's an arithmetic error

```diff
 def getUpdatedTransactions(userId: String): Future[Seq[Transaction]] = {
   fetchTransactionsUnsafe(userId).recoverWith {
-    case ex: Exception =>
+    case ex: ArithmeticException =>
-      logger.warn(s"[userId=$userId] Failure fetching up to date transactions")
+      logger.warn(s"[userId=$userId] Arithmetic failure fetching up to date transactions: $ex")
       fetchTransactionsSafe(userId)
   }
 }
```

---

# Error flow

```scala
def getUpdatedTransactions(userId: String): Future[Seq[Transaction]] = {
  fetchTransactionsUnsafe(userId).recoverWith {
    case ex: ArithmeticException =>
      logger.warn(s"[userId=$userId] Arithmetic failure fetching up to date transactions: $ex")
      fetchTransactionsSafe(userId)
  }
}
```

```
Success(transactions)          ---------->     Success(transactions)

Failure(ArithmeticException)   ---------->     Success(transactions)
                               ---------->     Failure(ex2)

Failure(ex1)                   ---------->     Failure(ex1)
   ex1 is any
   exception that isn't
   an ArithmeticException
```

---

# Terminology: Partial function

`recover` and `recoverWith` takes "partial functions"

```scala
doSomething.recoverWith {
  case ex: ArithmeticException => ...
  case ex: IllegalArgumentException => ...
}
```

It doesn't have to be defined on all inputs

`collect` is similar

---

# Recap of recovery

Used to get you back into `Success`-ville

---

# recover vs recoverWith

`recover` (synchronous), analogous to `map`

`recoverWith` (asynchronous), analogous to `flatMap`

---

# Partial functions

You can use different recovery logic for different kinds of errors

You can also just _not_ recover from some errors

---

```
 _     _  __ _   _             
| |   (_)/ _| |_(_)_ __   __ _ 
| |   | | |_| __| | '_ \ / _` |
| |___| |  _| |_| | | | | (_| |
|_____|_|_|  \__|_|_| |_|\__, |
                         |___/ 
```

---

# "Lifting"

Lifting a simple value into the context of a monad

```scala
// Lifting 3 into a List
List(3)

// Lifting "hi" into an Option
Some("hi")

// Lifting true into a Try
Success(true)
```

---

# More generally

This is `pure` from monad:

```
          flatMap f
M[A]      ----->     M[B]
f: A => M[B]

           pure
A         ----->     M[A]
```

```scala
// Int    ----->     List[Int]
List(3)

// String ----->     Option[String]
Some("hi")

// Boolean ---->     Try[Boolean]
Success(true)
```

---

# Lifting

Lifting a simple value into the context of the monad

---

# Why is lifting useful?

See an example

---

# Back to our transactions

```scala
case class Person(id: String, isRegistered: Boolean, ...)
```

We know that if a person isn't registered, then they will have no transactions

---

# Implementing getTransactions

```scala
def getUpdatedTransactions(person: Person): Future[Seq[Transaction]] = ???
```

Strategy:

- if the person is registered, get their transactions from the database (asynchronous)


- if the person isn't registered, return an empty sequence of transactions (synchronous)

---

# Implement it

To the repl!

---

# Future.successful

A "trick" to lift synchronous values into asynchronous futures

The future starts its life in the completed successful state

---

# Comparing approaches

```scala
val precomputed = 3

// Wasteful
Future { precomputed }
// Like using a truck to deliver a pea


// Efficient and clearer
Future.successful(precomputed)
```

---

# Pure

`Future.successful` is the pure of `Future`

```
           pure
A         ----->     M[A]


         Future.successful
A         ----->     Future[A]
```

---

# Type gymnastics

Often your code is a mixture is a mixture of synchronous and asynchronous code paths

To unify those code paths,

we "lift" the synchronous code paths into futures

```scala
def getUpdatedTransactions(person: Person): Future[Seq[Transaction]] = {
  if (person.isRegistered) getTransactionsForPerson(person.id)
  else Future.successful(Seq.empty)
}
```

---

```
 _____                                  
|_   _| __ __ ___   _____ _ __ ___  ___ 
  | || '__/ _` \ \ / / _ \ '__/ __|/ _ \
  | || | | (_| |\ V /  __/ |  \__ \  __/
  |_||_|  \__,_| \_/ \___|_|  |___/\___|
                                        
                 _ 
  __ _ _ __   __| |
 / _` | '_ \ / _` |
| (_| | | | | (_| |
 \__,_|_| |_|\__,_|
                   
 ____                                       
/ ___|  ___  __ _ _   _  ___ _ __   ___ ___ 
\___ \ / _ \/ _` | | | |/ _ \ '_ \ / __/ _ \
 ___) |  __/ (_| | |_| |  __/ | | | (_|  __/
|____/ \___|\__, |\__,_|\___|_| |_|\___\___|
               |_|                          
```

---

# Example

We have a list of id's and we want to get a list of people from the database

```scala
def getPersonById(id: String): Future[Person] = ...

val ids = List("000", "111", "222")

val peopleF: Future[List[Person]] = ???
```

---

# Conceptually

```
                ???
List[String]  ------>    Future[List[Person]]


List(                           List(
  "000",                          boban,
  "111",                          bobanita,
  "222",                          simon
)                               )
```

Feels like a map

---

# Try to implement it

To the repl!

---

# Hmmm...

```
                ???
List[String]  ------>    Future[List[Person]]


List(                           List(
  "000",                          boban,
  "111",                          bobanita,
  "222",                          simon
)                               )
```

```scala
def getPersonById(id: String): Future[Person] = ...

val ids = List("000", "111", "222")

ids.map { id => getPersonById(id) }
// List[Future[Person]]
```

```
                                  map f
F[A]                            --------->  F[B]
f: A => B

                                  map f
List[String]                    --------->  List[Future[Person]]
f: String => Future[Person]
```

but we want `Future[List[Person]]`

---

# What's the difference?

`Future[List[...]]` vs `List[Future[...]]`

---

# Analogy

Imagine ordering a bookshelf from ikea

## Future[List[...]]

A single package arrives container your bookshelf in pieces

## List[Future[...]]

Several packages arrive, each one containing one piece of your bookshelf

---

# Which one is better?

## Future[List[...]]

> A single package arrives container your bookshelf in pieces

Only have to wait for one package to arrive

## List[Future[...]]

> Several packages arrive, each one containing one piece of your bookshelf

Have to track several packages

---

# From a coding perspective

## Future[List[...]]

Can use with future combinators (map, flatMap, recover, etc...)

## List[Future[...]]

We can't do anything useful with the individual futures

---

# Overall

We want `Future` on the outside

`Future[List[...]]`

---

# Overall

We want `Future` on the outside

But `map` doesn't give us that:

```scala
def getPersonById(id: String): Future[Person] = ...

val ids = List("000", "111", "222")

ids.map { id => getPersonById(id) }
// List[Future[Person]]
```

---

# Solution 1

`Future.sequence`

To the repl!

---

# Summary

`Future.sequence` does the switcheroo:

```scala
val listOfFutures: List[Future[Int]] = ...

val futureOfList: Future[List[Int]] = Future.sequence(listOfFutures)
```

---

# Solution 2

Get there more directly with `Future.traverse`

To the repl!

---

# Traverse

`Future.traverse` builds and sequences in one step

```scala
val futureOfList: Future[List[Person]] = Future.traverse(ids) { id => getPersonById(id) }
```

Cuts out the middle man

---

# sequence vs traverse

Sometimes you're too late to use traverse:

```scala
def doSomething(futures: List[Future[String]]): Unit = {
  val listF = Future.sequence(futures)
  ...
}
```

But if you're the one building it, it's more direct to use traverse

---

# Failure

If one fails, the whole batch fails

To the repl!

---

# Summary of Future.sequence and Future.traverse

Use them to "switcheroo" your sequence with your Future

Very useful when you have to kick off a list of asynchronous tasks

---

# All must succeed

All sequenced futures must succeed

---

# Scratching the surface

Traverse and Sequence are much more universal concepts

And sequence is just a special case of traverse

(That is for another day)

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

# Recovering from errors

`recover` (synchronous) and `recoverWith` (asynchronous)

---

# Lifing

`Future.successful` lifts synchronous computations into asynchronous ones

Good for type gymnastics

---

# Sequence and Traverse

Use them to "switcheroo" your types

e.g. `Seq[Future[...]]` to `Future[Seq[...]]`

---

# Next time

Going under the hood

Threads, cores, execution contexts etc...

---

```
  ___                  _   _                 
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___ 
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \ ?
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
                                             
```
