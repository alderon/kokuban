package controllers

import play._
import play.mvc._
import play.data.validation.Annotations._
import models._

import java.util.{Date}

object Fragments extends Controller {
    
    def index = {
        Template('fragments -> Fragment.find().as(Fragment*))
    }

    def new_ = Template("Fragments/new.html")
    
    def create(@Required title:String, @Required content:String) = {
        if (validation.hasErrors) {
            flash += "error" -> "All fields are required."
            new_
        } else {
            Fragment.create(Fragment(title, content))
            Redirect("/fragments")
        }
    }
    
    def show(id: Long) = {
        Template('fragment -> Fragment.find("id={id}").on("id" -> id).as(Fragment*).first)
    }
    
}
