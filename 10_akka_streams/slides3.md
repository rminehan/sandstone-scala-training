---
author: Rohan
date: 2022-01-24
title: Akka Streams 3
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

Part 3

---

# Recap

---


# Lego blocks

- Source   `|======`


- (via) Flow   `=======`


- (to) Sink   `=======|`


- RunnableGraph  (.run)   `|===========|`

---

# Combinators

map, filter etc...

Built on top of our stream concepts for convenience

---

# Execution vs Description

`RunnableGraph` is a description/blueprint

Use `.run` with some execution resources to start the stream

---

# Today

- homework solution


- complex topologies/shapes

---

```
 _   _                                         _
| | | | ___  _ __ ___   _____      _____  _ __| | __
| |_| |/ _ \| '_ ` _ \ / _ \ \ /\ / / _ \| '__| |/ /
|  _  | (_) | | | | | |  __/\ V  V / (_) | |  |   <
|_| |_|\___/|_| |_| |_|\___| \_/\_/ \___/|_|  |_|\_\
```

Detention for everyone who didn't do it!

---

# Homework

> Create a script that uses an akka stream to uppercase a text file
>
> and remove lines over 10 characters

```
input.txt                   output.txt
this is                     THIS IS
a                           A
lowercase file              FILE
file
```

But using constant memory

---

# Constant Memory

We can't load the entire file into memory

This is the easiest way to code it, but won't scale

---

# Constant Memory

> We can't load the entire file into memory

Stream the contents in chunks

```
           10MB                  10MB
input.txt  --->  (script logic)  --->  output.txt
           load                  done,
                                 discard
           <-----------------------
              communication
              "slow down"
              "speed up"
```

Keep the input rate roughly the same as the output rate

---

# Model as an akka stream

```
input.txt  --->  (script logic)  --->  output.txt
```

- input.txt represented by a `Source`


- internal steps represented by `Flow`'s


- output.txt represented by a `Sink`

---

# FileIO

Thankfully akka streams has tools for representing files as stream concepts

> input.txt represented by a `Source`

`FileIO.fromPath(path)` (Source)

> output.txt represented by a `Sink`

`FileIO.toPath(path)` (Sink)

---

# FileIO.fromPath

Streams in chunks of a file

e.g. 32MB's at a time

`Source[ByteString]`

---

# Line based logic

> Uppercase lines and remove lines over 10 characters

```
input.txt                   output.txt
this is                     THIS IS
a                           A
lowercase file              FILE
file
```

> Streams in chunks of a file e.g. 32MB's at a time

```
FileIO.fromPath(Paths.get("input.txt))           --->
                                        32MB chunk, 32MB chunk, ...
                                        ByteString  ByteString
```

Will be much easier if we can reframe the chunks into lines

---

# Reframe

```scala
  FileIO
    .fromPath(Paths.get("input.txt")) // Source[ByteString]
    .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256)) // Flow[ByteString, ByteString)]
```

```
input.txt     --->        frame        --->         ...
           32MB chunk               line chunk
           ByteString               ByteString
                                    - "this is"
                                    - "a"
                                    - "lowercase file"
                                    - "file"
```

Just need to convert to `String` with `.utf8String` and we're good to go

---

# FileIO.toPath

Similarly `FileIO.toPath(...)` is a `Sink[ByteString]`,

so we'll need to convert our lines back to `ByteString` before writing it out

---

# Enough gasbagging!

To the solution!

---

# Try yourself

Even though you've seen the solution,

there's still value in trying yourself

(and you can peek if you get stuck)

---

```
  ____                      _           
 / ___|___  _ __ ___  _ __ | | _____  __
| |   / _ \| '_ ` _ \| '_ \| |/ _ \ \/ /
| |__| (_) | | | | | | |_) | |  __/>  < 
 \____\___/|_| |_| |_| .__/|_|\___/_/\_\
                     |_|                
  ____                 _         
 / ___|_ __ __ _ _ __ | |__  ___ 
| |  _| '__/ _` | '_ \| '_ \/ __|
| |_| | | | (_| | |_) | | | \__ \
 \____|_|  \__,_| .__/|_| |_|___/
                |_|              
```

All our streams so far have been linear.

Streams can have more interesting topologies.

---

# Example

```
Diamond

        -- B --
       /        \
A -->--          ----> D
       \        /
        -- C --

     Fan        Fan
     out        in
```

---

# Everything has a shape

```scala
class Source[Out](...) extends Graph[SourceShape[Out]]

class Flow[In, Out](...) extends Graph[FlowShape[In, Out]]

class Sink[In](...) extends Graph[SinkShape[In]]

abstract class RunnableGraph extends Graph[ClosedShape]
```

---

# Hierarchies

```
Source  Flow   Sink  RunnableGraph

            Graph
```

```
SourceShape  FlowShape  SinkShape

             Shape
```

e.g. `Source[Int]` is a `Graph[SourceShape[Int]]`

---

# Why do we care?

We'll need to get our hands dirty when we want to build custom graphs

---

# GraphDSL

Used to build custom graph shapes

---

# Demo 3!

The graph dsl looks weird,

so we'll first use it to build a familiar linear graph

---

# Recapping our example

Used the graph dsl to create a closed graph

```scala
val graph = GraphDSL.create() { implicit builder =>
  ...
}
```

---

# Recapping our example

Inside, define the smaller shapes:

```scala
// Define the individual shapes
val A: SourceShape[Int]    = builder.add(...)
val B: FlowShape[Int, Int] = builder.add(...)
val C: FlowShape[Int, Int] = builder.add(...)
val D: SinkShape[Int]      = builder.add(...)
```

---

# Recapping our example

Connect them together

```scala
A  ~>  B  ~>  C  ~>  D
```

---

# Recapping our example

Return a final shape describing the graph thing you built

```scala
ClosedShape
```

---

# Diamond flow

```
         ---- +1 ----
        /      B     \
 3  ---> A           D -->  4,2
        \      C     /
         ---- -1 ----
```

```scala
val diamond = GraphDSL.create() { ... }

val graph = Source.repeat(3).via(diamond).to(Sink.foreach(println))
```

Note we're merging here, not zipping.

---

# New graph components needed

```
         ---- +1 ----
        /      B     \
 3  ---> A           D -->  4,2
        \      C     /
         ---- -1 ----
```

A: `UniformFanOutShape`

```
       /==
======
       \==
```

D: `UniformFanInShape`

```
==\
   ======
==/
```

(B and C are simple flows)

---

# Demo time!

```
         ---- +1 ----
        /      B     \
 3  ---> A           D -->  4,2
        \      C     /
         ---- -1 ----
```

demo4.sc

---

# Recap of demo 4

We used the dsl to build a sub-graph (a flow)

Then incorporated that flow into another graph

---

# Recap of demo 4

Introduced the fan-in and fan-out shapes

```
==\
   ======
==/

       /==
======
       \==
```

They connect to multiple other parts of the graph

---

# Recap of demo 4

Also saw a much simpler approach was `mapConcat`

---

# Demo 5

Zipping

```
    6,3
---->------
           \
             -->  (6,10), (3,4)
           /
---->------
   10,4
```

Not the same as merging:

```
    6,3
---->------
           \
             -->  6,3,10,4
           /
---->------
   10,4
```

(order depends on when they arrive)

Let's go!

---

# Demo 5 recap

Saw that you can zip sources together

The slower source pushes back on the faster source

---

# That's it!

---

# Recap

If you have a simple linear topology,

```
A ---> B ---> C ---> ... ---> Z
```

you can probably get away with using `Source`, `Flow` and `Sink`

and their nice combinators

---

# Complex topologies

Can use the DSL

---

# Time

Also saw that you can affect the speed messages pass through a stage

e.g. throttling

---

# Homework

Even if you've seen the answer,

there's still value in trying it

---

# Next time

Not sure...

Either materialisation concepts or async concepts

---

```
  ___                  _   _                 
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___ 
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
                                             
```
