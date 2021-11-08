import java.time.LocalDate
import java.time.format.DateTimeFormatter

object AnzId {
  val pattern = """anz--(\d{8})--([0-9a-f]{8})""".r
  val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

  // Example: "anz--20210923--ac423f32"
  def unapply(raw: String): Option[(LocalDate, String)] = raw match {
    case pattern(dateStr, hash) => Some((LocalDate.parse(dateStr, formatter), hash))
    case _ => None
  }
}

object CommbankId {
  val pattern = """(\d{10})\.(basic|premium|ultimate)""".r

  // Example: "2394222933.basic"
  def unapply(raw: String): Option[(String, String)] = raw match {
    case pattern(code, plan) => Some((code, plan))
    case _ => None
  }
}

def demo(id: String): Unit = id match {

  case AnzId(date, hash) =>
    println("---------------")
    println(s"Anz id found: '$id'")
    println(s"  Date: $date")
    println(s"  Hash: $hash")

  case CommbankId(code, plan) =>
    println("---------------")
    println(s"Commbank id found: '$id'")
    println(s"  code: '$code'")
    println(s"  plan: '$plan'")

  case _ =>
    println("---------------")
    println(s"Invalid id format: '$id'")
}

demo("anz--20210923--ac423f32")
demo("amp--20211106--dc457f02")
demo("0123456789.basic")
demo("9876543210.ultimate")
println("---------------")
