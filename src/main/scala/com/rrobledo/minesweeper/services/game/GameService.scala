package com.rrobledo.minesweeper.services.game

import com.rrobledo.minesweeper.repositories.mongodb.collections.Game

import scala.concurrent.Future

trait GameService {

  /**
    * create a new minesweeper game
    *
    * @return a new game.
    */
  def newGame(game: Game): Future[Game]
}
