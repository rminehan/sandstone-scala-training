object AnzId {
  // Example: "anz--20210923--ac423f32"
  def unapply(raw: String): Boolean = raw.matches("""anz--\d{8}--[0-9a-f]{8}""")
}

object CommbankId {
  // Example: "2394222933.basic"
  def unapply(raw: String): Boolean = raw.matches("""\d{10}\.(basic|premium|ultimate)""")
}

def demo(id: String): Unit = id match {
  case AnzId() => println(s"Anz id found: '$id'")
  case CommbankId() => println(s"Commbank id found: '$id'")
  case _ => println(s"Invalid id format: '$id'")
}

demo("anz--20210923--ac423f32")
demo("amp--20211106--dc457f02")
demo("0123456789.basic")
demo("9876543210.ultimate")
