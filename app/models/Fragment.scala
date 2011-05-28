package models

import play.db.anorm._
import play.db.anorm.SqlParser._
import defaults._
import java.util.{Date}

case class Fragment(
    id: Pk[Long], 
    title: String, body: String, created_at: Date, updated_at: Date
)
object Fragment extends Magic[Fragment] {

    // Create a new Fragment.
    def apply(title: String, body: String) = {
        val now = new Date()
        new Fragment(NotAssigned, title, body, now, now)
    }

}
