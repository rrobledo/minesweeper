package com.rrobledo.minesweeper.rest.controllers

import com.rrobledo.minesweeper.models.game.Game
import scala.concurrent.Future

trait GameController {

  /**
    * Create a new Game
    * @return The game created.
    */
  def newGame(game: Game): Future[Game]

}
