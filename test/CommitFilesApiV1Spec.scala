import ApplicationFixture.fixture
import org.specs2.matcher.Scope
import play.api.mvc.Result

import scala.concurrent.Future

class CommitFilesApiV1Spec extends ApiSpecification {
  fixture.populate()

  "/api commits files" should {
    s"return a list of commits files" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/commitfiles")
      status(result) must equalTo(OK)
      val json = contentAsJson(result)
      val typeModification = (json \ 2 \ "typeModification").as[Int]
      typeModification must beEqualTo(65)
    }
    s"return a commit files by id" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/commitfiles/3")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val json = contentAsJson(result)
      val typeModification = (json \ 0 \ "typeModification").as[Int]
      typeModification must beEqualTo(65)
    }
  }
}
