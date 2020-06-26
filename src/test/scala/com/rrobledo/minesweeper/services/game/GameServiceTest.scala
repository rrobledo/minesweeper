package com.rrobledo.minesweeper.services.game

import com.rrobledo.minesweeper.models.game.{Game, GameOptions}
import com.rrobledo.minesweeper.repositories.RepoModule
import com.rrobledo.minesweeper.rest.entities.GameCreate
import com.rrobledo.minesweeper.services.ServicesModule
import com.rrobledo.minesweeper.utils.UtilsModule
import com.typesafe.config.{Config, ConfigFactory}
import org.mockito.Mockito.{verifyZeroInteractions, when}
import scala.async.Async.{async, await}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{FunSuiteLike, Inside, Matchers}
import scaldi.{Module, MutableInjectorAggregation, TypesafeConfigInjector}

import scala.concurrent.ExecutionContext.Implicits.global

class GameServiceTest extends FunSuiteLike
  with MockitoSugar
  with Matchers
  with Inside
  with ScalaFutures {

  //noinspection ScalaStyle
  implicit val defaultPatience: PatienceConfig = PatienceConfig(timeout = Span(30, Seconds), interval = Span(5000, Millis))

  implicit val config: Config = ConfigFactory.load()
  private implicit val testModule: MutableInjectorAggregation =
    new Module {
      } ::
    new UtilsModule ::
    new ServicesModule ::
    new RepoModule ::
    TypesafeConfigInjector(config)

  lazy val service: GameService = new DefaultGameService() {
    override def generateMineIndexes(rows: Int, cols: Int, mines: Int) : List[Int] = {
      List(0, 2)
    }
  }

  test("new game end game over") {
    val gameOptions = GameOptions(rows = 3, cols = 3, mines = 2, limitTime = 60)
    val game = GameCreate(userId = "raul.osvaldo.robledo@gmail.com", gameOptions).toGame()

    whenReady(service.newGame(game)) { game =>
      whenReady(service.getGame(game._id)) { gameStored =>
        game.userId shouldEqual gameStored.userId
        game.options.cols shouldEqual gameStored.options.cols
        game.options.rows shouldEqual gameStored.options.rows
        game.options.mines shouldEqual gameStored.options.mines
        game.options.limitTime shouldEqual gameStored.options.limitTime
      }
      whenReady(service.getCells(game._id)) { cells =>
        cells.size shouldEqual 9
        cells.filter(cell => cell.isMine).size shouldEqual 2

        cells.find(cell => cell.row == 1 && cell.col == 1) match {
          case Some(cell) => {
            cell.neighborCount shouldEqual -1
            cell.isMine shouldEqual true
          }
          case _ => fail()
        }

        cells.find(cell => cell.row == 2 && cell.col == 2) match {
          case Some(cell) => {
            cell.neighborCount shouldEqual 2
            cell.isMine shouldEqual false
          }
          case _ => fail()
        }

        cells.find(cell => cell.row == 2 && cell.col == 3) match {
          case Some(cell) => {
            cell.neighborCount shouldEqual 1
            cell.isMine shouldEqual false
          }
          case _ => fail()
        }

        cells.find(cell => cell.row == 3 && cell.col == 3) match {
          case Some(cell) => {
            cell.neighborCount shouldEqual 0
            cell.isMine shouldEqual false
          }
          case _ => fail()
        }
      }
      whenReady(service.revealCell(game._id, 2, 3)) { revealedCells =>
        revealedCells.size shouldEqual 1
      }
      whenReady(service.revealCell(game._id, 3, 3)) { revealedCells =>
        revealedCells.size shouldEqual 3
      }
      whenReady(service.getCells(game._id)) { cells =>
        cells.filter(cell => cell.revealed).size shouldEqual 4
      }

      whenReady(service.getGame(game._id)) { gameStored =>
        gameStored.status shouldEqual "PLAYING"
      }

      whenReady(service.revealCell(game._id, 1, 1)) { revealedCells =>
        revealedCells.size shouldEqual 5
      }

      whenReady(service.getCells(game._id)) { cells =>
        cells.filter(cell => cell.revealed).size shouldEqual 9
      }

      whenReady(service.getGame(game._id)) { gameStored =>
        gameStored.status shouldEqual "GAME_OVER_MINE_FOUND"
      }
    }
  }

  test("new game end success") {
    val gameOptions = GameOptions(rows = 3, cols = 3, mines = 2, limitTime = 60)
    val game = GameCreate(userId = "raul.osvaldo.robledo@gmail.com", gameOptions).toGame()

    whenReady(service.newGame(game)) { game =>
      whenReady(service.revealCell(game._id, 1, 2)) { revealedCells =>
        revealedCells.size shouldEqual 1
      }
      whenReady(service.revealCell(game._id, 2, 1)) { revealedCells =>
        revealedCells.size shouldEqual 1
      }
      whenReady(service.revealCell(game._id, 2, 2)) { revealedCells =>
        revealedCells.size shouldEqual 1
      }
      whenReady(service.revealCell(game._id, 2, 3)) { revealedCells =>
        revealedCells.size shouldEqual 1
      }
      whenReady(service.revealCell(game._id, 3, 1)) { revealedCells =>
        revealedCells.size shouldEqual 3
      }
      whenReady(service.getCells(game._id)) { cells =>
        cells.filter(cell => cell.revealed).size shouldEqual 9
      }

      whenReady(service.getGame(game._id)) { gameStored =>
        gameStored.status shouldEqual "SUCCESS"
      }

    }
  }
}

