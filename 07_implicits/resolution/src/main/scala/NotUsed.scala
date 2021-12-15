// Used as an example of a conversion the compiler _won't_ find
object NotUsed {
  implicit def compoundName2Name(value: CompoundName): Name =
    Name(s"${value.first} NOT USED ${value.second}")
}

