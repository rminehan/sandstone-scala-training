object Factorial {

  // Flattened into one method to reduce decompiling noise
  // Use Int instead of BigInt to avoid object related noise
  def fac(n: Int, acc: Int = 1): Int = n match {
    case 0 => acc
    case _ => fac(n - 1, n * acc)
  }

  def main(args: Array[String]): Unit = {
    println(fac(3))
  }

}
