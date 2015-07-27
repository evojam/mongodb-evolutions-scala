package com.evojam.mongodb.evolutions.command

trait Commands {
  def acquireLock: Command
  def releaseLock: Command
}
