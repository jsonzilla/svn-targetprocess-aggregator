import ApplicationFixture.fixture
import org.specs2.matcher.Scope
import play.api.mvc.Result

import scala.concurrent.Future

class CommitsApiV1Spec extends ApiSpecification {
  fixture.populate()
  "/api commits" should {
    s"return a list of commits" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/commits")
      status(result) must equalTo(OK)
      val json = contentAsJson(result)
      val message = (json \ 2 \ "message").as[String]
      message must beEqualTo("Bug #5")
    }
    s"return a commit by id" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/commits/3")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val json = contentAsJson(result)
      val message = (json \ 0 \ "message").as[String]
      message must beEqualTo("Bug #5")
    }
    s"return a commit by revision number" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/commits/revision/3")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val json = contentAsJson(result)
      val message = (json \ 0 \ "message").as[String]
      message must beEqualTo("Bug #5")
    }
    s"return a commit by date" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/commits/2014-01-06/to/2016-01-06")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val json = contentAsJson(result)
      val message = (json \ 1 \ "message").as[String]
      message must beEqualTo("Bug #4")
    }
    s"return a commit files counter custom field by date" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/commits/custom/internal/2014-01-06/to/2017-01-06")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val s: String = contentAsString(result)
      s must beEqualTo(
        s"""[["${ExtractorFixture.file3}",1],["${ExtractorFixture.file2}",2],["${ExtractorFixture.file1}",2]]""".stripMargin).ignoreSpace
    }
    s"return a commit files csv counter custom field by date" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/commits/custom/internal/2014-01-06/to/2017-01-06/csv")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "text/plain")
      val s: String = contentAsString(result)
      s must beEqualTo(
        s"""2,"${ExtractorFixture.file1}"2,"${ExtractorFixture.file2}"1,"${ExtractorFixture.file3}"""".stripMargin).ignoreSpace
    }
    s"return a error with a invalid date" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/commits/custom/internal/2014/to/2017")
      status(result) must equalTo(BAD_REQUEST)
    }
  }
}
