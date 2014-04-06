package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import models.Event
import models.Event.eventFormat
import models.Event.EventBSONReader
import models.Event.EventBSONWriter
import models.User
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

    	val owner = request.body.\("owner").toString().replace("\"", "")
    	val place = request.body.\("place").toString().replace("\"", "")
    	val date = request.body.\("date").toString().replace("\"", "")
    	val level = request.body.\("level").toString().replace("\"", "")
    	val sportKind = request.body.\("sportKind").toString.replace("\"", "")
    	val tradeText = request.body.\("tradeText").toString.replace("\"", "")	
		val ownerID = new BSONObjectID(owner)
      val event = Event(Option(BSONObjectID.generate), ownerID, place, date, level, sportKind, tradeText) // create an event
    	collection.insert(event).flatMap(_=> {
      val futureUser = collection.find(BSONDocument("owner" -> ownerID)).cursor[Event].toList
      futureUser.map { events => Ok(Json.toJson(events)) }
		
		}) // return the created event in a JSON
    }
  }

  /** retrieve the event for the given id as JSON */
  def show(owner: String) = Action(parse.empty) { request =>
    Async {
      val ownerID = new BSONObjectID(owner) // get the corresponding BSONObjectID
      // get the event having this id (there will be 0 or 1 result)
      val futureUser = collection.find(BSONDocument("owner" ->ownerID)).cursor[Event].toList
      futureUser.map { events => Ok(Json.toJson(events)) }
    }
  }

  
  
  /** retrieve the event for the given id as JSON */
  def join() = Action(parse.json) { request =>
    Async {
    	val event = request.body.\("event").toString().replace("\"", "")
    	val user = request.body.\("user").toString().replace("\"", "")
  		val eventID = new BSONObjectID(event) // get the correspond 
		val userId = new BSONObjectID(user)
  		val modifier = BSONDocument( // create the modifier event
  			"$addToSet" -> BSONDocument(
  				"users" -> userId))
  		collection.update(BSONDocument("_id" -> eventID), modifier).flatMap(_=> {
      val futureUser = collection.find(BSONDocument("users" -> userId)).cursor[Event].toList
      futureUser.map { events => Ok(Json.toJson(events)) }
		
		}) 
  	}
  }

  
  /** retrieve the event for the given id as JSON */
  def joined(user: String) = Action(parse.empty) { request =>
    Async {
      val userID = new BSONObjectID(user) // get the corresponding BSONObjectID
      // get the event having this id (there will be 0 or 1 result)
      val futureUser = collection.find(BSONDocument("users" -> userID)).cursor[Event].toList
      futureUser.map { events => Ok(Json.toJson(events)) }
    }
  }

  
  
  /** update the event for the given id as JSON */
  def update(id: String) = Action(parse.json) { request =>
  	Async {
  		val objectID = new BSONObjectID(id) // get the correspond 
    	val owner = request.body.\("owner").toString().replace("\"", "")
  		val place = request.body.\("place").toString().replace("\"", "")
  		val date = request.body.\("date").toString().replace("\"", "")
  		val level = request.body.\("level").toString().replace("\"", "")
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
  			_=> Ok(Json.toJson(Event(Option(objectID), new BSONObjectID(owner), place, date, level, sportKind, tradeText))) // return the modified event in a JSON
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