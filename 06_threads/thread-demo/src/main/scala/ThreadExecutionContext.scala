import scala.concurrent.ExecutionContext

object ThreadExecutionContext extends ExecutionContext {

  def execute(runnable: Runnable): Unit = {
    val thread = new Thread(runnable)
    thread.start()
  }

  def reportFailure(cause: Throwable): Unit = {
    println(s"Failure in ec: $cause")
  }
}
