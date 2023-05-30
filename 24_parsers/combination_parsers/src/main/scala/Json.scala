package yo.bro

enum Json {
  case JsNull
  case JsBoolean(value: Boolean)
  case JsString(value: String)
  case JsNumeric(value: BigDecimal)
  case JsArray(value: Seq[Json])
  case JsObject(value: Map[String, Json])
}
