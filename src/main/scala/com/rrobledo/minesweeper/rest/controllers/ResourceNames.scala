package com.rrobledo.minesweeper.rest.controllers

object ResourceNames {
  val Api = "api"
  val ApiVersion = "v1"
  val ApiPrefix = s"/${Api}/${ApiVersion}"

  val Health = "health"
  val Games = "games"
  val Cells = "cells"
  val Rows = "rows"
  val Cols = "cols"
  val Reveal = "reveal"
}
