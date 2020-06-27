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
    * Get user's game
    * @param userId userId.
    * @return list of games.
    */
  def getGamesByUserId(userId: String): Future[List[Game]]

  /**
    * Get game cells
    * @param gameId game identifier.
    * @return list of cells.
    */
  def getCells(gameId: String): Future[List[Cell]]

  /**
    * Get game cell
    * @param gameId game identifier.
    * @param row row.
    * @param col col.
    * @return cell.
    */
  def getCell(gameId: String, row: Int, col: Int): Future[Option[Cell]]

  /**
    * Reveal cells to a game
    * @param gameId game identifier.
    * @param row row number
    * @param col col number
    * @return list cell revealed.
    */
  def revealCell(gameId: String, row: Int, col: Int): Future[List[Cell]]

  /**
    * Validate time limit of playing games, in case that analized game reach limit it shall be GAME_OVER_LIMIT
    */
  def validateTimeLimitPlayingGaming(): Future[Unit]

}
