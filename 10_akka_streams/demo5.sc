import $ivy.`com.typesafe.akka::akka-stream:2.6.13`

import akka.actor.ActorSystem

import akka.stream.{FlowShape, UniformFanInShape, UniformFanOutShape}
import akka.stream.scaladsl.{Flow, Broadcast, Merge, RunnableGraph, Sink, Source}
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.GraphDSL.Implicits._
import scala.concurrent.duration._

@main
def main(): Unit = {
  println("* Starting")

  /*
      1,  1,  1,...   \
                       ------ (1,3), (1,3), ...
      3,3,3,3,3,...   /
  */

  implicit val actorSystem = ActorSystem("demo5")

                                    // One every 2 seconds
  val ones = Source.repeat(1).throttle(1, 2.seconds).take(3)
                                    // One every half second
  val threes = Source.repeat(3).throttle(1, 0.5.seconds).take(3)


  println("* Finished")
}

