package com.rrobledo.minesweeper.rest.entities

import java.util.UUID

import com.rrobledo.minesweeper.models.game.{Game, GameOptions}
import org.joda.time.DateTime

case class GameCreate(userId: String,
                      options: GameOptions)
{
  def toGame() : Game = {
    Game(_id = UUID.randomUUID().toString, userId = this.userId, created=DateTime.now(), status="PLAYING", options = this.options)
  }
}
