package com.rrobledo.minesweeper.repositories.mongodb

import com.typesafe.scalalogging.LazyLogging
import scaldi.Module

class MongodbModule extends Module with LazyLogging {
  bind[Connector] to new MongodbConnector()
}
