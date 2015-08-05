package com.evojam.mongodb.evolutions.clock

import org.joda.time.DateTime

trait ClockComponent {
  val clock: Clock

  class ClockImpl extends Clock {
    override def now() =
      new DateTime()
  }
}
