package com.evojam.mongodb.evolutions.mock

import org.joda.time.DateTime

import com.evojam.mongodb.evolutions.clock.Clock

class ClockMock extends Clock {
  override def now(): DateTime =
    ClockMock.now
}

object ClockMock {
  lazy val now = new DateTime
}