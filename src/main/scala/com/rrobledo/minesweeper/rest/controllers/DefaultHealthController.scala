package com.rrobledo.minesweeper.rest.controllers

import com.rrobledo.minesweeper.models.health.Health
import com.rrobledo.minesweeper.services.health.HealthCheckService
import com.typesafe.scalalogging.LazyLogging
import scaldi.{Injectable, Injector}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultHealthController(implicit inj: Injector)
  extends HealthController
    with Injectable
    with LazyLogging {

  private lazy val healthCheckService: HealthCheckService = inject[HealthCheckService]('self)

  override def check(): Future[Health] = {
    logger.underlying.info(s"Requested HealthCheck")

    val fHealth: Future[Health] = healthCheckService.check()
    fHealth
  }
}
