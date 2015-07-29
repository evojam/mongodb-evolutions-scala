package com.evojam.mongodb.evolutions.dao

import com.typesafe.config.ConfigFactory
import org.scalatest._

import com.evojam.mongodb.evolutions.command.{Commands, CommandsComponent}
import com.evojam.mongodb.evolutions.config.{Configuration, ConfigurationComponent}
import com.evojam.mongodb.evolutions.executor.{ExecutorResult, Executor, ExecutorComponent}
import com.evojam.mongodb.evolutions.model.evolution.{State, Script, Evolution}
import com.evojam.mongodb.evolutions.util.LoggerComponent

class EvolutionsDaoSpecs extends FlatSpec with Matchers
  with EvolutionsDaoComponent
  with LoggerComponent
  with ConfigurationComponent
  with CommandsComponent
  with ExecutorComponent {

  override val config = Configuration(ConfigFactory.load())
  override def commands = new CommandsImpl
  override val executor = new ExecutorImpl
  override val dao = new EvolutionsDaoImpl

  val evo1 = Evolution(1, None, None, State.Ready, None, None)
  val evo1Update = Evolution(1, Some(Script("show dbs;")), None, State.Ready, None, None)
  val evo2 = Evolution(2, None, None, State.Ready, None, None)

  "EvolutionsDao" should "return no evolutions from empty db" in {
    dao.getAll().size should be (0)
  }

  it should "insert evolution" in {
    dao.insert(evo1) should be (ExecutorResult.Success)

    val all1 = dao.getAll()
    all1.size should be (1)
    all1 should contain (evo1)

    dao.insert(evo2) should be (ExecutorResult.Success)

    val all2 = dao.getAll()
    all2.size should be (2)
    all2 should contain (evo1)
    all2 should contain (evo2)
  }

  it should "update evolution" in {
    dao.save(evo1Update) should be (ExecutorResult.Success)

    val all = dao.getAll()
    all.size should be (2)
    all should contain (evo1Update)
    all should contain (evo2)
  }

  it should "get specified evolution" in {
    dao.get(evo2.revision) should be (Some(evo2))
  }

  it should "remove specified evolution" in {
    dao.remove(evo1Update.revision) should be (ExecutorResult.Success)

    val all = dao.getAll()
    all.size should be(1)
    all should contain (evo2)
  }

  it should "return no evolution after call to removeAll" in {
    dao.removeAll()
    dao.getAll().size should be (0)
  }
}
