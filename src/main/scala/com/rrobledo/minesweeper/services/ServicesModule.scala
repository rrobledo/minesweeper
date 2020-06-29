package com.rrobledo.minesweeper.services

import akka.actor.ActorSystem
import com.rrobledo.minesweeper.services.game.{DefaultGameService, GameService}
import com.rrobledo.minesweeper.services.health.{DefaultHealthCheckService, HealthCheckService}
import com.rrobledo.minesweeper.services.monitoring.Monitoring
import scaldi.{Injectable, Module}

import scala.concurrent.ExecutionContext

class ServicesModule(implicit ec: ExecutionContext) extends Module with Injectable {

  bind[HealthCheckService] to new DefaultHealthCheckService()
  bind[GameService] to new DefaultGameService()
  bind[Monitoring] to new Monitoring()

  bind[Set[HealthCheckService]] as 'healthDependencies to {
    Set.empty[HealthCheckService].empty
  }
}
