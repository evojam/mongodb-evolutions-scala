package com.evojam.mongodb.evolutions.executor

import play.api.libs.json.Reads

import com.evojam.mongodb.evolutions.command.Command
import com.evojam.mongodb.evolutions.config.ConfigurationComponent
import com.evojam.mongodb.evolutions.util.LoggerComponent

trait ExecutorComponent {
  this: ConfigurationComponent
    with LoggerComponent =>

  val executor: Executor

  class ExecutorImpl extends Executor {
    override def execute[T: Reads](cmd: Command) =
      ???
  }
}
