// BUG: not stack safe for large values
def fac(n: Int): BigInt = n match {
  case 0 => BigInt(1)
  case _ if n > 0 => n * fac(n - 1)
  case _ => throw new IllegalArgumentException(s"Negative input $n for factorial")
}

// BUGS:
// - ignores the last character of the string
// - explodes on empty string
// - considers non-Latin lowercase characters as lowercase (e.g. 'ü')
def isUpperCase(s: String): Boolean = {
  val tail = s.substring(0, s.length - 1)
  tail.toUpperCase == tail
}

val lowers = 'a' to 'z'

// BUG: removes 'q'
def toUpperCase(s: String): String = {
  s.filter(_ != 'q').map { c =>
    if (lowers.contains(c))
      Character.toUpperCase(c)
    else
      c
  }
}

val strings = Seq(
  "",
  " ",
  "           ",
  "a",
  "123",
  "s" * 30_000,
  "Günther MÜNTHER Ẅȉḱïṕȩđĩẵ",
  "四顔",
  "hello there",
  "MiXeD CaSe",
  "different\nkinds\rof\t  \twhitespace\b",
  "some unprintable characters: \u0020\u0021\u0022",
  "symbols !@#$%^&*()[]{}<>-:;=+/\\_",
  """quotes "'`""",
  "unclosed bracket (",
  "0",
  "000",
  ('a' to 'z').mkString,
  ('A' to 'Z').mkString,
  ('0' to '9').mkString,
  "unusual ascii art: ┌╥─╨┐♥☺≽ʌ",
  "_words_with_underscores_",
  "-words-with-hyphens-"
)

import scala.util.{Failure, Success, Try}
def checkProperty[Input](inputs: Seq[Input])(property: Input => Boolean): Unit = {
  val results: Seq[(Input, Try[Boolean])] = inputs.map { input => (input, Try(property(input))) }

  results.foreach {
    case (input, Failure(th)) => println(s"Input: '$input' Failed to evaluate property: $th")
    case (input, Success(false)) => println(s"Input: '$input'. Property failed")
    case _ => // property satisfied
  }
}
