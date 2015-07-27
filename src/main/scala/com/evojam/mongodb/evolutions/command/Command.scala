package com.evojam.mongodb.evolutions.command

import java.io.{FileNotFoundException, IOException}

import scala.io.Source
import scala.util.control.Exception.catching

case class Command(resourceName: String, formatArgs: String*) {
  require(resourceName != null, "resourceName cannot be null")
  require(resourceName.nonEmpty, "resourceName cannot be empty")

  lazy val command: String =
    catching(classOf[IOException]).opt {
      val sb = new StringBuilder
      sb.append(CommandPrefix)
      sb.append(
        Source.fromInputStream(
          getClass().getClassLoader().getResourceAsStream(resourceName))
          .mkString.format(formatArgs: _*))
      sb.append(CommandSuffix)
      sb.result
    }.getOrElse(throw new FileNotFoundException(s"Cannot read resource: $resourceName"))

  private[evolutions] val CommandPrefix =
    "var result="

  private[evolutions] val CommandSuffix =
    "printjson(result);"
}
