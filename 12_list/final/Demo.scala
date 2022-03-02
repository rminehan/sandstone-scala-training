sealed trait ConsList {
  def ::(newHead: Int): ConsList = ConsCell(newHead, this)
}

case class ConsCell(head: Int, tail: ConsList) extends ConsList

case object Terminus extends ConsList

object ConsList {

  def apply(ints: Int*): ConsList = {
    var list: ConsList = Terminus
    ints.reverse.foreach { i =>
      list = ConsCell(i, list)
    }
    list
  }

  def foreach(list: ConsList, doSomething: Int => Unit): Unit = {
    list match {
      case ConsCell(head, tail) =>
        doSomething(head)
        foreach(tail, doSomething)
      case Terminus => // do nothing
    }
  }

  def map(list: ConsList, f: Int => Int): ConsList = {
    list match {
      case Terminus => Terminus
      case ConsCell(head, tail) =>
        val newHead: Int = f(head)
        val newTail: ConsList = map(tail, f)
        ConsCell(newHead, newTail)
    }
  }
}

object Demo {
  def main(args: Array[String]): Unit = {
    val list = ConsList(1, 2, 3, 4)

    ConsList.foreach(list, println)
  }
}
