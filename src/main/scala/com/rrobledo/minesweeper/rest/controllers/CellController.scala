package com.rrobledo.minesweeper.rest.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server._
import com.rrobledo.minesweeper.models.game.{Cell, Game}
import com.rrobledo.minesweeper.rest.BaseController
import com.rrobledo.minesweeper.rest.entities.GameCreate
import com.rrobledo.minesweeper.services.game.GameService
import com.typesafe.scalalogging.LazyLogging
import scaldi.{Injectable, Injector}

trait CellController
  extends BaseController
    with Injectable
    with LazyLogging {
  implicit val inj: Injector

  private lazy val gameService: GameService = inject[GameService]

  def cellRoutes: Route = pathPrefix(ResourceNames.Games) {
    headerValueByName("X-User-Id") { userId => {
      pathPrefix(Segment) { gameId => {
        pathPrefix(ResourceNames.Cells) {
          pathEnd {
            get {
              onSuccess(gameService.getCells(gameId)) { cells: List[Cell] =>
                complete(StatusCodes.Accepted, cells)
              }
            }
          } ~
          pathPrefix(ResourceNames.Rows) {
            pathPrefix(IntNumber) { row => {
              pathPrefix(ResourceNames.Cols) {
                pathPrefix(IntNumber) { col => {
                  pathEnd {
                    get {
                      onSuccess(gameService.getCell(gameId, row, col)) { cell: Option[Cell] =>
                        cell match {
                          case Some(cell) => complete(StatusCodes.Accepted, cell)
                          case _ => complete(StatusCodes.NotFound)
                        }
                      }
                    }
                  } ~
                  pathPrefix(ResourceNames.Reveal) {
                    put {
                      onSuccess(gameService.revealCell(gameId, row, col)) { cells: List[Cell] =>
                        complete(StatusCodes.Accepted, cells)
                      }
                    }
                  }
                }}
              }
            }}
          }
        }
      }}
    }}
  }
}
