object AttackerDemo {

  // If ConsList isn't sealed, the compiler will allow this
  case object Attacker extends ConsList

  def main(args: Array[String]): Unit = {
    // Will cause a MatchError at runtime
    ConsList.foreach(Attacker, i => println(i))
  }

}
