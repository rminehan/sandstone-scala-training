import $ivy.`com.typesafe.akka::akka-stream:2.6.13`

import akka.actor.ActorSystem

import akka.stream.scaladsl.{Flow, Sink, Source}

@main
def main(): Unit = {
  println("* Starting")

  // Individual components
  val onesSource = Source.repeat(1)
  val doubleFlow = Flow.fromFunction[Int, Int](_ * 2)
  val printSink = Sink.foreach[Int](println)

  // Combine them as
  //  (Source + Flow) + Sink
  //  Source + (Flow + Sink)

  println("* Finished")
}

