package com.evojam.mongodb.evolutions.model.command

case class RawCommand(commandName: String, commandArgs: String*) extends Command {
  require(commandName != null, "commandName cannot be null")
  require(commandName.nonEmpty, "commandName cannot be empty")

  override def value =
    loadResource(commandName, commandArgs: _*)
}
