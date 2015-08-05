package com.evojam.mongodb.evolutions.clock

import org.joda.time.DateTime

trait Clock {
  def now(): DateTime
}
