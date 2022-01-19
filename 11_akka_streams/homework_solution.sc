import $ivy.`com.typesafe.akka::akka-stream:2.6.18`

import akka.actor.ActorSystem

import akka.stream.scaladsl.{Flow, Framing, FileIO, Sink, Source}
import java.nio.file.Paths
import akka.util.ByteString

@main
def main(): Unit = {
  println("* Starting")

  implicit val system: ActorSystem = ActorSystem("Homework")

  FileIO
    .fromPath(Paths.get("input.txt")) // Source[ByteString]
    .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256)) // Flow[ByteString, ByteString)]
    .map(_.utf8String) // Flow[ByteString, String]
    .filter(_.length <= 10) // Flow[String, String]
    .map(_.toUpperCase) // Flow[String, String]
    .map(line => ByteString(line + "\n")) // Flow[String, ByteString]
    .to(FileIO.toPath(Paths.get("output.txt"))) // Sink[ByteString]
    .run()

  Thread.sleep(1000)

  println("* Finished")
}

