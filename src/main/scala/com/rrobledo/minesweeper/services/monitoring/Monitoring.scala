package com.rrobledo.minesweeper.services.monitoring

import akka.actor.ActorSystem
import scala.util.{Success, Failure}
import com.rrobledo.minesweeper.services.game.GameService
import com.typesafe.scalalogging.LazyLogging
import scaldi.{Injectable, Injector}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class Monitoring(implicit val inj: Injector, implicit val ec: ExecutionContext)
  extends Injectable
    with LazyLogging {
  val gameService : GameService = inject[GameService]

  def start() : Unit = {
    val system = ActorSystem("Monitoring")
    system.scheduler.schedule(initialDelay = 10 seconds, interval = 10 seconds) {
      gameService.validateTimeLimitPlayingGaming() onComplete {
        case Success(_) => Unit
        case Failure(ex) => {
          logger.debug(ex.toString)
        }
      }
    }
  }

}
