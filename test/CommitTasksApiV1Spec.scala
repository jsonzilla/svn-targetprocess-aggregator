import ApplicationFixture.fixture
import org.specs2.matcher.Scope
import play.api.mvc.Result

import scala.concurrent.Future

class CommitTasksApiV1Spec extends ApiSpecification {
  fixture.populate()
  "/api commit tastks" should {
    s"return a list of commit tasks" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/committasks")
      status(result) must equalTo(OK)
      val json = contentAsJson(result)
      val taskId = (json \ 1 \ "taskId").as[Int]
      taskId must beEqualTo(2)
    }
    s"return a commit tasks by id" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/committasks/3")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val json = contentAsJson(result)
      val taskId = (json \ 0 \ "taskId").as[Int]
      taskId must beEqualTo(3)
    }
  }
}
