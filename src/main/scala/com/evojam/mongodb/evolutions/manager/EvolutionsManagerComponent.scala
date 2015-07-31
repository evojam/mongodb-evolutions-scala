package com.evojam.mongodb.evolutions.manager

import java.io.{FilenameFilter, File}

import com.evojam.mongodb.evolutions.config.ConfigurationComponent
import com.evojam.mongodb.evolutions.model.evolution.Evolution
import com.evojam.mongodb.evolutions.util.{Resources, LoggerComponent}

case class EvolutionsManagerException(msg: String) extends Exception(msg)

trait EvolutionsManagerComponent {
  this: LoggerComponent
    with ConfigurationComponent =>

  val evolutionsManager: EvolutionsManager

  class EvolutionsManagerImpl extends EvolutionsManager {
    override def getAll() =
      getAllFiles(new File(config.evolutionsPath))
        .map(Evolution.fromFile(_))

    override def getActions() =
      ???

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
