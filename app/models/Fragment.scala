package models

import play.db.anorm._
import play.db.anorm.SqlParser._
import play.data.validation.Annotations._
import scala.collection.SortedMap
import defaults._

import java.util.{Date}

case class Fragment(
    id: Pk[Long], 
    @Required title: String, // Roel's note: doesn't seem to work, untestable?
    body: String,
    style: String,
    created_at: Date
) {
    var tags = List[Tag]()
    
    def addTag(tag: Tag) = {
        tags = tag :: tags
    }
}

/**
 * Roel's note: In Scala an 'object' is a singleton.
 * A quote from "Programming Scala":
 *   Scala uses objects for situations where other languages would use
 *   "class-level" members, like statics in Java.
 */
object Fragment extends Magic[Fragment] {
    val STYLES = SortedMap(
        "actionscript3"     -> "ActionScript3",
        "bash/shell"        -> "Bash/shell",
        "coldfusion"        -> "ColdFusion",
        "csharp"            -> "C#",
        "cpp"               -> "C/C++",
        "css"               -> "CSS",
        "delphi"            -> "Delphi",
        "diff"              -> "Diff",
        "erlang"            -> "Erlang",
        "groovy"            -> "Groovy",
        "html"              -> "HTML",
        "java"              -> "Java",
        "javascript"        -> "JavaScript",
        "javafx"            -> "JavaFX",
        "perl"              -> "Perl",
        "pascal"            -> "Pascal",
        "php"               -> "PHP",
        "plain"             -> "Plain Text",
        "powershell"        -> "PowerShell",
        "python"            -> "Python",
        "ruby"              -> "Ruby",
        "scala"             -> "Scala",
        "sql"               -> "SQL",
        "vb"                -> "Visual Basic",
        "xml"               -> "XML/XSLT"
    )
    
    /**
     * Create a new Fragment.
     *
     * Roel's note: This is using the "apply method" feature in Scala. We can
     * now call this class with the arguments of the apply method. The
     * following statements are equivalent:
     *
     *   Fragment.apply("title", "body")
     *   Fragment("title", "body")
     *
     * http://programming-scala.labs.oreilly.com/ch06.html
     * http://jackcoughonsoftware.blogspot.com/2009/01/deeper-look-at-apply-method-in-scala.html
     */
    def apply(@Required title: String, body: String, style: String) = {
        if (!STYLES.contains(style)) {
            throw new IllegalArgumentException("Given fragment style not allowed.");
        }
        new Fragment(NotAssigned, title, body, style, new Date())
    }
    
    /**
     * Add links between Fragments and Tags, it does not delete existing ones.
     * Note: This only adds links, doesn't delete existing links.
     */
    def linkTags(fragment: Fragment) {
        // Roel's note: Batch insert doesn't seem to work in test..?
        // var sql = SQL("insert into FragmentTag (fragment_id, tag_id) values( {fragmentId}, {tagId})").asBatch
        // fragment.tags.foreach( tag => sql = sql.addBatch("fragmentId" -> fragment.id.get.get, "tagId" -> tag.id.get.get))
        // sql.execute();
        
        fragment.tags.foreach( tag =>
            SQL("INSERT INTO FragmentTag (fragment_id, tag_id, created_at) VALUES( {fragmentId}, {tagId}, NOW())")
            .on("fragmentId" -> fragment.id.get.get, "tagId" -> tag.id.get.get)
            .execute()
        )
    }
    
    val sqlQueryBase = 
        """
        SELECT f.*, t.* FROM Fragment f
        LEFT OUTER JOIN FragmentTag ft ON ft.fragment_id=f.id
        LEFT OUTER JOIN Tag t ON t.id = ft.tag_id
        """
    
    
    def findWithTags(fragmentId: Long):Fragment = {
        val fragment~tags = SQL(
            sqlQueryBase +
            """
            WHERE f.id = {id}
            """
        )
        .on("id" -> fragmentId)
        .as(Fragment ~< Fragment.span(Tag*))
        
        fragment.tags = tags
        fragment
    }
    
    def findAllWithTags():List[Fragment] = {
        var fragmentsAndTags:List[(Fragment,List[Tag])] = SQL(
            sqlQueryBase
        )
        .as( Fragment ~< Fragment.span(Tag*) ^^ flatten * )
        
        fragmentsAndTags.map { arg => 
            arg._1.tags = arg._2
            arg._1
        }
    }

    // This query is ready for optimization.
    def findAllWithTagsByTagId(tagId:Long):List[Fragment] = {
        var fragmentsAndTags:List[(Fragment,List[Tag])] = SQL(
            sqlQueryBase +
            """
            WHERE f.id IN (
              SELECT f.id FROM Fragment f
              LEFT OUTER JOIN FragmentTag ft ON ft.fragment_id=f.id
              LEFT OUTER JOIN Tag t ON t.id = ft.tag_id
              WHERE t.id={id}
            )
            """
        )
        .on("id" -> tagId)
        .as( Fragment ~< Fragment.span(Tag*) ^^ flatten * )
        
        fragmentsAndTags.map { arg => 
            arg._1.tags = arg._2
            arg._1
        }
    }
    
    /**
     * A note about full text search:
     * "If a word is present in more than 50% of the rows it will have a
     * weight of zero. This has advantages on large datasets, but can make 
     * testing difficult on small ones."
     */
    def search(term:String):List[Fragment~Long] = {
        SQL(
            """
                SELECT f.*, CAST( (MATCH (f.title, f.body) AGAINST({term}) * 1000) AS SIGNED INTEGER) as score
                FROM Fragment f
                WHERE MATCH (f.title, f.body) AGAINST({term})
            """
        )
        .on("term" -> term)
        .as( Fragment ~< long("score") * )
    }

}
