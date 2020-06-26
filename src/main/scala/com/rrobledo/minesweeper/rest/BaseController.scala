package com.rrobledo.minesweeper.rest

import akka.http.scaladsl.server.{Directives, Route}
import com.rrobledo.minesweeper.rest.serializers.JsonSupport

trait BaseController extends Directives with JsonSupport {

}
