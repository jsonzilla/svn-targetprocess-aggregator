import ApplicationFixture.fixture
import models.DatabaseSuffix
import org.specs2.matcher.Scope
import play.api.mvc.Result

import scala.concurrent.Future

class CommitsApiV1Spec extends ApiSpecification {
  fixture.populate(DatabaseSuffix("COMMITS_"))
  "/api commits" should {
    s"return a list of commits" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/COMMITS_/commits")
      status(result) must equalTo(OK)
      val s = contentAsString(result)
      s must beEqualTo(
        s"""[{"message":"Task#1","timestamp":"2015-09-06T10:11:00.00Z","revision":1,"authorId":1,"id":1},
           |{"message":"Bug#4","timestamp":"2015-12-05T10:11:00.00Z","revision":2,"authorId":2,"id":2},
           |{"message":"Bug#5","timestamp":"2016-01-06T10:11:00.00Z","revision":3,"authorId":3,"id":3}]""".stripMargin).ignoreSpace
    }
    s"return a commit by id" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/COMMITS_/commits/3")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val s = contentAsString(result)
      s must beEqualTo(
        s"""[
             {
               "message": "Bug #5",
               "timestamp":"2016-01-06T10:11:00.00Z",
               "revision": 3,
               "authorId": 3,
               "id": 3
             }
           ]""").ignoreSpace
    }
    s"return a commit by revision number" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/COMMITS_/commits/revision/3")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val s = contentAsString(result)
      s must beEqualTo(
        s"""[{"message":"Bug#5","timestamp":"2016-01-06T10:11:00.00Z","revision":3,"authorId":3,"id":3}]""").ignoreSpace
    }
    s"return a commit by date" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/COMMITS_/commits/2014-01-06/to/2016-01-06")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val s = contentAsString(result)
      s must beEqualTo(
        s"""[{"message":"Task#1","timestamp":"2015-09-06T10:11:00.00Z","revision":1,"authorId":1,"id":1},
           |{"message":"Bug#4","timestamp":"2015-12-05T10:11:00.00Z","revision":2,"authorId":2,"id":2}]""".stripMargin).ignoreSpace
    }
  }
}
