package com.rrobledo.minesweeper.repositories.mongodb

import com.rrobledo.minesweeper.models.game.{Game, GameOptions}
import com.rrobledo.minesweeper.repositories.MineSweeperRepository
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import scaldi.{Injectable, Injector}

import scala.concurrent.{ExecutionContext, Future}

class MongodbMineSweeperRepository(implicit val inj: Injector, implicit val ec: ExecutionContext)
  extends MineSweeperRepository
    with Injectable {

  private val connector = inject[Connector]
  private val gamesCodecRegistry = fromRegistries(fromProviders(classOf[Game], classOf[GameOptions]), DEFAULT_CODEC_REGISTRY)
  private val gamesCollection = connector.getDatabase.getCollection[Game]("games").withCodecRegistry(gamesCodecRegistry)

  override def addGame(game: Game): Future[Game] = {
    gamesCollection
      .insertOne(game)
      .head
      .map { _ => game }
  }
}
