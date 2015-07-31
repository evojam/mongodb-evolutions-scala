package com.evojam.mongodb.evolutions.manager

import java.io.{FilenameFilter, File}

import com.evojam.mongodb.evolutions.config.ConfigurationComponent
import com.evojam.mongodb.evolutions.dao.EvolutionsDaoComponent
import com.evojam.mongodb.evolutions.model.evolution.{Action, Evolution}
import com.evojam.mongodb.evolutions.util.LoggerComponent

case class EvolutionsManagerException(msg: String) extends Exception(msg)

trait EvolutionsManagerComponent {
  this: LoggerComponent
    with ConfigurationComponent
    with EvolutionsDaoComponent =>

  val evolutionsManager: EvolutionsManager

  class EvolutionsManagerImpl extends EvolutionsManager {
    override def getAll() =
      getAllFiles(new File(config.evolutionsPath))
        .map(Evolution.fromFile(_))

    override def getActions() =
      getAll().foldLeft(noAction)(processEvolution(dao.getAll)) match {
        case Actions(downs, updates, ups, _) =>
          downs.map((Action.ApplyDown, _)).reverse :::
          updates.map((Action.Update, _)) :::
          ups.map((Action.ApplyUp, _))
      }

    private case class Actions(
      downs: List[Evolution],
      updates: List[Evolution],
      ups: List[Evolution],
      revert: Boolean)

    private val noAction = Actions(List.empty[Evolution], List.empty[Evolution], List.empty[Evolution], false)

    private def processEvolution(dbEvolutions: List[Evolution])(step: Actions, evolution: Evolution) =
      dbEvolutions.find(_.revision == evolution.revision)
        .map(dbEvolution =>
          step.revert match {
            case true =>
              step.copy(
                downs = step.downs :+ dbEvolution,
                ups = step.ups :+ evolution)
            case false =>
              compareEvolutions(evolution, dbEvolution)(step)
          }
        ).getOrElse(step.copy(ups = step.ups :+ evolution))

    private def compareEvolutions(evolution: Evolution, dbEvolution: Evolution)(step: Actions) =
      (evolution, dbEvolution) match {
        case (evo, dbEvo) if evo.hash.equals(dbEvo.hash) =>
          step
        case (evo, dbEvo) if evo.up.md5.equals(dbEvo.up.md5) =>
          step.copy(updates = step.updates :+ evolution)
        case _ =>
          step.copy(
            downs = step.downs :+ dbEvolution,
            ups = step.ups :+ evolution,
            revert = true)
      }

    private def getAllFiles(evolutionsDir: File): List[File] =
      evolutionsDir match {
        case es if es.exists && es.isDirectory =>
          evolutionsDir.listFiles(evolutionNameFilter)
            .toList.sortBy(_.getName)
        case _ =>
          throw EvolutionsManagerException(s"Cannot find directory: ${evolutionsDir.getAbsolutePath}")
      }

    private lazy val evolutionNameFilter = new FilenameFilter {
      override def accept(dir: File, name: String)=
        name.matches("\\d+.js")
    }
  }
}
