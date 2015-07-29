package com.evojam.mongodb.evolutions.executor

import play.api.libs.json.Reads

import com.evojam.mongodb.evolutions.executor.ExecutorResult.ExecutorResult
import com.evojam.mongodb.evolutions.model.command.Command

trait Executor {
  def executeAndCollect[T: Reads](cmd: Command): Option[T]
  def execute(cmd: Command): ExecutorResult
}