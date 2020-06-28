package com.rrobledo.minesweeper.rest.controllers

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import com.rrobledo.minesweeper.models.health.Health
import com.rrobledo.minesweeper.rest.BaseController
import com.rrobledo.minesweeper.services.health.HealthCheckService
import com.typesafe.scalalogging.LazyLogging
import scaldi.{Injectable, Injector}

trait HealthController
  extends BaseController
    with Injectable
    with LazyLogging {
  implicit val inj: Injector

  private lazy val healthCheckService: HealthCheckService = inject[HealthCheckService]

  def healthRoutes: Route = pathPrefix(ResourceNames.Health) {
    pathEnd {
      get {
        onSuccess(healthCheckService.check()) { health : Health =>
          health.healthy match {
            case (true) => complete(health)
            case (false) => complete(InternalServerError)
          }
        }
      }
    }
  }

  def getHealthRoutes(): Route = corsHandler(healthRoutes)

}
