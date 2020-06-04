import org.specs2.matcher.Scope
import play.api.mvc.Result

import scala.concurrent.Future

class AuthorsApiV1Spec extends ApiSpecification {
  ApplicationFixture.initializeWithData()

  "/api author" should {
    s"return a list of authors" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/authors")
      status(result) must equalTo(OK)
      val s: String = contentAsString(result)
      s must beEqualTo(
        s"""[
              {
                "author": "john",
                "id": 1
              },
              {
                "author": "philips",
                "id": 2
              },
              {
                "author": "thomas",
                "id": 3
              }
            ]""").ignoreSpace
    }
    s"return a author by id" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/authors/3")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val s: String = contentAsString(result)
      s must beEqualTo(
        s"""{
              "author": "thomas",
              "id": 3
            }""").ignoreSpace
    }

    s"return a file path and a counter for all tasks of type bug for the author" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/authors/bugs/thomas")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val s: String = contentAsString(result)
      s must beEqualTo(
        s"""[["${ExtractorFixture.file1}",1]]""").ignoreSpace
    }
    s"return a files paths for the author" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/authors/bugs/thomas")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val s: String = contentAsString(result)
      s must beEqualTo(
        s"""[["${ExtractorFixture.file1}",1]]""").ignoreSpace
    }
  }
}
