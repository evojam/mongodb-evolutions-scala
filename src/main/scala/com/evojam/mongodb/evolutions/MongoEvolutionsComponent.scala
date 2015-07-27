package com.evojam.mongodb.evolutions

import com.typesafe.config.ConfigFactory

import com.evojam.mongodb.evolutions.config.{Configuration, ConfigurationComponent}
import com.evojam.mongodb.evolutions.util.LoggerComponent

trait MongoEvolutionsComponent
  extends LoggerComponent
  with ConfigurationComponent {

  val globalConfig = ConfigFactory.load()

  override val config = Configuration(globalConfig)
}
