package com.rrobledo.minesweeper.services.game

import com.rrobledo.minesweeper.models.game.{Cell, Game}
import com.rrobledo.minesweeper.repositories.MineSweeperRepository
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.{DateTime, LocalDate, Seconds}
import scaldi.{Injectable, Injector}

import scala.async.Async.{async, await}
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class DefaultGameService(implicit val inj: Injector, implicit val ec: ExecutionContext)
  extends GameService
    with Injectable with LazyLogging {

  val repository : MineSweeperRepository = inject[MineSweeperRepository]

  override def newGame(game: Game): Future[Game] = async {
    val cells = generateCells(game._id, game.options.rows, game.options.cols, game.options.mines)
    val cellsWithNeighborCount = calculateNeighborCountMines(cells, game._id, game.options.rows, game.options.cols)
    await(repository.addGame(game))
    await(repository.addCells(cellsWithNeighborCount))
    game
  }

  /**
    * Get game
    *
    * @param gameId game identifier.
    * @return a new game.
    */
  override def getGame(gameId: String): Future[Game] = async {
    await(repository.getGame(gameId)) match {
      case Some(game) => game
      case _ => throw new ClassNotFoundException()
    }
  }

  /**
    * Get game cells
    *
    * @param gameId game identifier.
    * @return list of cells.
    */
  override def getCells(gameId: String): Future[List[Cell]] = async {
    await(repository.getCells(gameId)).toList
  }

  /**
    * Reveal cells
    *
    * @param row row number
    * @param col col number
    * @return list cell revealed.
    */
  override def revealCell(gameId: String, row: Int, col: Int): Future[List[Cell]] = async {
    val game = await(repository.getGame(gameId)) match {
                case Some(game) => game
                case _ => throw new ClassNotFoundException()
              }
    val cell = await(repository.getCellByGameRowCol(gameId, row, col)) match {
                case Some(cell) => cell
                case _ => throw new ClassNotFoundException()
              }
    val cells = await(repository.getCells(gameId)).toList

    val revealedCells = cell match {
      case cell if cell.isMine => {
        cells.filter(cell => !cell.revealed)
      }
      case cell if cell.neighborCount == 0 => {
        getNeighborCellsToReveal(cell, game.options.rows, game.options.cols, cells, List.empty )
      }
      case _ => {
        List(cell)
      }
    }
    val cellsToReveal = revealedCells.map(cell => Cell(cell.gameId, cell.row, cell.col, cell.isMine, cell.neighborCount, true))
    await(Future.sequence(cellsToReveal.map( cell => {
      repository.updateCellRevealed(cell, cell.revealed)
    })))

    await(validateGameFinished(gameId, cell.isMine))
    cellsToReveal
  }

  /**
    * Validate time limit of playing games, in case that analized game reach limit it shall be GAME_OVER_LIMIT
    */
  override def validateTimeLimitPlayingGaming(): Future[Unit] = async {
    val playingGames = await(repository.getPlayingGames())
    await(Future.sequence(playingGames.map { game =>
      val currentDate = DateTime.now()
      Seconds.secondsBetween(game.created, currentDate).getSeconds > game.options.limitTime match {
        case true => {
          repository.updateGameStatus(game._id, status = "GAME_OVER_LIMIT")
        }
        case false => Future.successful()
      }
    }))
  }

  /** Generate a list of cells to the game where the list size is (row - 1) * cols + (col - 1).
    *  @param rows rows on game board.
    *  @param cols columns on game board.
    *  @param mines number of mines on game board.
    *  @return a list of cells.
    */
  private def generateCells(gameId: String, rows: Int, cols: Int, mines: Int) : List[Cell] = {
    val mineIndexes = generateMineIndexes(rows, cols, mines)
    val cells = for {
      row <- 1 to rows
      col <- 1 to cols
    } yield {
      val index = (row - 1) * cols + (col - 1)
      mineIndexes.contains(index) match {
        case true => Cell(gameId, row, col, isMine = true)
        case _ => Cell(gameId, row, col)
      }
    }
    cells.toList
  }

  /** Generate a list of mine indexes, where each index is [row(i)*col(i)] and the list size is the amount of mines.
    *  @param rows rows on game board.
    *  @param cols columns on game board.
    *  @param mines number of mines on game board.
    *  @return a list of mine indexes.
    */
  protected def generateMineIndexes(rows: Int, cols: Int, mines: Int) : List[Int] = {
    val indexCount = (rows * cols)
    val indexes = ListBuffer.range(0, indexCount)

    val mineIndexes = for (i <- 1 to mines) yield {
      val index = new scala.util.Random().nextInt(indexes.size)
      indexes.remove(index)
    }
    mineIndexes.toList
  }

  /** Calculate the neighbor count mines.
    * @param cells cells of games.
    * @param rows rows on game board.
    * @param cols columns on game board.
    * @return a list of cells.
    */
  private def calculateNeighborCountMines(cells: List[Cell], gameId: String, rows: Int, cols: Int) : List[Cell] = {
    cells.map(cell => {
      val neighborCountMines = cell.isMine match {
                                case true => -1
                                case _ => getNeighborCountMines(cell, cells, rows, cols)
                              }
      Cell(gameId, cell.row, cell.col, cell.isMine, neighborCountMines)
    })
  }

  /** Return the neighbor count mines.
    * @param cell current cell.
    * @param rows rows on game board.
    * @param cols columns on game board.
    * @return a list cells.
    */
  private def getNeighborCountMines(cell: Cell, cells: List[Cell], rows: Int, cols: Int) : Int = {
    val neighborCoordinates = getNeighborCoordinates(cell, rows, cols)
    val neighborCountMines = neighborCoordinates.map(
      coord => {
        val cell: Cell = cells.find(cell => cell.row == coord._1 && cell.col == coord._2).head
        cell.isMine match {
          case true => 1
          case _ => 0
        }
      }
    ).sum
    neighborCountMines
  }

  /** Return the neighbor coordinates.
    * @param cell current cell.
    * @param rows rows on game board.
    * @param cols columns on game board.
    * @return a list coordinates.
    */
  private def getNeighborCoordinates(cell: Cell, rows: Int, cols: Int) : List[(Int, Int)] = {
    val neighborCoordinates = for {
      row <- {
        val fromRow = if (cell.row > 1) cell.row - 1 else cell.row
        val toRow = if (cell.row < rows) cell.row + 1 else cell.row
        fromRow to toRow
      }
      col <- {
        val fromCol = if (cell.col > 1) cell.col - 1 else cell.col
        val toCol = if (cell.col < cols) cell.col + 1 else cell.col
        fromCol to toCol
      }
    } yield {
      (row, col)
    }
    neighborCoordinates.toList
  }

  /** Return the neighbor coordinates.
    * @param cell current cell.
    * @param rows rows on game board.
    * @param cols columns on game board.
    * @return a list coordinates.
    */
  private def getNeighborCellsToReveal(cell: Cell, rows: Int, cols: Int, cells: List[Cell], revealedCells: List[Cell]) : List[Cell] = {
    val neighborCoordinates = getNeighborCoordinates(cell, rows, cols)
    val neighborCellsToReveal = neighborCoordinates
      .map {
        coord => cells.find(currentCell => currentCell.row == coord._1 && currentCell.col == coord._2).head
      }
      .filter ( neighborCell => {
        neighborCell.neighborCount match {
          case 0 => {
            revealedCells.find(cell => cell.row == neighborCell.row && cell.col == neighborCell.col).size match {
              case 0 => true
              case _ => false
            }
          }
          case _ => false
        }
      }
    )
    neighborCellsToReveal ++ neighborCellsToReveal.flatMap( cell => {
      getNeighborCellsToReveal(cell, rows, cols, cells, revealedCells ++ neighborCellsToReveal)
    })
  }

  /** Validate and update if game finished.
    *  @param gameId game identifier.
    */
  private def validateGameFinished(gameId: String, isMine : Boolean) : Future[Unit] = async {
    isMine match {
      case true =>
        await(repository.updateGameStatus(gameId, status = "GAME_OVER_MINE_FOUND"))
      case false => {
        await(repository.getCells(gameId)).toList
          .map(cell => cell.revealed || cell.isMine)
          .reduce((a, b) => a && b) match {
          case true => {
            await(repository.updateGameStatus(gameId, status = "SUCCESS"))
            val mines = await(repository.getCells(gameId)).toList
              .filter(cell => !cell.revealed)
            await(Future.sequence(mines.map( cell => {
              repository.updateCellRevealed(cell, revealed = true)
            })))
          }
          case _ => Unit
        }
      }
    }
  }

}
