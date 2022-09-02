// Hello!
// Run this demo with `amm precision.sc`
// It will show the internals of how floating point numbers are represented.
// This relates to the slides5.md and how float/double are not good for representing currency amounts.
// In a nutshell, float/double are base 2 concepts and currency is a base 10 concept, so they don't mix well.

/** Returns whether the bit at position `bitPos` in `int` is 1
  *
  * Conceptually an `Int` is 4 bytes or 32 bits
  * hence `bitPos` should be a number from 0-31
  *
  * Example:
  *   - int is 13 = 8 + 4 + 1, ie. 00000000 00000000 00000000 00001101
  *   - bitPos is 3
  *
  * A mask is created for pos 3:   00000000 00000000 00000000 00001000
  *
  * The two numbers are &'d:       00000000 00000000 00000000 00001000
  *
  * Because the & isn't 0, we know there must have been a 1 at position 3
  */
def bit(int: Int, bitPos: Int): Boolean = {
  val mask = 1 << bitPos
  if ((int & mask) == 0) false else true
}

def bits(int: Int): Seq[Boolean] = for {
  bitPos <- 31 to 0 by -1
} yield bit(int, bitPos)

def bitToChar(bit: Boolean): Char = if (bit) '1' else '0'

// Formats a char seq into "xxxx-xxxx" style
def byteString(chars: Seq[Boolean], dividerPos: Int = 4): String = {
  val frontChunk = chars.slice(0, dividerPos).map(bitToChar).mkString
  val backChunk = chars.slice(dividerPos, chars.size).map(bitToChar).mkString
  s"$frontChunk-$backChunk"
}

/** Constructs a human readable string of 0's and 1's representing the int
  *
  * The RHS is the little end of the int.
  * Formatting is applied to make the half-bytes easier to read.
  *
  * e.g.
  *         25 = 16 + 8 + 1
  *
  *            produces:
  *
  *         "0000-0000 0000-0000 0000-0000 0001-1001"
  */
def bitString(int: Int): String = {
  val intBits = bits(int)

  (0 until 4).map(byteNumber => byteString(intBits.slice(byteNumber * 8, (byteNumber + 1) * 8))).mkString(" ")
}

/** Constructs a human readable string of 0's and 1's to represent the Float.
  *
  * The bytes are printed to match the internal structure of a Float:
  *
  *  x|yyyy-yyyy|zzz-zzzz zzzz-zzzz zzzz-zzzz zzzz-zzzz|
  *
  *  1  -- 8 --                 -- 23 --
  *
  * x = sign bit (1 bit)
  * y = exponent (8 bits)
  * z = mantissa/significand/base (23 bits)
  *
  * e.g.
  *         0.75F = 3/4 = 3 * 2^-2
  *
  *             produces:
  *
  *         "0|0111-1110|100-0000 0000-0000 0000-0000
  *
  */
def bitString(float: Float): String = {
  val FloatStructure(signed, exponent, mantissa) = FloatStructure.fromFloat(float)

  val signChar = bitToChar(signed)

  val exponentChunk = byteString(exponent)

  val mantissaChunk0 = byteString(mantissa.slice(0, 7), dividerPos = 3) // special case
  val mantissaChunk1 = byteString(mantissa.slice(7, 15))
  val mantissaChunk2 = byteString(mantissa.slice(15, 23))

  s"$signChar|$exponentChunk|$mantissaChunk0 $mantissaChunk1 $mantissaChunk2"
}

/** Represents the 3 parts of a Float.
  *
  * A Float is 32 bits:
  * - first bit is the sign
  * - next 8 are the exponent
  * - next 23 are the mantissa/base/significand
  *
  * In this structure, bits are represented by Booleans.
  */
case class FloatStructure(signed: Boolean, exponent: Seq[Boolean], mantissa: Seq[Boolean])

object FloatStructure {
  def fromFloat(float: Float): FloatStructure = {
    // Creates an Int with the equivalent bits.
    // Note this is not the same as float.toInt which creates an Int that's mathematically
    // similar to the Float, but would have a completely different internal bit structure
    val floatBits = bits(java.lang.Float.floatToIntBits(float))

    FloatStructure(
      signed = floatBits(0),
      exponent = floatBits.slice(1, 9),
      mantissa = floatBits.slice(9, 32)
    )
  }

  def describe(float: Float): String = {
    val FloatStructure(signed, exponent, mantissa) = FloatStructure.fromFloat(float)
    val rawExponent = bitsToLong(exponent)
    val adjustedExponent = rawExponent - 127 // see "bias"
    val simplifiedMantissa = {
      if (mantissa.forall(_ == false)) "1.0"
      else {
        "1." + mantissa.reverse.dropWhile(_ == false).reverse.map(bitToChar).mkString
      }
    }
    s"""|----------------------------------------------
        |decimal:    $float  (toString)
        |            ${BigDecimal(float)}  (BigDecimal)
        |bits:       ${bitString(float)}
        |sign:       ${if (signed) "negative" else "positive"}
        |exponent:   $adjustedExponent  (raw: $rawExponent)
        |mantissa:   ${describeMantissa(mantissa)}
        |            ${simplifiedMantissa}   (base 2)
        |scientific: ${if (signed) "-" else ""}${simplifiedMantissa} x 2^$adjustedExponent
        |""".stripMargin
  }

  private def describeMantissa(bits: Seq[Boolean]): String = {
    // Each position in the bit is a negative power of 2
    // e.g. 100100...
    // is really 1*1/2 + 0*1/4 + 0*1/8 + 1*1/16
    // Note as well the mantissa has an implied "1." before it, which is equivalent to prepending a true
    val allBits = true +: bits
    allBits.zipWithIndex.flatMap {
      case (bit, index) => if (bit) Some(s"1/${BigInt(2).pow(index)}") else None
    }.mkString(" + ")
  }
}

/** Interprets the bits in an integral manner with the left most being most signficant.
  *
  * Example:
  *            110101
  *
  *             becomes:
  *
  *           32 + 16 + 4 + 1 = 53
  *
  *
  * The return type is Long, hence the input sequence can have at most 64 bits (less is okay too).
  */
def bitsToLong(bits: Seq[Boolean]): Long = {
  bits.reverse.zipWithIndex.map {
    case (bit, index) => if (bit) BigInt(2).pow(index).toLong else 0L
  }.sum
}

/** Demo method to simultaneously demo and unit test the method */
def printAndAssertInt(int: Int, expected: String, humanFriendlyDescription: Option[String] = None): Unit = {
  val actual = bitString(int)
  val intDescription = humanFriendlyDescription.getOrElse(int.toString)
  println(f"$intDescription%25s = $actual")
  if (actual != expected)
    println(s"""|!! Value not as expected
                |   actual:   $actual
                |   expected: $expected
                |""".stripMargin)
}

/** Like above but for Float */
def printAndAssertFloat(float: Float, expected: String, humanFriendlyDescription: Option[String] = None): Unit = {
  val actual = bitString(float)
  val floatDescription = humanFriendlyDescription.getOrElse(float.toString)
  println(f"$floatDescription%25s = $actual")
  if (actual != expected)
    println(s"""|!! Value not as expected
                |   actual:   $actual
                |   expected: $expected
                |""".stripMargin)
}

println("* Begin demo")

println("\n** Ints")
printAndAssertInt(0, "0000-0000 0000-0000 0000-0000 0000-0000")
printAndAssertInt(1, "0000-0000 0000-0000 0000-0000 0000-0001")
printAndAssertInt(-1, "1111-1111 1111-1111 1111-1111 1111-1111")
printAndAssertInt(25, "0000-0000 0000-0000 0000-0000 0001-1001")
printAndAssertInt(Int.MinValue, "1000-0000 0000-0000 0000-0000 0000-0000", Some("MinValue"))
printAndAssertInt(Int.MaxValue, "0111-1111 1111-1111 1111-1111 1111-1111", Some("MaxValue"))

println("\n** Floats")
printAndAssertFloat(0F, "0|0000-0000|000-0000 0000-0000 0000-0000")
printAndAssertFloat(-0F, "1|0000-0000|000-0000 0000-0000 0000-0000") // Note 0F and -0F are different bit configurations!
printAndAssertFloat(1F, "0|0111-1111|000-0000 0000-0000 0000-0000")
printAndAssertFloat(-1F, "1|0111-1111|000-0000 0000-0000 0000-0000")
printAndAssertFloat(0.5F, "0|0111-1110|000-0000 0000-0000 0000-0000")
printAndAssertFloat(1.75F, "0|0111-1111|110-0000 0000-0000 0000-0000")
printAndAssertFloat(Float.MinValue, "1|1111-1110|111-1111 1111-1111 1111-1111", Some("MinValue"))
printAndAssertFloat(Float.MaxValue, "0|1111-1110|111-1111 1111-1111 1111-1111", Some("MaxValue"))
printAndAssertFloat(Float.NaN, "0|1111-1111|100-0000 0000-0000 0000-0000")
printAndAssertFloat(Float.NegativeInfinity, "1|1111-1111|000-0000 0000-0000 0000-0000")
printAndAssertFloat(Float.PositiveInfinity, "0|1111-1111|000-0000 0000-0000 0000-0000")
// Do different powers of 1.5 to demo how the exponent changes, but the mantissa doesn't
printAndAssertFloat(1.5F, "0|0111-1111|100-0000 0000-0000 0000-0000", Some("1.5 * 2^0 = 1.5F"))
printAndAssertFloat(3F, "0|1000-0000|100-0000 0000-0000 0000-0000", Some("1.5 * 2^1 = 3F"))
printAndAssertFloat(6F, "0|1000-0001|100-0000 0000-0000 0000-0000", Some("1.5 * 2^2 = 6F"))
printAndAssertFloat(12F, "0|1000-0010|100-0000 0000-0000 0000-0000", Some("1.5 * 2^3 = 12F"))
printAndAssertFloat(24F, "0|1000-0011|100-0000 0000-0000 0000-0000", Some("1.5 * 2^4 = 24F"))
// Some others
printAndAssertFloat(0.75F, "0|0111-1110|100-0000 0000-0000 0000-0000")
printAndAssertFloat(0.125F, "0|0111-1100|000-0000 0000-0000 0000-0000")

println("\n** Floats interpreted")
println(FloatStructure.describe(1.75F))
println(FloatStructure.describe(0.1F))
println(FloatStructure.describe(-0.5F))
