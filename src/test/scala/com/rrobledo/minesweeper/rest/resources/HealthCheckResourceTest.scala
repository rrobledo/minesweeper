package com.rrobledo.minesweeper.rest.resources

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.rrobledo.minesweeper.models.health.{Health, HealthStatus}
import com.rrobledo.minesweeper.rest.controllers.HealthController
import org.mockito.Mockito._
import org.scalatest._
import org.scalatest.mockito.MockitoSugar

import scala.concurrent.Future

class HealthCheckResourceTest extends WordSpec
  with Matchers
  with Inside
  with ScalatestRouteTest
  with MockitoSugar
  with BeforeAndAfterEach
  with BeforeAndAfterAll with HealthCheckResource {

  override val healthController: HealthController = mock[HealthController]

  "Get /health" should {
    "respond with Status 200, and shallow check status" in {
      val healthResponse = green()

      when(healthController.check()).thenReturn(Future.successful(healthResponse))
      Get(s"/${ResourceNames.Health}") ~> healthRoutes ~> check {
        status shouldEqual OK
        responseAs[Health] shouldEqual healthResponse
      }
    }
  }

  private[this] def green(): Health = {
    Health("minesweeper", "1.0.0", "1", healthy = true, HealthStatus.Green)
  }
}
