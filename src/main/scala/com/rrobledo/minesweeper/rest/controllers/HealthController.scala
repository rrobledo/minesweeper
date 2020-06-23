package com.rrobledo.minesweeper.rest.controllers

import com.rrobledo.minesweeper.models.health.Health
import scala.concurrent.Future

trait HealthController {

  /**
    *
    * @return the status of this service and optionally its dependencies
    */
  def check(): Future[Health]

}
