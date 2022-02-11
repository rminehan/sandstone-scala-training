---
author: Rohan
date: 2022-02-21
title: Kafka Concepts
---

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

Finish off our concepts

---

# Today

- error handling


- compatibility

---

# Overall

Kafka is a different ball game

Some subtle differences in how we think about design:

- error handling


- compatibility


- idempotency

---

```
 ____  _            _
| __ )| | ___   ___| | ____ _  __ _  ___
|  _ \| |/ _ \ / __| |/ / _` |/ _` |/ _ \
| |_) | | (_) | (__|   < (_| | (_| |  __/
|____/|_|\___/ \___|_|\_\__,_|\__, |\___|
                              |___/
 _         _   _
(_)_ __   | |_| |__   ___
| | '_ \  | __| '_ \ / _ \
| | | | | | |_| | | |  __/
|_|_| |_|  \__|_| |_|\___|

       _
 _ __ (_)_ __   ___
| '_ \| | '_ \ / _ \
| |_) | | |_) |  __/
| .__/|_| .__/ \___|
|_|     |_|
```

(error handling)

---

# A blockage in the pipe

Partitions are processed one message at a time

```
A:

B:  -7 Barry

...

P:  +45 Prue, +100 Pranali, -3 Pranali

Q:

R:  -5 Rohan
```

---

# A blockage in the pipe

> Partitions are processed one message at a time

So you can't get to the next message until you finish the previous one

---

# Bad message

Perhaps our producer puts a bad message on the topic

Or there is a bug in the consumer

---

# Case study

A few bad messages littered through the topic

Causes a consumer to fail

```
         ---   ---   ---   ---   ---
        |   | |   | |   | |   | |   | ....
         ---   ---   ---   ---   ---
                     bad
                      ^

         ---   ---   ---   ---   ---
        |   | |   | |   | |   | |   | ....
         ---   ---   ---   ---   ---
               bad         bad
                ^
```

---

# Offset stuck

```
         ---   ---   ---   ---   ---
        |   | |   | |   | |   | |   | ....
         ---   ---   ---   ---   ---
                     bad
                      ^

         ---   ---   ---   ---   ---
        |   | |   | |   | |   | |   | ....
         ---   ---   ---   ---   ---
               bad         bad
                ^
```

The offset only moves when a message is successfully processed

Usually it's the last step in processing a message

---

# Offset stuck

```
         ---   ---   ---   ---   ---
        |   | |   | |   | |   | |   | ....
         ---   ---   ---   ---   ---
                     bad
                      ^

         ---   ---   ---   ---   ---
        |   | |   | |   | |   | |   | ....
         ---   ---   ---   ---   ---
               bad         bad
                ^
```

These bad messages causes the consumer workers to die

One by one they each try and die

---

# Dead consumer group

> One by one they each try and die

Entire consumer group dies

Throughput of topic drops, loss of service etc...

---

# Restart service

Will this work?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Restart service

> Will this work?

Nope

The consumer group will spin up,

then they'll gradually all die again as they hit those bad messages

```
         ---   ---   ---   ---   ---
        |   | |   | |   | |   | |   | ....
         ---   ---   ---   ---   ---
                     bad
                      ^

         ---   ---   ---   ---   ---
        |   | |   | |   | |   | |   | ....
         ---   ---   ---   ---   ---
               bad         bad
                ^
```

---

# Summary: Stuck

This bad message is stuck blocking our pipe

We can still push messages onto the topic,

but nothing is being processed

---

# We're stuck

What can we do?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Ideas

- delete the bad messages


- fix the bad messages


- manually move the offset after those bad messages


- fix our consumer


- ignore it


- throw it over the fence to SPS

---

# Let's discuss the options

---

# Message tampering

> delete the bad messages
>
> fix the bad messages

A kafka message is immutable

(Aside: SQS lets you kill messages)

Also deleting the message means we don't process it

---

# Offset fiddling

> manually move the offset after those bad messages

```
         ---   ---   ---   ---   ---
        |   | |   | |   | |   | |   | ....
         ---   ---   ---   ---   ---
                     bad
                      ^ -->-

         ---   ---   ---   ---   ---
        |   | |   | |   | |   | |   | ....
         ---   ---   ---   ---   ---
               bad         bad
                ^ --->    (crash)
```

Will be fiddly as bad messages are scattered through our partitions

Loop: Move offset, restart, crash, ...

Also means we're not processing those bad messages

---

# Offset fiddling

One time solution

What if there's another bug and we have to replay again

```
         ---   ---   ---   ---   ---
        |   | |   | |   | |   | |   | ....
         ---   ---   ---   ---   ---
                     bad
                      ^
        <--------------------------------------------

         ---   ---   ---   ---   ---
        |   | |   | |   | |   | |   | ....
         ---   ---   ---   ---   ---
               bad         bad
                ^
        <--------------------------------------------
```

Will need to nurse it through these messages again

---

# Fix consumer

> fix our consumer

Makes sense if the bug is in consumer

(might take a while though)

---

# Fix consumer

> fix our consumer and replay messages

Might be really hard if the bug is in the producer,

and it's produced a non-sensical message

Responsibilities: Is it fair to make the consumer deal with this?

(and fixing the producer is good, but it doesn't undo these bad messages)

---

# Ignore it

If we can't process it, log the error, and shift the offset

Again this means we don't process it though

---

# Park the message

> throw it over the fence to ~~SPS~~ another topic

Like a "bad boy" topic

Alert the team and get it out of the way

That gets it out of the way without completely ignoring it

---

# My main point

Think about error handling

---

# Sandstone: "one size fits all"

If you use `KafkaConsumerService`, understand its policy:

- failures are logged and ignored


- probably no one will notice


- even throwables are caught


- temporary outages (like a network failure) may cause valid messages to be skipped

Does this make sense in your context?

---

# Questions to ask

> If we hit a dodgy message, how should we handle it?

(will depend on context)

> Is the consumer robust?

Would an exception in the processing logic crash it

Do we have good error handling built in

> Should we have a retry mechanism?

Some messages fail due to external circumstances

---

```
  ____                            _   _ _     _ _ _ _
 / ___|___  _ __ ___  _ __   __ _| |_(_) |__ (_) (_) |_ _   _
| |   / _ \| '_ ` _ \| '_ \ / _` | __| | '_ \| | | | __| | | |
| |__| (_) | | | | | | |_) | (_| | |_| | |_) | | | | |_| |_| |
 \____\___/|_| |_| |_| .__/ \__,_|\__|_|_.__/|_|_|_|\__|\__, |
                     |_|                                |___/
```

---

# Scenario

You produce events:

```scala
case class BalanceChange(accountId: UUID, amount: BigDecimal)

// example
producer.sendWithKey(BalanceChange(ferozId, BigDecimal("0.1")), ferozId) // Winnings from one-dollar-ionairre
```

---

# Spec change

One of our consumers wants a description

```diff
-case class BalanceChange(accountId: UUID, amount: BigDecimal)
+case class BalanceChange(accountId: UUID, amount: BigDecimal, description: String)
```

e.g. a notification on your phone that you were credited/debited

> You got 10c from one-dollar-ionairre

---

# Change it!

We implement the change and deploy our new producer and consumer

```diff
-case class BalanceChange(accountId: UUID, amount: BigDecimal)
+case class BalanceChange(accountId: UUID, amount: BigDecimal, description: String)
```

```scala
producer.sendWithKey(BalanceChange(ferozId, BigDecimal("0.1"), "one-dollar-ionairre"), ferozId)
```

What issues might this cause?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Compatibility

What happens when an old consumer tries to deserialise a new message?

---

# Compatibility

> What happens when an old consumer tries to deserialise a new message?

Probably will blow up...

## Producer model

```scala
case class BalanceChange(accountId: UUID, amount: BigDecimal, description: String)
```

## Consumer model

```scala
case class BalanceChange(accountId: UUID, amount: BigDecimal)
```

---

# Lockstep deployment

You say:

> Okay, deploy the new producer and new consumer at the same time

---

# Multiple consumers?

What if multiple consumers on this topic

Mega lockstep deployment

Sounds dodgy...

---

# Assume one consumer

> Okay, deploy the new producer and new consumer at the same time

Even if we had one consumer,

would a lockstep deployment fix this issue?

```diff
-// v1
-case class BalanceChange(accountId: UUID, amount: BigDecimal)
+// v2
+case class BalanceChange(accountId: UUID, amount: BigDecimal, description: String)
```

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# The topic

```diff
-// v1
-case class BalanceChange(accountId: UUID, amount: BigDecimal)
+// v2
+case class BalanceChange(accountId: UUID, amount: BigDecimal, description: String)
```

Suppose the consumer is lagging

```
             time ------>

... 45  46  47  48  49  50 | 51  52  53 ...
                           |
                           |
    --------- v1 --------  | --- v2 --->
                        deploy
                        new producer
                        and consumer

                    ^ offset (49)
```

---

# Lagging

```diff
-// v1
-case class BalanceChange(accountId: UUID, amount: BigDecimal)
+// v2
+case class BalanceChange(accountId: UUID, amount: BigDecimal, description: String)
```

Suppose the consumer is lagging

```
             time ------>

... 45  46  47  48  49  50 | 51  52  53 ...
                           |
                           |
    --------- v1 --------  | --- v2 --->
                        deploy
                        new producer
                        and consumer

                    ^ offset (49)
```

Now a v2 consumer is trying to process a v1 message

Kaboom!

---

# Draining the consumer

Could do:

- stop v1 producer


- wait until v1 consumer is caught up


- update producer pods to v2


- update consumer pods to v2

```
             time ------>

... 45  46  47  48  49  50 | 51  52  53 ...
                           |
                           |
    --------- v1 --------  | --- v2 --->
                        deploy
                        new producer
                        and consumer

                    ^ ----->
```

---

# Problems

Stopping the producer might mean a loss of service

---

# Problems

And what about replaying messages?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Replaying?

```diff
-// v1
-case class BalanceChange(accountId: UUID, amount: BigDecimal)
+// v2
+case class BalanceChange(accountId: UUID, amount: BigDecimal, description: String)
```

```
... 45  46  47  48  49  50 | 51  52  53 ...
                           |
                           |
    --------- v1 --------  | --- v2 --->

          <-------------------  ^
```

v2 consumer processing v1 messages

Kaboom!

---

# Rollback

A lockstep change between two services creates a nasty coupling

---

# Example

There is some unrelated bug in the producer's service causing crashes and alerts

What might the ops team try?

---

# Rollback!

Now our producer is generating v1 messages

But our consumer is meant to process v2 messages

---

# Whoops!

We fixed the producer

But broke the consumer

(and the ops team would not understand the relationship)

---

# Coupling

Lockstep rolling forward,

means lockstep rolling back

Widened the footprint of a rollback

---

# My point

There is a lot of value in making your changes backwards compatible

---

# Example

> There is a lot of value in making your changes backwards compatible

```diff
-// v1
-case class BalanceChange(accountId: UUID, amount: BigDecimal)
+// v2
+case class BalanceChange(accountId: UUID, amount: BigDecimal, description: Option[String])
                                                                            ^^^^^^
```

Deserialisation can handle missing data:

```
// v1 message
{
    id: "abc123",                        ------>       BalanceChange("abc123", BigDecimal("0.1"), None)
    amount: "0.1"                                                                                 ^^^^
}


// v2 message
{
    id: "xyz456",                        ------>       BalanceChange("xyz456", BigDecimal("-2.4"), Some("Coffee"))
    amount: "-2.4",
    description: "Coffee"
}
```

---

# The essence

Used a model that can represent v1 and v2 messages:

```diff
-// v1
-case class BalanceChange(accountId: UUID, amount: BigDecimal)
+// v2
+case class BalanceChange(accountId: UUID, amount: BigDecimal, description: Option[String])
                                                                            ^^^^^^
```

v2 consumer is capable of handling both (ie. doesn't _assume_ there's a description):

```scala
val balanceChange = ...

val description = balanceChange.description.getOrElse("<Not provided>")
```

---

# Icky?

You say:

> So I have to handle different versions of messages?
>
> What if there are many changes?
>
> There will be options everywhere in my models!
>
> That's a bit icky...

---

# Icky!

> So I have to handle different versions of messages?
>
> What if there are many changes?
>
> There will be options everywhere in my models!
>
> That's a bit icky...

It can be

It's a trade off

Let's look at how it addresses the issues we raised

---

# Lockstep deployments

---

# Lockstep deployments

Recall:

> non-backwards compatible changes require draining the consumers,
>
> then doing a lock step deployment

---

# Lockstep deployments

Recall:

> non-backwards compatible changes require draining the consumers,
>
> then doing a lock step deployment

If the changes are backwards compatible:

- we can gradually upgrade all consumers to v2


- then upgrade producer to v2

```diff
-// v1
-case class BalanceChange(accountId: UUID, amount: BigDecimal)
+// v2
+case class BalanceChange(accountId: UUID, amount: BigDecimal, description: Option[String])
```

(note the consumers have to all go first, can't have v1 consumers processing v2 messages)

---

# Lockstep deployment

If the changes are backwards compatible:

- much more flexibility and freedom in deployment


- no downtime (don't need to drain consumers)

---

# Replaying messages

---

# Replaying messages

Recall:

> non-backwards compatible consumers won't be able to reprocess older messages

```diff
-// v1
-case class BalanceChange(accountId: UUID, amount: BigDecimal)
+// v2
+case class BalanceChange(accountId: UUID, amount: BigDecimal, description: String)
```

This limits our options when tackling some bug

---

# Replaying messages

Recall:

> non-backwards compatible consumers won't be able to reprocess older messages

But if we make it backwards compatible, no problem

```diff
-// v1
-case class BalanceChange(accountId: UUID, amount: BigDecimal)
+// v2
+case class BalanceChange(accountId: UUID, amount: BigDecimal, description: Option[String])
```

Our new consumer will be able to handle v1 and v2 messages

---

# Rollbacks

---

# Rollbacks

Recall:

> non-backwards compatible changes require a lockstep roll forward,
>
> that means any rollback has to also be lockstep too

Rollback footprint widens and you're more likely to rollback other unrelated changes

---

# Rollbacks

Recall:

> non-backwards compatible changes require a lockstep roll forward,
>
> that means any rollback has to also be lockstep too

But if our change is backwards compatible,

we can at least rollback the producer to v1

and can leave the consumer on v2

---

# Rollback consumer?

Could we rollback the consumer to v1

and leave the producer at v2?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Rollback consumer

> Could we rollback the consumer to v1
>
> and leave the producer at v2?

No   :(

Messages will have descriptions

Old consumer won't understand them

```scala
// v1
case class BalanceChange(accountId: UUID, amount: BigDecimal)

// Message
BalanceChange("abc123", BigDecimal("0.1"), Some("Coffee"))
```

---

# Note

Consumers have to stay ahead of producers

(but we can at least rollback producers)

---

# Tradeoff

Maintaining backwards compatibility can make your models and service logic icky

But it makes life easier at an operational level

Analogy: Java vs Scala

---

# Best of both worlds?

---

# Retention

Suppose you have retention of 2 months

(recall retention is how long your messages live on the topic before being cleaned up)

---

# Clean up

> Suppose you have retention of 2 months

```
             time ------>

... xx  xx  xx  xx  xx  xx | 51  52  53 ...                        3000
                           |
                           |
    --------- v1 --------  | --------------- v2 ---------------------
                        deploy
                        new producer
                        and consumer
                          Jan 1st                                   Mar 1st
```

As of March 1st, all v1 messages are dead

---

# Rollbacks?

Only way we'd get more v1 messages is if we rolled back the producer

```
             time ------>

... xx  xx  xx  xx  xx  xx | 51  52  53 ...                        3000
                           |
                           |
    --------- v1 --------  | --------------- v2 ---------------------
                        deploy
                        new producer
                        and consumer
                          Jan 1st                                   Mar 1st
```

---

# No rollback

> Only way we'd get more v1 messages is if we rolled back the producer

Very unlikely we'd rollback the producer 2 months

Would be rolling back heaps of other stuff

---

# So

We can assume we never have to process v1 messages

(even if we have to replay old messages)

---

# Remove Option

> We can assume we never have to process v1 messages

All new messages must have a descriptions

```diff
-// v2
-case class BalanceChange(accountId: UUID, amount: BigDecimal, description: Option[String])
+// v3
+case class BalanceChange(accountId: UUID, amount: BigDecimal, description: String)
```

Deploy to consumers and producers in whatever order

---

# Putting it together

Backwards compatible changes make operations easier but code icky

Once old messages are clear of your system you can remove the backwards compatibility ickyness

It just needs to exist through that migration period

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

# Blockage in the pipe

Think about error handling when you architect your kafka systems

A blockage in the pipe can be very damaging to your service,

and time consuming to fix

---

# Error handling

Not fun to think about,

but important

(and often overlooked)

---

# Backwards compatibility

Be careful when you make changes

Backwards compatible changes will avoid a lot of operational headaches

---

# That's it for kafka!

Hope that helped

If not, you get what you pay for

---

# Coming up

Back to scala/FP

Pranali will doing a session soon!

---

```
  ___                  _   _
 / _ \ _   _  ___  ___| |_(_) ___  _ __  ___
| | | | | | |/ _ \/ __| __| |/ _ \| '_ \/ __| ?
| |_| | |_| |  __/\__ \ |_| | (_) | | | \__ \
 \__\_\__,_|\___||___/\__|_|\___/|_| |_|___/

```

