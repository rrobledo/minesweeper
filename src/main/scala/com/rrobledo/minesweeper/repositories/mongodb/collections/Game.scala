package com.rrobledo.minesweeper.repositories.mongodb.collections

import org.joda.time.DateTime

case class Game
(
  _id: Int,
  userId: Int,
  created: DateTime,
  status: String,
)
