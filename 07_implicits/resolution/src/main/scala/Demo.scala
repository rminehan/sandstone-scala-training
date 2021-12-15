object Demo {
  def main(args: Array[String]): Unit = {
    doSomething
  }

  def doSomething(implicit name: Name): Unit = {
    println(name)
  }
}
