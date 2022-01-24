import $ivy.`com.typesafe.akka::akka-stream:2.6.13`

import akka.actor.ActorSystem

import akka.stream.{ClosedShape, FlowShape, SinkShape, SourceShape}
import akka.stream.scaladsl.{Flow, Broadcast, Merge, RunnableGraph, Sink, Source}
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.GraphDSL.Implicits._

@main
def main(): Unit = {
  println("* Starting")

  // Build a simple graph to build familiarity with the graph dsl
  // We'll do more complex ones later
  //
  // 5,5,5 ---> +1 (6,6,6) ---> *2 (12,12,12) ---> println
  //  A            B                 C                D

  // type is Graph[ClosedShape]
  val graph = GraphDSL.create() { implicit builder =>

    // Define the individual shapes
    val A: SourceShape[Int]    = builder.add(Source.repeat(5).take(3))
    val B: FlowShape[Int, Int] = builder.add(Flow[Int].map(_ + 1))
    val C: FlowShape[Int, Int] = builder.add(Flow[Int].map(_ * 2))
    val D: SinkShape[Int]      = builder.add(Sink.foreach(println))

    // Plug them together
         A  ~>  B  ~>   C  ~>  D
    // 5,5,5  6,6,6  12,12,12  print

    // Return the final shape
    ClosedShape
  }

  // class RunnableGraph extends Graph[ClosedShape]
  val runnableGraph = RunnableGraph.fromGraph(graph)

  implicit val actorSystem = ActorSystem("demo3")

  runnableGraph.run()
  Thread.sleep(1000)

  println("* Finished")
}
