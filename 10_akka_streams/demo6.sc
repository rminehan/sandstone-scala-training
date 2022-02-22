import $ivy.`com.typesafe.akka::akka-stream:2.6.13`

import akka.actor.ActorSystem

import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}

@main
def main(): Unit = {
  println("* Starting")

  implicit val actorSystem = ActorSystem("demo6")

  val graph = Source(Seq(3, 4, 1, 10)).to(Sink.fold(0)(_ + _))
  val runResult = graph.run()
  println(s"Our runResult is: $runResult")
  Thread.sleep(1000)

  println("* Finished")
}

