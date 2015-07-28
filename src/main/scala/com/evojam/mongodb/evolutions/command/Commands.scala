package com.evojam.mongodb.evolutions.command

import com.evojam.mongodb.evolutions.model.command.Command

trait Commands {
  def acquireLock: Command
  def releaseLock: Command
}
