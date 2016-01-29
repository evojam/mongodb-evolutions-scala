package com.evojam.mongodb.evolutions.model.journal

import org.joda.time.DateTime
import play.api.libs.json._

import com.evojam.mongodb.evolutions.model.evolution.Evolution

case class Entry(
  operation: String,
  timestamp: DateTime,
  evolution: Option[Evolution]) {

  require(operation != null, "operation cannot be null")
  require(operation.nonEmpty, "operation cannot be empty")
  require(timestamp != null, "timestamp cannot be null")
  require(evolution != null, "evolution cannot be null")
}

object Entry {
  implicit val format = Json.format[Entry]
}
