package models

import play.db.anorm._
import defaults._

case class Tag(
    id: Pk[Long], 
    name: String,
    created_at: Date
)

object Tag extends Magic[Tag]
