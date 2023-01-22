package demo

import org.scalacheck.Properties
import org.scalacheck.Prop.{forAll, forAllNoShrink}
import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary
import scala.util.Try

import demo.Fac.fac

// NOTE Currently fac is way way way too slow for property tests on large n > 0 values
// Those tests have been commented out for now (TODO - find out how to mark tests as ignored in scalacheck)
  // See Fac.scala and GenerateFac.scala for more context.

class FacSpecification extends Properties("fac") {
  // Generates values strictly larger than 0
  val positives: Gen[Int] = arbitrary[Int].filter(_ > 0)

  // property("grows by n") = forAllNoShrink(positives) { (n: Int) =>
  //   fac(n) == n * fac(n - 1)
  // }

  // property("should divide n") = forAllNoShrink(positives) { (n: Int) =>
  //   fac(n) % n == 0
  // }

  val negatives: Gen[Int] = arbitrary[Int].filter(_ < 0)

  property("fails on negatives") = forAll(negatives) { (n: Int) =>
    Try(fac(n)).isFailure
  }
}
