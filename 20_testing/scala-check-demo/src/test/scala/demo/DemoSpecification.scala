package demo

import org.scalacheck.Properties
import org.scalacheck.Prop.{forAll, forAllNoShrink}
import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary

import demo.Demo._

class ToUpperCaseSpecification extends Properties("toUpperCase") {
  property("length preserving") = forAll { (s: String) =>
    s.length == toUpperCase(s).length
  }

  property("idempotent") = forAll { (s: String) =>
    val upper = toUpperCase(s)
    upper == toUpperCase(upper)
  }
}

class IsUpperCaseSpecification extends Properties("isUpperCase") {
  // property("concatenation AND's") = forAll { (s1: String, s2: String) =>
  //   isUpperCase(s1 + s2) == isUpperCase(s1) && isUpperCase(s2)
  // }
}

class HalveSpecification extends Properties("halve") {
  val mathematicalDoubles: Gen[Double] = arbitrary[Double].filter {
    case Double.NaN | Double.NegativeInfinity | Double.PositiveInfinity => false
    case _ => true
  }

  // "magnitude" means distance to 0
  property("should decrease magnitude") = forAll(mathematicalDoubles) { d =>
    math.abs(halve(d)) <= math.abs(d)
  }

}

class PersonSpecification extends Properties("person") {
  val personGen: Gen[Person] = for {
    name <- arbitrary[String]
    if name.nonEmpty && name.length <= 30
    age <- arbitrary[Int]
    if age >= 18 && age <= 120
  } yield Person(name, age)
}
