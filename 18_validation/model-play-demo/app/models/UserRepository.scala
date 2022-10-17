package models

import javax.inject._
import java.util.UUID

@Singleton
class UserRepository @Inject() {

  // Mimic mongo with in memory store (note it's not persistent)
  private val users = scala.collection.mutable.ListBuffer.empty[User]

  def saveUser(user: User): Unit = {
    users.append(user)
  }

  def findById(id: UUID): User = {
    users.find(_.id == Some(id)).getOrElse(
      throw new IllegalArgumentException(s"Attempt to access non-existent user at id: $id")
    )
  }
}
