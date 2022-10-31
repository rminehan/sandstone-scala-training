package models

import javax.inject._
import java.util.UUID
import java.time.Instant
import reactivemongo.api.bson.{BSONDocumentHandler, Macros}

@Singleton
class UserRepository @Inject() {

  // Mimic mongo with in memory store (note it's not persistent)
  private val users = scala.collection.mutable.ListBuffer.empty[UserBson]

  def saveUser(user: User): Unit = {
    val userBson = UserBson(
      _id = user.id.toString,
      name = user.name,
      age = user.age,
      created = user.created,
      updated = user.updated
    )
    users.append(userBson)
  }

  // def findById(id: UUID): User = {
  //   users.find(_._id == Some(id)).map(UserBson -> User logic).getOrElse(
  //     throw new IllegalArgumentException(s"Attempt to access non-existent user at id: $id")
  //   )
  // }
}

case class UserBson(
  _id: String,
  name: String,
  age: Int,
  created: Instant,
  updated: Instant
)

object UserBson {
  implicit val handler: BSONDocumentHandler[UserBson] = Macros.handler[UserBson]
}
