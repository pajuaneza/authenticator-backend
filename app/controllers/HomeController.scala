package controllers

import java.util.UUID
import javax.inject._
import models.domain.Emp
import models.repo.EmpRepo
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent._
import security.Authenticator
import security.UserRequest

@Singleton
class HomeController @Inject() (
  authenticator: Authenticator,
  val empRepo: EmpRepo,
  val cc: ControllerComponents,
)(implicit val ec: ExecutionContext)
    extends AbstractController(cc) {

  val empForm = Form(
    mapping(
      "id" -> ignored(UUID.randomUUID()),
      "firstName" -> nonEmptyText,
      "middleName" -> optional(text),
      "lastName" -> nonEmptyText,
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "role" -> nonEmptyText,
    )(Emp.apply)(Emp.unapply)
  )

  val loginForm = Form(
    tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
    )
  )

  def index() = Action.async { implicit request =>
    empRepo.createEmployeeTable().map { _ =>
      Ok("Employee table created!")
    }
  }

  def getAllEmployees() = Action.async { implicit request =>
    empRepo.getAllEmployees().map { employees =>
      Ok(Json.toJson(employees))
    }
  }

  def addEmployee() = Action.async { implicit request =>
    empForm
      .bindFromRequest()
      .fold(
        errors => {
          Future.successful(BadRequest)
        },
        employee => {
          empRepo.addEmployee(employee.copy(id = UUID.randomUUID())).map { _ =>
            Ok("Employee added!")
          }
        },
      )
  }

  def login() = authenticator.async { implicit request =>
    loginForm
      .bindFromRequest()
      .fold(
        error => {
          Future.successful(Unauthorized)
        },
        credentials => {
          empRepo
            .findEmployeeByUsernameAndPassword(credentials._1, credentials._2)
            .map { employee =>
              employee match {
                case Some(em) =>
                  Ok(Json.toJson(em)).withSession("username" -> em.username)
                case None => Unauthorized
              }
            }
        },
      )
  }
}
