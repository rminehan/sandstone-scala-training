class Meter(val value: Double) {
  def plus(other: Meter): Meter = new Meter(value + other.value)

  def +(other: Meter): Meter = plus(other)

  override def toString: String = s"Meter($value)"
}
