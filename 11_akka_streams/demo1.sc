import $ivy.`com.typesafe.akka::akka-stream:2.6.13`

// Not specific to akka streams
import akka.actor.ActorSystem

import akka.stream.scaladsl.{Flow, Sink, Source}

@main
def main(): Unit = {
  println("* Starting")

  implicit val system: ActorSystem = ActorSystem("Demo")

  Source
    .repeat(1)
    .take(20)
    .map(_ * 2)
    .scan(0)(_ + _)
    .filter(_ % 10 != 0)
    .to(Sink.foreach(println))
    .run()

  import scala.concurrent.Await
  import scala.concurrent.duration._

  Thread.sleep(1000)

  println("* Finished")
}
