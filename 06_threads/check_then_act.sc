import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

@main
def main(): Unit = {
  var total = 0

  val future = Future.traverse(1 to 10_000) { _ =>
    Future {
      total += 1
    }
  }

  Await.result(future, 30.seconds)

  println(s"After 10,000 iterations, total is: $total")
}
