package com.rrobledo.minesweeper.models.health

object HealthStatus extends Enumeration {
  type HealthStatus = String

  val Green: String = "GREEN"
  val Yellow: String = "YELLOW"
  val Red: String = "RED"

  def fromHealthy(status: HealthStatus, b: Boolean): HealthStatus = if (b) status else Red

  def from(s: String): HealthStatus = {
    s.toUpperCase match {
      case "GREEN" => Green
      case "YELLOW" => Yellow
      case "RED" => Red
      case _ => throw new IllegalArgumentException(s"$s is not a valid value for health status")
    }
  }
}

case class Health(name: String,
                  version: String,
                  build: String,
                  healthy: Boolean,
                  status: String,
                  items: Set[Health] = Set.empty)
