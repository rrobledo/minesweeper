package com.rrobledo.minesweeper.rest.controllers

import scaldi.Module

class ControllersModule extends Module {
  bind[HealthController] to new DefaultHealthController()
}
