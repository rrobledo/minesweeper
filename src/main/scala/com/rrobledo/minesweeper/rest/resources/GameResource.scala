package com.rrobledo.minesweeper.rest.resources

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server._
import com.rrobledo.minesweeper.models.game.Game
import com.rrobledo.minesweeper.rest.controllers.GameController
import com.rrobledo.minesweeper.rest.routing.BaseRouter
import com.typesafe.scalalogging.LazyLogging

trait GameResource extends BaseRouter with LazyLogging {

  val gameController: GameController

  def gameRoutes: Route = pathPrefix(ResourceNames.Games) {
    (post & pathEndOrSingleSlash) {
      (entity(as[Game])) { game =>
        onSuccess(gameController.newGame(game)) { game =>
          respondWithHeader(Location(s"${pathPrefix(ResourceNames.Games)}/${game._id}")) {
            complete(StatusCodes.Created)
          }
        }
      }
    }
  }
}