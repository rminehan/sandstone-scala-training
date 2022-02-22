---
author: Rohan
date: 2022-02-17
title: Kafka Case Study
---

```
 _  __      __ _         
| |/ /__ _ / _| | ____ _ 
| ' // _` | |_| |/ / _` |
| . \ (_| |  _|   < (_| |
|_|\_\__,_|_| |_|\_\__,_|
                         
  ____               
 / ___|__ _ ___  ___ 
| |   / _` / __|/ _ \
| |__| (_| \__ \  __/
 \____\__,_|___/\___|
                     
 ____  _             _       
/ ___|| |_ _   _  __| |_   _ 
\___ \| __| | | |/ _` | | | |
 ___) | |_| |_| | (_| | |_| |
|____/ \__|\__,_|\__,_|\__, |
                       |___/ 
```

---

# Case study

Take a break from theory

Look at sandstone code

---

# Today

Understand the producer and consumer scaffolding

and how it's used

---

# How

- look briefly at our scaffolding


- look at `docrequirementservice` (has producers and consumers)


- looks deeper at the scaffolding

---

```
 ____             __  __       _     _ _             
/ ___|  ___ __ _ / _|/ _| ___ | | __| (_)_ __   __ _ 
\___ \ / __/ _` | |_| |_ / _ \| |/ _` | | '_ \ / _` |
 ___) | (_| (_| |  _|  _| (_) | | (_| | | | | | (_| |
|____/ \___\__,_|_| |_|  \___/|_|\__,_|_|_| |_|\__, |
                                               |___/ 
```

---

# `docservicemodel`

Contains common definitions:

- (producer) `services.KafkaEventProducer`


- (consumer) `services.KafkaConsumerService`


- (message) `events.EventEnvelope`

To the code!

---

# Scaffolding

Wrappers to abstract over kafka/akka-streams details

```scala
case class EventEnvelope(...)

class KafkaEventProducer(...) {
  def sendWithKey(envelope: EventEnvelope, key: String): Future[RecordMetadata] = {
    ...
  }
  // Kafka stuff inside
  ...
}

class KafkaConsumerService(...) {
  def subscribe(topicNames: Set[String], handleEvent: EventEnvelope => Unit): Future[Unit] = {
    ...
  }
  // Akka stream stuff inside
  ...
}
```

---

```
  ____               
 / ___|__ _ ___  ___ 
| |   / _` / __|/ _ \
| |__| (_| \__ \  __/
 \____\__,_|___/\___|
                     
     _             _       
 ___| |_ _   _  __| |_   _ 
/ __| __| | | |/ _` | | | |
\__ \ |_| |_| | (_| | |_| |
|___/\__|\__,_|\__,_|\__, |
                     |___/ 
```

---

# AnalysisEventConsumer/FrontendUpdateProducer

- 1 producer


- 2 consumers

Linked together

---

# Consumer 1

```scala
class AnalysisEventConsumer @Inject() (
    ...
    kafkaConsumerService: KafkaConsumerService // from docservicemodels
) {

  kafkaConsumerService.subscribe(
    Set(KafkaTopics.Analysis, KafkaTopics.Verification),
    KafkaConsumerService.analysisConsumerGroupName,
    handleEvent, // Handling logic: EventEnvelope => Unit
    enablePing = true
  )

  private def handleEvent(event: EventEnvelope): Unit = {
    ...
  }

  ...
}
```

To the code!

---

# Producer

```scala
class FrontendUpdateProducer(...) {

  private val kafkaProducer = new KafkaEventProducer(KafkaTopics.FrontendUpdate, ...)

  def forwardFrontendUpdate(orderKey: String, originalEvent: EventEnvelope): Future[RecordMetadata] = {
    ...
    kafkaProducer.sendWithKey(record, orderKey)
  }
}
```

Sends events to the "frontend-update" topic

To the code!

---

# Usage

Who uses this producer?

To the code!

---

# Summary

`AnalysisEventConsumer.handleEvent` uses `FrontendUpdateProducer.forwardFrontendUpdate` to broadcast analysis events

---

# Topology

```
 --------------
|              |
|   analysis   |  ------
|              |        \                                  -----------------
 --------------          \                                |                 |
                          ---> AnalysisEventConsumer ---> | frontend-update |
 --------------          /         handleEvent            |                 |
|              |        /                                  -----------------
| verification |  ------
|              |
 --------------
```

---

# And then?

```
 --------------
|              |
|   analysis   |  ------
|              |        \                                  -----------------
 --------------          \                                |                 |
                          ---> AnalysisEventConsumer ---> | frontend-update | ---> ???
 --------------          /         handleEvent            |                 |
|              |        /                                  -----------------
| verification |  ------
|              |
 --------------
```

Who consumes the frontend updates?

How do they get to the FE?

To the code!

---

# AnalysisEventConsumer!

It consumes its own messages!

```scala
kafkaConsumerService.subscribe(
  Set(KafkaTopics.Analysis, KafkaTopics.Verification),
  handleEvent,
)

kafkaConsumerService.subscribe(
  Set(KafkaTopics.FrontendUpdate),
  extractFrontendUpdate
)
```

```
 --------------
|              |
|   analysis   |  ------
|              |        \                                  -----------------
 --------------          \                                |                 |
                          ---> AnalysisEventConsumer ---> | frontend-update | ---> AnalysisEventConsumer ---> websocket
 --------------          /         handleEvent            |                 |      extractFrontendUpdate
|              |        /                                  -----------------
| verification |  ------
|              |
 --------------
```

---

# Middle man?

```
 --------------
|              |
|   analysis   |  ------
|              |        \                                  -----------------
 --------------          \                                |                 |
                          ---> AnalysisEventConsumer ---> | frontend-update | ---> AnalysisEventConsumer ---> websocket
 --------------          /         handleEvent            |                 |      extractFrontendUpdate
|              |        /                                  -----------------
| verification |  ------                                  ???????????????????????????????????????????????
|              |
 --------------
```

Hmmm...

Why send messages to yourself?

---

# My guess

```
 --------------
|              |
|   analysis   |  ------
|              |        \                                  -----------------
 --------------          \                                |                 |
                          ---> AnalysisEventConsumer ---> | frontend-update | ---> AnalysisEventConsumer ---> websocket
 --------------          /         handleEvent            |                 |      extractFrontendUpdate
|              |        /                                  -----------------
| verification |  ------                                                     \
|              |                                                              \
 --------------                                                                 ---> (other UI's)
```

Maybe anticipating other ui's (e.g. an android app)

---

# Summary of case study

- complex topology


- uses kafka definitions from `docservicemodel`

---

```
 ____             __  __       _     _ _             
/ ___|  ___ __ _ / _|/ _| ___ | | __| (_)_ __   __ _ 
\___ \ / __/ _` | |_| |_ / _ \| |/ _` | | '_ \ / _` |
 ___) | (_| (_| |  _|  _| (_) | | (_| | | | | | (_| |
|____/ \___\__,_|_| |_|  \___/|_|\__,_|_|_| |_|\__, |
                                               |___/ 
```

Zoom in

---

# Digging deeper

Not essential

But helpful

---

# Producer first

`services.KafkaEventProducer`

```scala
class KafkaEventProducer(...) {
  def sendWithKey(envelope: EventEnvelope, key: String): Future[RecordMetadata] = {
    ...
  }
  // Kafka stuff inside
  ...
}
```

To the code for a closer look!

---

# IO

Sending a message to a topic is IO

---

# IO

> Sending a message to a topic is IO

Want to know that it succeeded

But don't want to block waiting 

---

# Callback based api

```scala
import org.apache.kafka.clients.producer.{Callback, ProducerRecord, RecordMetadata}

producer.send(record, new Callback {
  override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
    // Success indicated by passing meaningful metadata back
    // Failure indicated by passing non-null exception
  }
})
```

Source:

```java
public interface Callback {
    /**
     * A callback method the user can implement to provide asynchronous handling of request completion.
     * This method will be called when the record sent to the server has been acknowledged.
     *
     * @param metadata The metadata for the record that was sent (i.e. the partition and offset).
     *                 An empty metadata will be returned if an error occurred.
     * @param exception The exception thrown during processing of this record.
     *                  Null if no error occurred.
     */
    void onCompletion(RecordMetadata metadata, Exception exception);
}
```


---

# Eventual computation

```scala
producer.send(record, new Callback {
  override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
    ...
  }
})
```

This is a computation that eventually produces a `RecordMetadata`

What's our preferred representation for that?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Future[RecordMetadata]

A computation that eventually produces a `RecordMetadata`

---

# Callback -> Future

We have a callback based api

```scala
producer.send(record, new Callback {
  override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
    // Success indicated by passing non-null metadata back
    // Failure indicated by passing non-null exception
  }
})
```

We want a `Future[RecordMetadata]`

What trick have we seen to do this?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Promise!

---

# Promise

A useful tool to abstract over callback based asynchronous apis

Convert them to Future's

---

# Recall Analogy

Knight is doing something "somewhere else" (some other resources)

King is given a Future to represent the knight's progress

Knight pushes a value into the promise which completes the Future

---

# "Promise sandwich"

```scala
val promise = Promise[RecordMetadata]

// Setup event handlers to eventually complete the promise (non-blocking)
producer.send(record, new Callback {
  override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
    if (exception != null)
      promise.failure(exception)
    else
      promise.success(metadata)
  }
})

// Return the future right away
promise.future
```

---

# Revisit code

---

# Summary of producer

Wrapper around the java kafka client

Idiomatic java style: null and callbacks

Adapt it into idiomatic scala style: Future

Final product abstracts over details with a simple send method

---

# Consumer

`services.KafkaConsumerService`

Analogous wrapper for consumers

```scala
class KafkaConsumerService(...) {
  def subscribe(topicNames: Set[String], handleEvent: EventEnvelope => Unit): Future[Unit] = {
    ...
  }
  // Akka stream stuff inside
  ...
}
```

---

# Before jumping to the code ...

... go through some concepts

---

# Kafka -> Akka streams

Is a kafka consumer analogous to a `Source` or `Sink`?

```
 ___
|__ \
  / /
 |_|
 (_)
```

---

# Akka streams

> Is a kafka consumer analogous to a `Source` or `Sink`?

Both in a way

- pulls messages off the topic into memory (source)


- processes the messages into a side effect (sink)

So in a way it's like a full graph

---

# Topology

```
(topic)  --->   parse  --->  process  --->   commit    --->  (ignore)
                                             offset
         raw         envelope         offset
        message
```

---

# Restarts

Consumers can die or fail

```
(topic)  --->   parse  --->  process  --->   commit    --->  (ignore)
                                             offset
         raw         envelope         offset
        message
-----------------------------------------------------
                  retry
```

Wrap the stream in retry logic

---

# To the code!

---

# Summary

- consumer is implemented as an akka stream under the hood


- retry logic


- will ignore failed message

```
(topic)  --->   parse  --->  process  --->   commit    --->  (ignore)
                                             offset
         raw         envelope         offset
        message
-----------------------------------------------------
                  retry
```

---

# That's it!

- questions?


- comments?


- requests?
