package com.evojam.mongodb.evolutions.executor

import play.api.libs.json.Reads

import com.evojam.mongodb.evolutions.command.Command

trait Executor {
  def execute[T: Reads](cmd: Command): Option[T]
}
