package controllers

import play.mvc._

import models._

object Tags extends Controller {
    
    def show(id: Long) = Template("/Fragments/index.html", 'fragments -> Fragment.findAllWithTagsByTagId(id))
    
}
