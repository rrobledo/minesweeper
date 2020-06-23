package com.rrobledo.minesweeper.rest.serializers

import java.text.SimpleDateFormat

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.ext.JodaTimeSerializers
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Formats, native}

trait JsonSupport extends Json4sSupport {

  implicit val serialization: Serialization.type = native.Serialization

  implicit def json4sFormats: Formats = customDateFormat ++ JodaTimeSerializers.all ++ CustomSerializers.all

  val customDateFormat: DefaultFormats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
  }
}
