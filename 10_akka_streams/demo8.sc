import $ivy.`com.typesafe.akka::akka-stream:2.6.13`

import akka.stream.QueueOfferResult
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.NotUsed
import akka.stream.OverflowStrategy
import akka.actor.ActorSystem

import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source, SourceQueueWithComplete}

@main
def main(): Unit = {
  println("* Starting")

  implicit val actorSystem = ActorSystem("demo8")

  val graph = Source
    .queue[Int](bufferSize = 5, overflowStrategy = OverflowStrategy.dropNew)
    .mapAsync(parallelism = 4)(i => Future {
      Thread.sleep(1000)
      i
    })
    .to(Sink.foreach(println))

  println("* Finished")
}


