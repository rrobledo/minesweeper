package com.rrobledo.minesweeper.services.game

import com.rrobledo.minesweeper.models.game.Game

import scala.concurrent.Future

trait GameService {

  /**
    * create a new minesweeper game
    * @param game structure containing information about game. For instance, board rows, board columns, mines etc.
    * @return a new game.
    */
  def newGame(game: Game): Future[Game]
}
