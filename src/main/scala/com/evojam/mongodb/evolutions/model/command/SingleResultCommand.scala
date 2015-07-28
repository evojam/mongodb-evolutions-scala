package com.evojam.mongodb.evolutions.model.command

case class SingleResultCommand(commandName: String, commandArgs: String*) extends Command {
  require(commandName != null, "commandName cannot be null")
  require(commandName.nonEmpty, "commandName cannot be empty")

  override lazy val value: String =
    loadResource("command/singleResultCommand.js.template")
      .format(command)

  private lazy val command =
    loadResource(commandName, commandArgs: _*)
}
