package com.rrobledo.minesweeper.services

import akka.actor.ActorSystem
import com.rrobledo.minesweeper.services.health.{DefaultHealthCheckService, HealthCheckService}
import scaldi.{Injectable, Module}

import scala.concurrent.ExecutionContext
import scala.concurrent.forkjoin.ForkJoinPool
import scala.concurrent.ExecutionContext.fromExecutor

class ServicesModule extends Module with Injectable {

  bind[HealthCheckService] as 'self to new DefaultHealthCheckService()

  bind[Set[HealthCheckService]] as 'healthDependencies to {
    Set.empty[HealthCheckService].empty
  }

  bind[ExecutionContext] as 'httpResolver to {
    fromExecutor(new ForkJoinPool(sys.runtime.availableProcessors()))
  }

  bind[ActorSystem] to ActorSystem("minesweeper")
}
