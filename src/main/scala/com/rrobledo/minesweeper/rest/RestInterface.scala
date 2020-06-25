package com.rrobledo.minesweeper.rest

import akka.http.scaladsl.server._
import com.rrobledo.minesweeper.rest.controllers.{GameController, HealthController}
import com.rrobledo.minesweeper.rest.resources.{GameResource, HealthCheckResource}
import com.rrobledo.minesweeper.rest.resources.ResourceNames.{Api, ApiVersion}
import scaldi.{Injectable, Injector}

abstract class RestInterface(implicit inj: Injector)
  extends Resources
    with Injectable {

  // dependency injection of services
  lazy val healthController: HealthController = inject[HealthController]
  lazy val gameController: GameController = inject[GameController]

  val routes: Route =
    encodeResponse {
      pathPrefix(Api / ApiVersion) {
        healthRoutes ~
        gameRoutes
      }
    }
}

trait Resources
  extends HealthCheckResource
     with GameResource
