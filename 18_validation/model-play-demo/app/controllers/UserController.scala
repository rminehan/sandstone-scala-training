package controllers

import javax.inject._
import play.api._
import play.api.http.{ContentTypes, Writeable}
import play.api.libs.json.{Json, JsValue, OFormat, Reads, Writes}
import play.api.mvc._
import java.util.UUID
import java.time.Instant

import models.{Age, Name}
import models.Strong._
import cats.data.ValidatedNel
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.apply._

@Singleton
class UserController @Inject()(val controllerComponents: ControllerComponents, userService: UserService) extends BaseController {

  def saveUser(): Action[JsValue] = Action(parse.json) { request =>
    println("---")
    println(s"Save request: ${request.body}")

    request
      .body
      .validate[Post]
      .fold(
        _ => BadRequest,
        post => {
          val nameVNel = Name.from(post.name)
          val ageVNel = Age.from(post.age)

          val userVNel = (nameVNel, ageVNel).mapN {
            case (name, age) => userService.saveUser(name, age)
          }

          userVNel match {
            case Valid(user) => Created(Json.toJson(Response(user.id)))
            case Invalid(errors) =>
              println("Request failed additional validation")
              BadRequest(Json.toJson(Errors(errors.toList)))
          }
        }
      )
  }

  def findById(id: UUID): Action[AnyContent] = Action {
    // TODO
    BadRequest
  }

  private case class Post(name: String, age: Int)
  private implicit val postReads: Reads[Post] = Json.reads[Post]

  private case class Response(id: UUID)
  private implicit val responseWrites: Writes[Response] = Json.writes[Response]

  private case class Errors(errors: Seq[String])
  private implicit val errorsWrites: Writes[Errors] = Json.writes[Errors]
}
