package com.evojam.mongodb.evolutions.manager

import scalaz._, Scalaz._

import java.io.{FilenameFilter, File}

import com.evojam.mongodb.evolutions.config.ConfigurationComponent
import com.evojam.mongodb.evolutions.dao.EvolutionsDaoComponent
import com.evojam.mongodb.evolutions.model.evolution.{Action, Evolution}
import com.evojam.mongodb.evolutions.util.LoggerComponent
import com.evojam.mongodb.evolutions.validator.input.{EvolutionsInputException, InputValidatorComponent}

case class EvolutionsManagerException(msg: String) extends Exception(msg)

trait EvolutionsManagerComponent {
  this: LoggerComponent
    with ConfigurationComponent
    with EvolutionsDaoComponent
    with InputValidatorComponent =>

  val evolutionsManager: EvolutionsManager

  private case class Actions(
    downs: List[Evolution],
    updates: List[Evolution],
    ups: List[Evolution],
    revert: Boolean)

  class EvolutionsManagerImpl extends EvolutionsManager {
    override def getAll() =
      validateEvolutions(
        getAllFiles(new File(config.evolutionsPath))
          .map(Evolution.fromFile(_)))

    override def getActions() = {
      val evolutions = getAll()
      val dbEvolutions = dao.getAll()

      evolutions.foldLeft(noAction)(processEvolution(dbEvolutions)) match {
        case Actions(downs, updates, ups, _) =>
          (missingEvolutions(evolutions, dbEvolutions) ::: downs)
            .map((Action.ApplyDown, _)).reverse :::
          updates.map((Action.Update, _)) :::
          ups.map((Action.ApplyUp, _))
      }
    }

    private def missingEvolutions(evolutions: List[Evolution], dbEvolutions: List[Evolution]) =
      dbEvolutions.filter(_.revision > evolutions.maxBy(_.revision).revision)
        .sortBy(_.revision)

    private val noAction =
      Actions(List.empty[Evolution], List.empty[Evolution], List.empty[Evolution], false)

    private def processEvolution(dbEvolutions: List[Evolution])(step: Actions, evolution: Evolution) =
      dbEvolutions
        .find(_.revision === evolution.revision)
        .map(dbEvolution =>
          step.revert
            .option(
              step.copy(
                downs = step.downs :+ dbEvolution,
                ups = step.ups :+ evolution))
            .getOrElse(compareEvolutions(evolution, dbEvolution)(step)))
        .getOrElse(step.copy(ups = step.ups :+ evolution))

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
            .toList.sortBy(_.getName match {
              case EvolutionFileName(digit) => digit.toInt
              case _ => -1
            })
        case _ =>
          throw EvolutionsManagerException(s"Cannot find directory: ${evolutionsDir.getAbsolutePath}")
      }

    private def validateEvolutions(input: List[Evolution]) =
      input.nonEmpty
        .option(inputValidator.validate(input)
          .toEither match {
            case Right(_) => input
            case Left(err) => throw EvolutionsInputException(err.toList)
          })
        .getOrElse(input)

    private lazy val evolutionNameFilter = new FilenameFilter {
      override def accept(dir: File, name: String)=
        name.matches(EvolutionFileName.regex)
    }

    private val EvolutionFileName = "(\\d+).js".r
  }
}
