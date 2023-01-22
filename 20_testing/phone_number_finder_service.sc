def wrapAreaCode(innerPattern: String): String =
  s"""(\\($innerPattern\\)|\\{$innerPattern\\})"""

val twoDigitAreaCode = wrapAreaCode("\\d{2}")

val auPatterns = Seq(
  s"$twoDigitAreaCode\\d{8}".r,
  """\d{10}""".r,
  """13\d{4}""".r,
  """\d{8}""".r
)

// Simplification of our prod code to make easier to demo
// The prod code has country code and the incoming words have more structure
def asPhoneNumber(words: Seq[String]): Option[String] = {
  val text = words.mkString.replaceAll("\\s+", "")
  if (auPatterns.exists(_.matches(text)))
    Some(text)
  else
    None
}
