package com.rrobledo.minesweeper.repositories

import org.bson.{BsonReader, BsonWriter}
import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.joda.time.DateTime

class JodaCodec extends Codec[DateTime] {
  override def decode(bsonReader: BsonReader, decoderContext: DecoderContext): DateTime = new DateTime(bsonReader.readDateTime())
  override def encode(bsonWriter: BsonWriter, t: DateTime, encoderContext: EncoderContext): Unit = bsonWriter.writeDateTime(t.getMillis)
  override def getEncoderClass: Class[DateTime] = classOf[DateTime]
}

case class Record(id: String, created: DateTime)
