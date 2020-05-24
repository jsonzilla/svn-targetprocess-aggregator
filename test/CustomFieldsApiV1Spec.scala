import ApplicationFixture.fixture
import org.specs2.matcher.Scope
import play.api.mvc.Result

import scala.concurrent.Future

class CustomFieldsApiV1Spec extends ApiSpecification {
  fixture.populate()
  "/api customfields" should {
    s"return a list of customfields" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/customfields")
      status(result) must equalTo(OK)
      val s: String = contentAsString(result)
      s must beEqualTo(
        s"""[{"fieldValue":"internal","field":"Bug","taskId":1,"id":1}]""").ignoreSpace
    }
    s"return a customfields by id" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/customfields/1")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val s: String = contentAsString(result)
      s must beEqualTo(
        s"""[{"fieldValue":"internal","field":"Bug","taskId":1,"id":1}]""").ignoreSpace
    }
    s"return a customfields by field Bug" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/customfields/field/Bug")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val s: String = contentAsString(result)
      s must beEqualTo(
        s"""[{"fieldValue":"internal","field":"Bug","taskId":1,"id":1}]""").ignoreSpace
    }
    s"return a customfields by field Issue must return a bad request" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/customfields/field/Issue")
      status(result) must equalTo(BAD_REQUEST)
    }
  }

}
