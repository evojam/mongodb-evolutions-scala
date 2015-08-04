package com.evojam.mongodb.evolutions.dao

import com.evojam.mongodb.evolutions.command.CommandsComponent
import com.evojam.mongodb.evolutions.executor.ExecutorComponent
import com.evojam.mongodb.evolutions.model.evolution.{State, Evolution}
import com.evojam.mongodb.evolutions.util.LoggerComponent

trait EvolutionsDaoComponent {
  this: LoggerComponent
    with CommandsComponent
    with ExecutorComponent =>

  val dao: EvolutionsDao

  class EvolutionsDaoImpl extends EvolutionsDao {
    override def get(revision: Int) =
      executor.executeAndCollect[Evolution](
        commands.getEvolution(revision))

    override def getAll() =
      executor.executeAndCollect[List[Evolution]](
        commands.getAllEvolutions).getOrElse(Nil)

    override def insert(evolution: Evolution) =
      executor.execute(
        commands.insertEvolution(evolution))

    override def save(evolution: Evolution) =
      executor.execute(
        commands.saveEvolution(evolution))

    override def remove(revision: Int) =
      executor.execute(
        commands.removeEvolution(revision))

    override def remove(evolution: Evolution) =
      remove(evolution.revision)

    override def removeAll() =
      executor.execute(
        commands.removeAllEvolutions)

    override def isProcessing() =
      getAll()
        .filter(evo => evo.state == Some(State.ApplyingUp) || evo.state == Some(State.ApplyingDown))
        .nonEmpty
  }
}
