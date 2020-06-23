package com.rrobledo.minesweeper.utils

import java.text.SimpleDateFormat

import com.rrobledo.minesweeper.rest.serializers.CustomSerializers
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.native.Serialization

trait JsonSerializer {

  def write[A <: AnyRef](a: A): String

  def read[A](json: String)(implicit mf: Manifest[A]): A

}

class DefaultJsonSerializer extends JsonSerializer {

  implicit val serialization: Serialization.type = native.Serialization

  implicit def json4sFormats: Formats = customDateFormat ++ JodaTimeSerializers.all ++ CustomSerializers.all

  val customDateFormat: DefaultFormats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  override def write[A <: AnyRef](a: A): String = {
    serialization.write(a)
  }

  override def read[A](json: String)(implicit mf: Manifest[A]): A = {
    serialization.read(json)
  }
}
