package com.evojam.mongodb.evolutions.manager

import com.evojam.mongodb.evolutions.command.CommandsComponent
import com.evojam.mongodb.evolutions.dao.EvolutionsDaoComponent
import com.evojam.mongodb.evolutions.executor.ExecutorComponent
import com.evojam.mongodb.evolutions.journal.JournalComponent
import com.evojam.mongodb.evolutions.mock.{Evolutions, EvolutionsDaoMock}
import com.typesafe.config.ConfigFactory
import org.scalatest._

import com.evojam.mongodb.evolutions.config.{Configuration, ConfigurationComponent}
import com.evojam.mongodb.evolutions.model.evolution.{Action, Script, Evolution}
import com.evojam.mongodb.evolutions.util.LoggerComponent

class EvolutionsManagerSpecs extends FlatSpec with Matchers with Evolutions
  with EvolutionsManagerComponent
  with EvolutionsDaoComponent
  with ExecutorComponent
  with CommandsComponent
  with LoggerComponent
  with ConfigurationComponent
  with JournalComponent {

  override val config = Configuration(ConfigFactory.load())
  override val dao = new EvolutionsDaoMock(predefEvolutions)
  override val executor = new ExecutorImpl
  override val commands = new CommandsImpl
  override val journal = new JournalImpl
  override val evolutionsManager = new EvolutionsManagerImpl()

  "EvolutionsManager" should "load all evolutions" in {
    evolutionsManager.getAll should be (predefEvolutions)
  }

  it should "assign no actions when there's no new evolutions" in {
    evolutionsManager.getActions should be (Nil)
  }

  it should "assign ApplyUp Action when new script appears" in {
    dao.remove(predefEvolution05)

    evolutionsManager.getActions should be {
      List((Action.ApplyUp, predefEvolution05))
    }
  }

  it should "assign ApplyDown Actions when old up script has been modified" in {
    dao.insert(predefEvolution05ModifiedUp)

    evolutionsManager.getActions should be {
      List(
        (Action.ApplyDown, predefEvolution05ModifiedUp),
        (Action.ApplyUp, predefEvolution05))
    }

    dao.save(predefEvolution05)
    dao.save(predefEvolution03ModifiedUp)

    evolutionsManager.getActions should be {
      List(
        (Action.ApplyDown, predefEvolution05),
        (Action.ApplyDown, predefEvolution04),
        (Action.ApplyDown, predefEvolution03ModifiedUp),
        (Action.ApplyUp, predefEvolution03),
        (Action.ApplyUp, predefEvolution04),
        (Action.ApplyUp, predefEvolution05))
    }
  }

  it should "assign Update Action when old down script has been modified" in {
    dao.save(predefEvolution03)
    dao.save(predefEvolution05ModifiedDown)

    evolutionsManager.getActions should be {
      List((Action.Update, predefEvolution05))
    }

    dao.save(predefEvolution03ModifiedDown)

    evolutionsManager.getActions should be {
      List(
        (Action.Update, predefEvolution03),
        (Action.Update, predefEvolution05))
    }
  }
}
