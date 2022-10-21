package controllers

import javax.inject._
import play.api._
import play.api.http.{ContentTypes, Writeable}
import play.api.libs.json.{Json, JsValue, OFormat, Reads, Writes}
import play.api.mvc._
import java.util.UUID

@Singleton
class UserController @Inject()(val controllerComponents: ControllerComponents, userService: UserService) extends BaseController {

  def saveUser(): Action[JsValue] = Action(parse.json) { request =>
    println("---")
    println(s"Save request: ${request.body}")

    request
      .body
      .validate[User]
      .fold(
        _ => BadRequest,
        post => {
          val user = userService.saveUser(post.name, user.age)
          Created(userWithId.id.toString)
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
