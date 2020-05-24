import org.specs2.matcher._
import play.api.mvc.Result

import scala.concurrent.Future
import play.api.libs.json.Json

class UpdateApiV1Spec extends ApiSpecification {

  "/api update" should {
    s"return accepted a update command" in new Scope {
      val result: Future[Result] = routePOST(
        "/api/v1/updateall",Json.obj())
      status(result) must equalTo(ACCEPTED)
    }
    s"return accepted a update demo table command" in new Scope {
      val result: Future[Result] = routePOST(
        "/api/v1/update",Json.obj())
      status(result) must equalTo(ACCEPTED)
    }
    s"return accepted a update demo custom field table" in new Scope {
      val result: Future[Result] = routePOST(
        "/api/v1/update/custom/field",Json.obj())
      status(result) must equalTo(ACCEPTED)
    }
  }

}
