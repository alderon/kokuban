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
        // Create one tag with two tags:
        val fragment = Fragment.create(Fragment("A fragment", "int i=0;", "java"), List("dsl", "weird")).get
        
        // Retrieve the tag from the database:
        val fragmentWithTags = Fragment.findWithTags(fragment.id.get.get)
        
        // Assertions:
        fragmentWithTags.tags.length should be (2)
        fragmentWithTags.tags.map(t => t.name) should contain ("dsl")
        fragmentWithTags.tags.map(t => t.name) should contain ("weird")
    }
    
    it should "not create unique tags" in {
        // Create one tag with two tags:
        val fragment = Fragment.create(Fragment("A fragment", "int i=0;", "java"), List("basic", "basic", "basic", "basic")).get
        
        // Retrieve the tag from the database:
        val fragmentWithTags = Fragment.findWithTags(fragment.id.get.get)
        
        // Assertions:
        fragmentWithTags.tags.length should be (1)
        fragmentWithTags.tags.map(t => t.name) should contain ("basic")
    }
    
    it should "load Fragments without Tags" in {
        val fragment = Fragment.create(Fragment("A fragment", "int i=0;", "java")).get
        val fragmentWithTags = Fragment.findWithTags(fragment.id.get.get)
        fragmentWithTags.tags.length should be (0)
    }
    
    it should "find an existing tag by name" in {
        val existingTag = models.Tag.create(models.Tag("performance")).get
        val tag = models.Tag.findOrCreate("performance")
        existingTag.id should be (tag.id)
    }
    
    it should "create a tag if can't be found" in {
        val tag = models.Tag.findOrCreate("performance")
        tag should not be (null)
        tag.id should not be (NotAssigned)
    }
    
    it should "find multiple Fragments with linked Tags" in {
        // Create two fragments with two tags each.
        createTwoFragmentsWithTwoTags();
        
        // Retrieve the saved fragments from the database.
        val fragmentsWithTags = Fragment.findAllWithTags()
        
        // Assertions:
        fragmentsWithTags.length should be (2)
        
        fragmentsWithTags.head.title should be ("A java fragment")
        fragmentsWithTags.head.tags.map(t => t.name) should contain ("basic")
        fragmentsWithTags.head.tags.map(t => t.name) should contain ("handy")
        
        fragmentsWithTags.last.title should be ("A ruby fragment")
        fragmentsWithTags.last.tags.map(t => t.name) should contain ("handy")
        fragmentsWithTags.last.tags.map(t => t.name) should contain ("math")
    }
    
    it should "find fragments by tag id" in {
        // Create two fragments with two tags each.
        createTwoFragmentsWithTwoTags();
        
        // Retrieve the saved fragments by tag id.
        val overlappingTag = models.Tag.findOrCreate("handy")
        val fragments = Fragment.findAllWithTagsByTagId(overlappingTag.id.get.get);
        
        fragments.length should be (2)
    }
    
    it should "count tags by usage" in {
        // Create two fragments with two tags each.
        createTwoFragmentsWithTwoTags();
        
        // Retrieve tags and counts.
        val tagCounts = models.Tag.allByCount()
        val overlappingTag = models.Tag.findOrCreate("handy")
        val singleTag1 = models.Tag.findOrCreate("basic")
        val singleTag2 = models.Tag.findOrCreate("math")
        
        // Assertions:
        tagCounts.find(count => count._1.id == overlappingTag.id).get._2 should be (2)
        tagCounts.find(count => count._1.id == singleTag1.id).get._2 should be (1)
        tagCounts.find(count => count._1.id == singleTag2.id).get._2 should be (1)
    }
    
    it should "have functioning full text search" in {
        // content from randomtext.com
        Fragment.create(Fragment("write about light orange reading libraries", " scratch soft mice;", "java"), List("foo", "bar"))
        Fragment.create(Fragment("drink to brilliant white coasters", "destroy wise old hanging baskets", "java"), List("bar", "baz"))
        Fragment.create(Fragment("sit on sticky pen tops", "scratch shimmering Christmas hampers", "java"), List("baz", "qux"))
        Fragment.create(Fragment("look at smelly pen tops", "call prominent pencils", "java"), List("qux", "quux"))
        Fragment.create(Fragment("tidy proud reading libraries", "be amazed at web sites", "java"), List("quux", "corge"))
        Fragment.create(Fragment("eat wise old cameras", "scratch soft pencils", "java"), List("corge", "gault"))
        
        val searchResultsPencils = Fragment.search("pencils")
        searchResultsPencils.length should be (2)
        
        val searchResultsShimmering = Fragment.search("shimmering")
        searchResultsShimmering.length should be (1)
    }
    
    private def createTwoFragmentsWithTwoTags() = {
        Fragment.create(Fragment("A java fragment", "int i=0;", "java"), List("basic", "handy")).get
        Fragment.create(Fragment("A ruby fragment", "[1,2,3,4,5].collect { |n| n**n }", "ruby"), List("handy", "math")).get
    }
}