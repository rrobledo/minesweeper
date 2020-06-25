package com.rrobledo.minesweeper.services.game

import com.rrobledo.minesweeper.models.game.{Cell, Game}
import com.rrobledo.minesweeper.repositories.MineSweeperRepository
import com.typesafe.scalalogging.LazyLogging
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
    val cells = generateCells(game.options.rows, game.options.cols, game.options.mines)
    val cellsWithNeighborCount = calculateNeighborCountMines(cells, game.options.rows, game.options.cols)
    // val result = await(repository.addGame(game))
    game
  }

  /** Generate a list of cells to the game where the list size is (row - 1) * cols + (col - 1).
    *  @param rows rows on game board.
    *  @param cols columns on game board.
    *  @param mines number of mines on game board.
    *  @return a list of cells.
    */
  private def generateCells(rows: Int, cols: Int, mines: Int) : List[Cell] = {
    val mineIndexes = generateMineIndexes(rows, cols, mines)
    val cells = for {
      row <- 1 to rows
      col <- 1 to cols
    } yield {
      val index = (row - 1) * cols + (col - 1)
      mineIndexes.contains(index) match {
        case true => Cell(row, col, isMine = true)
        case _ => Cell(row, col)
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
  private def generateMineIndexes(rows: Int, cols: Int, mines: Int) : List[Int] = {
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
  private def calculateNeighborCountMines(cells: List[Cell], rows: Int, cols: Int) : List[Cell] = {
    cells.map(cell => {
      val neighborCountMines = cell.isMine match {
                                case true => -1
                                case _ => getNeighborCountMines(cell, cells, rows, cols)
                              }
      Cell(cell.row, cell.col, cell.isMine, neighborCountMines)
    })
  }

  /** Return the neighbor count mines.
    *  @param cell current cell.
    *  @return a list cells.
    */
  private def getNeighborCountMines(cell: Cell, cells: List[Cell], rows: Int, cols: Int) : Int = {
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

}
