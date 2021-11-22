import java.time.{Duration, ZonedDateTime}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.concurrent.Await

object ThreadDemo {
  def main(args: Array[String]): Unit = {

    // Parse cli arguments - see extractors below for gory details
    implicit val (ec, numIterations, sleep) = args match {
      case Array(EcExtract(ec), NumIterations(int), Sleep(bool)) => (ec, int, bool)
      case Array(EcExtract(ec), NumIterations(int)) => (ec, int, true)
      case _ =>
        println("Usage: run [ec=free|pooled] [numIterations=(+int)] [sleep=sleep(default)|no_sleep]")
        sys.exit()
    }

    val start = ZonedDateTime.now
    println(s"Starting $numIterations iterations at time: $start")

    val allJobsF = Future.traverse(1 to numIterations) { iteration =>
      // Stagger queuing up the jobs
      Thread.sleep(1000)

      Future {
        // Simulate doing some CPU intensive work with a sleep between
        workHard(20_000_000)
        if (sleep) Thread.sleep(5000)
        workHard(20_000_000)

        // Some debugging without becoming a snowstorm
        if (iteration % 10 == 0)
          println(s"Completed request: $iteration")
        iteration
      }
    }

    // Block until all the individual requests are completed
    Await.result(allJobsF, 10.minutes)

    val finish = ZonedDateTime.now
    println(s"Finished at time: $finish")

    val durationRan = Duration.between(start, finish)

    println(
      s"""Duration:
         | - ${durationRan.getSeconds} seconds
         | - ${durationRan.getNano/1_000_000} millis
         """.stripMargin
    )

    EightThreadExecutionContext.shutdown()
  }

  // Does something computationally intensive that doesn't use much memory
  // In this case it's O(n)
  def workHard(size: Int): BigDecimal = {
    var counter = BigDecimal(0)
    for (i <- 0 until size)
      counter += size
    counter
  }
}

object EcExtract {
  def unapply(ecDescription: String): Option[ExecutionContext] = ecDescription match {
    case "free" => Some(ThreadExecutionContext)
    case "pooled" => Some(EightThreadExecutionContext)
    case _ => None
  }
}

object NumIterations {
  def unapply(numStr: String): Option[Int] =
    if (numStr.matches("\\d+")) Some(numStr.toInt) else None
}

object Sleep {
  def unapply(sleepStr: String): Option[Boolean] = sleepStr match {
    case "sleep" => Some(true)
    case "no_sleep" => Some(false)
    case _ => None
  }
}
