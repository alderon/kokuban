# INTRODUCTION #

Kokuban (黒板) is an online code-sharing tool implemented using the Play Scala
Framework.

* 黒 = Black
* 板 = Board

## FUNCTIONALITY ##

Goals:

* Share code fragments
* Tag code fragments, one or more times
* Code fragments can be of any type (Java, HTML, CSS, more.)
* Search code fragments by tags
* Search code fragments by content

## MODELS ##

Let's start off with the simplest possible model to capture the above goals.

A Fragment holds the code fragment and has a mandatory title. For now, every
fragement is of one type.

* Fragment
  * title
  * body
  * type

A Tag is simply that:

* Tag
  * name

There is a many-to-many relationship between these models; fragments can have
any number of tags, and tags can be assigned to any number of fragments.

## SCREENS ##

There are X screens:

* An index page which shows a list of code fragments.
  * Default: sorted by date, newest first.
  * Links are titles, also shows type and tags.
  * This is the landing page of the website.
* A page showing one code fragment.
  * Shows the title and body.
  * Code is highlighted depending by its type (if possible).
  * Shows the tags assigned to it.
* A page to add a new code fragment.
  * Title and body are mandatory.
  * Type and tags are optional.
* A page to list code fragments by tag.
  * The same view as the main index page.
* A page to list code fragements by search term.
  * The same view as the main index page.

Considerations:

* Every screen shows a list of tags, ordered by use.
* Every screen shows a search box to search code fragments.
* A list shows a maximum of 25 code fragment titles.
* A tag is always clickable.

## CHOICES MADE ##

This sections describes some of the choices made during the implementation of
this project.

### Database layer ###

Play Scala offers two ways to interact with a database:

1. JPA. Uses Hibernate as the unlying implementation, and is the default in
   the regular Play Framework.
2. Anorm. To quote:

   > Anorm is simplification of JDBC with a minimal interface reusing
   > pre-existing Scala interfaces (collections, pattern-matching, parsers
   > combinators).

   Using Anorm would mean writing SQL statements.

Going back to writing SQL statements feels like stepping back in time.

To quote the Play Scala manual on the subject:

> It is still possible to use JPA from a Play Scala application, but it is
> probably not the best way, and it should be considered as legacy and
> deprecated.

**Choice made**: Let's use Anorm since it's recommended, and the more "Scala"
thing to do.

### Full text search ###

Full text search is not natively supported by Play-Scala, unless JPA/Hibernate
is used as ORM, in which case Hibernate-search can be used. Since this project
uses Anorm, database fulltext search is more logical option.

**Choice made**: Database fulltext search.

## LINKS ##

Useful links found:

* Play:
  * http://www.playframework.org/documentation/1.2
  * http://scala.playframework.org/documentation/scala-0.9/home
  * http://scala.playframework.org/documentation/scala-0.9/guide1
  * http://www.playframework.org/documentation/1.2.1/validation
  * http://scala.playframework.org/documentation/scala-0.9/anorm
* Scala:
  * http://programming-scala.labs.oreilly.com/index.html
  * http://www.artima.com/scalazine/articles/steps.html
  * http://www.codecommit.com/blog/scala/scala-for-java-refugees-part-3
  * http://www.scala-lang.org/docu/files/ScalaByExample.pdf
  * http://www.codecommit.com/blog/scala/scala-collections-for-the-easily-bored-part-2
