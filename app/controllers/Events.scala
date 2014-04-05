package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import models.User
import models.User.userFormat
import models.User.UserBSONReader
import models.User.UserBSONWriter
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentIdentity
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONObjectIDIdentity
import reactivemongo.bson.BSONStringHandler
import reactivemongo.bson.Producer.nameValue2Producer

/*
 * Author: Andrei Ruban
 */

 object Events extends Controller with MongoController {
	val collection = db[BSONCollection]("events")

	/** list all events */
  def index = Action { implicit request =>
    Async {
      val cursor = collection.find(
        BSONDocument(), BSONDocument()).cursor[Event] // get all the fields of all the events
      val futureList = cursor.toList // convert it to a list of Event
      futureList.map { events => Ok(Json.toJson(events)) } // convert it to a JSON and return it
    }
  }

  /** create an event from the given JSON */
  def create() = Action(parse.json) { request =>
    Async {

    	val place = request.body.\("place").toString().replace("\"", "")
    	val date = request.body.\("date").toString().replace("\"", "")
    	val level = replace.body.\("level").toString().replace("\"", "")
    	val sportKind = request.body.\("sportKind").toString.replace("\"", "")
    	val tradeText = request.body.\("tradeText").toString.replace("\"", "")	
    	val event = Event(Option(BSONObjectID.generate), place, date, level, sportKind, tradeText) // create an event
    	collection.insert(event).map(_=> Ok(Json.toJson(event))) // return the created event in a JSON
    }
  }

  /** retrieve the event for the given id as JSON */
  def show(id: String) = Action(parse.empty) { request =>
    Async {
      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
      // get the event having this id (there will be 0 or 1 result)
      val futureUser = collection.find(BSONDocument("_id" -> objectID)).one[Event]
      futureUser.map { event => Ok(Json.toJson(event)) }
    }
  }

  /** update the event for the given id as JSON */
  def update(id: String) = Action(parse.json) { request =>
  	Async {
  		val objectID = new BSONObjectID(id) // get the correspond 
  		val place = request.body.\("place").toString().replace("\"", "")
  		val date = request.body.\("date").toString().replace("\"", "")
  		val level = request.body.\("level").toString().replacee("\"", "")
  		val sportKind = request.body.\("sportKind").toString.replace("\"", "")
  		val tradeText = request.body.\("tradeText").toString.replace("\"", "")
  		val modifier = BSONDocument( // create the modifier event
  			"$set" -> BSONDocument(
  				"place" -> place,
  				"date" -> date,
  				"level" -> level,
  				"sportKind" -> sportKind,
  				"tradeText" -> tradeText))
  		collection.update(BSONDocument("_id" -> objectID), modifier).map(
  			_=> Ok(Json.toJson(Event(Option(objectID), place, date, level, sportKind, tradeText))) // return the modified event in a JSON
  			) 
  	}
  }

 /** delete an event for a given id */
def delete(id: String) = Action(parse.empty) { request =>
	Async{
		val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
		collection.remove(BSONDocument("_id" -> objectID)).map( // remove the event
			_=> Ok(Json.obj())).recover{ case _ => InternalServerError} // and return an empty JSON while recovering from errors if any
			
	}
}

 }