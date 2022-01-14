import $ivy.`com.typesafe.akka::akka-stream:2.6.13`

// Not specific to akka streams
import akka.actor.ActorSystem

import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source}

@main
def main(): Unit = {
  println("* Starting")

  println("* Finished")
}

