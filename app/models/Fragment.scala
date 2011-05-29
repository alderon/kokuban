package models

import play.db.anorm._
import play.db.anorm.SqlParser._
import defaults._
import java.util.{Date}
import scala.collection.SortedMap

case class Fragment(
    id: Pk[Long], 
    title: String, body: String, style: String, created_at: Date
)

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
     * now call this method with the arguments of the apply method. The
     * following statements are equivalent:
     *
     *   Fragment.apply("title", "body")
     *   Fragment("title", "body")
     *
     * http://programming-scala.labs.oreilly.com/ch06.html
     * http://jackcoughonsoftware.blogspot.com/2009/01/deeper-look-at-apply-method-in-scala.html
     */
    def apply(title: String, body: String, style: String) = {
        new Fragment(NotAssigned, title, body, style, new Date())
    }

}
