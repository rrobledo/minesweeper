package com.rrobledo.minesweeper.services.game

import com.rrobledo.minesweeper.models.game.{Cell, Game}

import scala.concurrent.Future

trait GameService {

  /**
    * create a new minesweeper game
    * @param game structure containing information about game. For instance, board rows, board columns, mines etc.
    * @return a new game.
    */
  def newGame(game: Game): Future[Game]

  /**
    * Get game
    * @param gameId game identifier.
    * @return a new game.
    */
  def getGame(gameId: String): Future[Game]

  /**
    * Get game cells
    * @param gameId game identifier.
    * @return list of cells.
    */
  def getCells(gameId: String): Future[List[Cell]]

  /**
    * Reveal cells to a game
    * @param gameId game identifier.
    * @param row row number
    * @param col col number
    * @return list cell revealed.
    */
  def revealCell(gameId: String, row: Int, col: Int): Future[List[Cell]]

}
