package com.rrobledo.minesweeper.models.game

case class Cell
(
  gameId: String,
  row: Int,
  col: Int,
  isMine: Boolean = false,
  neighborCount: Int = 0,
  revealed: Boolean = false
)
