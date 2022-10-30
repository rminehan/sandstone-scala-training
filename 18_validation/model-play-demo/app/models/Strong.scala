package models

import cats.data.ValidatedNel
import cats.syntax.validated._

trait Strong[Weak] {
  sealed trait Tag
  type Type = Weak with Tag

  def validate(weak: Weak): ValidatedNel[String, Weak]

  def from(weak: Weak): ValidatedNel[String, Type] = validate(weak).map(_.asInstanceOf[Type])
}

object Strong {
  type Name = Name.Type
  type Age = Age.Type
}

object Name extends Strong[String] {
  def validate(weak: String): ValidatedNel[String, String] = weak.trim.validNel
}

object Age extends Strong[Int] {
  def validate(weak: Int): ValidatedNel[String, Int] = if (weak < 18) "Age is less than 18".invalidNel else weak.validNel
}
