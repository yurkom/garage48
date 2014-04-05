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

  /** list all users */
  def index = Action { implicit request =>
    Async {
      val cursor = collection.find(
        BSONDocument(), BSONDocument()).cursor[User] // get all the fields of all the users
      val futureList = cursor.toList // convert it to a list of User
      futureList.map { users => Ok(Json.toJson(users)) } // convert it to a JSON and return it
    }
  }
  
  /** create a user from the given JSON */
  def create() = Action(parse.json) { request =>
    Async {
      val login = request.body.\("login").toString().replace("\"", "")
      val password = request.body.\("password").toString().replace("\"", "")
      val email = request.body.\("email").toString().replace("\"", "")
      val user = User(Option(BSONObjectID.generate), login, password, email) // create the user
      collection.insert(user).map(
        _ => Ok(Json.toJson(user))) // return the created user in a JSON
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
  def update(id: String) = Action(parse.json) { request =>
    Async {
      val objectID = new BSONObjectID(id) // get the corresponding BSONObjectID
      val login = request.body.\("login").toString().replace("\"", "")
      val password = request.body.\("password").toString().replace("\"", "")
      val email = request.body.\("email").toString().replace("\"", "")
      val modifier = BSONDocument( // create the modifier user
        "$set" -> BSONDocument(
          "login" -> login,
          "password" -> password,
          "email" -> email))
      collection.update(BSONDocument("_id" -> objectID), modifier).map(
        _ => Ok(Json.toJson(User(Option(objectID), login, password, email)))) // return the modified user in a JSON
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
