package controllers

import play._
import play.data.validation.Annotations._
import play.db.anorm._ 
import play.i18n.Messages
import play.mvc._

import models._

import java.util.{Date}

object Fragments extends Controller {
    
    def index(search: String = "") = {
        var fragments = search.trim match {
            case "" => Fragment.findAllWithTags()
            case term:String => Fragment.search(term.trim)
            case _ => Fragment.findAllWithTags()
        }
        
        Template('fragments -> fragments)
    }

    def new_ = Template("Fragments/new.html", 'styles -> Fragment.STYLES)
    
    def create(@Required title:String, @Required content:String, @Required style:String, @Required tags:String) = {
        if (validation.hasErrors) {
            flash += "error" -> Messages.get("fields.all.required")
            new_
        } else {
            val fragment = Fragment.create(Fragment(title, content, style)).get

            // Assign and save tags:
            val tagModels = tags.split(' ').toList.map(name => Tag.findOrCreate(name))
            fragment.tags = tagModels
            Fragment.linkTags(fragment)
            
            Redirect("/fragments")
        }
    }
    
    def show(id: Long) = {
        Template('fragment -> Fragment.findWithTags(id))
    }
    
    def destroy(id: Long) = {
        val sql = Fragment.delete("id={id}").on("id" -> id)
        var result = sql.executeUpdate().fold(
            e => Messages.get("error.generic"),
            c => Messages.get("fragment.deleted")
        )
        flash += "info" -> result
        Redirect("/fragments")
    }
    
}
