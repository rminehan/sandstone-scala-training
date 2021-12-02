---
author: Rohan
date: 2021-12-03
title: Concurrency Gotchas
---

```
  ____
 / ___|___  _ __   ___ _   _ _ __ _ __ ___ _ __   ___ _   _
| |   / _ \| '_ \ / __| | | | '__| '__/ _ \ '_ \ / __| | | |
| |__| (_) | | | | (__| |_| | |  | | |  __/ | | | (__| |_| |
 \____\___/|_| |_|\___|\__,_|_|  |_|  \___|_| |_|\___|\__, |
                                                      |___/
  ____       _       _
 / ___| ___ | |_ ___| |__   __ _ ___
| |  _ / _ \| __/ __| '_ \ / _` / __|
| |_| | (_) | || (__| | | | (_| \__ \
 \____|\___/ \__\___|_| |_|\__,_|___/

```

---

# Today

I'll show you examples concurrency code

Spot the issue

---

# Spot the issue

Might be a:

- bug


- code smell

---

# Why?

Gets us thinking

Helps us appreciate the subtleties of multi-threading

---

# MVP?

Most Valued Player award

(Just guess and compliment me a lot)

---

# Prize for MVP?

Maybe Simon will give you some cash

---

# Assumption

We have:

```scala
case class Person(...)

// Fetches from database
def getPersonById(id: String): Future[Person] = ...

// Saves a person
def savePerson(person: Person): Future[Unit] = ...
```

(and other stubs like this)

---

# Code samples

`gotchas.sc` is a compiling script showing off the gotchas

---

```
  ____       _       _
 / ___| ___ | |_ ___| |__   __ _
| |  _ / _ \| __/ __| '_ \ / _` |
| |_| | (_) | || (__| | | | (_| |
 \____|\___/ \__\___|_| |_|\__,_|

                 _
                / |
                | |
                | |
                |_|

```

Fetching Boban and Bobanita

---

# Gotcha 1

```scala
for {
  boban <- getPersonById("000")
  bobanita <- getPersonById("111")
} yield ...
```

---

# Answer

```scala
for {
  boban <- getPersonById("000")
  bobanita <- getPersonById("111")
} yield ...
```

We don't start fetching Bobanita until Boban has arrived

These steps don't depend on each other and could be done in parallel

---

# Takes longer

```scala
for {
  boban <- getPersonById("000")
  bobanita <- getPersonById("111")
} yield ...
```

> We don't start fetching Bobanita until Boban has arrived

If it takes roughly 2ms to fetch a user,

then this takes roughly 4ms

---

# Order switching

```scala
for {
  boban <- getPersonById("000")
  bobanita <- getPersonById("111")
} yield ...
```

We could switch the order:

```scala
for {
  bobanita <- getPersonById("111")
  boban <- getPersonById("000")
} yield ...
```

Being able to switch the order of steps is often a sign of this gotcha

---

# Contrast

Consider this instead:

```scala
for {
  boban <- getPersonById("000")
  nextOfKin <- getPersonById(boban.nextOfKinId)
} yield ...
```

---

# Contrast

Could we swap the steps?

```scala
for {
  boban <- getPersonById("000")
  nextOfKin <- getPersonById(boban.nextOfKinId)
} yield ...

// Swapped

for {
  nextOfKin <- getPersonById(boban.nextOfKinId)
  boban <- getPersonById("000")
} yield ...
```

Doesn't make sense

---

# Independent vs Dependent

Can't switch the order

## Independent

ie. parallel

```scala
for {
  boban <- getPersonById("000")
  bobanita <- getPersonById("111")
} yield ...
```

Cheater's flatMap (Applicative)

## Dependent

ie. sequential

```scala
for {
  boban <- getPersonById("000")
  nextOfKin <- getPersonById(boban.nextOfKinId)
} yield ...
```

True flatMap

---

# Solution?

Start the futures independently before you flatMap:

```scala
val bobanF = getPersonById("000")
val bobanitaF = getPersonById("111")

for {
  boban <- bobanF
  bobanita <- bobanitaF
} yield ...
```

Ugly, but much faster and communicates the independence to the reader

---

# Recap of gotcha 1

Sometimes independent computations can be started sequentially:

```scala
for {
  boban <- getPersonById("000")
  bobanita <- getPersonById("111")
} yield ...
```

We miss an opportunity to run slow IO steps in parallel

---

```
  ____       _       _
 / ___| ___ | |_ ___| |__   __ _
| |  _ / _ \| __/ __| '_ \ / _` |
| |_| | (_) | || (__| | | | (_| |
 \____|\___/ \__\___|_| |_|\__,_|


         ____
        |___ \
          __) |
         / __/
        |_____|
```

Update person in db

---

# Gotcha 2

```scala
def incrementAge(id: String): Future[Unit] = {
  getPersonById(id).map { person =>
    savePerson(person.copy(age = person.age + 1))
  }
}
```

---

# Should be flatMap

```scala
def incrementAge(id: String): Future[Unit] = {
  getPersonById(id).map { person =>
    //              ^^^
    savePerson(person.copy(age = person.age + 1))
  }
}
```

---

# Map

```scala
def incrementAge(id: String): Future[Unit] = {
  getPersonById(id).map { person =>                // |
    savePerson(person.copy(age = person.age + 1))  // | Future[Future[Unit]]
  }                                                // |
}
```

```
F[A]           map f     F[B]
f: A => B     ------>

F = Future
A = Person
B = Future[Unit]

Future[Person]              map f    Future[Future[Unit]]
f: Person => Future[Unit]  ------->
```

---

# Compiler asleep on the job?

Then how does it compile?

```scala
def incrementAge(id: String): Future[Unit] = {
//                            ^^^^^^^^^^^^ return type

  getPersonById(id).map { person =>                // |
    savePerson(person.copy(age = person.age + 1))  // | Future[Future[Unit]]
  }                                                // |
}
```

---

# Value Discarding

> Then how does it compile?

```scala
def incrementAge(id: String): Future[Unit] = {
//                            ^^^^^^^^^^^^ return type

  getPersonById(id).map { person =>                // |
    savePerson(person.copy(age = person.age + 1))  // | Future[Future[Unit]]
  }                                                // |
}
```

Compiler is being "helpful" in letting it be `Future[Unit]`

Wouldn't happen if `savePerson` returned `Future[something else]`

(see notes in `gotchas.sc` about Unit and value discarding)

---

# Very nasty bug

```scala
for {
  _ <- incrementAge("000")
  _ = println("Age is incremented")
} yield ...


def incrementAge(id: String): Future[Unit] = {
  getPersonById(id).map { person =>
    savePerson(person.copy(age = person.age + 1))
  }
}
```

---

# Stray callback

```scala
for {
  _ <- incrementAge("000")
  _ = println("Age is incremented")
  ...
} yield ...
```

## Expected

```
                getPersonById(id)
                      |
                  savePerson(...)
                      |
            println("Age is incremented")
                      |
                     ...
```

## Actual

```
                 getPersonById(id) ---------
                       |                     \ detached from pipeline
       println("Age is incremented")      savePerson(...)
                       |
                      ...
```

Not sequenced in with the others

---

# Non-deterministic

Unclear when it will finish

## Happens to be right

```
                 getPersonById(id) ---------
                       |                     \
                       |                  savePerson(...)
                       |
       println("Age is incremented")
```

Often what happens in unit tests

## Later

```
                 getPersonById(id) ---------
                       |                     \
       println("Age is incremented")          |
                       |                      |
                      ...                 savePerson(...)

```

Now observers have acted on this before the data was saved

---

# Demo code

Run the demo to see it in action

---

# Recap

Be careful not to map into `Future[Unit]`

Nasty concurrency bug the compiler won't catch

---

```
  ____       _       _
 / ___| ___ | |_ ___| |__   __ _
| |  _ / _ \| __/ __| '_ \ / _` |
| |_| | (_) | || (__| | | | (_| |
 \____|\___/ \__\___|_| |_|\__,_|


         _____
        |___ /
          |_ \
         ___) |
        |____/

```

Get transactions

---

# Gotcha 3

```scala
def getTransactions(person: Person): Future[Seq[Transaction]] = {
  if (person.isRegistered) getTransactionsFromDatabase(person)
  else if (person.isTrialUser) throw new CustomerException("...")
  else Future(Seq.empty)
}

// Typical usage
getTransactions(boban).recover {
  case CustomerException(_) =>
    log.error("Got customer exception...")
    ...
}

// Assume this exists
def getTransactionsFromDatabase(id: String): Future[Seq[Transaction]]
```

---

# Two gotchas!

---

# First gotcha

Future.successful!

```scala
def getTransactions(person: Person): Future[Seq[Transaction]] = {
  if (person.isRegistered) getTransactionsFromDatabase(person)
  else if (person.isTrialUser) throw new CustomerException("...")
  else Future(Seq.empty)
  //   ^^^^^^
}
```

## apply

`Future(...)` is really `Future.apply(...)(ec)`

ie. schedules a thunk to run on an ec

## successful

`Future.successful`:

- makes it clearer you're "lifting"


- avoids all that overhead

---

# Second gotcha

```scala
def getTransactions(person: Person): Future[Seq[Transaction]] = {
  if (person.isRegistered) getTransactionsFromDatabase(person)
  else if (person.isTrialUser) throw new CustomerException("...")
  //                           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  else Future(Seq.empty)
}

// Typical usage
getTransactions(boban).recover {
  case CustomerException(_) =>
    log.error("Got customer exception...")
    ...
}
```

Throws an exception in the wrong place

---

# Comparison

```scala
def getTransactions(person: Person): Future[Seq[Transaction]] = {
  if (person.isRegistered) getTransactionsFromDatabase(person)

  else if (person.isTrialUser) throw new CustomerException("...")          // A
  else if (person.isTrialUser) Future.failed(new CustomerException("...")) // B

  else Future(Seq.empty)
}

// Typical usage
getTransactions(boban).recover {
  case CustomerException(_) =>
    log.error("Got customer exception...")
    ...
}
```

## A

Fails to create a Future

## B

Creates a failed Future

---

# Simplify it

```scala
// A
def getTransactions(person: Person): Future[Seq[Transaction]] = {
  throw new CustomerException("...")
}

// B
def getTransactions(person: Person): Future[Seq[Transaction]] = {
  Future.failed(new CustomerException("..."))
}

getTransactions(boban).recover {
  case CustomerException(_) =>
    log.error("Got customer exception...")
    ...
}
```

## A

Fails to create a Future

Thread driving `getTransactions` blows up with an exception

`recover` is never called

## B

Creates a failed Future

Thread driving `getTransactions` returns gracefully with a failed `Future`

`recover` catches the error in the future

---

# Recap

We can be sloppy with _where_ exceptions are thrown

- the thread scheduling the Future


- the thread running the Future

Your error handling code will usually assume the latter

---

```
  ____       _       _
 / ___| ___ | |_ ___| |__   __ _
| |  _ / _ \| __/ __| '_ \ / _` |
| |_| | (_) | || (__| | | | (_| |
 \____|\___/ \__\___|_| |_|\__,_|


              _  _
             | || |
             | || |_
             |__   _|
                |_|

```

Deactivation

---

# Gotcha 4

```scala
def deactiveUsers(users: Seq[Person]): Future[Unit] = {
  Future {
    users.foreach { user =>
      savePerson(user.copy(isActive = false))
    }
  }
}
```

---

# Stray futures again

```scala
def deactiveUsers(users: Seq[Person]): Future[Unit] = {
  Future {
    users.foreach { user =>
      savePerson(user.copy(isActive = false))
    }
  }
}
```

```
caller         ---

thread doing      ------ "I'm done"
Future {... }      schedule other jobs

                  -------------------
individual         -----------------------
threads for         ---------------(fail)
 different           -------------------------
 updates              ----------------
```

---

# Concurrency bug

```scala
for {
  _ <- deactiveUsers(users)
  _ = println("Users have all been deactivated") // Doubt it
}

def deactiveUsers(users: Seq[Person]): Future[Unit] = {
  Future {
    users.foreach { user =>
      savePerson(user.copy(isActive = false))
    }
  }
}
```

Will print "Users have all been deactivated" before any
of the writes to the database have completed

```
caller         ---

thread doing      ------ "I'm done"
Future {... }      schedule other jobs

                  -------------------
individual         -----------------------
threads for         ---------------(fail)
 different           -------------------------
 updates              ----------------
```

---

# Fix?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# traverse!

```scala
def deactiveUsers(users: Seq[Person]): Future[Unit] = {
  Future.traverse(users) { user =>
    savePerson(user.copy(isActive = false))
  }.map { _ => () } // deal with Future[List[Unit]]
}
```

Generates a future that completes when all individual ones complete

---

# Recap

It's easy to create stray futures that are detached from your main flow

Particularly when `Unit` is involved

---

```
  ____       _       _
 / ___| ___ | |_ ___| |__   __ _
| |  _ / _ \| __/ __| '_ \ / _` |
| |_| | (_) | || (__| | | | (_| |
 \____|\___/ \__\___|_| |_|\__,_|


      ____
     | ___|
     |___ \
      ___) |
     |____/

```

---

# Gotcha 5

```scala
import scala.concurrent.blocking

val lock = new Object

def refillPhotocopier(): Future[Unit] = Future {
  val paper = getPaper
  // Signal we're blocking in case of lock contention
  blocking {
    lock.synchronized {
      photocopier.refill(paper) // Long running
    }
  }
}
```

---

# Not always blocked though

```scala
import scala.concurrent.blocking

val lock = new Object

def refillPhotocopier(): Future[Unit] = Future {
  val paper = getPaper
  // Signal we're blocking in case of lock contention
  blocking {
    lock.synchronized {
      photocopier.refill(paper) // Long running
    }
  }
}
```

- blocked whilst waiting for lock (ie. not using CPU)


- not blocked whilst refilling photocopier (ie. using CPU)

---

# Resource contention

```scala
blocking {
  lock.synchronized {
    photocopier.refill(paper) // Long running
  }
}
```

> not blocked whilst refilling photocopier (ie. using CPU)

Manager will think there's CPU available

Will create another thread

Some context switching, resource contention etc...

(Just one thread though)

---

# And don't forget...

`blocking` construct might not work anyway

```scala
blocking {
  lock.synchronized {
    photocopier.refill(paper) // Long running
  }
}
```

Depends on the execution context

Dangerous to use because someone might modify the execution context

---

# Recap

```scala
blocking {
  lock.synchronized {
    photocopier.refill(paper) // Long running
  }
}
```

`blocking` assumes everything inside it is blocking

We only want to signal we're blocking whilst _waiting_ for the lock though

Not once we have it

---

```
  ____       _       _
 / ___| ___ | |_ ___| |__   __ _
| |  _ / _ \| __/ __| '_ \ / _` |
| |_| | (_) | || (__| | | | (_| |
 \____|\___/ \__\___|_| |_|\__,_|


        __
       / /_
      | '_ \
      | (_) |
       \___/

```

Racing horses

---

# Gotcha 6

```scala
implicit val ec = ... // Fixed size thread pool

val horsesF = Future.traverse(1 to 8) { horseNumber =>
  Future {
    val horse = Horse(horseNumber)

    waitUntilGun()

    runRunRun()
  }
}

val startF = Future {
  loadGun()

  fireGun()
}

Await.result(horsesF, 10.seconds)
```

---

# Gun may never fire...

Horses will never run

System freezes

---

# Issue

> Gun may never fire...

```scala
implicit val ec = ... // Fixed size thread pool
```

Suppose we have _8_ threads in our ec (one per horse)

---

# Thread pool

8 messages arrive at the thread pool

```scala
val horsesF = Future.traverse(1 to 8) { horseNumber =>
  Future {
    val horse = Horse(horseNumber)

    waitUntilGun()

    runRunRun()
  }
}
```

```
           Thread-1:  (parked)
| 8 |      Thread-2:  (parked)
|...|      Thread-3:  (parked)
| 2 |      Thread-4:  (parked)
| 1 |      Thread-5:  (parked)
  --->     Thread-6:  (parked)
           Thread-7:  (parked)
           Thread-8:  (parked)
```

---

# Thread pool starts the Threads

```scala
val horsesF = Future.traverse(1 to 8) { horseNumber =>
  Future {
    val horse = Horse(horseNumber)

    waitUntilGun()

    runRunRun()
  }
}
```

```
           Thread-1:  horse 1   (waiting)
|   |      Thread-2:  horse 2   (waiting)
|   |      Thread-3:  horse 3   (waiting)
|   |      Thread-4:  horse 4   (waiting)
|   |      Thread-5:  horse 5   (waiting)
  --->     Thread-6:  horse 6   (waiting)
           Thread-7:  horse 7   (waiting)
           Thread-8:  horse 8   (waiting)
```

---

# Fire the gun?

9th message dropped onto the ec

```scala
val startF = Future {
  loadGun()

  fireGun()
}
```

```
           Thread-1:  horse 1   (waiting)
|   |      Thread-2:  horse 2   (waiting)
|   |      Thread-3:  horse 3   (waiting)
|   |      Thread-4:  horse 4   (waiting)
| 9 |      Thread-5:  horse 5   (waiting)
  --->     Thread-6:  horse 6   (waiting)
           Thread-7:  horse 7   (waiting)
           Thread-8:  horse 8   (waiting)
```

No threads available to run it though...

---

# Deadlock

- horse won't finish with thread until gun goes off


- gun won't go off until a horse finishes with thread

```
           Thread-1:  horse 1   (waiting)
|   |      Thread-2:  horse 2   (waiting)
|   |      Thread-3:  horse 3   (waiting)
|   |      Thread-4:  horse 4   (waiting)
| 9 |      Thread-5:  horse 5   (waiting)
  --->     Thread-6:  horse 6   (waiting)
           Thread-7:  horse 7   (waiting)
           Thread-8:  horse 8   (waiting)
```

---

# 9+ threads?

If our ec had more than 9+ threads it would be fine

```
           Thread-1:  horse 1   (waiting)
|   |      Thread-2:  horse 2   (waiting)
|   |      Thread-3:  horse 3   (waiting)
|   |      Thread-4:  horse 4   (waiting)
|   |      Thread-5:  horse 5   (waiting)
  --->     Thread-6:  horse 6   (waiting)
           Thread-7:  horse 7   (waiting)
           Thread-8:  horse 8   (waiting)
           Thread-9:  gun
```

---

# This happened

Youngster at LeadIQ created one of these bugs

---

# Environment dependent

You test this on your monstrous linux developer desktop,

no problems

---

# Environment dependent

> You test this on your monstrous linux developer desktop,
>
> no problems

Your friend runs it on their gutless macbook pro,

locks up

---

# Environment dependent

> You test this on your monstrous linux developer desktop,
>
> no problems
>
> Your friend runs it on their gutless macbook pro,
>
> locks up

e.g. app automatically sets #threads = #cores

Linux machine has 12 cores, macbook has 3 cores

---

# Environment dependent

Worse, is production will be different to your local machine

---

# Recap

When threads wait on other threads for something to happen,

you can get deadlocks

---

```
 ____                                             
/ ___| _   _ _ __ ___  _ __ ___   __ _ _ __ _   _ 
\___ \| | | | '_ ` _ \| '_ ` _ \ / _` | '__| | | |
 ___) | |_| | | | | | | | | | | | (_| | |  | |_| |
|____/ \__,_|_| |_| |_|_| |_| |_|\__,_|_|   \__, |
                                            |___/ 
```

That's it for threads

---

# Concepts

- Futures


- execution context


- threads


- fixed size thread pools


- fork join pools


- thread contention (too many threads)


- thread starvation (too few threads)


- blocking (causes thread starvation)


- concurrency bugs and locks

---

# Examples of blocking

- sleeping


- IO


- locks

---

# Today

Brought it together with some random bugs and code smells

---

# Please please please

Think about what you're doing when using `Future`'s

_Particularly_ if there is blocking code

---

# Get clarity on things like

- difference between `map` and `flatMap`


- what kind of execution context is my code running on?


- is it safe for me to block on this execution context?

---

# Things that should raise warning flags

When using play framework:

- using locks in prod code (tests are okay)


- using `Await.result` in prod code (tests are okay)


- using the global execution context in prod code (tests are okay)


- running blocking code on play's "standard" execution context


- sleeps


- using shared mutable state


- the `blocking` keyword

---

# Think about what you're doing

Common fallacy of developers:

> It looks okay, therefore it is okay

---

# Think about what you're doing

Common fallacy of developers:

> It looks okay, therefore it is okay

ie. often translates to:

> It compiles and tests pass,
>
> therefore it's okay

---

# Gotchas

> It compiles and tests pass,
>
> therefore it's okay

Today's gotchas would all compile

Unit tests generally don't put any real load on a system,

won't expose unusual concurrency bugs

---

# Expensive concurrency bugs

Concurrency bugs are terrible:

- occur randomly


- can be hard/impossible to reproduce


- cause customer data to go missing


- pathological and so very hard to diagnose


- needle in a haystack

---

# Know what you're doing

Think of concurrency like a dangerous powertool

If you're not sure what you're doing, ask someone

Refactor code to make clearer if necessary

Ideally you have a clear understanding of what you're doing, no fuzziness

---

# Don't cut corners

The time you'll save will be miniscule compared to the time hunting the bugs you create

---

# MVP?

---

```
  ___                  _   _                 
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___ 
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
                                             
```
