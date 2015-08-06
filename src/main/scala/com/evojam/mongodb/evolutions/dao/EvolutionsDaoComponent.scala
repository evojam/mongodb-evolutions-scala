package com.evojam.mongodb.evolutions.dao

import com.evojam.mongodb.evolutions.clock.ClockComponent
import com.evojam.mongodb.evolutions.command.CommandsComponent
import com.evojam.mongodb.evolutions.executor.ExecutorComponent
import com.evojam.mongodb.evolutions.journal.JournalComponent
import com.evojam.mongodb.evolutions.model.evolution.{State, Evolution}
import com.evojam.mongodb.evolutions.util.LoggerComponent

trait EvolutionsDaoComponent {
  this: LoggerComponent
    with CommandsComponent
    with ExecutorComponent
    with JournalComponent
    with ClockComponent =>

  val dao: EvolutionsDao

  class EvolutionsDaoImpl extends EvolutionsDao {
    override def get(revision: Int) =
      executor.executeAndCollect[Evolution](
        commands.getEvolution(revision))

    override def getAll() =
      executor.executeAndCollect[List[Evolution]](
        commands.getAllEvolutions).getOrElse(Nil)

    override def insert(evolution: Evolution) = {
      journal.push("insert", evolution)
      executor.execute(
        commands.insertEvolution(
          evolution.copy(timestamp = Some(clock.now()))))
    }

    override def save(evolution: Evolution) = {
      journal.push("save", evolution)
      executor.execute(
        commands.saveEvolution(
          evolution.copy(timestamp = Some(clock.now()))))
    }

    override def remove(evolution: Evolution) = {
      journal.push("remove", evolution)
      executor.execute(
        commands.removeEvolution(evolution.revision))
    }

    override def removeAll() = {
      journal.push("removeAll")
      executor.execute(
        commands.removeAllEvolutions)
    }

    override def isProcessing() =
      getAll()
        .filter(evo => evo.state == Some(State.ApplyingUp) || evo.state == Some(State.ApplyingDown))
        .nonEmpty
  }
}
