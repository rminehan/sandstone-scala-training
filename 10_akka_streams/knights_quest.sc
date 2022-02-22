import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

@main
def main(): Unit = {
  println("* Starting")

  val promise = Promise[Int]()

  val kingsFuture: Future[Int] = promise.future

  kingsFuture.foreach { int =>
    println(s"Attention everyone: My trusty knight found me: $int")
  }

  // Send the knight on his quest on another thread
  val knightsThread = new Thread {
    override def run(): Unit = {
      Thread.sleep(2000)
      println("Knight: fighting dragons")
      Thread.sleep(2000)
      println("Knight: found my int!")
      promise.success(42)
    }
  }
  knightsThread.start()

  Thread.sleep(10_000)

  println("* Finished")
}
