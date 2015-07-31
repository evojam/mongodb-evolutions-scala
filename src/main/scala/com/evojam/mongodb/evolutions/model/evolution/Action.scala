package com.evojam.mongodb.evolutions.model.evolution

object Action extends Enumeration {
  type Action = Value

  val Update, ApplyUp, ApplyDown = Value
}