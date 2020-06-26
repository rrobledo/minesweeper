package com.rrobledo.minesweeper.models.game

import org.joda.time.DateTime

case class GameOptions
(
  rows: Int,
  cols: Int,
  mines: Int,
  limitTime: Int
)

case class Game
(
  _id: String,
  userId: String,
  created: DateTime,
  status: String,
  options: GameOptions
)
