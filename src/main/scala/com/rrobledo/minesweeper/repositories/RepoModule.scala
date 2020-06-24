package com.rrobledo.minesweeper.repositories

import com.rrobledo.minesweeper.repositories.mongodb.{MongodbMineSweeperRepository, MongodbModule}

import scala.concurrent.ExecutionContext

class RepoModule (implicit ec: ExecutionContext) extends MongodbModule {
  bind[MineSweeperRepository] to new MongodbMineSweeperRepository()
}
