package com.evojam.mongodb.evolutions.command

import play.api.libs.json.Json

import com.evojam.mongodb.evolutions.config.ConfigurationComponent
import com.evojam.mongodb.evolutions.model.command._
import com.evojam.mongodb.evolutions.model.evolution.{Script, Evolution}

trait CommandsComponent {
  this: ConfigurationComponent =>

  def commands: Commands

  class CommandsImpl extends Commands {
    override lazy val acquireLock =
      SingleResultCommand(
        "lock/acquireLock.js.template",
        config.lockCollection)

    override lazy val releaseLock =
      SingleResultCommand(
        "lock/releaseLock.js.template",
        config.lockCollection)

    override def getEvolution(revision: Int) =
      SingleResultCommand(
        "command/findById.js.template",
        config.evolutionsCollection,
        revision.toString)

    override lazy val getAllEvolutions =
      QueryCommand(
        config.evolutionsCollection,
        "query/all.js.template")

    override def insertEvolution(evolution: Evolution) =
      RawCommand(
        "command/insert.js.template",
        config.evolutionsCollection,
        Json.stringify(Json.toJson(evolution)))

    override def saveEvolution(evolution: Evolution) =
      RawCommand(
        "command/save.js.template",
        config.evolutionsCollection,
        Json.stringify(Json.toJson(evolution)))

    override def removeEvolution(revision: Int) =
      RemoveCommand(
        config.evolutionsCollection,
        "query/revision.js.template",
        revision.toString)

    override lazy val removeAllEvolutions =
      RemoveCommand(
        config.evolutionsCollection,
        "query/all.js.template")

    override def applyScript(script: Script) =
      ScriptCommand(script)
  }
}
