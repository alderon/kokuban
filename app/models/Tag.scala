package models

import play.db.anorm._
import defaults._
import java.util.{Date}

case class Tag(
    id: Pk[Long], 
    name: String,
    created_at: Date
)

object Tag extends Magic[Tag] {
    def apply(name: String) = {
        new Tag(NotAssigned, name, new Date())
    }
}
