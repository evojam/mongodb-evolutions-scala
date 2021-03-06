package com.evojam.mongodb.evolutions.dao

import com.typesafe.config.ConfigFactory
import org.scalatest._

import com.evojam.mongodb.evolutions.clock.ClockComponent
import com.evojam.mongodb.evolutions.command.CommandsComponent
import com.evojam.mongodb.evolutions.config.{Configuration, ConfigurationComponent}
import com.evojam.mongodb.evolutions.executor.{ExecutorComponent, ExecutorResult}
import com.evojam.mongodb.evolutions.journal.JournalComponent
import com.evojam.mongodb.evolutions.mock.ClockMock
import com.evojam.mongodb.evolutions.model.evolution.{Evolution, Script, State}
import com.evojam.mongodb.evolutions.util.{EmbeddedMongoDb, LoggerComponent, MongodProps, TestConfiguration}

class EvolutionsDaoSpecs extends FlatSpec with Matchers
  with EvolutionsDaoComponent
  with LoggerComponent
  with ConfigurationComponent
  with EmbeddedMongoDb
  with CommandsComponent
  with ExecutorComponent
  with JournalComponent
  with ClockComponent {

  override val config = Configuration(ConfigFactory.load())
  override def commands = new CommandsImpl
  override val executor = new ExecutorImpl
  override val journal = new JournalImpl
  override val clock = new ClockMock
  override val dao = new EvolutionsDaoImpl

  val up = Script("show dbs;")
  val evo1 = Evolution(1, up, None, None, Some(clock.now), None)
  val evo2 = Evolution(2, up, None, Some(State.ApplyingUp), Some(clock.now), None)
  val evo2Update = Evolution(2, up, Some(Script("db.dropDatabase();")), Some(State.Applied), Some(clock.now), None)

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

  it should "be in processing state" in {
    dao.isProcessing() should be (true)
  }

  it should "update evolution" in {
    dao.save(evo2Update) should be (ExecutorResult.Success)

    val all = dao.getAll()
    all.size should be (2)
    all should contain (evo1)
    all should contain (evo2Update)
  }

  it should "get specified evolution" in {
    dao.get(evo2.revision) should be (Some(evo2Update))
  }

  it should "not be in processing state" in {
    dao.isProcessing() should be (false)
  }

  it should "remove specified evolution" in {
    dao.remove(evo1) should be (ExecutorResult.Success)

    val all = dao.getAll()
    all.size should be(1)
    all should contain (evo2Update)
  }

  it should "return no evolution after call to removeAll" in {
    dao.removeAll()
    dao.getAll().size should be (0)
  }
}
