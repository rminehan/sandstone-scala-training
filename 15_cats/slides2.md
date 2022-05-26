---
author: Rohan
date: 2022-05-26
title: The problem with for
---

```
 _____ _                            _     _
|_   _| |__   ___   _ __  _ __ ___ | |__ | | ___ _ __ ___
  | | | '_ \ / _ \ | '_ \| '__/ _ \| '_ \| |/ _ \ '_ ` _ \
  | | | | | |  __/ | |_) | | | (_) | |_) | |  __/ | | | | |
  |_| |_| |_|\___| | .__/|_|  \___/|_.__/|_|\___|_| |_| |_|
                   |_|
          _ _   _        __
__      _(_) |_| |__    / _| ___  _ __
\ \ /\ / / | __| '_ \  | |_ / _ \| '__|
 \ V  V /| | |_| | | | |  _| (_) | |
  \_/\_/ |_|\__|_| |_| |_|  \___/|_|

```

---

# for

for comprehensions are very useful

---

# for

for comprehensions are very useful,

they can get a bit overused

---

# Today

- recap `for`


- short circuiting problems


- combine problems

---

# What about cats?

This will motivate `Applicative` and `mapN` from cats

---

```
 ____                            _
|  _ \ ___  ___ __ _ _ __  _ __ (_)_ __   __ _
| |_) / _ \/ __/ _` | '_ \| '_ \| | '_ \ / _` |
|  _ <  __/ (_| (_| | |_) | |_) | | | | | (_| |
|_| \_\___|\___\__,_| .__/| .__/|_|_| |_|\__, |
                    |_|   |_|            |___/
  __
 / _| ___  _ __
| |_ / _ \| '__|
|  _| (_) | |
|_|  \___/|_|

```

---

# Syntactic Sugar

A scala for comprehension is syntactic sugar

---

# Example

```scala
for {
  a <- futA
  b <- futB
  c <- futC
} yield a + b + c
```

---

# Desugars

```scala
for {
  a <- futA
  b <- futB
  c <- futC
} yield a + b + c
```

desugars to:

```scala
futA.flatMap { a =>      // <---- flatMap
  futB.flatMap { b =>    // <---- flatMap
    futC.map { c =>
      a + b + c
    }
  }
}
```

---

# First class support

`for` means that scala has a nice way to write flatMap/map chains

---

# Why use it

## for

```scala
for {
  a <- futA
  b <- futB
  c <- futC
} yield a + b + c
```

Fairly clean, hides details

Linear

## flatMap/map

```scala
futA.flatMap { a =>
  futB.flatMap { b =>
    futC.map { c =>
      a + b + c
    }
  }
}
```

A bit noisy

Gets nested

---

# Summary

`for` helps us work with tricky types like `Future`

Developers like to use `for`

Under the hood it's using `flatMap/map`

---

# The only tool

`for` is the only syntactic sugar we have for this kind of thing

Sometimes means we overuse it

---

# Today

Use an example to explore these issues

---

```
  ___        _   _
 / _ \ _ __ | |_(_) ___  _ __
| | | | '_ \| __| |/ _ \| '_ \
| |_| | |_) | |_| | (_) | | | |
 \___/| .__/ \__|_|\___/|_| |_|
      |_|
 _____                           _
| ____|_  ____ _ _ __ ___  _ __ | | ___
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \
| |___ >  < (_| | | | | | | |_) | |  __/
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___|
                          |_|
```

---

# Pranali's Prize

Remember this old problem:

> Feroz, James and Yuhan enter into a competition.
>
> When all 3 win a prize, Pranali also wins the sum of their prizes.

---

# Previously

We used this to learn about monoids

---

# Previously

> We used this to learn about monoids

For today, forget about all that

Let's solve it with a `for`

To the repl!

---

# Summary

Solved using a `for`:

```scala
def pranaliPrize(ferozPrize: Option[Int], yuhanPrize: Option[Int], jamesPrize: Option[Int]): Option[Int] = {
  for {
    feroz <- ferozPrize
    yuhan <- yuhanPrize
    james <- jamesPrize
  } yield feroz + yuhan + james
}

pranaliPrize(Some(1), Some(2), Some(3))
// Some(value = 6)

pranaliPrize(Some(1), Some(2), None)
// None

pranaliPrize(Some(1), None, Some(3))
// None

pranaliPrize(None, Some(2), Some(3))
// None
```

---

# Reflecting

Looks nice

But they're not dependent computations

```scala
def pranaliPrize(ferozPrize: Option[Int], yuhanPrize: Option[Int], jamesPrize: Option[Int]): Option[Int] = {
  for {
    feroz <- ferozPrize
    yuhan <- yuhanPrize
    james <- jamesPrize
  } yield feroz + yuhan + james
}
```

---

```
 _____     _       _      _
|_   _| __(_)_ __ | | ___| |_
  | || '__| | '_ \| |/ _ \ __|
  | || |  | | |_) | |  __/ |_
  |_||_|  |_| .__/|_|\___|\__|
            |_|
 _____                           _
| ____|_  ____ _ _ __ ___  _ __ | | ___
|  _| \ \/ / _` | '_ ` _ \| '_ \| |/ _ \
| |___ >  < (_| | | | | | | |_) | |  __/
|_____/_/\_\__,_|_| |_| |_| .__/|_|\___|
                          |_|
```

---

# Analogue

What would an analogous triplet example look like...

---

# Analogue

> What would an analogous triplet example look like...

Previously we combined 3 Options to give an Option

---

# Analogue

> What would an analogous triplet example look like...
>
> Previously we combined 3 Options to give an Option

Let's combine 3 triplets to give a triplet

---

# Scenario

> James, Yuhan and Feroz play an online Star Trek game as Ferengi
>
> In the game you can win Dilithium, Credits and Latinum

(ie. there's 3 resources)

---

# Scenario

> James, Yuhan and Feroz play an online Star Trek game as Ferengi
>
> In the game you can win Dilithium, Credits and Latinum
>
> Pranali is the Grand Nagus and gets the sum of the other three's resources

---

# Modelling this

> In the game you can win Dilithium, Credits and Latinum

3 resources = `Triplet[Int]`

---

# Summing the triplets

```scala
def grandNagusPranaliResources(
  jamesResources: Triplet[Int],
  ferozResources: Triplet[Int],
  yuhanResources: Triplet[Int]): Triplet[Int] = ... // Add them together


// Example
grandNagusPranaliResources(
  jamesResources = Triplet(1, 3, 4),
  ferozResources = Triplet(2, 3, 0),
  yuhanResources = Triplet(4, 1, 5)
)
// Triplet(7, 7, 9)

```

---

# for?

Could we use the same `for` trick to add them?

```scala
def grandNagusPranaliResources(
  jamesResources: Triplet[Int],
  ferozResources: Triplet[Int],
  yuhanResources: Triplet[Int]): Triplet[Int] = {

  for {
    james <- jamesResources
    feroz <- ferozResources
    yuhan <- yuhanResources
  } yield james + feroz + yuhan
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

# Desugaring

```scala
def grandNagusPranaliResources(
  jamesResources: Triplet[Int],
  ferozResources: Triplet[Int],
  yuhanResources: Triplet[Int]): Triplet[Int] = {

  for {
    james <- jamesResources
    feroz <- ferozResources
    yuhan <- yuhanResources
  } yield james + feroz + yuhan
}
```

The `for` desugars to:

```scala
jamesResources.flatMap { james =>      // <---- flatMap
  ferozResources.flatMap { feroz =>    // <---- flatMap
    yuhanResources.map { yuhan =>
      james + feroz + yuhan
    }
  }
}
```

---

# flatMap?

That's a monad thing!

---

# Monad?

Is `Triplet` a monad?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Nonad!

> Is `Triplet` a monad?

No

Doesn't have a `flatMap` concept

---

# Recall

It was too rigid

```scala
val stringTriplet = Triplet("a", "bcd", "efghi")

stringTriplet.flatMap(s => Triplet(s, s, s)) // Returns Triplet[String]
```

But there's 9 strings!

---

# Summing that up

Our `for` trick only works with monads

---

# Implementing for Triplet

Would add the triplets directly:

```scala
def grandNagusPranaliResources(
  jamesResources: Triplet[Int],
  ferozResources: Triplet[Int],
  yuhanResources: Triplet[Int]): Triplet[Int] = {

  Triplet(
    _1 = jamesResources._1 + ferozResources._1 + yuhanResources._1,
    _2 = jamesResources._2 + ferozResources._2 + yuhanResources._2,
    _3 = jamesResources._3 + ferozResources._3 + yuhanResources._3
  )
}
```

---

```
 _____      _
|  ___|   _| |_ _   _ _ __ ___
| |_ | | | | __| | | | '__/ _ \
|  _|| |_| | |_| |_| | | |  __/
|_|   \__,_|\__|\__,_|_|  \___|

```

---

# Future

This time we combine 3 `Future[Int]`'s into a final `Future[Int]`

---

# Scenario

James, Feroz and Yuhan fly to different countries to collect a prize

---

# Scenario

James, Feroz and Yuhan fly to different countries to collect a prize

If they all return without their planes crashing,

Pranali gets the sum of their prizes

---

# Modelling with Future

> James, Feroz and Yuhan fly to different countries to collect a prize

Each flight is like a `Future[Int]`:

- long running/async


- potential for failure

---

# Implementing it

Emulating the flights:

```scala
def getPrize(country: String): Future[Int] = country match {
  case "Germany" => Future {
    println("Viel Gluck!")
    Thread.sleep(100)
    40
  }
  case "New Zealand" => Future {
    println("Gud lick!")
    Thread.sleep(50)
    200
  }
  case "Australia" => Future.successful(45)
  case "North Korea" => Future.failed(new WingsFellOffException("Fell off on return trip"))
  case _ => Future.successful(0)
}
```

---

# Example

Using `for`:

```scala
val pranaliPrize = for {
  jamesPrize <- getPrize("Germany")
  ferozPrize <- getPrize("North Korea") // Fails
  yuhanPrize <- getPrize("New Zealand")
} yield jamesPrize + ferozPrize + yuhanPrize

// No prize for Pranali as there was a failure
```

Thoughts about this?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Thoughts

```scala
val pranaliPrize = for {
  jamesPrize <- getPrize("Germany")
  ferozPrize <- getPrize("North Korea")
  yuhanPrize <- getPrize("New Zealand")
} yield jamesPrize + ferozPrize + yuhanPrize
```

- these are not dependent computations


- flights are run in series, not parallel


- side effect for Yuhan's flight depends on the success of James' and Feroz's flight

---

# Fixing it

```scala
val jamesPrizeFut = getPrize("Germany")
val ferozPrizeFut = getPrize("North Korea")
val yuhanPrizeFut = getPrize("New Zealand")

val pranaliPrize = for {
  jamesPrize <- jamesPrizeFut
  ferozPrize <- ferozPrizeFut
  yuhanPrize <- yuhanPrizeFut
} yield jamesPrize + ferozPrize + yuhanPrize
```

Now they are all started independently

Flights run in parallel

Side effect for Yuhan's flight isn't dependent on other flights

---

# Short circuiting

But what if there were two failures?

```scala
val jamesPrizeFut = getPrize("Germany")
val ferozPrizeFut = getPrize("North Korea")
val yuhanPrizeFut = getPrize("Mars")

val pranaliPrize = for {
  jamesPrize <- jamesPrizeFut
  ferozPrize <- ferozPrizeFut // failure (short circuits)
  yuhanPrize <- yuhanPrizeFut // failure
} yield jamesPrize + ferozPrize + yuhanPrize
```

Hmmm...

---

# Short circuiting

But what if there were two failures?

```scala
val jamesPrizeFut = getPrize("Germany")
val ferozPrizeFut = getPrize("North Korea")
val yuhanPrizeFut = getPrize("Mars")

val pranaliPrize = for {
  jamesPrize <- jamesPrizeFut
  ferozPrize <- ferozPrizeFut // failure (short circuits)
  yuhanPrize <- yuhanPrizeFut // failure
} yield jamesPrize + ferozPrize + yuhanPrize
```

Only the first error gets captured in the failed future

It only has one "slot" for an error

Feels non-deterministic, what if we'd unpacked the futures in a different order?

---

# Dependent computations = one error

```scala
for {
  user1 <- getUserById(123)
  user2 <- getUserById(user1.nextOfKinId)
  user3 <- getUserById(user2.nextOfKinId)
  user4 <- getUserById(user3.nextOfKinId)
} yield ...
```

There can only be one failure here

---

# Summing this up

---

# Three problems

- combine 3 `Option`'s into an `Option`


- combine 3 `Triplet`'s into a `Triplet`


- combine 3 `Future`'s into a `Future`

---

# Similar problems

More generally,

- combine 3 thingies into a thingy

(where thingy is a type constructor)

---

# Using `for`?

Leads to inconsistent solutions

---

# Our solutions

## Option

```scala
for {
  james <- jamesResources
  feroz <- ferozResources
  yuhan <- yuhanResources
} yield james + feroz + yuhan
```

## Triplet

404 - soution not found

## Future

```scala
// make sure to start the futures...

for {
  jamesPrize <- jamesPrizeFut
  ferozPrize <- ferozPrizeFut
  yuhanPrize <- yuhanPrizeFut
} yield jamesPrize + ferozPrize + yuhanPrize
```

Short circuits :(

---

# Listen to the universe

The universe is trying to tell us something about monad here...

---

# Listen to the universe

> The universe is trying to tell us something about monad here...

It's whispering: "it's the wrong abstraction"

---

```
 ____                            _
/ ___| _   _ _ __ ___  _ __ ___ (_)_ __   __ _   _   _ _ __
\___ \| | | | '_ ` _ \| '_ ` _ \| | '_ \ / _` | | | | | '_ \
 ___) | |_| | | | | | | | | | | | | | | | (_| | | |_| | |_) |
|____/ \__,_|_| |_| |_|_| |_| |_|_|_| |_|\__, |  \__,_| .__/
                                         |___/        |_|
```

---

# for

A commonly used tool...

---

# Issues

- assumes you have `flatMap`


- subtle issues introduced when you use it for independent computations

---

# Next time

Look at `Applicative` and `mapN`

Much better for this kind of problem

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
