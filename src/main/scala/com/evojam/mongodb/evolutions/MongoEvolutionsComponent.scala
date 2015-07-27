package com.evojam.mongodb.evolutions

import com.typesafe.config.ConfigFactory

import com.evojam.mongodb.evolutions.command.CommandsComponent
import com.evojam.mongodb.evolutions.config.{Configuration, ConfigurationComponent}
import com.evojam.mongodb.evolutions.util.LoggerComponent

trait MongoEvolutionsComponent
  extends LoggerComponent
  with ConfigurationComponent
  with CommandsComponent {

  val globalConfig = ConfigFactory.load()

  override val config = Configuration(globalConfig)
  override val commands = new CommandsImpl()
}
