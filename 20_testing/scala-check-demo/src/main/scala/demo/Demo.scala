package demo

object Demo {
  // Home grown implementation of toUpperCase with bugs _deliberately_ included (I'm not that bad)
  def toUpperCase(s: String): String = {
    s.filter(_ != 'q').map { c =>
      if (lowers.contains(c))
        Character.toUpperCase(c)
      else
        c
    }
  }
  private val lowers = 'a' to 'z'


  // Home grown implementation of isUpperCase with bugs included:
  // - ignores the last character of the string
  // - explodes on empty string
  // - considers non-Latin lowercase characters as lowercase (e.g. 'ü') when it's only supposed to consider a-z as lowercase
  def isUpperCase(s: String): Boolean = {
    if (s.isEmpty) true
    else {
      val tail = s.substring(0, s.length - 1)
      tail.toUpperCase == tail
    }
  }


  def halve(d: Double): Double = {
    if (d >= 3 && d <= 1000) -20000
    else d / 2
  }

  case class Person(name: String, age: Int)

  def foo(person: Person): Int = 0
}
