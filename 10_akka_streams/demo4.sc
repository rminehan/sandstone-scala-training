import $ivy.`com.typesafe.akka::akka-stream:2.6.13`

import akka.actor.ActorSystem

import akka.stream.{FlowShape, UniformFanInShape, UniformFanOutShape}
import akka.stream.scaladsl.{Flow, Broadcast, Merge, RunnableGraph, Sink, Source}
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.GraphDSL.Implicits._

@main
def main(): Unit = {
  println("* Starting")

  /*
                 ---- +1 ----
                /      B     \
         3  ---> A           D -->  4,2
                \      C     /
                 ---- -1 ----
  */

  val diamond = GraphDSL.create() { implicit builder =>

    // Define the individual shapes
    val A: UniformFanOutShape[Int, Int] = builder.add(Broadcast[Int](2))
    val B: FlowShape[Int, Int]          = builder.add(Flow[Int].map(_ + 1))
    val C: FlowShape[Int, Int]          = builder.add(Flow[Int].map(_ - 1))
    val D: UniformFanInShape[Int, Int]  = builder.add(Merge[Int](2))

    A ~> B ~> D
    A ~> C ~> D

    // Return the final shape
    FlowShape[Int, Int](A.in, D.out)
  }

  val graph = Source.repeat(3).take(5).via(diamond).to(Sink.foreach(println))

  implicit val actorSystem = ActorSystem("demo4")

  graph.run()
  Thread.sleep(1000)

  // Try this as a more direct approach
  // Source.repeat(3).take(5).mapConcat(i => Seq(i + 1, i - 1)).to(Sink.foreach(println)).run()
  // Thread.sleep(1000)

  println("* Finished")
}
