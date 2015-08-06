package com.evojam.mongodb.evolutions.journal

import com.evojam.mongodb.evolutions.model.evolution.Evolution

trait Journal {
  def push(operation: String, evolution: Evolution): Unit
  def push(operation: String): Unit
}
