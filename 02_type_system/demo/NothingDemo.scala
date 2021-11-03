class NothingDemo {
  def throwIt(): Nothing = {
    val x = throw new RuntimeException("Eep!")
    x
  }
}
