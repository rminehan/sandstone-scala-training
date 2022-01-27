---
author: Rohan
date: 2022-01-27
title: Akka Streams 4
---

```
    _    _    _
   / \  | | _| | ____ _
  / _ \ | |/ / |/ / _` |
 / ___ \|   <|   < (_| |
/_/   \_\_|\_\_|\_\__,_|

 ____  _
/ ___|| |_ _ __ ___  __ _ _ __ ___  ___
\___ \| __| '__/ _ \/ _` | '_ ` _ \/ __|
 ___) | |_| | |  __/ (_| | | | | | \__ \
|____/ \__|_|  \___|\__,_|_| |_| |_|___/

```

Part 4

---

# Recap

---

# Complex topologies

Using the graph dsl to create non-linear shapes

```
         ---- +1 ----
        /      B     \
 3  ---> A           D -->  4,2
        \      C     /
         ---- -1 ----
```

---

# Today

```
 __  __       _            _       _ _           _   _
|  \/  | __ _| |_ ___ _ __(_) __ _| (_)___  __ _| |_(_) ___  _ __
| |\/| |/ _` | __/ _ \ '__| |/ _` | | / __|/ _` | __| |/ _ \| '_ \
| |  | | (_| | ||  __/ |  | | (_| | | \__ \ (_| | |_| | (_) | | | |
|_|  |_|\__,_|\__\___|_|  |_|\__,_|_|_|___/\__,_|\__|_|\___/|_| |_|

```

---

# Undo the lying

Been hiding these details to avoid distraction/confusion

---


# Lies!

```scala
// I said
class Source[Out](...) extends Graph[SourceShape[Out]]

// Actually there's also `Mat`
class Source[Out, Mat](...) extends Graph[SourceShape[Out], Mat]
```

(Same for other shapes like Flow and Sink)

---

# What is `Mat`?

```scala
class Source[Out, Mat](...) extends Graph[SourceShape[Out], Mat]
```

Any guesses?

```
 ___
|__ \
  / /
 |_|
 (_)
```

(hint: relates to today's topic)

---

# Mat ~ Materialisation

```scala
class Source[Out, Mat](...) extends Graph[SourceShape[Out], Mat]
```

---

# What is "materialisation"?

Hmmm...

Something to do with running a stream

That's part of it

---

# Related question

Could you make a stream "return" something?

---

# Examples

- the first n elements processed


- the last n elements processed


- an aggregation (e.g. summing all the elements)


- the number of elements processed in the stream

---

# Examples

- the first n elements processed


- the last n elements processed


- an aggregation (e.g. summing all the elements)


- the number of elements processed in the stream

Which of these would require a finite stream?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Examples

> Which of these would require a finite stream?

- the first n elements processed


- the last n elements processed (this)


- an aggregation (e.g. summing all the elements) (this)


- the number of elements processed in the stream (this)

---

# Example

Suppose we were summing a finite stream

```scala
val graph = numbers.to(sumSink)

// Kick it off and then move on
val sum = graph.run()
```

---

# Does this make sense?

```scala
implicit val actorSystem = ...

val graph = numbers.to(sumSink)

// Kick it off and then move on
val sum = graph.run()
```

The stream is running "somewhere else" though (e.g. actor system)

So what type would we use to represent the outcome?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Future!

```scala
implicit val actorSystem = ...

val graph = numbers.to(sumSink)

// Kick it off and then move on
val sum: Future[Int] = graph.run()
```

The Future tracks the computation running somewhere else

in a non-blocking way

---

# Lingo

```scala
implicit val actorSystem = ...

val graph = numbers.to(sumSink)

// Kick it off and then move on
val sum: Future[Int] = graph.run()
```

Our graph is "materialising" a `Future[Int]`

---

# Demo 6

Into the murky waters of materialisation!

---

# Demo 6 recap

```scala
// Materialises a `NotUsed`
Source(...).to(...)

// Materialises a `Future[Int]`
Source(...).toMat(...)(Keep.right)
```

Changes:

- `to` vs `toMat`


- extra `Keep.right`

---

# Very mysterious...

Will (hopefully) make sense by the end of the session

---

```
 __  __       _
|  \/  | __ _| |_
| |\/| |/ _` | __|
| |  | | (_| | |_
|_|  |_|\__,_|\__|
```

---

# Quick summary

A graph doesn't just have a shape,

it also has a `Mat` type

```scala
class Source[Out, Mat](...) extends Graph[SourceShape[Out], Mat]
```

---

# Mat

```scala
class Source[Out, Mat](...) extends Graph[SourceShape[Out], Mat]
```

`Mat` represents what that graph will materialise when run

---

# Example 1

Our folder sink:

```scala
val sink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)
//                  ^^^ Mat ^^^



class Sink[Out, Mat](...) extends Graph[SinkShape[Out], Mat]
```

---

# Example 2

Our number source:

```scala
val source: Source[Int, NotUsed] = Source(Seq(3, 4, 1, 10))
//                      ^ Mat ^



class Source[Out, Mat](...) extends Graph[SourceShape[Out], Mat]
```

(we'll explain what `NotUsed` is later)

---

# Combining elements

Our two little graphs have different materialisation types

What happens when we combine them

```

     |=======           +         =========|             =     |=========================|
Source[Int, NotUsed]         Sink[Int, Future[Int]]              Graph[ClosedShape, ???]
```

Hmmm...

---

# Remember demo 6

```scala
// Materialises a `NotUsed`
val graph = Source(...).to(Sink.fold(0)(_ + _))

// Materialises a `Future[Int]`
val graph = Source(...).toMat(Sink.fold(0)(_ + _))(Keep.right)
```

```
     |=======            to           =========|             =     |=========================|
Source[Int, NotUsed]             Sink[Int, Future[Int]]              Graph[ClosedShape, NotUsed]


     |=======   toMat(Keep.right)      =========|             =     |=========================|
Source[Int, NotUsed]             Sink[Int, Future[Int]]              Graph[ClosedShape, Future[Int]]
```

---

# Conclusion 1 (to)

## In our example

```
     |=======            to           =========|             =     |=========================|
Source[Int, NotUsed]             Sink[Int, Future[Int]]              Graph[ClosedShape, NotUsed]
               |                            (ignored)                                     / \
               |                                                                           |
                ---------------------------------------------------------------------------
```

## More generally

`to` uses the left value and ignores the right one

```
     |=======           to           =========|             =     |=========================|
Source[A, MatLeft]              Sink[A, MatRight]                   Graph[ClosedShape, MatLeft]
```

---

# Conclusion 2 (toMat)

## In our example

```
     |=======   toMat(Keep.right)      =========|             =     |=========================|
Source[Int, NotUsed]             Sink[Int, Future[Int]]              Graph[ClosedShape, Future[Int]
           (ignored)                          |                                           / \
                                              |                                            |
                                              ---------------------------------------------
```

## More generally

`toMat` uses the side you specify

```
     |=======      toMat(Keep.left)  =========|             =     |=========================|
Source[A, MatLeft]              Sink[A, MatRight]                   Graph[ClosedShape, MatLeft]

     |=======      toMat(Keep.right) =========|             =     |=========================|
Source[A, MatLeft]              Sink[A, MatRight]                   Graph[ClosedShape, MatRight]
```

---

# to vs toMat

```scala
// Source.scala
def to(sink: Sink) = toMat(sink)(Keep.left)
```

Aha!

`to` is just a wrapper of `toMat` that is left biased

(rare victory for lefties)

---

# via?

Same deal

```scala
def via(flow: Flow) = viaMat(flow)(Keep.left)
```

`via` is just a wrapper around `viaMat` that is left biased

---

# Recap so far

Been a lot to take in

---

# Materialisation

Every piece of graph can potentially materialise value over the lifetime of the stream

e.g. sources, flows, sinks

---

# Combining

To build bigger graphs, we combine smaller pieces of graph

But we need to tell akka streams what value this new graph will materialise

```
     |=======           +           =========|         =      |=========================|
Source[A, MatLeft]              Sink[A, MatRight]               Graph[ClosedShape, ???]
```

---

# Most of the time

... we don't care

---

# Default behaviour

> Most of the time, we don't care

Not trying to materialise anything

Just use `to` and `via` to keep the code clean

They `Keep.left`

---

# If we do care

Methods like `toMat` and `viaMat` give more control

```scala
source.toMat(sink)(...)
//                 ^^^ strategy
```

---

```
 ____  _             _             _
/ ___|| |_ _ __ __ _| |_ ___  __ _(_) ___  ___
\___ \| __| '__/ _` | __/ _ \/ _` | |/ _ \/ __|
 ___) | |_| | | (_| | ||  __/ (_| | |  __/\__ \
|____/ \__|_|  \__,_|\__\___|\__, |_|\___||___/
                             |___/
```

---

# Common strategies

```scala
source.toMat(sink)(...)
//                 ^^^ strategy
```

- Keep.left


- Keep.right


- Keep.both


- put in `NotUsed` explicitly


- custom logic

---

# Play around with them

```scala
source.toMat(sink)(...)
//                 ^^^ strategy
```

Back to demo 6!

---

# Recap demo 6

Strategy is just a lambda to produce a new materialised value

```scala
source.toMat(sink)(...)
//                 ^^^ strategy

(leftMat, rightMat) => ... // newMat
```

---

# Recap demo 6

`Keep` is just a bunch of common strategies

```scala
Keep.left  (leftMat, rightMat) => leftMat

Keep.right (leftMat, rightMat) => rightMat

Keep.both  (leftMat, rightMat) => (leftMat, rightMat)
```

---

# Why use Keep?

Why not just write out an explicit lambda like `(left, right) => ...`?

---

# Why use Keep?

- it's a little more terse and readable

---

# Why use Keep?

- there are optimisations inside akka streams which can use it

```scala
source.toMat(sink)(Keep.right)

// vs

source.toMat(sink)((left, right) => right)
```

---

# Example source code

(you don't need to understand the code, just notice specific Keep logic)

```scala
  override def viaMat[T, Mat2, Mat3](flow: Graph[FlowShape[Out, T], Mat2])(
      combine: (Mat, Mat2) => Mat3): Flow[In, T, Mat3] = {
    if (this.isIdentity) {
      // optimization by returning flow if possible since we know Mat2 == Mat3 from flow
      if (combine == Keep.right) Flow.fromGraph(flow).asInstanceOf[Flow[In, T, Mat3]]
      else {
        // Keep.none is optimized and we know left means Mat3 == NotUsed
        val useCombine =
          if (combine == Keep.left) Keep.none
          else combine
        new Flow(LinearTraversalBuilder.empty().append(flow.traversalBuilder, flow.shape, useCombine), flow.shape)
          .asInstanceOf[Flow[In, T, Mat3]]
      }
      ...
  }
  ...
}
```

---

# to/via default to Keep.left

With all the discrimination right handers already face, this seems unfair

Why not use something more symmetrical like Keep.both?

Any thoughts?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# One thought: Associativity

```
(a + b) + c  =?=  a + (b + c)
```

Would Keep.both be associative?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Associativity?

> Would Keep.both be associative?

Probably not, or I wouldn't have mentioned it

---

# Associativity

```
(a + b) + c  =?=  a + (b + c)

Keep.both: (matLeft, matRight) => (matLeft, matRight)
```

Suppose:

- a materialises `matA`
- b materialises `matB`
- c materialises `matC`

## LHS: (a + b) + c

(a + b) materialises (matA, matB)

(a + b) + c materialises ((matA, matB), matC)

## RHS: a + (b + c)

(b + c) materialises (matB, matC)

a + (b + c) materialises (matA, (matB, matC))

---

# Conclusion

With Keep.both

```scala
(a + b) + c  !=  a + (b + c)
```

The materialisation types will be different

On a bigger graph, this difference would become more prominent

---

# Keep.left

```scala
(a + b) + c  ==  a + (b + c)
```

Will end up being `matA` regardless of the order

---

Lastly

```
 _   _       _   _   _              _
| \ | | ___ | |_| | | |___  ___  __| |
|  \| |/ _ \| __| | | / __|/ _ \/ _` |
| |\  | (_) | |_| |_| \__ \  __/ (_| |
|_| \_|\___/ \__|\___/|___/\___|\__,_|

```

---

# Where there is nothing to materialise

In a lot of instances,

- there isn't a sensible value to materialise


- we don't care

---

# akka.NotUsed

Just a simple placeholder type for those cases

```scala
/**
 * This type is used in generic type signatures wherever the actual value is of no importance.
 * It is a combination of Scala’s `Unit` and Java’s `Void`, which both have different issues when
 * used from the other language.
 * An example use-case is the materialized value of an Akka Stream for cases
 * where no result shall be returned from materialization.
 */
sealed abstract class NotUsed

case object NotUsed extends NotUsed {

  /**
   * Java API: the singleton instance
   */
  def getInstance(): NotUsed = this

  /**
   * Java API: the singleton instance
   *
   * This is equivalent to [[NotUsed.getInstance]], but can be used with static import.
   */
  def notUsed(): NotUsed = this
}
```

---

# Example

What value would `Source.repeat(3)` materialise?

Can't think of anything useful...

```scala
  def repeat[T](element: T): Source[T, NotUsed] = {
    ...
  }
```

`NotUsed` is very common in the standard library

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

# Undoing the lies

Graph has an extra type parameter `Mat` which I was hiding from you until you were older

```scala
// I said
Graph[Shape]

// Actually there's also `Mat`
Graph[Shape, Mat]
```

and that leaks into all the other graphs

```scala
class Source[Out, Mat](...) extends Graph[SourceShape[Out], Mat]
```

---

# Mat

A type parameter for the materialised value from that piece of graph

---

# NotUsed

Most of the time you don't care about materialising a value,

or there isn't a sensible candidate for materialisation anyway

---

# Non-blocking

When you do materialise a meaningful value, it's usually a `Future[...]`

```
Sink.head[A] // materialises Future[A]
```

because the stream is running elsewhere and we don't want to block the thread

that starts the stream

---

# run

The materialised value of a runnable graph is returned when you call `run`

```scala
val mat = runnableGraph.run()
```

---

# Combining graphs

When two graphs combine to form a bigger graph,

you need to decide how to combine the materialised values

```
graphLeft     +   graphRight    =       graphCombined

 matLeft           matRight               ???
```

---

# Defaults

```
graphLeft     +   graphRight    =       graphCombined

 matLeft           matRight               matLeft
```

`via` and `to` will keep the left one

```scala
source.via(flow1).via(flow2).via(flow3).to(sink)
// ^^
```

In a long chain of `via/to` you'll end up with the leftmost one

---

# Overriding the default

Use the more general `viaMat` and `toMat`

---

# Common strategies

`Keep` has nicely named methods

```scala
object Keep {
  def left  = (left, right) => left
  def right = (left, right) => right
  def both  = (left, right) => (left, right)
  def none  = (left, right) => NotUsed
}
```

---

# Back to our first example

```scala
val source: Source[Int, NotUsed] = Source(Seq(3, 4, 1, 10))
val sink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

val mat1: NotUsed = source.to(sink).run() // `to` uses Keep.left

val mat2: Future[Int] = source.toMat(sink)(Keep.right)
```

---

# Next time

Async concepts

For example, what if a stage in your pipeline introduces a future

```scala
val userIds: Source[UserId, NotUsed] = ...

def getUser(userId: UserId): Future[User]

userIds.map(getUser) // Source[Future[User], NotUsed]
```

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/

```
