package models

import play.db.anorm._
import defaults._
import java.util.{Date}
import play.db.anorm.SqlParser._

case class Tag(
    id: Pk[Long], 
    name: String,
    created_at: Date
) {
    override def equals(that: Any) = that match {
        case other: Tag => other.name equals name
        case _ => false
    }
}

object Tag extends Magic[Tag] {
    def apply(name: String) = {
        new Tag(NotAssigned, name, new Date())
    }
    
    def findOrCreate(name: String) = {
        find("name={name}").on("name" -> name).as(Tag*) match {
            case Nil => Tag.create(Tag(name)).get
            case head :: tail => head
        }
    }
    
    def all() = {
        Tag.find().as(Tag *)
    }
    
    // Roel's note: explicitly list all Tag fields, needed during testing,
    // and actually make the GROUP BY statement SQL compliant.
    def allByCount():List[Tag~Long] = {
        SQL(
            """
            SELECT t.id, t.name, t.created_at, count(ft.fragment_id) as tagcount
            FROM Tag t
            JOIN FragmentTag ft ON ft.tag_id = t.id
            GROUP BY t.id, t.name, t.created_at
            ORDER BY tagcount DESC
            """
        )
        .as( Tag ~< long("tagcount") * )
    }
}
