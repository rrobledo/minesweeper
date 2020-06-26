package com.rrobledo.minesweeper.rest.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server._
import com.rrobledo.minesweeper.models.game.Game
import com.rrobledo.minesweeper.rest.BaseController
import com.rrobledo.minesweeper.rest.entities.GameCreate
import com.rrobledo.minesweeper.services.game.GameService
import com.typesafe.scalalogging.LazyLogging
import scaldi.{Injectable, Injector}

trait GameController
  extends BaseController
    with Injectable
    with LazyLogging {
  implicit val inj: Injector

  private lazy val gameService: GameService = inject[GameService]

  def gameRoutes: Route = pathPrefix(ResourceNames.Games) {
    (post & pathEndOrSingleSlash) {
      (entity(as[GameCreate])) { game =>
        onSuccess(gameService.newGame(game.toGame())) { game : Game =>
          respondWithHeader(Location(s"${ResourceNames.ApiPrefix}/${ResourceNames.Games}/${game._id}")) {
            complete(StatusCodes.Created, game)
          }
        }
      }
    }
  }
}
