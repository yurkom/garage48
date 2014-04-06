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

/**
* Author Andrei Ruban
*/

case class Event(id: Option[BSONObjectID], owner: BSONObjectID, place: String, date: String, level: String, sportKind: String, tradeText: String)

object Event {
	 /** serialize/Deserialize an Event into/from JSON value */
  implicit val eventFormat = Json.format[Event]

  /** serialize a Event into a BSON */
  implicit object EventBSONWriter extends BSONDocumentWriter[Event] {
  		def write(event: Event) : BSONDocument = BSONDocument (
		"_id" -> event.id.getOrElse(BSONObjectID.generate),
		"owner" -> event.owner,
        "place" -> event.place,
        "date" -> event.date,
        "level" -> event.level,
        "sportKind" -> event.sportKind,
        "tradeText" -> event.tradeText)
  }

   /** deserialize a Event from a BSON */
  implicit object EventBSONReader extends BSONDocumentReader[Event] {
    def read(doc: BSONDocument): Event =
      Event(
        doc.getAs[BSONObjectID]("_id"),
		doc.getAs[BSONObjectID]("owner").get,
        doc.getAs[String]("place").get,
        doc.getAs[String]("date").get,
        doc.getAs[String]("level").get,
        doc.getAs[String]("sportKind").get,
        doc.getAs[String]("tradeText").get)
  }
}
