import play._
import play.test._

import org.scalatest._
import org.scalatest.junit._
import org.scalatest.matchers._

import models._
import play.db.anorm._

class BasicTests extends UnitFlatSpec with ShouldMatchers with BeforeAndAfterEach {

    // Delete all data before each test.
    override def beforeEach() {
        Fixtures.deleteDatabase()
    }
    
    it should "create and retrieve a Fragment" in {
        val body = """public class Foo {
            public static void main(String[] argv) {
                System.out.println("bar");
            }
        }"""

        Fragment.create(Fragment("A piece of Java code", body, "java"))
        Fragment.count().single() should be (1)

        val fragment = Fragment.find("style={style}").on("style" -> "java").first()

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

    it should "create and retrieve a Tag" in {
        models.Tag.create(models.Tag("dsl"))
        models.Tag.count().single() should be (1)
        
        val tag = models.Tag.find().first()
        
        tag should not be (None)
        tag.get.name should be ("dsl")
    }
    
    it should "create link between Fragments and Tags" in {
        var fragment = Fragment.create(Fragment("A fragment", "int i=0;", "java")).get
        var tag1 = models.Tag.create(models.Tag("dsl")).get
        var tag2 = models.Tag.create(models.Tag("weird")).get
        
        fragment.addTag(tag1)
        fragment.addTag(tag2)
        
        Fragment.linkTags(fragment)
        
        var fragmentWithTags = Fragment.findWithTags(fragment.id.get.get)
        
        fragmentWithTags.tags.length should be (2)
        fragmentWithTags.tags.map(t => t.name) should contain ("dsl")
        fragmentWithTags.tags.map(t => t.name) should contain ("weird")
    }

    it should "load Fragments without tags" in {
        var fragment = Fragment.create(Fragment("A fragment", "int i=0;", "java")).get
        var fragmentWithTags = Fragment.findWithTags(fragment.id.get.get)
        fragmentWithTags.tags.length should be (0)
    }
}