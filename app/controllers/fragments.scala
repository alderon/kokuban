package controllers

import play._
import play.mvc._
import play.data.validation.Annotations._
import play.db.anorm._ 
import models._

import java.util.{Date}

object Fragments extends Controller {
    
    def index = {
        Template('fragments -> Fragment.find().as(Fragment*))
    }

    def new_ = Template("Fragments/new.html", 'styles -> Fragment.STYLES)
    
    def create(@Required title:String, @Required content:String, @Required style:String) = {
        if (validation.hasErrors) {
            flash += "error" -> "All fields are required." // TODO move to messages
            new_
        } else {
            Fragment.create(Fragment(title, content, style))
            Redirect("/fragments")
        }
    }
    
    def show(id: Long) = {
        Template('fragment -> Fragment.find("id={id}").on("id" -> id).as(Fragment*).head)
    }
    
    def destroy(id: Long) = {
        val sql = Fragment.delete("id={id}").on("id" -> id)
        var result = sql.executeUpdate().fold(
            e => "Something went wrong!",
            c => "Fragment deleted."
        )
        flash += "info" -> result
        Redirect("/fragments")
    }
    
}
