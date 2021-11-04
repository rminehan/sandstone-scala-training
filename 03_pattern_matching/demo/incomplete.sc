def describeNumber(int: Int): String = int match {
  case 0 => "zero"
  case 3 => "strong"
  case 4 => "unlucky"
  case 12 => "a nice round number"
}

// Demo the above
println(describeNumber(0))
println(describeNumber(4))
println(describeNumber(2))
