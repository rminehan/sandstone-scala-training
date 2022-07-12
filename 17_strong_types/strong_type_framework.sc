// General framework for creating strong types
// It needs you to specify:
// - the weak type
// - the validation logic
// Examples of how to use it below
trait Strong[Weak] {
  sealed trait Tag
  type Type = Weak with Tag

  def validate(i: Weak): Boolean

  def from(value: Weak): Option[Type] = if (validate(value)) Some(value.asInstanceOf[Type]) else None
  def fromUnsafe(value: Weak): Type = from(value).getOrElse(throw new Exception(s"[value=$value] Invalid weak value can't be parsed to strong value"))
}

// The advantage of this approach compared to the one in the slides is that there's no object creation overhead.
// The weak and strong values are the same at runtime - they are only differentiated at compile time.
// For example the "Natural" defined below is just an Int and gets all the performance advantages of that.
// It is a bit more complex though and can be "hacked" with some clever casting.

def demo[Weak](input: Weak, strong: Strong[Weak]): Unit = {
  println(s"  *** Input: '$input' => ${strong.from(input)}")
}

println("* Starting demo")

// Try it out with Natural
println("Naturals")
object Natural extends Strong[Int] {
  def validate(i: Int): Boolean = i >= 0
}
demo(3, Natural)
demo(-1, Natural)

// Try it out with NonEmptyString
println("NonEmptyString")
object NonEmptyString extends Strong[String] {
  def validate(s: String): Boolean = s.nonEmpty
}
demo("a", NonEmptyString)
demo("", NonEmptyString)
demo(" ", NonEmptyString)

// NonEmptyString allows whitespace string
// Let's make a Name which must be trimmed and non-empty
println("Name")
object Name extends Strong[String] {
  def validate(s: String): Boolean = {
    val trimmed = s.trim
    s == trimmed && s.nonEmpty
  }
}
demo("a", Name)
demo(" ", Name)
demo("a ", Name)
demo("", Name)


// Time for nasty hackery...
println("Nasty hackery")
// As mentioned above you can use clever casting to get around the tagging trick Strong uses
// We'll pass a negative value to this without a compiler error:
def sqrt(natural: Natural.Type): Double = math.sqrt(natural)

// Won't compile - uncomment this and you'll see it doesn't compile
// sqrt(-1)
// But this does...
println(sqrt(-1.asInstanceOf[Natural.Type]))

// If the strong type is for internal use and you're assuming developers are trying to do the right thing,
// then this security hole isn't a real issue.
// Usually these strong types are to guide devs in the right direction and you can assume they want to do the right thing.
// Also most devs wouldn't even realise they could do this.
// If it was an externally used library though, this would be an issue.

println("* Finishing demo")
