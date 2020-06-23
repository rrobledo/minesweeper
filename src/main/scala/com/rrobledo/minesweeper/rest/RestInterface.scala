package com.rrobledo.minesweeper.rest

import akka.http.scaladsl.server._
import com.rrobledo.minesweeper.rest.controllers.HealthController
import com.rrobledo.minesweeper.rest.resources.{HealthCheckResource}
import com.rrobledo.minesweeper.rest.resources.ResourceNames.{Api, ApiVersion}
import scaldi.{Injectable, Injector}

abstract class RestInterface(implicit inj: Injector)
  extends Resources
    with Injectable {

  // dependency injection of services
  lazy val healthController: HealthController = inject[HealthController]

  val routes: Route =
    encodeResponse {
      pathPrefix(Api / ApiVersion) {
        healthRoutes
      }
    }
}

trait Resources
  extends HealthCheckResource
