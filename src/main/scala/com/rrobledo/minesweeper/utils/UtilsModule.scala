package com.rrobledo.minesweeper.utils

import java.time.Clock

import com.typesafe.config.Config
import scaldi.Module

class UtilsModule(implicit config: Config) extends Module {
  bind[JsonSerializer] to new DefaultJsonSerializer
  bind[Clock] to Clock.systemUTC()
}
