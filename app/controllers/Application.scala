package controllers

import java.io.File

import play.Play
import play.api.mvc.Action
import play.api.mvc.Controller

/*
 * Author: Sari Haj Hussein
 */

object Application extends Controller {
  
  /** serve the index page app/views/index.scala.html */
  def index = Action {
    Ok(views.html.login())
  }
  
  def redirect(any: String) = Action {
    Redirect("/")
  }

  def login = Action {
    Ok(views.html.login())
  }

  def add_event = Action {
    Ok(views.html.add_event())
  }

  def main_trainer = Action {
    Ok(views.html.main_trainer())
  }

  def people_list = Action {
    Ok(views.html.people_list())
  }

  def registration = Action {
    Ok(views.html.registration())
  }

  def event_description = Action{
    Ok(views.html.event_description())
  }

  def my_events = Action{
    Ok(views.html.my_events())
  }

  def levels_description = Action{
    Ok(views.html.levels_description())
  }

  def userfilter = Action{
    Ok(views.html.userfilter())
  }
  
  /** resolve "any" into the corresponding HTML page URI */
  def getURI(any: String): String = any match {
    case "main" => "/public/html/main.html"
    case "detail" => "/public/html/detail.html"
    case _ => "error"
  }
  
  /** load an HTML page from public/html */
  def loadPublicHTML(any: String) = Action {
    val projectRoot = Play.application().path()
    var file = new File(projectRoot + getURI(any))
    if (file.exists())
      Ok(scala.io.Source.fromFile(file.getCanonicalPath()).mkString).as("text/html");
    else
      NotFound
  }
}