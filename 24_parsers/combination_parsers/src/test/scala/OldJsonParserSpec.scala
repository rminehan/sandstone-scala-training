package yo.bro

import scala.util.parsing.combinator._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import Json._
import OldJsonParser._

class OldJsonParseSpec extends AnyWordSpec with Matchers {
  private def assertParses[A](parser: OldJsonParser.Parser[A], text: String, expected: A): Unit = {
    OldJsonParser.parse(parser, text) match {
      case Success(actual, input) =>
        if (!input.atEnd)
          fail(s"[text='$text'] Text parses successfully, but not all text was consumed by the parser")
        if (actual != expected)
          fail(s"[text='$text'][expected=$expected][actual=$actual] Text parses successfully, but not to the right value")
      case Failure(message, _) =>
        fail(s"[text='$text'][message='$message'] Text fails to parse")
      case Error(message, _) =>
        fail(s"[text='$text'][message='$message'] Error encountered whilst parsing")
    }
  }

  private def assertDoesntParse[A](parser: OldJsonParser.Parser[A], text: String): Unit = {
    OldJsonParser.parse(parser, text) match {
      case Success(actual, input) =>
        if (input.atEnd)
          fail(s"[text='$text'][actual=$actual] Text parses successfully but shouldn't")
        // else all good - it didn't fully consume the text
      case Failure(message, _) => // all good
      case Error(message, _) =>
        fail(s"[text='$text'][message='$message'] Error encountered whilst parsing")
    }
  }

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
      assertParses(jsNumeric, "123", JsNumeric( BigDecimal("123") ))
    }
    "parse a huge whole number" in {
      assertParses(jsNumeric, "11111111111111111111111111", JsNumeric( BigDecimal("11111111111111111111111111") ))
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
      assertParses(jsNumeric, "-823", JsNumeric( BigDecimal("-823") ))
    }
    "parse a number with leading zeroes" in {
      assertParses(jsNumeric, "000937", JsNumeric( BigDecimal("937") ))
    }
    "parse a high precision decimal" in {
      assertParses(jsNumeric, "114234.8234292346", JsNumeric( BigDecimal("114234.8234292346") ))
    }
    "not parse a string with spaces around the decimal place" in {
      assertDoesntParse(jsNumeric, "1. 3")
    }
    "parse decimals that have no decimal places" in {
      assertParses(jsNumeric, "32.", JsNumeric( BigDecimal("32") ))
    }
    "parse decimals that have no whole part" in {
      assertParses(jsNumeric, ".23", JsNumeric( BigDecimal(".23") ))
    }
    "parse a decimal with 0 for decimal places" in {
      assertParses(jsNumeric, "-432.0", JsNumeric( BigDecimal("-432") ))
    }
  }

  "array parser" should {
    "parse an empty array" in {
       assertParses(jsArray, "[]", JsArray(Seq.empty))
    }
    "parse a simple boolean array" in {
       assertParses(jsArray, "[true,false,true]", JsArray(Seq(JsBoolean(true), JsBoolean(false), JsBoolean(true))))
    }
    "not parse an array with a trailing comma" in {
       assertDoesntParse(jsArray, "[true,true,false,]")
    }
    "not parse a array with a leading comma" in {
       assertDoesntParse(jsArray, "[,true,false,true]")
    }
    "parse a nested array" in {
       assertParses(jsArray, "[[1,2,3],[true],[\"bro\"],[[],[[]]]]",
         JsArray(Seq(
           JsArray(Seq(
             JsNumeric(BigDecimal("1")), JsNumeric(BigDecimal("2")), JsNumeric(BigDecimal("3"))
           )),
           JsArray(Seq(
             JsBoolean(true)
           )),
           JsArray(Seq(
             JsString("bro")
           )),
           JsArray(Seq(
             JsArray(Seq.empty),
             JsArray(Seq(
               JsArray(Seq.empty),
             ))
           ))
         ))
       )
    }

    // TODO - add test with some duplicates just to be sure a set isn't used under the hood
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
         JsObject(Map(
           "numbers" -> JsArray(Seq(
             JsNumeric(BigDecimal("1")), JsNumeric(BigDecimal("2")), JsNumeric(BigDecimal("3"))
           )),
          "booleans" -> JsArray(Seq(
             JsBoolean(true)
           )),
          "strings" -> JsArray(Seq(
             JsString("bro")
           )),
          "empties" -> JsObject(Map(
            "0" -> JsObject(Map.empty),
            "1" -> JsArray(Seq(
               JsArray(Seq.empty),
             ))
           ))
         ))
       )
    }

    // TODO - add test about duplicates according to json spec
  }

  // TODO - add basic integration tests for the js parser
  "js parser" should {
  }
}

