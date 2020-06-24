package com.rrobledo.minesweeper.services.game

import com.rrobledo.minesweeper.repositories.MineSweeperRepository
import com.rrobledo.minesweeper.repositories.mongodb.collections.Game
import com.typesafe.scalalogging.LazyLogging
import scaldi.{Injectable, Injector}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class DefaultGameService(implicit inj: Injector)
  extends GameService
    with Injectable with LazyLogging {

  val repository : MineSweeperRepository = inject[MineSweeperRepository]
  /**
    * create a new minesweeper game
    *
    * @return a new game.
    */
  override def newGame(game: Game): Future[Game] = {
    repository.addGame(game)
  }
}
