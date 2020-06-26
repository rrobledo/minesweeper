package com.rrobledo.minesweeper.rest

import akka.http.scaladsl.server._
import com.rrobledo.minesweeper.rest.controllers.{GameController, HealthController}
import com.rrobledo.minesweeper.rest.controllers.ResourceNames.{Api, ApiVersion}
import scaldi.{Injectable, Injector}

abstract class RestInterface(implicit val inj: Injector)
  extends Resources
    with Injectable {

  val routes: Route =
    encodeResponse {
      pathPrefix(Api / ApiVersion) {
        healthRoutes ~
        gameRoutes
      }
    }
}

trait Resources
  extends HealthController
     with GameController
