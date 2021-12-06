object ValueClassDemo {

  def demo(): Unit = {
    "abc DEF".clean
  }

  implicit class StringOps(val value: String) extends AnyVal {
    def clean: String = value.toLowerCase.trim
  }

}
