import play._
import play.test._

import org.scalatest._
import org.scalatest.junit._
import org.scalatest.matchers._

import models._
import play.db.anorm._

class BasicTests extends UnitFlatSpec with ShouldMatchers {
    
    it should "create and retrieve a Fragment" in {
        val body = """public class Foo {
            public static void main(String[] argv) {
                System.out.println("bar");
            }
        }"""

        Fragment.create(Fragment("A piece of Java code", body, "java"))

        val fragment = Fragment.find(
            "style={style}").on("style" -> "java"
        ).first()

        fragment should not be (None)
        fragment.get.title should be ("A piece of Java code")
        fragment.get.body should be (body)
        fragment.get.style should be ("java")
    }

    /**
     * Roel's note: It seems that validations on Anorm 'Magic' models are
     * not honored.
     */
    it should "not create a Fragment without a title" in (pending)

    it should "only accept the given code styles" in {
        // as found in example code:
        val error = evaluating {
            Fragment.create(Fragment("Spam title", "Spammy content", "spam"))
        } should produce [IllegalArgumentException]        
        
        // other way of writing the same thing:
        intercept[IllegalArgumentException] {
            Fragment.create(Fragment("Spam title", "Spammy content", "spam"))
        }
    }
    
}