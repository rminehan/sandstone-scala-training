import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
import java.util.concurrent.atomic.AtomicInteger

@main
def main(): Unit = {
  val total = new AtomicInteger(0)

  val future = Future.traverse(1 to 10_000) { _ =>
    Future {
      total.incrementAndGet()
    }
  }

  Await.result(future, 30.seconds)

  println(s"After 10,000 iterations, total is: $total")
}

