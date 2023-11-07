package security

import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent._
import scala.concurrent.Future

trait UserComponent {
  def username: String
  def password: String
}

case class User(val username: String, val password: String)
    extends UserComponent

class UserRequest[A](val user: Option[User], request: Request[A])
    extends WrappedRequest[A](request)

@Singleton
class Authenticator @Inject() (parser: BodyParsers.Default)(implicit
  ec: ExecutionContext
) extends ActionBuilder[UserRequest, AnyContent]
    with Logging {
  def parser: BodyParser[AnyContent] = parser

  protected def executionContext: ExecutionContext = ec

  override def invokeBlock[A](
    request: Request[A],
    block: UserRequest[A] => Future[Result],
  ): Future[Result] = {
    logger.info("Calling action")

    request.session.get("username") match {
      case Some(username) =>
        block(new UserRequest(Some(User(username, "admin")), request))
      case None =>
        block(new UserRequest(None, request))
    }
  }
}
