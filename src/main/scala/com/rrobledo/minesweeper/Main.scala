package com.rrobledo.minesweeper

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.rrobledo.minesweeper.repositories.RepoModule
import com.rrobledo.minesweeper.rest.RestInterface
import com.rrobledo.minesweeper.rest.controllers.ControllersModule
import com.rrobledo.minesweeper.services.ServicesModule
import com.rrobledo.minesweeper.services.monitoring.Monitoring
import com.rrobledo.minesweeper.utils.UtilsModule
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import scaldi.akka.AkkaInjectable
import scaldi.{MutableInjectorAggregation, TypesafeConfigInjector}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.language.postfixOps
import io.prometheus.client.hotspot.DefaultExports

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App with LazyLogging with AkkaInjectable {

  implicit val config: Config = ConfigFactory.load()
  implicit val applicationModule: MutableInjectorAggregation =
    new UtilsModule ::
      new ServicesModule ::
      new ControllersModule ::
      new RepoModule ::
      TypesafeConfigInjector(config)

  initRestAPI()
  initMetrics()
  initMonitoring()

  private def initRestAPI(): Unit = {
    new RestInterface() {
      private val host = config.getString("http.host")
      private val port = config.getInt("http.port")

      implicit val system: ActorSystem = inject[ActorSystem]
      implicit val executionContext: ExecutionContextExecutor = system.dispatcher
      implicit val timeout: Timeout = Timeout(42 seconds)
      Http().bindAndHandle(routes, host, port) map { binding => logArt(binding) } recover {
        case ex =>
          logger.error(s"REST interface could not bind to $host:l$port", ex)
          applicationModule.destroy()
          system.terminate()
          sys.exit(-1)
      }
    }
  }

  private def initMetrics(): Unit = {
    DefaultExports.initialize()
  }

  private def initMonitoring(): Unit = {
    implicit val monitoring: Monitoring = inject[Monitoring]
    monitoring.start()
  }

  private def logArt(binding: Http.ServerBinding): Unit = {
    logger.info(
      """
        |           _
        |          (_)
        | _ __ ___  _ _ __   ___  _____      _____  ___ _ __   ___ _ __
        || '_ ` _ \| | '_ \ / _ \/ __\ \ /\ / / _ \/ _ \ '_ \ / _ \ '__|
        || | | | | | | | | |  __/\__ \\ V  V /  __/  __/ |_) |  __/ |
        ||_| |_| |_|_|_| |_|\___||___/ \_/\_/ \___|\___| .__/ \___|_|
        |                                              | |
        |                                              |_|
        | %s
        |""".stripMargin
        format binding.localAddress)
  }
}
