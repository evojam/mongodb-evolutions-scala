package com.evojam.mongodb.evolutions.guard

trait Guard {
  def withLock(block: => Unit): Unit
}
