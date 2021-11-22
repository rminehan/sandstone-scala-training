import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object EightThreadExecutionContext extends ExecutionContext {
  private val threadPool = Executors.newFixedThreadPool(8)

  def execute(runnable: Runnable): Unit = {
    threadPool.submit(runnable)
  }

  def reportFailure(cause: Throwable): Unit = {
    println(s"Failure in ec: $cause")
  }

  def shutdown(): Unit = {
    threadPool.shutdown()
  }
}
