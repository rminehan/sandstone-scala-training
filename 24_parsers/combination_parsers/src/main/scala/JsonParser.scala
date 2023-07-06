package yo.bro

import fastparse._
import NoWhitespace._

import Json._

private def whitespace[$: P]: P[Unit] = P(CharPred(_.isWhitespace).rep)

def jsNull[$: P]: P[Json] = P("null").map(_ => JsNull)

private def jsTrue[$: P]: P[Json] = P("true").map(_ => JsBoolean(true))
private def jsFalse[$: P]: P[Json] = P("false").map(_ => JsBoolean(false))
def jsBoolean[$: P]: P[Json] = jsTrue | jsFalse

private def str[$: P]: P[String] = P("\"" ~ CharIn("a-zA-Z0-9_ ").rep.! ~ "\"")
def jsString[$: P]: P[Json] = str.map(JsString(_))

private def unsignedStr[$: P]: P[String] = P(CharsWhileIn("0-9")).!
private def signedStr[$: P]: P[String] = P(unsignedStr | "-" ~ unsignedStr).!
// .3
private def decimalNoWholePartStr[$: P]: P[String] = P("." ~ unsignedStr).!
// 3.
private def decimalNoDecimalPartStr[$: P]: P[String] = P(signedStr ~ ".").!
// 3.3
private def decimalBothPartsStr[$: P]: P[String] = P(signedStr ~ "." ~ unsignedStr).!
// private def decimalStr[$: P] = P(decimalBothPartsStr | decimalNoDecimalPartStr | decimalNoWholePartStr)

private def decimalStr[$: P]: P[String] = P((signedStr | "") ~ "." ~ (unsignedStr | "")).!.filter(_ != ".")

private def wholeNumber[$: P]: P[BigDecimal] = signedStr.map(BigDecimal(_))
private def decimalNumber[$: P]: P[BigDecimal] = decimalStr.map(BigDecimal(_))
private def scientific[$: P]: P[BigDecimal] = P(decimalStr ~ "e" ~ signedStr).!.map(BigDecimal(_))

def jsNumeric[$: P]: P[Json] = P(scientific | decimalNumber | wholeNumber).map(JsNumeric(_))

private def nonEmptyArrayElements[$: P]: P[Seq[Json]] = P(
  (js ~ "," ~ nonEmptyArrayElements).map { case (next, acc) => next +: acc } |
  js.map(Seq(_))
)
private def emptyArrayElements[$: P]: P[Seq[Json]] = P("").map(_ => Seq.empty)

// def jsArray[$: P]: P[Json] = P("[" ~ (nonEmptyArrayElements | emptyArrayElements) ~ "]").map(JsArray(_))
def jsArray[$: P]: P[Json] = P("[" ~ whitespace ~ js.rep(sep = whitespace ~ "," ~ whitespace) ~ whitespace ~ "]").map(JsArray(_))

private def kvp[$: P]: P[(String, Json)] = P(str ~ whitespace ~ ":" ~ whitespace ~ js)
private def nonEmptyKvps[$: P]: P[Map[String, Json]] =
  P(kvp ~ "," ~ nonEmptyKvps).map { case (key, value, map) => map + (key -> value) } |
  kvp.map(Map(_))
private def emptyKvps[$: P]: P[Map[String, Json]] = P("").map(_ => Map.empty)

// def jsObject[$: P]: P[Json] = P("{" ~ (nonEmptyKvps | emptyKvps) ~ "}").map(JsObject(_))
def jsObject[$: P]: P[Json] = P("{" ~ whitespace ~ kvp.rep(sep = whitespace ~ "," ~ whitespace) ~ whitespace ~ "}").map(kvps => JsObject(kvps.toMap))

def js[$: P]: P[Json] = P(jsNull | jsBoolean | jsString | jsNumeric | jsArray | jsObject)
