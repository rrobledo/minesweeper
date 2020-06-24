package com.rrobledo.minesweeper.repositories.mongodb

import com.typesafe.config.ConfigFactory
import org.mongodb.scala.{MongoClient, MongoDatabase}

trait Connector {
  def getDatabase: MongoDatabase
}

class MongodbConnector extends Connector {

  private lazy val config = ConfigFactory.load()
  private lazy val uri = config.getString("mongodb.uri")
  private lazy val database = config.getString("mongodb.database")

  private lazy val client = MongoClient(uri)
  private val db = client.getDatabase(database)

  override def getDatabase: MongoDatabase = this.db
}
