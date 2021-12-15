object WildCard {
  val a = 1
  val b = 2
  implicit def compoundName2Name(value: CompoundName): Name =
    Name(s"${value.first} WILD ${value.second}")
}
