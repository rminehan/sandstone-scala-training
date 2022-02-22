---
author: Rohan
date: 2022-02-14
title: Kafka Concepts
---

More

```
 _  __      __ _
| |/ /__ _ / _| | ____ _
| ' // _` | |_| |/ / _` |
| . \ (_| |  _|   < (_| |
|_|\_\__,_|_| |_|\_\__,_|

  ____                           _
 / ___|___  _ __   ___ ___ _ __ | |_ ___
| |   / _ \| '_ \ / __/ _ \ '_ \| __/ __|
| |__| (_) | | | | (_|  __/ |_) | |_\__ \
 \____\___/|_| |_|\___\___| .__/ \__|___/
                          |_|
```

---

# Last time

```
                            topic
                ---------------------------
               |      partitions           |
               |  ==================       |
producers ---> |  ==========               | ---> consumer groups
               |  ==============           |
               |  ===============          |
                ---------------------------
```

- use cases


- idempotency


- who wants to be a one-dollar-ionairre (MIS pending)

---

# Today

- partitions


- blockages in the pipe

---

```
 ____            _   _ _   _
|  _ \ __ _ _ __| |_(_) |_(_) ___  _ __  ___
| |_) / _` | '__| __| | __| |/ _ \| '_ \/ __|
|  __/ (_| | |  | |_| | |_| | (_) | | | \__ \
|_|   \__,_|_|   \__|_|\__|_|\___/|_| |_|___/

```

(and ordering)

---

# Ordering

Sometimes it's important you process messages in their original order

e.g. changes to a bank balance

```
balance:                  $0

message 0:  +$100       $100

message 1:   -$20        $80

message 2:  +$200       $280
```

---

# Ordering

Imagine if message 0 and message 1 were processed out of order

```
balance:                  $0

message 1:   -$20       -$20

message 0:  +$100        $80

message 2:  +$200       $280
```

Overdraught fees!

---

# Ordering

In some contexts it matters

---

# Bottlenecks

Ordering guarantees usually prevent parallel processing

---

# "One at a time"

> Ordering guarantees prevent parallel processing

e.g. you can't start processing newer messages,

until older ones are completely processed

```
balance:                  $0

message 0:  +$100       $100

message 1:   -$20        $80

message 2:  +$200       $280
```

It's a one person job

---

# Scale?

Suppose you have a "one at a time" job

What happens if the work is coming in faster than the worker can go?

---

# Scaling

> What happens if the work is coming in faster than the worker can go?

Can't parallelise (scale horizontally)

Limited to how fast you can make a worker (scale vertically)

Sounds like a bottleneck

---

# Modify our example

The queue has messages for different people

```
               amount     account      description

...

message 190:  +$100       Pranali      MVP money from Simon

message 191:    -$5       Rohan        Pork roll

message 192:    -$3       Pranali      Renting "The Castle"

message 193:    -$1       James        Who wants to be a one-dollar-ionairre sponsorship

...
```

---

# Partial ordering

> The queue has messages for different people

```
               amount     account      description

...

message 190:  +$100       Pranali      MVP money from Simon

message 191:    -$5       Rohan        Pork roll

message 192:    -$3       Pranali      Renting "The Castle"

...
```

Do we strictly need to process _all_ messages in their original order?

Could we relax that a little?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Partial ordering

> Could we relax that a little?

```
               amount     account      description

...

message 190:  +$100       Pranali      MVP money from Simon

message 191:    -$5       Rohan        Pork roll

message 192:    -$3       Pranali      Renting "The Castle"

...
```

Yes

Perhaps we just need to preserve order for particular users

---

# Partial ordering

Original order

```
message 190:  +$100       Pranali      MVP money from Simon
message 191:    -$5       Rohan        Pork roll
message 192:    -$3       Pranali      Renting "The Castle"
```

This is okay

```
message 190:  +$100       Pranali      MVP money from Simon
message 192:    -$3       Pranali      Renting "The Castle"  <---|
message 191:    -$5       Rohan        Pork roll             <---|
```

This is bad

```
message 192:    -$3       Pranali      Renting "The Castle"  <--
message 190:  +$100       Pranali      MVP money from Simon     |
message 191:    -$5       Rohan        Pork roll                |
                                            --------------------
```

---

# Maths alert

Borrowing terminology

Total ordering: when everything can be compared with everything

Partial ordering: when only some things can be compared

---

# Total ordering example

Real numbers

For any two numbers a and b, one of these will be true:

```
a < b

a == b

a > b
```

ie. they're always comparable

---

# Partial ordering

Say that one set is bigger than another if it contains it

```
{ 1, 2 }  <  { 1, 2, 3 }
```

---

# Complex example

```
{ 1, 2, 3 }  ???  { 2, 3, 4 }
```

---

# Who wants to be a one-dollar-ionairre

For 10c:

We say A > B if A contains B.

Which of the following are true:

```
(A)  { 1, 2, 3 } < { 2, 3, 4 }        (B)  { 1, 2, 3 } > { 2, 3, 4 }
(C)  { 1, 2, 3 } = { 2, 3, 4 }        (D)  None of the above
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# D

```
(A)  { 1, 2, 3 } < { 2, 3, 4 }        (B)  { 1, 2, 3 } > { 2, 3, 4 }
(C)  { 1, 2, 3 } = { 2, 3, 4 }        (D)  None of the above
```

They're not comparable

We have a partial ordering

---

# Back to our example

Original ordering

```
...
189:   +45       Pru
190:  +100       Pranali
191:    -5       Rohan
192:    -1       James
193:    -3       Pranali
194:    -7       Barry
...
```

Split our topic into 26 partitions, based on the first letter of the name:

```
A:

B:  -7 Barry

...

P:  +45 Prue, +100 Pranali, -3 Pranali

Q:

R:  -5 Rohan
```

---

# Why partition our topic?

```
A:

B:  -7 Barry

...

P:  +45 Prue, +100 Pranali, -3 Pranali

Q:

R:  -5 Rohan
```

Each partition is a mini-queue

Kafka lets you process each partition in a "one at a time" fashion

---

# Kafka's Guarantee

```
...
189:   +45       Pru
190:  +100       Pranali
...
193:    -3       Pranali
...
```

```
P:  +45 Prue, +100 Pranali, -3 Pranali
```

Partitions preserve order,

> If message A hits the topic before message B,
>
> and they go to the same partition,
>
> then message A will be before message B in that partition

---

# Mini-queue's

Partitions preserve order

```
A:

B:  -7 Barry

...

P:  +45 Prue, +100 Pranali, -3 Pranali

Q:

R:  -5 Rohan
```

(no guarantees across partitions though)

---

# Partial ordering

Our partitions are a kind of partial ordering

Messages on the same partition are comparable regarding time

Across partitions, you can't make any comparisons

---

# Parallelism

```
A:

B:  -7 Barry

...

P:  +45 Prue, +100 Pranali, -3 Pranali   (worker 1)

Q:

R:  -5 Rohan   (worker 2)
```

Kafka lets you process each partition in a "one at a time" fashion

They can work on separate partitions simultaneously

without interfering with each other

---

# Breaking the bottleneck

Instead of one big queue, you have many smaller queues

Each queue is still "one at a time", but we have more

---

# Max parallelism

In our example, what is the maximum parallelism we can achieve?

```
A:

B:  -7 Barry

...

P:  +45 Prue, +100 Pranali, -3 Pranali

Q:

R:  -5 Rohan

...
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Max parallelism

> In our example, what is the maximum parallelism we can achieve?

```
A:  +4 Allan   (worker 1)

B:  -7 Barry   (worker 2)

...

P:  +45 Prue, +100 Pranali, -3 Pranali  (worker 16)

Q:  +5 Quentin  (worker 17)

R:  -5 Rohan  (worker 18)

...

Z:  -300 Zack  (worker 26)
```

26 messages being processed simultaneously

---

# Scaling

> 26 messages being processed simultaneously

Throwing hundreds or workers at it won't help

26 workers will have the same throughput as 100 workers

---

# More throughput

How would you increase the parallelism?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# More partitions!

Your partitions control your parallelism

---

# More partitions

How could we re-partition the topic to get more partitions?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---


# More partitions

> How could we re-partition the topic to get more partitions?

Could partition by first 2 letters

```
AA:  +3 Aaron, +4 Aardvark

AB:  -5 Abraham

...
```

Now we have maximum theoretical parallelism of 26 x 26

(won't separate Pranali from Prue though...)

---

# So take note

The granularity of your partitions dictates your maximum parallelism

e.g. if you only have 5 partitions, you can process at most 5 messages simultaneously

---

# So take note

> The granularity of your partitions dictates your maximum parallelism

It is hard to re-partition a topic

So it's better to start with a fine grained partition which gives you lots of room to grow

---

# Real parallelism

Back to our 26 partitions

We have a maximum theoretical parallelism of 26

What parallelism do you think we'd actually get?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Theoretical vs Actual

> What parallelism do you think we'd actually get?

Probably not 26

---

# Partition skew

Distribution of names

```
A  ********
B  ****
C  ***********
D  ***
...
I  *
J  **********************
K  *
L  *
M  *******************
...
Q  *
R  **********
S  *********
T  ****
...
X  *
Y  *
Z  **
```

---

# Issues

```
A  ********
B  ****
C  ***********
D  ***
...
I  *
J  **********************
K  *
L  *
M  *******************
...
Q  *
R  **********
S  *********
T  ****
...
X  *
Y  *
Z  **
```

Bottlenecks and under utilised workers

Fair/predictable

---

# Partition skew

So choose a partition strategy that evenly distributes your work

```
   *******
   ******
   ********
   ****
   ******
   *****
```

---

# Default values

Watch out for highly populated values

In particular `null`, `None` and other common default values

The partition it lands in can be highly skewed

```
      ************************************************************************
      **
      **
      *
```

---

# Ideas

For our example, what kind of partitioning strategy could we use?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Example

Hash the user id to a value from 0-300

(assumes sensible things about user id and hash)

```
  -3 Pranali  ----> 5
+100 Pranali

  +3 Prue     ----> 6

  +5 Yuhan    ----> 5
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

# Partitions

A topic is internally partitioned using a partitioning strategy

```
                            topic
                ---------------------------
               |      partitions           |
               |  ==================       |
producers ---> |  ==========               | ---> consumer groups
               |  ==============           |
               |  ===============          |
                ---------------------------
```

---

# Total vs Partial ordering

Within each partition, message ordering will be preserved

(but not across partitions)

---

# Parallelism

The number of partitions limits the parallelism of your consumers

---

# Partition skew

Watch out for skew

A fine grained partitioning strategy is useless if all your messages end up in the same partition

Be careful of default values: `null`, `None`, `""`

---

# Importance of partitions

How you design your partitions is really important

It encodes your partial ordering

It affects performance and scale

---

# Importance of partitions

> How you design your partitions is really important

And it's not easy to change once you go live

So think carefully about it

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\__,_|\___||___/\__|_|\___/|_| |_|___/

```
