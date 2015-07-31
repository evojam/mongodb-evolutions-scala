package com.evojam.mongodb.evolutions.manager

import com.typesafe.config.ConfigFactory
import org.scalatest._

import com.evojam.mongodb.evolutions.config.{Configuration, ConfigurationComponent}
import com.evojam.mongodb.evolutions.model.evolution.{Script, State, Evolution}
import com.evojam.mongodb.evolutions.util.LoggerComponent

class EvolutionsManagerSpecs extends FlatSpec with Matchers
  with EvolutionsManagerComponent
  with LoggerComponent
  with ConfigurationComponent {

  override val config = Configuration(ConfigFactory.load())
  override val evolutionsManager = new EvolutionsManagerImpl()

  val sampleScript = Some(Script(
    """db.database.find({
      |  'name': 'aname'
      |});""".stripMargin))

  "EvolutionsManager" should "load all evolutions" in {
    val es = evolutionsManager.getAll()
    es.size should be (2)

    es(0) should be (Evolution(1, None, None, None, None, None))
    es(1) should be (Evolution(2, sampleScript, sampleScript, None, None, None))
  }
}
