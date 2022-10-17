package controllers

import javax.inject._
import play.api._
import play.api.http.{ContentTypes, Writeable}
import play.api.libs.json.{Json, JsValue, OFormat}
import play.api.mvc._
import java.util.UUID

@Singleton
class UserController @Inject()(val controllerComponents: ControllerComponents, userService: UserService) extends BaseController {

  def saveUser(): Action[JsValue] = Action(parse.json) { request =>
    println("---")
    println(s"Save request: ${request.body}")

    implicit val uuidWriter = play.api.libs.json.Writes.UuidWrites

    request
      .body
      .validate[Post]
      .fold(
        _ => BadRequest,
        post => {
          val user = userService.saveUser(post.name, post.age)
          Created(Json.toJson(Response(user.id)))
        }
      )
  }

  def findById(id: UUID): Action[AnyContent] = Action {
    // TODO
    BadRequest
  }

  private case class Post(name: String, age: Int)
  private implicit val postFormat: OFormat[Post] = Json.format[Post]

  private case class Response(id: UUID)
  private implicit val responseFormat: OFormat[Response] = Json.format[Response]
}
