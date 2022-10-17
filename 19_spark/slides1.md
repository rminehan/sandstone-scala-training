---
author: Rohan
date: 2022-09-30
title: Spark Overview
---

```
 ____                   _
/ ___| _ __   __ _ _ __| | __
\___ \| '_ \ / _` | '__| |/ /
 ___) | |_) | (_| | |  |   <
|____/| .__/ \__,_|_|  |_|\_\
      |_|
```

---

# Spark Training

Short notice training

Will continue with our regular training

---

# Spark: What's it for?

In a nutshell:

> For data pipelines that are too big for one computer

---

# Spark and ML?

You can do ML with Spark,

but its primary job is "data engineering"

---

# "Data engineering"?

Manipulating huge sets of data

o  ETL jobs ("extract transform load")

o  Analytics

---

# ETL Job Example

Imagine you had a large file of names `names.txt` that you wanted to upper case:

```
names.txt                      names_upper.txt
--------                       ---------
Boban                          BOBAN
Bobanita                       BOBANITA
Pranali                        PRANALI
Ben                ---->       BEN
Enxhell                        ENXHELL
Ritchie                        RITCHIE
Rohan                          ROHAN
...                            ...
Lulu                           LULU
Boban                          BOBAN
Yuman                          YUMAN
Zij                            ZIJ
Feroz                          FEROZ
Ben                            BEN
```

---

# ETL Job Example

```
names.txt                      names_upper.txt
--------                       ---------
Boban                          BOBAN
Bobanita                       BOBANITA
Pranali                        PRANALI
Ben                ---->       BEN
Enxhell                        ENXHELL
Ritchie                        RITCHIE
Rohan                          ROHAN
...                            ...
Lulu                           LULU
Boban                          BOBAN
Yuman                          YUMAN
Zij                            ZIJ
Feroz                          FEROZ
Ben                            BEN
```

What operation does this remind you of?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# ETL Job Example

```
names.txt                      names_upper.txt
--------                       ---------
Boban                          BOBAN
Bobanita                       BOBANITA
Pranali                        PRANALI
Ben                ---->       BEN
Enxhell                        ENXHELL
Ritchie                        RITCHIE
Rohan                          ROHAN
...                            ...
Lulu                           LULU
Boban                          BOBAN
Yuman                          YUMAN
Zij                            ZIJ
Feroz                          FEROZ
Ben                            BEN
```

> What operation does this remind you of?

`names.map(_.toUpperCase)`

---

# Analytics example

Count how many words start with 'J'

```
names.txt
--------
Boban
Bobanita
Pranali
Ben                ---->       150,332
Enxhell
Ritchie
Rohan
...
Lulu
Boban
Yuman
Zij
Feroz
Ben
```

---

# Analytics example

```
names.txt
--------
Boban
Bobanita
Pranali
Ben                ---->       150,332
Enxhell
Ritchie
Rohan
...
Lulu
Boban
Yuman
Zij
Feroz
Ben
```

What operation does this remind you of?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Analytics example

```
names.txt
--------
Boban
Bobanita
Pranali
Ben                ---->       150,332
Enxhell
Ritchie
Rohan
...
Lulu
Boban
Yuman
Zij
Feroz
Ben
```

> What operation does this remind you of?

`names.count(_.startsWith("J"))`

---

# Demo

Let's do this example in plain scala

---

# Summary

```scala
import ammonite.ops._
val names = read(pwd / "names.txt").split("\n")
names.count(_.startsWith("J"))
```

---

# Spark?

Pfft...

I can solve that problem with regular scala tools...

---

# Implicit assumption

What implicit assumptions does my solution make?

```scala
import ammonite.ops._
val names: Seq[String] = read(pwd / "names.txt").split("\n")
names.count(_.startsWith("J"))
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Implicit assumption

What implicit assumptions does my solution make?

```scala
import ammonite.ops._
val names: Seq[String] = read(pwd / "names.txt").split("\n")
names.count(_.startsWith("J"))
```

"Small" data

---

# Conceptually

```
DISK                            RAM (JVM)

names.txt                       names
--------                        --------
Boban                           Boban
Bobanita                        Bobanita
Pranali                         Pranali
Ben                ---->        Ben           ---->     50,332
Enxhell                         Enxhell
Ritchie                         Ritchie
Rohan                           Rohan
...                             ...
Lulu                            Lulu
Boban                           Boban
Yuman                           Yuman
Zij                             Zij
Feroz                           Feroz
Ben                             Ben
Varun                           Varun
```

```scala
import ammonite.ops._
val names: Seq[String] = read(pwd / "names.txt").split("\n")
names.count(_.startsWith("J"))
```

---

# Conceptually

```
DISK                            RAM (JVM)

names.txt                       names
--------                        --------
Boban                           Boban
Bobanita                        Bobanita
Pranali                         Pranali
Ben                ---->        Ben           ---->     50,332
Enxhell                         Enxhell
Ritchie                         Ritchie
Rohan                           Rohan
...                             ...
Lulu                            Lulu
Boban                           Boban
Yuman                           Yuman
Zij                             Zij
Feroz                           Feroz
Ben                             Ben
Varun                           Varun
```

Let n be the number of lines in `names.txt`.

What is the time/space complexity of our analytics job?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Conceptually

```
DISK                            RAM (JVM)

names.txt                       names
--------                        --------
Boban                           Boban
Bobanita                        Bobanita
Pranali                         Pranali
Ben                ---->        Ben           ---->     50,332
Enxhell                         Enxhell
Ritchie                         Ritchie
Rohan                           Rohan
...                             ...
Lulu                            Lulu
Boban                           Boban
Yuman                           Yuman
Zij                             Zij
Feroz                           Feroz
Ben                             Ben
Varun                           Varun
```

> What is the time/space complexity of our analytics job?

- time: O(n)


- space: O(n)

Ouch...

---

# Scale

Our approach doesn't scale

- JVM will run out of memory


- even if it didn't, the job will start to get slow

---

# Bias

Most tools/patterns you use as software engineers have an implicit assumption of "small" data

- loading a complete file into memory (like PDBox)


- loading the results of a database query into memory

---

# Aside

Similar to how a lot of libraries/patterns are implicitly blocking

Developers are so used to it they don't think about it

---

# Changing mindsets

In a data engineering context, you need to think more carefully about data

- how big is it?


- how much IO will there be moving it around?


- will it fit in memory?


- will it grow over time?


- how fast will it be?


- will there be shuffles?


- will there be partition skew?

---

# Changing mindsets

> In a data engineering context, you need to think more carefully about data

## Software engineering hat

- does it compile?


- do the tests pass?


- is it nice code?

...

## Data engineering hat

(add to the above)

- will it scale?


- will it take 10 minutes or 6 hours?


- if it crashes halfway through do I have to rerun it from the start?

---

# Spark

---

# Spark

Tool for "big data" engineering

- scaling across a cluster


- parallelism

---

# Partitioning

```
                                                 CLUSTER
DISK                            Worker 1         Worker 2         Worker 3
                                --------------------------------------------
names.txt
--------
Boban                           |
Bobanita                        |
Pranali                         |  ---> 15,110
Ben                ---->        |                                                      ---->     50,332
Enxhell                         |
Ritchie                                          |
Rohan                                            |
...                                              |  ---> 17,938
Lulu                                             |
Boban                                            |
Yuman                                            |
Zij                                                              |
Feroz                                                            |  ---> 17,284
Ben                                                              |
Varun                                                            |
```

---

# Scaling up

```
                                                 CLUSTER
DISK                            Worker 1         Worker 2         Worker 3
                                --------------------------------------------
names.txt
--------
Boban                           |
Bobanita                        |
Pranali                         |  ---> 15,110
Ben                ---->        |                                                      ---->     50,332
Enxhell                         |
Ritchie                                          |
Rohan                                            |
...                                              |  ---> 17,938
Lulu                                             |
Boban                                            |
Yuman                                            |
Zij                                                              |
Feroz                                                            |  ---> 17,284
Ben                                                              |
Varun                                                            |
```

We spread our data set across a cluster

ie. throw more hardware at it

---

# Analysing

```
                                                 CLUSTER
DISK                            Worker 1         Worker 2         Worker 3
                                --------------------------------------------
names.txt
--------
Boban                           |
Bobanita                        |
Pranali                         |  ---> 15,110
Ben                ---->        |                                                      ---->     50,332
Enxhell                         |
Ritchie                                          |
Rohan                                            |
...                                              |  ---> 17,938
Lulu                                             |
Boban                                            |
Yuman                                            |
Zij                                                              |
Feroz                                                            |  ---> 17,284
Ben                                                              |
Varun                                                            |
```

If n is the number of names, what's the time/space complexity?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Analysing

```
                                                 CLUSTER
DISK                            Worker 1         Worker 2         Worker 3
                                --------------------------------------------
names.txt
--------
Boban                           |
Bobanita                        |
Pranali                         |  ---> 15,110
Ben                ---->        |                                                      ---->     50,332
Enxhell                         |
Ritchie                                          |
Rohan                                            |
...                                              |  ---> 17,938
Lulu                                             |
Boban                                            |
Yuman                                            |
Zij                                                              |
Feroz                                                            |  ---> 17,284
Ben                                                              |
Varun                                                            |
```

> If n is the number of names, what's the time/space complexity?

- time O(n) (lower constant)


- space O(n)

---

# Time complexity

Horizontal scaling won't change the class of your algorithm, e.g. `O(n) -> O(logn)`

It will speed it up by a constant factor

e.g. 30 minutes -> 10 minutes

---

# Demo time

Let's see spark in action

---

# Summary

```scala
val names = session.read.textFile("names.txt")

val cleaned = names.map(_.trim).filter(_.nonEmpty)

val jNames = cleaned.filter(_.startsWith("J"))

val jNameCount = jNames.count()
```

(with a lot of boilerplate around it)

---

# Observations

---

# Seq'y vibe

```scala
val cleaned = names.map(_.trim).filter(_.nonEmpty) // map and filter!

val jNames = cleaned.filter(_.startsWith("J")) // filter!
```

Feels just like the `Seq` api from the scala standard library

---

# Seq'y vibe

> Feels just like the `Seq` api from the scala standard library

Feels very natural to us

Maybe _too_ natural - don't forget to wear your data engineering hat

---

# ML?

> I thought we used spark for ML?

---

# ML?

> I thought we used spark for ML?

More on that later

At its core, spark is a data engineering tool

It has some basic ML capabilities on top of that

---

# Sandstone

> Do we do "big data" at Sandstone?

---

# Sandstone

> Do we do "big data" at Sandstone?

Not really

Our training data is still quite small - doesn't need a cluster

---

# Scaling up and down

Spark is nice because you can scale up and down

---

# Upcoming

Today was just a teaser, there's so much more to talk about...

---

# Upcoming

- RDD's


- common gotchas


- spark sql


- spark ML

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\\__,_|\___||___/\__|_|\___/|_| |_|___/
```
