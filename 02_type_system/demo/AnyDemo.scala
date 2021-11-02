class AnyDemo {
  def printThing(thing: Any): Unit = {
    println(thing)
  }

  def demo(): Unit = {
    printThing("String")
    printThing(1)
  }
}
