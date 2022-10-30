package models

import java.util.UUID
import java.time.Instant

import Strong._

case class User(
  id: UUID,
  name: Name,
  age: Age,
  created: Instant,
  updated: Instant
)
