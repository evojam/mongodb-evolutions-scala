package com.evojam.mongodb.evolutions.model.evolution

private[evolution] object Marker extends Enumeration {
  type Marker = Value

  val Empty, Up, Down = Value
}
