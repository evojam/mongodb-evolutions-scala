package com.evojam.mongodb.evolutions.model.command

import java.io.{FileNotFoundException, IOException}

import scala.io.Source
import scala.util.control.Exception.catching

trait Command {
  def value: String

  protected def loadResource(resourceName: String, args: String*) =
    catching(classOf[IOException]).opt {
      val str = Source.fromInputStream(
        getClass().getClassLoader().getResourceAsStream(resourceName))
        .mkString
      args.length match {
        case x if x > 0 => str.format(args: _*)
        case _ => str
      }
    }.getOrElse(throw new FileNotFoundException(s"Cannot read resource $resourceName"))
}
