package com.evojam.mongodb.evolutions.command

import com.evojam.mongodb.evolutions.config.ConfigurationComponent

trait CommandsComponent {
  this: ConfigurationComponent =>

  def commands: Commands

  class CommandsImpl extends Commands {
    override def acquireLock =
      Command("lock/acquireLock.js", config.lockDBName)

    override def releaseLock =
      Command("lock/releaseLock.js", config.lockDBName)
  }
}
