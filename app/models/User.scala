package models

import play.api.libs.json.Json
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONObjectIDIdentity
import reactivemongo.bson.BSONStringHandler
import reactivemongo.bson.Producer.nameValue2Producer
import play.modules.reactivemongo.json.BSONFormats.BSONObjectIDFormat

/*
 * Author: Yury Molchan
 */

case class User(id: Option[BSONObjectID], 
		login: String, 
		password: String, 
		email: String, 
		role: String, 
		descr: String, 
		phone: String)

object User {
  /** serialize/Deserialize a User into/from JSON value */
  implicit val userFormat = Json.format[User]

  /** serialize a User into a BSON */
  implicit object UserBSONWriter extends BSONDocumentWriter[User] {
    def write(user: User): BSONDocument =
      BSONDocument(
        "_id" -> user.id.getOrElse(BSONObjectID.generate),
        "login" -> user.login,
        "password" -> user.password,
        "email" -> user.email,
		"phone" -> user.phone,
		"descr" -> user.descr,
		"role" -> user.role)
  }

  /** deserialize a User from a BSON */
  implicit object UserBSONReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument): User =
      User(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("login").orNull,
        doc.getAs[String]("password").orNull,
        doc.getAs[String]("email").orNull,
        doc.getAs[String]("role").orNull,
        doc.getAs[String]("descr").orNull,
        doc.getAs[String]("phone").orNull)
  }
}
