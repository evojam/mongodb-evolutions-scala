package com.evojam.mongodb.evolutions.util

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

trait LoggerComponent {
  lazy val logger =
    Logger(LoggerFactory.getLogger("mongodb-evolutions-scala"))
}
