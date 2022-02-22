---
author: Rohan
date: 2022-03-03
title: Time Space Complexity
---

```
 _____ _                   ______
|_   _(_)_ __ ___   ___   / / ___| _ __   __ _  ___ ___
  | | | | '_ ` _ \ / _ \ / /\___ \| '_ \ / _` |/ __/ _ \
  | | | | | | | | |  __// /  ___) | |_) | (_| | (_|  __/
  |_| |_|_| |_| |_|\___/_/  |____/| .__/ \__,_|\___\___|
                                  |_|
  ____                      _           _ _
 / ___|___  _ __ ___  _ __ | | _____  _(_) |_ _   _
| |   / _ \| '_ ` _ \| '_ \| |/ _ \ \/ / | __| | | |
| |__| (_) | | | | | | |_) | |  __/>  <| | |_| |_| |
 \____\___/|_| |_| |_| .__/|_|\___/_/\_\_|\__|\__, |
                     |_|                      |___/
```

---

# Detour from List

We'll return to it next time

Relates to functional data structures

---

# Today: Time/Space complexity

aka "Big O"

aka asymptotic analysis

---

# Why?

- good for job interviews


- helpful for understanding data structures (next time)

---

# University

Maybe it's been a while since university...

---

# Practical reality

We don't need this much,

often working at small scale

---

# Practical reality

> We don't need this much,
>
> often working at small scale

_Sometimes_ it will matter

(and when it does, it will matter a lot)

---

# Definitions: Time and Space

In the context of an algorithm:

- Time - how long something takes


- Space - how much memory it takes

(often a trade off)

---

# Example

```scala
case class Customer(id: UUID, name: String, totalSpent: BigDecimal)

def printReport(customers: Seq[Customer]): Unit = {
  val totalSpent = customers.map(_.totalSpent).sum
  println(s"Customers spent $totalSpent in total")
}
```

If we have `n` customers, roughly what is the time and space complexity?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Linear

```scala
def printReport(customers: Seq[Customer]): Unit = {
  val totalSpent = customers.map(_.totalSpent).sum
  println(s"Customers spent $totalSpent in total")
}
```

## Time

2n: loops twice (map, then sum)

O(n)

## Space

`map` creates an intermediate collection size `n`

`sum` probably uses constant space

O(n)

---

# Why we do this analysis

Usually worried about "scale"

- lots of requests


- lots of data

---

# Back to example

Time and space is O(n) or linear (unbounded)

If we had millions of customers this could become an issue

```scala
def printReport(customers: Seq[Customer]): Unit = {
  val totalSpent = customers.map(_.totalSpent).sum
  println(s"Customers spent $totalSpent in total")
}
```

Might need a more scalable approach

---

# Double loop example

```scala
def doSomething(customers: Seq[Customer]): Unit = {
  for {
    customer1 <- customers
    customer2 <- customers
  } yield ...

  for (customer <- customers) {
    ...
  }
}
```

What is the time complexity here?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Double loop example

```scala
def doSomething(customers: Seq[Customer]): Unit = {
  // n^2
  for {
    customer1 <- customers
    customer2 <- customers
  } yield ...

  // n
  for (customer <- customers) {
    ...
  }
}
```

> What is the time complexity here?

O(n^2) "quadratic"

Effectively looking at every pairing of customers

The second loop is O(n) but it's overshadowed

---

# Major term

We just care about the fastest growing term

It will make the other terms insignificant

---

# Example

> It will make the other terms insignificant

Suppose an algorithm takes time: 2n^2 + 30n

Think about a large n

---

# Example

> It will make the other terms insignificant

Suppose an algorithm takes time: 2n^2 + 30n

When n = 10,000

Quadratic term is 2 * 10,000^2 = 200,000,000

Linear term is 30 * 10,000 = 300,000

---

# Example

> It will make the other terms insignificant

Suppose an algorithm takes time: 2n^2 + 30n

When n = 10,000

Quadratic term is 2 * 10,000^2 = 200,000,000

Linear term is 30 * 10,000 = 300,000

Orders of magnitude different

Constants don't really matter either

---

# take example

```scala
def sampleCustomers(customers: Seq[Customer]): Seq[Customer] = customers.take(5)
```

What is the time/space complexity?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# take example

```scala
def sampleCustomers(customers: Seq[Customer]): Seq[Customer] = customers.take(5)
```

> What is the time/space complexity?

Probably "constant" or O(1) for both

(making some assumptions about the runtime type of the sequence)

---

# Bounded: O(1)

```scala
def sampleCustomers(customers: Seq[Customer]): Seq[Customer] = customers.take(5)
```

It's "bounded": there is a ceiling it won't cross regardless of how many customers you pass in

ie. it's independent of the number of customers

---

# Common classes

## Nice ones

- O(1) "constant"


- O(logn) "logarithmic"

## Unwieldy ones

- O(n) "linear"


- O(n*logn)


- O(n^2) "quadratic"


- (and all the other polynomials, n^3, n^4, ...)


- O(e^n) "exponential"


- O(n!) "factorial"

---

# What makes them nice

> O(1) "constant"
>
> O(logn) "logarithmic"

Effectively bounded

You can provision a certain amount of resources and know it will always be enough

---

# Objection

> Effectively bounded

But log isn't bounded!

---

# Objection overruled!

> But log isn't bounded!

Not technically, but it grows so slowly that it basically is

---

# Example

Algorithm is logn, where n = #customers

You'll never have more than 7 billion customers

log_10(7 billion) ~ 10

log_10(700 billion) ~ 12

---

# Bounded vs Unbounded

Bounded means we can tackle it with finite resources

Unbounded means we'll need some smart scaling

---

# Recap: complexity analysis

Gives us a simple way to think about how something will behave at scale

---

# Main points

Usually discussed in the context of scaling:

- just worry about the major factor


- don't worry about the constants, just the shape of the curve
    - "linear", "quadratic" etc...

---

# Generally

Life is simpler when it's "constant" or "logarithmic"

---

# Back to our first example

```scala
case class Customer(id: UUID, name: String, totalSpent: BigDecimal)

def printReport(customers: Seq[Customer]): Unit = {
  val totalSpent = customers.map(_.totalSpent).sum
  println(s"Customers spent $totalSpent in total")
}
```

Time was O(n)

---

# Slow

```scala
case class Customer(id: UUID, name: String, totalSpent: BigDecimal)

def printReport(customers: Seq[Customer]): Unit = {
  val totalSpent = customers.map(_.totalSpent).sum
  println(s"Customers spent $totalSpent in total")
}
```

> Time was O(n)

A bit slow at scale

---

# Parallelism

```scala
val totalSpent = customers.map(_.totalSpent).sum
```

Addition has nice properties (associativity, commutativity)

which lets us parallelise this

---

# Parallelism

```scala
val totalSpent = customers.map(_.totalSpent).sum
```

Suppose we have 100 machines

Divide our customers into 100 groups and sum individually

```
group 1   --->  subtotal1        |
group 2   --->  subtotal2        |
group 3   --->  subtotal3        |   ----> sum 100 subtotals
group 4   --->  subtotal4        |
...                              |
group 100 --->  subtotal100      |
```

---

# Time complexity

```
group 1   --->  subtotal1        |
group 2   --->  subtotal2        |
group 3   --->  subtotal3        |   ----> sum 100 subtotals
group 4   --->  subtotal4        |
...                              |
group 100 --->  subtotal100      |
```

What is its time complexity now? (to process `n` customers)

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Time complexity

```
group 1   --->  subtotal1        |
group 2   --->  subtotal2        |
group 3   --->  subtotal3        |   ----> sum 100 subtotals
group 4   --->  subtotal4        |
...                              |
group 100 --->  subtotal100      |
```

> What is its time complexity now?

Still linear, O(n)

But about 100 times faster

---

# Why I mention this

Parallelism usually speeds things up by a constant factor

But that doesn't change the time complexity

---

# Why I mention this

Parallelism usually speeds things up by a constant factor

But that doesn't change the time complexity

Changing it to logarithmic or constant time would still be better

---

# Last example

---

# factorial

```scala
// fac(0) = 1
// fac(n) = n * fac(n - 1), for n > 0
def fac(n: Int): Int = ???
```

e.g.

```
fac(3) = 3 * fac(2)
       = 3 * 2 * fac(1)
       = 3 * 2 * 1 * fac(0)
       = 3 * 2 * 1 * 1
       = 6
```

---

# Homework

```scala
// fac(0) = 1
// fac(n) = n * fac(n - 1), for n > 0
def fac(n: Int): Int = n match {
  case 0 => 1
  case n => n * fac(n - 1)
}
```

What is the time/space complexity of this implementation of factorial?

(and what are some issues with my definition and implementation)

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

```
 ____
|  _ \ ___  ___ __ _ _ __
| |_) / _ \/ __/ _` | '_ \
|  _ <  __/ (_| (_| | |_) |
|_| \_\___|\___\__,_| .__/
                    |_|
```

---

# Time/Space Complexity

The time and memory it takes to do something

---

# Nice ones

- O(1) "constant"


- O(logn) "logarithmic"

---

# Unwieldy ones

- O(n) "linear"


- O(n*logn)


- O(n^2) "quadratic"


- (and all the other polynomials, n^3, n^4, ...)


- O(e^n) "exponential"


- O(n!) "factorial"

---

# Practical reality

Only matters at "scale"

---

# Parallelism

Usually speeds things up by a constant factor

Often won't change the complexity though

---

# Next time

Applying time/space complexity concepts to `List`

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\__,_|\___||___/\__|_|\___/|_| |_|___/

```
