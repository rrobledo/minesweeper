package com.rrobledo.minesweeper.rest.resources

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import com.rrobledo.minesweeper.models.health.Health
import com.rrobledo.minesweeper.rest.controllers.HealthController
import com.rrobledo.minesweeper.rest.routing.BaseRouter
import com.typesafe.scalalogging.LazyLogging

trait HealthCheckResource extends BaseRouter with LazyLogging {

  val healthController: HealthController

  def healthRoutes: Route = pathPrefix(ResourceNames.Health) {
    pathEnd {
      get {
        onSuccess(healthController.check()) { health : Health =>
          health.healthy match {
            case (true) => complete(health)
            case (false) => complete(InternalServerError)
          }
        }
      }
    }
  }
}