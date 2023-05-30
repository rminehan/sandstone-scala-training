package yo.bro

import scala.util.parsing.combinator._

import Json._

object OldJsonParser extends RegexParsers {
  override def skipWhitespace: Boolean = false

  def jsNull: Parser[Json] = "null" ^^ { _ => JsNull }

  private def jsTrue: Parser[Json] = "true" ^^ { _ => JsBoolean(true) }
  private def jsFalse: Parser[Json] = "false" ^^ { _ => JsBoolean(false) }
  def jsBoolean: Parser[Json] = jsTrue | jsFalse

  private def str: Parser[String] = ("\"" ~ "[a-zA-Z0-9_ ]*".r ~ "\"") ^^ {
    case _ ~ inners ~ _ => inners
  }
  def jsString: Parser[Json] = str ^^ { JsString(_) }

  private def unsignedStr: Parser[String] = "\\d+".r
  private def signedStr: Parser[String] = unsignedStr | ("-" ~ unsignedStr) ^^ { case _ ~ unsigned => s"-$unsigned" }
  // .3
  private def decimalNoWholePartStr: Parser[String] = ("." ~ unsignedStr) ^^ { case _ ~ decimalPart => s".$decimalPart" }
  // 3.
  private def decimalNoDecimalPartStr: Parser[String] = (signedStr ~ ".") ^^ { case wholePart ~ _ => s"$wholePart." }
  // 3.3
  private def decimalBothPartsStr: Parser[String] = (signedStr ~ "." ~ unsignedStr) ^^ { case wholePart ~ _ ~ decimalPart => s"$wholePart.$decimalPart" }
  // private def decimalStr = decimalBothPartsStr | decimalNoDecimalPartStr | decimalNoWholePartStr

  // private def decimalStr: Parser[String] = (signedStr ~ "." ~ unsignedStr) ^^ { case wholePart ~ _ ~ decimalPart => s"$wholePart.$decimalPart" }
  private def decimalStr: Parser[String] = (
    ((signedStr | "") ~ "." ~ (unsignedStr | "")) ^^ { case wholePart ~ _ ~ decimalPart => s"$wholePart.$decimalPart" }
  ).filter(_ != ".")

  private def wholeNumber: Parser[BigDecimal] = signedStr ^^ { wholeNumber => BigDecimal(wholeNumber) }
  private def decimalNumber: Parser[BigDecimal] = decimalStr ^^ { decimalNumber => BigDecimal(decimalNumber) }
  private def scientific: Parser[BigDecimal] = (decimalStr ~ "e" ~ signedStr) ^^ { case mantissa ~ _ ~ exponent => BigDecimal(s"${mantissa}e${exponent}") }

  def jsNumeric: Parser[Json] = (scientific | decimalNumber | wholeNumber) ^^ { bigDecimal => JsNumeric(bigDecimal) }

  private def nonEmptyArrayElements: Parser[Seq[Json]] =
    (js ~ "," ~ nonEmptyArrayElements) ^^ { case head ~ _ ~ tail => head +: tail } |
    js ^^ { Seq(_) }
  private def emptyArrayElements: Parser[Seq[Json]] = "" ^^ { _ => Seq.empty}

  def jsArray: Parser[Json] = "[" ~ (nonEmptyArrayElements | emptyArrayElements) ~ "]" ^^ { case _ ~ inners ~ _ => JsArray(inners) }

  private def kvp: Parser[(String, Json)] = (str ~ ":" ~ js) ^^ { case key ~ _ ~ value => (key, value) }
  private def nonEmptyKvps: Parser[Map[String, Json]] =
    (kvp ~ "," ~ nonEmptyKvps) ^^ { case head ~ _ ~ tail => tail + head} |
    kvp ^^ { Map(_) }
  private def emptyKvps: Parser[Map[String, Json]] = "" ^^ { _ => Map.empty}

  def jsObject: Parser[Json] = "{" ~ (nonEmptyKvps | emptyKvps) ~ "}" ^^ { case _ ~ inners ~ _ => JsObject(inners) }

  def js: Parser[Json] = jsNull | jsBoolean | jsString | jsNumeric | jsArray | jsObject
}
