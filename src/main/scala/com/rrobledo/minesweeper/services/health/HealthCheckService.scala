package com.rrobledo.minesweeper.services.health

import com.rrobledo.minesweeper.models.health.Health
import scala.concurrent.Future

trait HealthCheckService {

  /**
    * performs a full check asking to all dependencies (self check)
    *
    * @return a future of a health instance
    */
  def check(): Future[Health]
}
