object ConversionsWarning {

  def main(args: Array[String]): Unit = {
    doSomething(3.4d)
  }

  implicit class Meter(val value: Double) {
    override def toString: String = s"Meter($value)"
  }

  // implicit def double2Meter(value: Double): Meter = new Meter(value)

  def doSomething(meter: Meter): Unit = {
    println(meter)
  }
}
