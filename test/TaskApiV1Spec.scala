import org.specs2.matcher.Scope
import play.api.mvc.Result

import scala.concurrent.Future

class TaskApiV1Spec extends ApiSpecification {
  ApplicationFixture.initializeWithData()

  "/api task" should {
    s"return a list of tasks" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/tasks")
      status(result) must equalTo(OK)
      val s: String = contentAsString(result)
      s must beEqualTo(
        s"""[
             {
               "typeTask": "Task",
               "typeTaskId": 5,
               "timeSpend": 20,
               "taskId": 1,
               "id": 1
             },
             {
               "typeTask": "Task",
               "typeTaskId": 5,
               "timeSpend": 20,
               "taskId": 2,
               "id": 2
             },
             {
               "typeTask": "Bug",
               "typeTaskId": 8,
               "timeSpend": 20,
               "parentId": 1,
               "taskId": 3,
               "id": 3
             },
             {
               "typeTask": "Bug",
               "typeTaskId": 8,
               "timeSpend": 20,
               "parentId": 2,
               "taskId": 4,
               "id": 4
             },
             {
               "typeTask": "Bug",
               "typeTaskId": 8,
               "timeSpend": 20,
               "parentId": 2,
               "taskId": 5,
               "id": 5
             }
           ]""").ignoreSpace
    }
    s"return a task by id" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/tasks/3")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val json = contentAsJson(result)
      val taskType = (json \ 0 \ "typeTask").as[String]
      taskType must beEqualTo("Bug")
    }
    s"return a task with task_id equal two" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/tasks/task/2")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val json = contentAsJson(result)
      val taskType = (json \ 0 \ "typeTask").as[String]
      taskType must beEqualTo("Task")
    }
    s"return a list of nested tasks by id of parent task id equal two" in new Scope {
      val result: Future[Result] = routeGET(
        "/api/v1/tasks/nested/2")
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val json = contentAsJson(result)
      val taskType = (json \ 1 \ "typeTask").as[String]
      taskType must beEqualTo("Bug")
    }
  }
}
