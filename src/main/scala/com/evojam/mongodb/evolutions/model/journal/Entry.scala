package com.evojam.mongodb.evolutions.model.journal

import com.kifi.macros.json

import com.evojam.mongodb.evolutions.model.evolution.Evolution

@json case class Entry(operation: String, evolution: Option[Evolution]) {
  require(operation != null, "operation cannot be null")
  require(operation.nonEmpty, "operation cannot be empty")
  require(evolution != null, "evolution cannot be null")
}