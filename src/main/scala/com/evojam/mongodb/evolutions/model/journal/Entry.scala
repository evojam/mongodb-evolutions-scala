package com.evojam.mongodb.evolutions.model.journal

import com.kifi.macros.json
import org.joda.time.DateTime

import com.evojam.mongodb.evolutions.model.evolution.Evolution

@json case class Entry(
  operation: String,
  timestamp: DateTime,
  evolution: Option[Evolution]) {

  require(operation != null, "operation cannot be null")
  require(operation.nonEmpty, "operation cannot be empty")
  require(timestamp != null, "timestamp cannot be null")
  require(evolution != null, "evolution cannot be null")
}