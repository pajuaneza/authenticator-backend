package models.domain

import java.util.UUID
import play.api.libs.json._

case class Emp (
  id: UUID,
  firstName: String,
  middleName: Option[String],
  lastName: String,
  username: String,
  password: String,
  role: String
)

object Emp {
  val tupled = (apply: (UUID, String, Option[String], String, String, String, String) => Emp).tupled
  def apply (firstName: String, middleName: Option[String], lastName: String,username: String, password: String, role: String): Emp = apply(UUID.randomUUID(), firstName, middleName, lastName, username, password, role)
  implicit val writes: Writes[Emp] = Json.writes[Emp]
}