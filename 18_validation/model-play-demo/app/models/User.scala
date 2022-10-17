package models

import java.util.UUID
import java.time.Instant

case class User(
  id: UUID,
  name: String,
  age: Int,
  created: Instant,
  updated: Instant
)
