package com.rrobledo.minesweeper.rest

import akka.http.scaladsl.server._
import com.rrobledo.minesweeper.rest.controllers.{CellController, GameController, HealthController}
import com.rrobledo.minesweeper.rest.controllers.ResourceNames.{Api, ApiVersion}
import scaldi.{Injectable, Injector}

abstract class RestInterface(implicit val inj: Injector)
  extends Resources
    with Injectable {

  val routes: Route =
    encodeResponse {
      pathPrefix(Api / ApiVersion) {
        getHealthRoutes() ~
        getGamesRoutes() ~
        getCellRoutes()
      }
    }
}

trait Resources
  extends HealthController
     with GameController
     with CellController
