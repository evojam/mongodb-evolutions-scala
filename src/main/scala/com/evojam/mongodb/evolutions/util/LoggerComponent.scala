package com.evojam.mongodb.evolutions.util

import org.slf4j.LoggerFactory

trait LoggerComponent {
  lazy val logger = LoggerFactory.getLogger("mongodb-evolutions-scala")
}
