package com.evojam.mongodb.evolutions

import com.typesafe.config.ConfigFactory

import com.evojam.mongodb.evolutions.clock.ClockComponent
import com.evojam.mongodb.evolutions.command.CommandsComponent
import com.evojam.mongodb.evolutions.config.{Configuration, ConfigurationComponent}
import com.evojam.mongodb.evolutions.dao.EvolutionsDaoComponent
import com.evojam.mongodb.evolutions.executor.ExecutorComponent
import com.evojam.mongodb.evolutions.guard.GuardComponent
import com.evojam.mongodb.evolutions.journal.JournalComponent
import com.evojam.mongodb.evolutions.manager.EvolutionsManagerComponent
import com.evojam.mongodb.evolutions.util.LoggerComponent
import com.evojam.mongodb.evolutions.validator.input.InputValidatorComponent

trait MongoEvolutionsComponent
  extends LoggerComponent
  with ConfigurationComponent
  with CommandsComponent
  with ExecutorComponent
  with GuardComponent
  with EvolutionsDaoComponent
  with EvolutionsManagerComponent
  with JournalComponent
  with ClockComponent
  with InputValidatorComponent {

  val globalConfig = ConfigFactory.load()

  override val config = Configuration(globalConfig)
  override val commands = new CommandsImpl()
  override val executor = new ExecutorImpl()
  override val guard = new GuardImpl()
  override val dao = new EvolutionsDaoImpl()
  override val journal = new JournalImpl()
  override val evolutionsManager = new EvolutionsManagerImpl()
  override val clock = new ClockImpl()
  override val inputValidator = new InputValidatorImpl()
}
