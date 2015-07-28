package com.evojam.mongodb.evolutions.command

import com.evojam.mongodb.evolutions.config.ConfigurationComponent
import com.evojam.mongodb.evolutions.model.command.SingleResultCommand

trait CommandsComponent {
  this: ConfigurationComponent =>

  def commands: Commands

  class CommandsImpl extends Commands {
    override def acquireLock =
      SingleResultCommand("lock/acquireLock.js.template", config.lockDBName)

    override def releaseLock =
      SingleResultCommand("lock/releaseLock.js.template", config.lockDBName)
  }
}
