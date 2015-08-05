package com.evojam.mongodb.evolutions.guard

import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, FlatSpec}

import com.evojam.mongodb.evolutions.command.CommandsComponent
import com.evojam.mongodb.evolutions.config.{Configuration, ConfigurationComponent}
import com.evojam.mongodb.evolutions.executor.ExecutorComponent
import com.evojam.mongodb.evolutions.util.LoggerComponent

class GuardSpecs extends FlatSpec with Matchers
  with GuardComponent
  with ExecutorComponent
  with CommandsComponent
  with ConfigurationComponent
  with LoggerComponent {

  override val config = Configuration(ConfigFactory.load)
  override def commands = new CommandsImpl
  override val executor = new ExecutorImpl
  override val guard = new GuardImpl

  "Guard" should "acquire lock" in {
    var res = 0
    guard.withLock {
      res = 1
    }
    res should be (1)
  }

  "Guard" should "acquire lock exactly once" in {
    var res = 0
    guard.withLock {
      res = 1
      guard.withLock {
        res = 2
      }
    }
    res should be (1)
  }

  "Guard" should "acquire lock twice in a row" in {
    var res = 0
    guard.withLock {
      res = 1
    }
    guard.withLock {
      res = 2
    }
    res should be (2)
  }
}
