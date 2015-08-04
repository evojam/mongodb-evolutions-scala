package com.evojam.mongodb.evolutions.model.command

import com.evojam.mongodb.evolutions.model.evolution.Script

case class ScriptCommand(script: Script) extends Command {
  require(script != null, "script cannot be null")

  override lazy val value = script.value
}
