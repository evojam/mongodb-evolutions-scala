package com.evojam.mongodb.evolutions.config

import com.typesafe.config.ConfigFactory
import org.scalatest._

class ConfigurationSpecs extends FlatSpec with Matchers
  with ConfigurationComponent {

  override val config = Configuration(ConfigFactory.load())

  "Configuration" should "load configuration properties" in {
    config.mongoCmd shouldNot be (null)
    config.useLocks should be (true)
    config.lockDBName should be ("lock-for-mongodb-evolutions")
  }
}
