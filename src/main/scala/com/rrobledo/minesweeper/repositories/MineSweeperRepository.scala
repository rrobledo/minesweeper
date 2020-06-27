package com.rrobledo.minesweeper.repositories

import com.rrobledo.minesweeper.models.game.{Cell, Game}

import scala.concurrent.Future

trait MineSweeperRepository {
  def addGame(game: Game) : Future[Game]
  def getGame(gameId: String) : Future[Option[Game]]
  def getGamesByUser(userId: String) : Future[Seq[Game]]
  def getPlayingGames() : Future[Seq[Game]]
  def updateGameStatus(gameId: String, status: String) : Future[Unit]

  def addCells(cells: List[Cell]) : Future[Unit]
  def updateCellRevealed(cell: Cell, revealed: Boolean) : Future[Unit]
  def getCellByGameRowCol(gameId: String, row: Int, col: Int) : Future[Option[Cell]]
  def getCells(gameId: String) : Future[Seq[Cell]]

}
