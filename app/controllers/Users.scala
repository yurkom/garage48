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
 * Author: Yury Molchan
 */

object Users extends Controller with MongoController {
  val collection = db[BSONCollection]("users")
  def getValue(map: Map[String, Seq[String]], key: String) : String = if (map.contains(key)) map.get(key).get(0) else ""

  /** Login */
  def enter() = Action { implicit request =>
    Async {
	  val login = request.body.asFormUrlEncoded.get("login")(0)
      val password = request.body.asFormUrlEncoded.get("password")(0)
	  
      // get the user having this login/password (there will be 0 or 1 result)
      val futureUser = collection.find(BSONDocument("login" -> login, "password" -> password)).one[User]
      futureUser.map { user => user match {
	    case None => Ok(views.html.login("Error login or password."))
		case Some(res) if (res.role == "user") => Ok(views.html.my_events())
		case Some(res) if (res.role == "trainer") => Ok(views.html.main_trainer())
	    case _ => Redirect("/")
	  } }
    }
  }
  
  def signup() = Action { request =>
    Async {
	  val login = getValue(request.body.asFormUrlEncoded.get, "login")
      val password = getValue(request.body.asFormUrlEncoded.get, "password")
	  val email = getValue(request.body.asFormUrlEncoded.get, "email")
	  val role = getValue(request.body.asFormUrlEncoded.get, "role")
	  val descr = getValue(request.body.asFormUrlEncoded.get, "descr")
	  val phone = getValue(request.body.asFormUrlEncoded.get, "phone")
	  
	  val user = User(Option(BSONObjectID.generate), login, password, email, role, descr, phone) // create the user
      collection.insert(user).map(
        _ => {
		    if (role == "user") Ok(views.html.my_events())
			else if (role == "trainer") Ok(views.html.main_trainer())
			else Redirect("/")
		}) // return the created user in a JSON
    }
  }
  
  
  /** list all users */
  def index = Action { implicit request =>
    Async {
      val cursor = collection.find(
        BSONDocument(), BSONDocument()).cursor[User] // get all the fields of all the users
      val futureList = cursor.toList // convert it to a list of User
      futureList.map { users => Ok(Json.toJson(users)) } // convert it to a JSON and return it
    }
  }

  
  /** retrieve the user for the given id as JSON */
  def show(id: String) = Action(parse.empty) { request =>
    Async {
      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
      // get the user having this id (there will be 0 or 1 result)
      val futureUser = collection.find(BSONDocument("_id" -> objectID)).one[User]
      futureUser.map { user => Ok(Json.toJson(user)) }
    }
  }
  
  /** update the user for the given id from the JSON body */
  def update(id: String) = Action { implicit request =>
    Async {
      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
	  val login = getValue(request.body.asFormUrlEncoded.get, "login")
      val password = getValue(request.body.asFormUrlEncoded.get, "password")
	  val email = getValue(request.body.asFormUrlEncoded.get, "email")
	  val role = getValue(request.body.asFormUrlEncoded.get, "role")
	  val descr = getValue(request.body.asFormUrlEncoded.get, "descr")
	  val phone = getValue(request.body.asFormUrlEncoded.get, "phone")
      val modifier = BSONDocument( // create the modifier user
        "$set" -> BSONDocument(
        "login" -> login,
        "password" -> password,
        "email" -> email,
		"phone" -> phone,
		"descr" -> descr,
		"role" -> role))
      collection.update(BSONDocument("_id" -> objectID), modifier).map(
        _ => Ok(Json.toJson(User(Option(objectID), login, password, email, role, descr, phone)))) // return the modified user in a JSON
    }
  }
  
  /** delete a user for the given id */
  def delete(id: String) = Action(parse.empty) { request =>
    Async {
      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
      collection.remove(BSONDocument("_id" -> objectID)).map( // remove the user
        _ => Ok(Json.obj())).recover { case _ => InternalServerError } // and return an empty JSON while recovering from errors if any
    }
  }
}
