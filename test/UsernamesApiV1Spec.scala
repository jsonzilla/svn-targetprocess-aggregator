import org.specs2.matcher.Scope
import play.api.mvc.Result

import scala.concurrent.Future

class UsernamesApiV1Spec extends ApiSpecification {
  "/api usernames" should {
    s"return a list of users" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/usernames")
      status(result) must equalTo(OK)
      val json = contentAsJson(result)

      val name1 = (json \ 0 \ "name").as[String]
      name1 must beEqualTo("User 1")
    }
  }
}