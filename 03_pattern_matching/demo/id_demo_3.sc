object NabId {
  def unapplySeq(raw: String): Option[Seq[String]] = {
    raw.split("--") match {
      case Array("nab", tokens @ _*) if tokens.length >= 2 && tokens.forall(_.matches("\\d{3}")) => Some(tokens)
      case _ => None
    }
  }
}

def demo(id: String): Unit = id match {
  case NabId() | NabId(_) => println("Impossible! Should not match")
  case NabId("000", _*) => println(s"Admin found")
  case NabId(token1, token2) => println(s"Two tokens: '$token1' and '$token2'")
  case NabId(token1, token2, token3) => println(s"Three tokens: '$token1' and '$token2' and '$token3'")
  case NabId(_, _, _, _, _*) => println(s"Four or more tokens...")
  case _ => println(s"Invalid nab id: '$id'")
}

println("--------------")
println("VALID ID'S")
demo("nab--000--225")
demo("nab--193--339--003")
demo("nab--193--003")
demo("nab--193--003--382--118--052--129")

println("--------------")
println("INVALID ID'S")
demo("nab")
demo("nab--338")
demo("noob--193--339--003")
demo("NAB--193--339--003")
demo("nab--338--11x")

println("--------------")
