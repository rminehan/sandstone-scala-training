package controllers

import java.util.UUID
import java.time.Instant
import javax.inject._

import models.{User, UserRepository}
import models.Strong._

class UserService @Inject() (userRepository: UserRepository) {
  def saveUser(name: Name, age: Age): User = {
    val id = UUID.randomUUID()
    val now = Instant.now
    val userToSave = User(
      id = id,
      name = name,
      age = age,
      created = now,
      updated = now
    )
    println(s"About to save user: $userToSave")
    userRepository.saveUser(userToSave)
    userToSave
  }

  def getUser(id: UUID): User = ???
}
