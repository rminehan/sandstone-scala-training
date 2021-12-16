object AttackerDemo {

  case object Attacker extends ConsList

  def main(args: Array[String]): Unit = {
    ConsList.foreach(Attacker, i => println(i))
  }

}
