package com.rrobledo.minesweeper.rest.controllers

import com.rrobledo.minesweeper.models.game.Game
import com.rrobledo.minesweeper.services.game.GameService
import com.typesafe.scalalogging.LazyLogging
import scaldi.{Injectable, Injector}

import scala.concurrent.Future

class DefaultGameController(implicit inj: Injector)
  extends GameController
    with Injectable
    with LazyLogging {

  private lazy val gameService: GameService = inject[GameService]

  def newGame(game: Game): Future[Game] = {
    gameService.newGame(game)
  }
}
