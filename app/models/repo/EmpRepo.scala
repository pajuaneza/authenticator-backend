package models.repo

import java.util.UUID
import javax.inject._
import models.domain.Emp
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import scala.concurrent.ExecutionContext
import slick.jdbc.JdbcProfile

@Singleton
final class EmpRepo @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider,
  implicit val ec: ExecutionContext,
) extends HasDatabaseConfigProvider[JdbcProfile] {
  import slick.jdbc.PostgresProfile.api._

  protected class EmpTable(tag: Tag) extends Table[Emp](tag, "EMPLOYEES") {
    def id = column[UUID]("ID", O.PrimaryKey)
    def firstName = column[String]("FIRST_NAME")
    def middleName = column[Option[String]]("MIDDLE_NAME")
    def lastName = column[String]("LAST_NAME")
    def username = column[String]("USERNAME")
    def password = column[String]("PASSWORD")
    def role = column[String]("ROLE")

    def * =
      (id, firstName, middleName, lastName, username, password, role).mapTo[Emp]
  }

  val employees = TableQuery[EmpTable]

  def createEmployeeTable() = db.run {
    employees.schema.createIfNotExists
  }

  def getAllEmployees() = db.run {
    employees.result
  }

  def addEmployee(employee: Emp) = db.run {
    employees += employee
  }

  def findEmployeeByUsernameAndPassword(username: String, password: String) =
    db.run {
      employees
        .filter(e => e.username === username && e.password === password)
        .result
        .headOption
    }
}
