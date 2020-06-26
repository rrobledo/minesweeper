package com.rrobledo.minesweeper.repositories.mongodb

import com.rrobledo.minesweeper.models.game.{Cell, Game, GameOptions}
import com.rrobledo.minesweeper.repositories.{JodaCodec, MineSweeperRepository, Record}
import com.typesafe.scalalogging.LazyLogging
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.joda.time.DateTime
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import scaldi.{Injectable, Injector}

import scala.concurrent.{ExecutionContext, Future}

class MongodbMineSweeperRepository(implicit val inj: Injector, implicit val ec: ExecutionContext)
  extends MineSweeperRepository
    with LazyLogging
    with Injectable {

  private val connector = inject[Connector]
  private val gamesCodecRegistry = fromRegistries(fromProviders(classOf[Game],
                                          classOf[GameOptions],
                                          classOf[Record]),
                                          CodecRegistries.fromCodecs(new JodaCodec),
                                          DEFAULT_CODEC_REGISTRY)
  private val gamesCollection = connector.getDatabase.getCollection[Game]("games").withCodecRegistry(gamesCodecRegistry)

  private val cellsCodecRegistry = fromRegistries(fromProviders(classOf[Cell]), DEFAULT_CODEC_REGISTRY)
  private val cellsCollection = connector.getDatabase.getCollection[Cell]("cells").withCodecRegistry(cellsCodecRegistry)

  override def addGame(game: Game): Future[Game] = {
    gamesCollection
      .insertOne(game)
      .head
      .map( _ => game)
  }

  override def addCells(cells: List[Cell]): Future[Unit] = Future {
    cellsCollection
      .insertMany(cells)
      .head
      .map(_ => Unit)
  }

  override def getGame(gameId: String): Future[Option[Game]] = {
    gamesCollection
      .find(Document("_id" -> gameId))
      .first
      .head
      .map(Option(_))
  }

  override def getCells(gameId: String): Future[Seq[Cell]] = {
    cellsCollection
      .find(Document("gameId" -> gameId))
      .collect()
      .toFuture()
  }

  override def getCellByGameRowCol(gameId: String, row: Int, col: Int): Future[Option[Cell]] = {
    cellsCollection
      .find(Document("gameId" -> gameId, "row" -> row, "col" -> col))
      .first
      .head
      .map(Option(_))
  }

  override def updateCellRevealed(cell: Cell, revealed: Boolean): Future[Unit] = {
    cellsCollection
      .updateOne(Document("gameId" -> cell.gameId, "row" -> cell.row, "col" -> cell.col), Document("$set" -> Document("revealed" -> revealed)))
      .head
      .map(_ => Unit)
  }

  override def updateGameStatus(gameId: String, status: String): Future[Unit] = {
    gamesCollection
      .updateOne(Document("_id" -> gameId), Document("$set" -> Document("status" -> status)))
      .head
      .map(_ => Unit)
  }
}
