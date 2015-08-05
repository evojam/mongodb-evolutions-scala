package com.evojam.mongodb.evolutions.command

import com.evojam.mongodb.evolutions.model.command.Command
import com.evojam.mongodb.evolutions.model.evolution.{Script, Evolution}
import com.evojam.mongodb.evolutions.model.journal.Entry

trait Commands {
  def acquireLock: Command
  def releaseLock: Command
  def getLock: Command

  def getEvolution(revision: Int): Command
  def getAllEvolutions: Command
  def insertEvolution(evolution: Evolution): Command
  def saveEvolution(evolution: Evolution): Command
  def removeEvolution(revision: Int): Command
  def removeAllEvolutions: Command

  def applyScript(script: Script): Command

  def addToJournal(entry: Entry): Command
}
