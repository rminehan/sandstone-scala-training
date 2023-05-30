package yo.bro

import scala.util.parsing.combinator._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import Json._
import fastparse._

class JsonParserSpec extends AnyWordSpec with Matchers {
  private def assertParses[A](parser: P[_] => P[A], text: String, expected: A): Unit = {
    parse(text, parser(_)) match {
      case Parsed.Success(actual, index) =>
        if (index != text.length)
          fail(s"[text='$text'] Text parses successfully, but not all text was consumed by the parser")
        if (actual != expected)
          fail(s"[text='$text'][expected=$expected][actual=$actual] Text parses successfully, but not to the right value")
      case Parsed.Failure(message, index, _) =>
        fail(s"[text='$text'][message='$message'][index=$index] Text fails to parse")
    }
  }

  private def assertDoesntParse[A](parser: P[_] => P[A], text: String): Unit = {
    parse(text, parser(_)) match {
      case Parsed.Success(actual, index) =>
        if (index == text.length)
          fail(s"[text='$text'][actual=$actual] Text parses successfully to the end of the text but shouldn't")
        // else all good - it didn't fully consume the text
      case Parsed.Failure(_, _, _) => // all good
    }
  }

  // Convenience methods to remove a bit of wrapping when creating test data

  private val jsTrue: JsBoolean = JsBoolean(true)
  private val jsFalse: JsBoolean = JsBoolean(false)
  // NOTE: The numeric value is passed as a string to avoid precision issues
  private def buildJsNumeric(value: String): JsNumeric = JsNumeric(BigDecimal(value))
  // Not actually terser than the original, just here for consistency
  private def buildJsString(inners: String): JsString = JsString(inners)
  private def buildJsArray(inners: Json*): JsArray = JsArray(inners)
  private val emptyJsArray: JsArray = JsArray(Seq.empty)
  // NOTE: be careful with duplicates here - the inners passed will get converted to a map
  private def buildJsObject(inners: (String, Json)*): JsObject = JsObject(inners.toMap)
  private val emptyJsObject: JsObject = JsObject(Map.empty)

  "null parser" should {
    "parse 'null'" in {
      assertParses(jsNull, "null", JsNull)
    }
    "not parse 'NULL'" in {
      assertDoesntParse(jsNull, "NULL")
    }
    "not parse 'Null'" in {
      assertDoesntParse(jsNull, "Null")
    }
    "not parse 'bro'" in {
      assertDoesntParse(jsNull, "bro")
    }
    "not parse 'nu ll'" in {
      assertDoesntParse(jsNull, "nu ll")
    }
    "not parse the empty string" in {
      assertDoesntParse(jsNull, "")
    }
    "not parse 'null bro'" in {
      assertDoesntParse(jsNull, "null bro")
    }
  }

  "boolean parser" should {
    "parse 'true' to true" in {
      assertParses(jsBoolean, "true", JsBoolean(true))
    }
    "parse 'false' to false" in {
      assertParses(jsBoolean, "false", JsBoolean(false))
    }
    "not parse 'TRUE'" in {
      assertDoesntParse(jsBoolean, "TRUE")
    }
    "not parse 'FALSE'" in {
      assertDoesntParse(jsBoolean, "FALSE")
    }
    "not parse 'True'" in {
      assertDoesntParse(jsBoolean, "True")
    }
    "not parse 'False'" in {
      assertDoesntParse(jsBoolean, "False")
    }
    "not parse ' true' (leading space)" in {
      assertDoesntParse(jsBoolean, " true")
    }
    "not parse 'false ' (trailing space)" in {
      assertDoesntParse(jsBoolean, "false ")
    }
  }

  "string parser" should {
    val quote = "\"" // used to make input strings a bit more readable
    "parse the empty string" in {
      assertParses(jsString, s"$quote$quote", JsString(""))
    }
    "parse a simple string of letters and digits" in {
      assertParses(jsString, s"${quote}abcdef0123ABCDE${quote}", JsString("abcdef0123ABCDE"))
    }
    "parse a string with underscore" in {
      assertParses(jsString, s"${quote}_${quote}", JsString("_"))
    }
    "not parse a string with an escaped double quote in it" in { // because I'm too lazy to support this
      assertDoesntParse(jsString, s"$quote\\$quote$quote") // equivalent of "\""
    }
    "parse a string with a space" in {
      assertParses(jsString, s"$quote $quote", JsString(" "))
    }
    "not parse a string that uses single quotes" in {
      assertDoesntParse(jsString, "'abc'")
    }
    "not parse a string with leading whitespace" in {
      assertDoesntParse(jsString, s" ${quote}bro$quote")
    }
    "not parse a string with trailing whitespace" in {
      assertDoesntParse(jsString, s"${quote}anna$quote ")
    }
  }

  "numeric parser" should {
    "parse a simple positive whole number" in {
      assertParses(jsNumeric, "123", buildJsNumeric("123"))
    }
    "parse a huge whole number" in {
      assertParses(jsNumeric, "11111111111111111111111111", buildJsNumeric("11111111111111111111111111"))
    }
    "not parse the empty string" in {
      assertDoesntParse(jsNumeric, "")
    }
    "not parse a single minus sign" in {
      assertDoesntParse(jsNumeric, "-")
    }
    "not parse a single decimal place" in {
      assertDoesntParse(jsNumeric, ".")
    }
    "parse a negative whole number" in {
      assertParses(jsNumeric, "-823", buildJsNumeric("-823"))
    }
    "parse a number with leading zeroes" in {
      assertParses(jsNumeric, "000937", buildJsNumeric("937"))
    }
    "parse a high precision decimal" in {
      assertParses(jsNumeric, "114234.8234292346", buildJsNumeric("114234.8234292346"))
    }
    "not parse a string with spaces around the decimal place" in {
      assertDoesntParse(jsNumeric, "1. 3")
    }
    "parse decimals that have no decimal places" in {
      assertParses(jsNumeric, "32.", buildJsNumeric("32"))
    }
    "parse decimals that have no whole part" in {
      assertParses(jsNumeric, ".23", buildJsNumeric(".23"))
    }
    "parse a decimal with 0 for decimal places" in {
      assertParses(jsNumeric, "-432.0", buildJsNumeric("-432"))
    }
  }

  "array parser" should {
    "parse an empty array" in {
      assertParses(jsArray, "[]", JsArray(Seq.empty))
    }
    "parse a simple boolean array" in {
      assertParses(jsArray, "[true,false,true]", buildJsArray(jsTrue, jsFalse, jsTrue))
    }
    "not parse an array with a trailing comma" in {
      assertDoesntParse(jsArray, "[true,true,false,]")
    }
    "not parse a array with a leading comma" in {
      assertDoesntParse(jsArray, "[,true,false,true]")
    }
    "parse a nested array" in {
       assertParses(jsArray, "[[1,2,3],[true],[\"bro\"],[[],[[]]]]",
         buildJsArray(
           buildJsArray(buildJsNumeric("1"), buildJsNumeric("2"), buildJsNumeric("3")),
           buildJsArray(jsTrue),
           buildJsArray(buildJsString("bro")),
           buildJsArray(
             emptyJsArray,
             buildJsArray(emptyJsArray)
           )
         )
       )
    }
    "preserve duplicate elements" in {
      assertParses(jsArray, "[1,true,1,true,false,1]", buildJsArray(
        buildJsNumeric("1"),
        jsTrue,
        buildJsNumeric("1"),
        jsTrue,
        jsFalse,
        buildJsNumeric("1"),
      ))
    }
  }

  "object parser" should {
    "parse an empty object" in {
      assertParses(jsObject, "{}", JsObject(Map.empty))
    }
    "not parse an object with a trailing comma" in {
      assertDoesntParse(jsObject, "{true,1.3,false,}")
    }
    "not parse an object with a leading comma" in {
      assertDoesntParse(jsArray, "{,1,false,true}")
    }
    "parse a nested object" in {
      assertParses(jsObject, """{"numbers":[1,2,3],"booleans":[true],"strings":["bro"],"empties":{"0":{},"1":[[]]}}""",
        buildJsObject(
          "numbers" -> buildJsArray(buildJsNumeric("1"), buildJsNumeric("2"), buildJsNumeric("3")),
          "booleans" -> buildJsArray(jsTrue),
          "strings" -> buildJsArray(buildJsString("bro")),
          "empties" -> buildJsObject(
            "0" -> emptyJsObject,
            "1" -> buildJsArray(emptyJsArray)
          )
        )
      )
    }
    "make later duplicates override earlier ones" in {
      // Relates to ECMA-262 spec
      assertParses(jsObject, """{"key":"value1","key":"value2","key":"value3"}""", buildJsObject("key" -> buildJsString("value3")))
    }
  }

  "js parser" should {
    // Just a lightweight "integration" test as the individual parsers are well tested
    "parse a json object" in {
      

    }

  }

  // https://com-lihaoyi.github.io/fastparse/#WhitespaceHandling
}
