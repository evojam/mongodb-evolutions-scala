package com.evojam.mongodb.evolutions.executor

import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

import com.evojam.mongodb.evolutions.config.{Configuration, ConfigurationComponent}
import com.evojam.mongodb.evolutions.model.command.QueryCommand
import com.evojam.mongodb.evolutions.model.evolution.Evolution
import com.evojam.mongodb.evolutions.util.LoggerComponent

class ExecutorSpec extends FlatSpec with Matchers
  with LoggerComponent
  with ConfigurationComponent
  with ExecutorComponent {

  private val fakeMongoUri = "127.0.0.0"

  override val config = Configuration(ConfigFactory.load())
    .copy(mongoCmd = s"mongo $fakeMongoUri")

  override val executor = new ExecutorImpl

  it should "throw connection failed when it cannot connect to MongoDB" in {

    an[ConnectionFailed] shouldBe thrownBy {
      val cmd = QueryCommand("db-name", "query/all.js.template")
      executor.executeAndCollect[List[Evolution]](cmd)
    }

  }
}
