package com.rrobledo.minesweeper.repositories

import com.rrobledo.minesweeper.repositories.mongodb.collections.Game

import scala.concurrent.Future

trait MineSweeperRepository {
  def addGame(game: Game) : Future[Game]
}
