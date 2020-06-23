package com.rrobledo.minesweeper.services.health

import com.rrobledo.minesweeper.models.health.Health
import com.rrobledo.minesweeper.models.health.HealthStatus._
import com.typesafe.scalalogging.LazyLogging
import scaldi.{Injectable, Injector}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.sequence
import scala.concurrent.{Future}
import scala.language.postfixOps

class DefaultHealthCheckService(implicit inj: Injector)
  extends HealthCheckService
    with Injectable with LazyLogging {

  private[this] val name = inject[String]("health.name")
  private[this] val build = inject[String]("health.build")
  private[this] val version = inject[String]("health.version")

  private[this] lazy val healthDependencies: Set[HealthCheckService] = inject[Set[HealthCheckService]]("healthDependencies")

  override def check(): Future[Health] = {
    for {
      self <- innerSelf()
      dependencies <- sequence(healthDependencies map (_.check()))
    } yield {
      val areAllHealthy = self.healthy && (dependencies forall (dh => dh.healthy))
      self.copy(healthy = areAllHealthy, items = dependencies, status = if (areAllHealthy) self.status else Red)
    }
  }

  private def innerSelf(): Future[Health] = Future {
    Health(name, version, build, healthy = true, Green)
  }
}
