---
author: Rohan
date: 2021-11-10
title: Functor and Monad
---

```
 _____                 _
|  ___|   _ _ __   ___| |_ ___  _ __
| |_ | | | | '_ \ / __| __/ _ \| '__|
|  _|| |_| | | | | (__| || (_) | |
|_|   \__,_|_| |_|\___|\__\___/|_|



             and


 __  __                       _
|  \/  | ___  _ __   __ _  __| |
| |\/| |/ _ \| '_ \ / _` |/ _` |
| |  | | (_) | | | | (_| | (_| |
|_|  |_|\___/|_| |_|\__,_|\__,_|

```

---

# Fear not!

> Functor and Monad?

Weird names

Simple concepts

---

# In fact!

You've probably already used this stuff

But maybe not understood it in the bigger picture

---

# Why care about these?

---

# Why care about these?

They are everywhere in programming and nature

---

# Why care about these?

Scala has first class support for these concepts

---

# Today

- functor


- monad

---

# Next time

- for comprehensions

---

```
 _____                 _
|  ___|   _ _ __   ___| |_ ___  _ __
| |_ | | | | '_ \ / __| __/ _ \| '__|
|  _|| |_| | | | | (__| || (_) | |
|_|   \__,_|_| |_|\___|\__\___/|_|

```

Structures you can "map" functions over

---

# Example: Matrix

We have a 3x2 matrix of integers:

```
  |  1  2  |
  |  3  4  |
  |  5  6  |
```

and a doubling function `f`:

```
f: i => i * 2
```

---

# Example: Matrix

Let's "map the function over" our matrix

```
  |  1  2  |           | f(1) f(2) |    |  2  4 |
  |  3  4  |  ----->   | f(3) f(4) | =  |  6  8 |
  |  5  6  |           | f(5) f(6) |    | 10 12 |
```

---

# Main observations

```
     Before              After
               map
  |  1  2  |    f      |  2  4 |
  |  3  4  |  ----->   |  6  8 |
  |  5  6  |           | 10 12 |
```

- shape is the same (didn't transpose, drop elements, add elements)


- inner data transformed

---

# Example: Tree

We have a tree of integers with a particular shape

```
     3
   /   \
  0     10
       /  \
      7    5
```

and a function `f`:

```
f: i => i - 1
```

---

# Example: Tree

Map our `f` over the tree:

```
     3                      f(3)               2
   /   \                  /   \              /   \
  0     10      -->     f(0) f(10)     =    -1    9
       /  \                   /  \               / \
      7    5               f(7)  f(5)           6   4
```

---

# Again

```
     3          map        2
   /   \         f       /   \
  0     10      -->     -1    9
       /  \                  / \
      7    5                6   4
```

- shape is the same


- inner data transformed

---

# Functor

Matrix and Tree are "functors" because they have a way to map functions over them

---

# Example: List

Like a 1D matrix

```
| 3 |           | false |
| 2 |           | true  |
| 5 |   --->    | false |
| 9 |           | false |
| 0 |           | true  |
```

`f` is whether the value is even

---

# From concepts to code

To the repl!

---

# Option

Is `Option` a functor?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Quick recap of Option

Represents when data may not exist

e.g. the result of a search

---

# Two cases/shapes

```
           -----
          |     |
Some(a)   |  a  |     box with value
          |     |
           -----

None      -----       flattened box
          -----
```

---

# Viewing Option as a functor

Some case:

> If there is data, ie. Some(a)
>
> apply your function to the data inside the box
>
> ie. return Some(f(a))

```
    -----           ------
   |     |         |      |
   |  a  |  ---->  | f(a) |
   |     |         |      |
    -----           ------
```

A bit like a 1x1 matrix

---

# Viewing Option as a functor

> If there is no data, ie. None
>
> just return None back
>
> (there is no data to apply f to)

```
   -----  ---->  -----
   -----         -----
```

A bit like a 0x0 matrix or an empty list

---

# Putting that together

Mapping f over an Option:

```
    before           after

    -----           ------
   |     |         |      |
   |  a  |  ---->  | f(a) |
   |     |         |      |
    -----           ------

    -----   ---->   -----
    -----           -----
```

Note how the structure doesn't change

---

# Implement it

To the repl!

---

# Option

It's conceptually a functor,

because there is a way to map functions over it:

```scala
def mapOption[A, B](option: Option[A], f: A => B): Option[B] = option match {
  case Some(a) => Some(f(a))
  case None => None
}
```

in a shape preserving way:

```
    before           after

    -----           ------
   |     |         |      |
   |  a  |  ---->  | f(a) |
   |     |         |      |
    -----           ------

    -----   ---->   -----
    -----           -----
```

---

# Formally

`F` is a functor if there is some mapping concept:

```
              map f
F[A]         ------>     F[B]
f: A => B
```

Example `List`:

```
              map f
List[A]      ------>     List[B]
f: A => B
```

---

# Rules?

What if we defined a troll `List` mapping like:

```
| a |       <missing>
| b |       | f(b) |
| c |  -->  | f(c) |
| . |       |  .   |
| . |       |  .   |
| . |       |  .   |
| y |       | f(y) |
| z |       | f(z) |
```

Is this a "proper" mapping?

---

# Hmmm

> Is this a "proper" mapping?

```
| a |       <missing>
| b |       | f(b) |
| c |  -->  | f(c) |
| . |       |  .   |
| . |       |  .   |
| . |       |  .   |
| y |       | f(y) |
| z |       | f(z) |
```

It fits our type system concept:

```
              map f
List[A]      ------>     List[B]
f: A => B
```

But we know it's not really preserving the shape...

---

# "Shape"

Fuzzy human concept

How do you express it to a computer in a general way?

---

# Laws

Laws help capture this idea in a testable form

---

# Law 1 - identity

> mapping the identity function over your structure should give you the same thing back

```
| a |  map  | a |
| b |  id   | b |
| c |  -->  | c |
| . |       | . |
| . |       | . |
| . |       | . |
| y |       | y |
| z |       | z |
```

---

# Law 2 - composition

> two individual maps should be the same as one combined map

Two maps:

```
| a |  map  | f(a) |  map  | g(f(a)) |
| b |   f   | f(b) |   g   | g(f(b)) |
| c |  -->  | f(c) |  -->  | g(f(c)) |
| . |       |  .   |       |   .     |
| . |       |  .   |       |   .     |
| . |       |  .   |       |   .     |
| y |       | f(y) |       | g(f(y)) |
| z |       | f(z) |       | g(f(z)) |
```

One combined map:

```
| a |  map  | g(f(a)) |
| b |  gf   | g(f(b)) |
| c |  -->  | g(f(c)) |
| . |       |   .     |
| . |       |   .     |
| . |       |   .     |
| y |       | g(f(y)) |
| z |       | g(f(z)) |
```

---

# Testing our dodgy map (Law 1)

If we mapped the identity function over our structure,

do we get the same thing back?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Law broken

> If we mapped the identity function over our structure,
>
> do we get the same thing back?

```
| a |  map  <missing>
| b |  id   | b |
| c |  -->  | c |
| . |       | . |
| . |       | . |
| . |       | . |
| y |       | y |
| z |       | z |
```

No!

---

#  Testing our dodgy map (Law 2)

If we did two maps,

would that be the same as one combined map?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Law broken

> If we did two maps,
>
> would that be the same as one combined map?

No!

Just use the identity function as the two maps

---

# Two applications

```
| a |  map         map
| b |  id   | b |  id
| c |  -->  | c |  -->  | c |
| . |       | . |       | . |
| . |       | . |       | . |
| . |       | . |       | . |
| y |       | y |       | y |
| z |       | z |       | z |
```

Shrunk by 2

---

# One application

Composing identity with itself is just the identity function

Applying two identity's together

```
| a |  map
| b | id id | b |
| c |  -->  | c |
| . |       | . |
| . |       | . |
| . |       | . |
| y |       | y |
| z |       | z |
```

Shrunk by 1

---

# Pop Quiz

Which of these are functors?

- `Array`


- `Set`


- `ZonedDateTime`

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Summary of functor

---

# Informal definition

> A type is conceptually a functor if it provides a way to map functions over it that preserves shape/structure

ie. something with `map`

---

# More formally

`F` is a functor if it has a map concept:

```
                map
F[A]         ------>   F[B]
f: A => B
```

and it obeys the law of identity and composition

---

# Examples

So many...

- matrix (of any dimensions - ie. tensors)


- any sequence type (`Seq`, `List`, `Array`, `Vector`, `Range`)


- `Set`


- tree


- `Option`

and many more

---

# Thinking more abstractly

Now you know the general pattern,

you have a language to express this concept to other developers

You can write better libraries

---

```
 __  __                       _
|  \/  | ___  _ __   __ _  __| |
| |\/| |/ _ \| '_ \ / _` |/ _` |
| |  | | (_) | | | | (_| | (_| |
|_|  |_|\___/|_| |_|\__,_|\__,_|

```

Functor's big brother

---

# Back to our matrix example

```
  |  1  2  |
  |  3  4  |
  |  5  6  |
```

We map `f` over it, where `f` conceptually looks like:

```
           |  a  a  |
  a  --->  |  a  a  |


e.g.

           |  1  1  |
  1  --->  |  1  1  |
```

---

# The result?

```
  |  1  2  |
  |  3  4  |
  |  5  6  |

           |  a  a  |
  a  --->  |  a  a  |
```

What will it look like?

What will its dimensions be?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Follow up question

```
  |  1  2  |  Matrix[Int]
  |  3  4  |
  |  5  6  |


           |  a  a  |
  a  --->  |  a  a  |    Int => Matrix[Int]
```

What will be the type of the output matrix?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Nested matrix

```
  |  |1 1|  |2 2|  |
  |  |1 1|  |2 2|  |
  |                |
  |  |3 3|  |4 4|  |
  |  |3 3|  |4 4|  |
  |                |
  |  |5 5|  |6 6|  |
  |  |5 5|  |6 6|  |
```

Type: `Matrix[Matrix[Int]]`

Dimensions: 3x2 matrix of 2x2 matrices

---

# Often we don't want this

```
   we got 3x2 of 2x2              we want 6x4

  |  |1 1|  |2 2|  |             | 1 1 2 2 |
  |  |1 1|  |2 2|  |             | 1 1 2 2 |
  |                |             | 3 3 4 4 |
  |  |3 3|  |4 4|  |             | 3 3 4 4 |
  |  |3 3|  |4 4|  |             | 5 5 6 6 |
  |                |             | 5 5 6 6 |
  |  |5 5|  |6 6|  |
  |  |5 5|  |6 6|  |
```

---

# Can we use map?

```
|  1  2  |       map f        | 1 1 2 2 |
|  3  4  |       ---->        | 1 1 2 2 |
|  5  6  |        ???         | 3 3 4 4 |
                              | 3 3 4 4 |
                              | 5 5 6 6 |
                              | 5 5 6 6 |
```

Is there an `f` we can use with `map` to achieve this?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Shape preserving

> Is there an `f` we can use with `map` to achieve this?

No

`map` is shape preserving

Our input matrix is 3x2

Desired matrix is 6x4

```
|  1  2  |       map f        | 1 1 2 2 |
|  3  4  |       ---->        | 1 1 2 2 |
|  5  6  |        ???         | 3 3 4 4 |
                              | 3 3 4 4 |
                              | 5 5 6 6 |
                              | 5 5 6 6 |
```

---

# Map

Locks you into a structure

---

# What we need

Something that maps...

But can also flatten...

Any ideas?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# What we need

> Something that maps...
>
> But can also flatten...

flatMap!

---

# What is flatMap?

Like map, but it flattens out the structure you introduce:

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
```

---

# List example

Play with some code

To the repl!

---

# Growing and shrinking structure

To the repl!

---

# Monads

Formal definition

---

# Lifting

Monads have a way to "lift" simple values into their context

```
3  ->  List(3)   (list of 1 element)


5  ->  | 5 |     (1x1 matrix)
```

This is called "pure"

---

# General Form

`M` is a monad, if it has a `flatMap` and `pure` concept:

```
                flatMap f
M[A]            --------->  M[B]    (not M[M[B]])
f: A => M[B]


                  pure
A               --------->  M[A]
```

and it follows some laws (which we'll skip)

---

# Option?

Is Option a monad?

ie. does it have a `flatMap` and `pure` concept?

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# Pure

> Is there an easy way to lift a simple value into an `Option`?

Yep!

Wrap in `Some`!

```
3   ---->   Some(3)
```

---

# flatMap

Think of `Option` as a mini-List

- `Some(a)` is like a list length 1: `List(a)`


- `None` is like the empty list `Nil`

---

# Example

```scala
def demo(option: Option[Int]): Option[Int] = {
  option.flatMap { i =>
    if (i == 1) None
    else Some(i + 1)
  }
}

// List equivalent
def demo(option: List[Int]): List[Int] = {
  list.flatMap { i =>
    if (i == 1) Nil
    else List(i + 1)
  }
}


demo(None)    // None
demo(Some(1)) // None
demo(Some(3)) // Some(4)
```

---

# Matrix?

We've seen that matrix is a functor.

Is matrix a monad?

(`pure` and `flatMap`)

```
 ___
|__ \
  / /
 |_|
 (_)

```

---

# pure?

Yes

Lift your value into a 1x1 matrix

```
3   --->   | 3 |
```

---

# flatMap?

Our example turned every element into the same sized matrix

What if we had to combine different sized matrices?

---

# Example

Consider a function like this:

```
f: i -> i*i matrix of i's


0   --->   (empty)

1   --->   | 1 |

2   --->   | 2 2 |
           | 2 2 |

3   --->   | 3 3 3 |
           | 3 3 3 |
           | 3 3 3 |

...
```

---

# flatMap?

`flatMap` it over this matrix:

```
| 1  2  3 |
```

What would it look like

```
 ___
|__ \
  / /
 |_|
 (_)


1   --->   | 1 |

2   --->   | 2 2 |
           | 2 2 |

3   --->   | 3 3 3 |
           | 3 3 3 |
           | 3 3 3 |
```

---

# Doesn't work

```
            flatMap f
| 1  2  3 |   --->         |  1   2 2   3 3 3 |
                           |      2 2   3 3 3 |
                           |            3 3 3 |
```

Doesn't look like a matrix anymore...

---

# Not all functors are monads

Monad is stricter

---

# Summary of monad

---

# Informal Definition

Something is a monad if it has a `flatMap` and `pure` concept

---

# Formal Definition

`M` is a monad if it has some `flatMap` and `pure` concept like:

```
                flatMap f
M[A]            --------->  M[B]
f: A => M[B]


                  pure
A               --------->  M[A]
```

(And there are some laws)

---

# Why is monad useful?

That flattening keeps your types simple

Allows some change in the structure

---

# Monad vs Functor

Monad is the big brother of functor

It's stricter

ie.

- all monads are functors


- not all functors are monads

---

# Examples of monads

- all sequence types (`Array`, `List`, `Vector`, `Range` etc...)


- `Option`

and many more

---

# Next time

for comprehensions

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __|
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \  ?
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
