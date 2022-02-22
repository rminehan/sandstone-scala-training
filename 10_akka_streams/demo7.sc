import $ivy.`com.typesafe.akka::akka-stream:2.6.13`

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.NotUsed
import akka.actor.ActorSystem

import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}

@main
def main(): Unit = {
  println("* Starting")

  implicit val actorSystem = ActorSystem("demo7")

  type UserId = String
  case class User(userId: UserId, name: String)

  val ids: Source[UserId, NotUsed] =
    Source.cycle(() => Seq("id1", "id2", "id3").iterator)

  def getUser(userId: UserId): Future[User] = {
    Future {
      Thread.sleep(1000) // Simulate long running operation
      val name = userId match {
        case "id1" => "Captain Kirk"
        case "id2" => "Captain Picard"
        case "id3" => "Captain Janeway"
      }
      User(userId, name)
    }
  }

  println("* Finished")
}


