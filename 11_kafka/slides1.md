---
author: Rohan
date: 2022-02-10
title: Kafka Concepts
---

```
 _  __      __ _
| |/ /__ _ / _| | ____ _
| ' // _` | |_| |/ / _` |
| . \ (_| |  _|   < (_| | !!
|_|\_\__,_|_| |_|\_\__,_|

```

Concepts

---

# Background

Some requests for kafka training

Assuming you have a basic understanding

---

# Plan

3-4 sessions

- concepts


- case studies

---

# Overall goal

Get across the essence

Leave the gritty details to moocs

---

# Aside: Kafka Hype

Like an expensive complex powertool

Don't assume it's the best tool for every job

---

```
  ____                           _
 / ___|___  _ __   ___ ___ _ __ | |_ ___
| |   / _ \| '_ \ / __/ _ \ '_ \| __/ __|
| |__| (_) | | | | (_|  __/ |_) | |_\__ \
 \____\___/|_| |_|\___\___| .__/ \__|___/
                          |_|
```

---

# Today

- basic terminology


- major use cases


- idempotence

---

# Basic terminology

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

```
 __  __        _
|  \/  | __ _ (_) ___  _ __
| |\/| |/ _` || |/ _ \| '__|
| |  | | (_| || | (_) | |
|_|  |_|\__,_|/ |\___/|_|
            |__/
 _   _
| | | |___  ___
| | | / __|/ _ \
| |_| \__ \  __/
 \___/|___/\___|

  ____
 / ___|__ _ ___  ___  ___
| |   / _` / __|/ _ \/ __|
| |__| (_| \__ \  __/\__ \
 \____\__,_|___/\___||___/

```

---

# Major use cases

Major ones I've seen

- events


- fire'n'forget back end processing

---

# Events

---

# Events

When you broadcast that something happened

There are interested parties listening

---

# Example

Event: A big document uploads

Listeners:

- send a notification email


- increment a counter (used in analytics)


- notify FE using a websocket

---

# Direct notification

Imagine if the broadcaster directly notified the listeners:

```scala
val bigDocumentUploadFut: Future[Document] = ...

bigDocumentUploadedFut.foreach { document =>
  sendNotificationEmail(document)
  incrementUploadCounter()
  notifyFE()
  ...
}
```

What are some issues with this approach?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Direct notification

Imagine if the broadcaster directly notified the listeners:

```scala
val bigDocumentUploadFut: Future[Document] = ...

bigDocumentUploadedFut.foreach { document =>
  sendNotificationEmail(document)
  incrementUploadCounter()
  notifyFE()
  ...
}
```

> What are some issues with this approach?

- couples the broadcaster to the listeners, not "open-closed"


- responsibility creep


- availability issues


- some tricky issues related to execution resources

---

# Middle man

Nice to introduce a middle layer

```
                             ---->  listener 1
broadcast  ---->   messages  ---->  listener 2
                             ---->  listener 3
```

"publish subscribe" (pubsub)

---

# Pubsub

Don't immediately think: we must use kafka!

---

# Pubsub

Don't immediately think:

> Pubsub? We must use kafka!

Maybe a simpler, cheaper tool will be fine

Will depend

---

# Persistence

Keeping events after they're fired

---

# Persistence

Can be useful

Some architectures don't have it

Analogy: sports stadium with announcements over the PA system

---

# Persistence

> Some architectures don't persist announcements

If you miss the announcement,

too bad

---

# Persistence

> Some architectures don't persist announcements

If there is a burst of announcements and you can't keep up,

too bad

---

# Persistence

> Some architectures don't persist announcements

If you want to go back through the announcements (ie. replay them),

too bad

---

# Persistence

> Some architectures don't persist announcements

If you encounter an error whilst processing an announcement,

too bad

---

# Kafka

Has persistence

Gives us more flexibility

---

# Persistence analogy

Each announcement is turned into a post-it note and put on a board

Each consumer has an "offset" saying where they're up to

```
         ---   ---   ---   ---   ---
        | 0 | | 1 | | 2 | | 3 | | 4 | ....
         ---   ---   ---   ---   ---

consumer        ^
   1

consumer  ^
   2

consumer             ^
   3
```

(simplification that ignores partitions)

---

# Topics

Kafka topics play the role of the board

```
         ---   ---   ---   ---   ---
TOPIC   | 0 | | 1 | | 2 | | 3 | | 4 | ....
         ---   ---   ---   ---   ---

consumer        ^
   1

consumer  ^
   2

consumer             ^
   3
```

---

# Useful

```
         ---   ---   ---   ---   ---
TOPIC   | 0 | | 1 | | 2 | | 3 | | 4 | ....
         ---   ---   ---   ---   ---

consumer        ^
   1

consumer  ^
   2

consumer             ^
   3
```

Persistence makes it possible for consumers to:

- reprocess old messages


- handle bursts


- retry processing on an error


- have downtime without missing messages

---

# Useful persistence

So if you need persistence, kafka might be the right tool

If you don't, look at some simpler alternatives

---

# Retention

---

# Retention

How long a message sits on a topic before it's cleaned up

```
         ---   ---   ---   ---   ---
TOPIC   | x | | x | | 2 | | 3 | | 4 | ....
         ---   ---   ---   ---   ---

         cleaning --->
```

---

# Infinite retention

> Why not keep them messages forever?

You can, but messages use disk space so your cost will keep rising

Having finite retention lets you recycle space

---

# Setting retention period

Long enough to

- endure bursts


- let you debug issues


- replay recent messages

Usually a few weeks or months is good

---

# That was events

(and some persistence thrown in)

---

# Fire'n'forget jobs

---

# Fire'n'forget

Analogous to putting a letter in the postbox

---

# Fire'n'forget

Analogous to putting a letter in the postbox

Your part is done, go and do something else

Now the postal service will (hopefully) take over processing it

---

# Translating to kafka

A kafka topic is a work queue

Upstream producers push work onto the queue in the form of messages

Downstream consumers are the workers processing the messages

```
                            ---->  worker 1
producer  ---->  work queue ---->  worker 2
                            ---->  worker 3
```

---

# Example 1

Your product is a database of company contact information

- company name


- company domain (e.g. google.com)


- employee count


- linkedin page


- location


- revenue

---

# Change

> Your product is a database of company contact information

This data constantly changes

- companies grow and shrink


- revenue changes


- companies die


- companies are born

Don't want a stale db

---

# Keeping data fresh

```
         Back end processing
                                      |
                                      |
 --->                                 |
 --->  updates ---> updater --->  database   <----   customer requests
 --->    topic                        |      ---->
                                      |
                                      |
```

Many different sources of updates hitting the updates topic

---

# Big queue

```
         Back end processing
                                      |
                                      |
 --->                                 |
 --->  updates ---> updater --->  database   <----   customer requests
 --->    topic                        |      ---->
                                      |
                                      |
```

Messages sit in the queue for days when there's a burst (e.g. a big import)

But that's okay, it's not customer generated work

---

# Example 2

User POST's a heavy job (2 hours to process)

---

# In detail

- user does POST


- service generates an id and returns it immediately to the user


- work item gets pushed to a queue


- user can use their id to check on the status of the work item

---

# Summarising so far

---

# Events and fire'n'forget

Two different paradigms that can both work on kafka

## Events

Kafka messages represents things that happened

e.g. page uploaded

Usually many consumers

## Fire'n'forget

Kafka messages represent work to be done

Usually one consumer group and perhaps many producers

---

```
 ___    _                            _
|_ _|__| | ___ _ __ ___  _ __   ___ | |_ ___ _ __   ___ _   _
 | |/ _` |/ _ \ '_ ` _ \| '_ \ / _ \| __/ _ \ '_ \ / __| | | |
 | | (_| |  __/ | | | | | |_) | (_) | ||  __/ | | | (__| |_| |
|___\__,_|\___|_| |_| |_| .__/ \___/ \__\___|_| |_|\___|\__, |
                        |_|                             |___/
```

---

# Idempotency

It's helpful when your consumers are "idempotent"

---

# Idempotent?

What is it?

```
  ******************

  WARNING!

  WARNING!

  MATHS APPROACHING!

  ******************
```

---

# Idempotent

A function/operation is idempotent if applying it twice

has the same effect as applying it once

---

# Real world example

You have a garage with a "open door" button

---

# Real world example

Push the button once: the door opens

---

# Real world example

Push the button once: the door opens

Push the button again: the door doesn't get any more open

---

# Real world example

> Push the button once: the door opens
>
> Push the button again: the door doesn't get any more open

Pushing the button twice has the same effect as pushing it once

---

# Hammering a nail

Has an idempotent vibe to it


After a while, hammering it more doesn't do anything

---

# Formal definition

A transformation `f` is idempotent if:

```
f(a) = f(f(a))    for all a
```

---

# Maths examples

Truncating floats/doubles

```
3.54  --->  3   --->  3
```

---

# Actually...

You can prove:

If doing something twice is the same as doing it once

Then doing it 2,3,4,5,... times is the same as doing it once

```
3.54  --->  3   --->  3  --->  3  --->  3
```

(can be a homework proof)

---

# Other examples

```
toUpperCase:  "abc"  --->  "ABC"  --->  "ABC"

take(3):      List(1,2,3,4,5)  --->  List(1,2,3)  --->  List(1,2,3)
```

---

# For fun

We'll play "Who wants to be a one-dollar-ionairre"

Sponsored by James (thanks James!)

---

# First...

A message from our sponsor

---

# Contestant come on down

---

# Question 1

For 5 cents,

which of the following _isn't_ idempotent:

```
(A) toLowerCase     (B) trim

(C) i => -i         (D) math.abs
```

(can do 50/50 and phone a friend)

---

# Answer 1: (C)

For 5 cents,

which of the following _isn't_ idempotent:

```
(A) toLowerCase     (B) trim

(C) i => -i         (D) math.abs
^^^^^^^^^^^



(A) "Hello"  --->  "hello"  --->  "hello"
(B) " foo "  --->  "foo"  --->  "foo"
(C) 3  --->  -3  --->  3
(D) -4  --->  4  --->  4
```

---

# That's it...

Sponsor ran out of money

Pulled the plug

---

# Back to kafka...

---

# Earlier we said

> It's helpful when your consumers are "idempotent"

What does that mean?

---

# The reality of message based systems

Sometimes messages get processed more than once

---

# Reprocessing

> Sometimes messages get processed more than once

Why?

- buggy producer duplicates a message


- replaying old messages


- retrying due to failure

---

# Event example

A message represents a "document uploaded" event

A listener has simple logic that sends the user an email

---

# Bug discovered

We realised that for UK customers, the emails weren't sending

There is a compliance requirement to send them this email

---

# Looking at our topic

A mixture of UK and AU customers

```
             time ------>

... 50 | 51  ...  150  151  152  153  154  155  156  157 (present)
    AU | UK       AU   UK   UK   AU   AU   AU   AU   UK
       |                                                  ^ offset
       |
      bug
    introduced
      here
```

---

# Fix the bug

We have fixed the bug so that now UK customers will get emails

```
             time ------>

... 50 | 51  ...  150  151  152  153  154  155  156  157  |  158  159
    AU | UK       AU   UK   UK   AU   AU   AU   AU   UK   |  UK   UK
       |                                                  |       ^ offset
       |                                                  |
      bug                                               bug
    introduced                                          fixed
      here
```

But we still have to send emails for those older events

---

# Idea

We need to generate emails for those events processed whilst the bug was active

```
             time ------>

... 50 | 51  ...  150  151  152  153  154  155  156  157  |  158  159
    AU | UK       AU   UK   UK   AU   AU   AU   AU   UK   |  UK   UK
       | ^                                                |
       |  ---------<-------------<------------<---------  |
      bug                                               bug
    introduced                                          fixed
      here
```

Idea: reset the offset back to message 51 so that they are reprocessed

What's the problem?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---


# Icky problem

```
             time ------>

... 50 | 51  ...  150  151  152  153  154  155  156  157  |  158  159
    AU | UK       AU   UK   UK   AU   AU   AU   AU   UK   |  UK   UK
       | ^       dup            dup  dup  dup  dup        |
       |  ---------<-------------<------------<---------  |
      bug                                               bug
    introduced                                          fixed
      here
```

> Idea: reset the offset back to message 51 so that they are reprocessed

AU customers in that time window will get a second email

Might need to do something icky like get all the UK messages and hand write a script

to email them (days of fiddly work)

---

# Hopefully

Hopefully the original author made the consumer idempotent

```
             time ------>

... 50 | 51  ...  150  151  152  153  154  155  156  157  |  158  159
    AU | UK       AU   UK   UK   AU   AU   AU   AU   UK   |  UK   UK
       | ^                                                |
       |  ---------<-------------<------------<---------  |
      bug                                               bug
    introduced                                          fixed
      here
```

e.g. there is a flag for whether they've been emailed

Pseudo-code

- check if flag is false


- send email


- set flag to true

---

# So...

Idempotent consumer logic can be really handy

Gives you more weapons when you need to tackle unusual situations

---

```
__        __                     _               _   _
\ \      / / __ __ _ _ __  _ __ (_)_ __   __ _  | | | |_ __
 \ \ /\ / / '__/ _` | '_ \| '_ \| | '_ \ / _` | | | | | '_ \
  \ V  V /| | | (_| | |_) | |_) | | | | | (_| | | |_| | |_) |
   \_/\_/ |_|  \__,_| .__/| .__/|_|_| |_|\__, |  \___/| .__/
                    |_|   |_|            |___/        |_|
```

---

# Kafka

A messaging technology that supports persistence

---

# Persistence

Gives you flexibility

- can process messages at your own speed


- can reprocess messages


- can be offline when messages are produced

---

# Common use cases

- events


- work items

---

# Idempotency

When applying something multiple times is the same as applying it once

Idempotent consumer logic can be very helpful

---

# Next time

Partitions

---

# Feedback

Questions?

How much of that did you guys already know?

Anything unclear?

Any requests?

James to put in MIS request for more one-dollar-ionairre budget?
