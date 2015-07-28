package com.evojam.mongodb.evolutions.util

import java.io.{File, IOException}

import scala.io.Source
import scala.util.control.Exception.catching

object Resources {
  def load(resourceName: String): Option[String] =
    catching(classOf[IOException]).opt {
      Source.fromInputStream(
        getClass().getClassLoader().getResourceAsStream(resourceName))
        .mkString
    }

  def load(file: File): Option[String] =
    catching(classOf[IOException]).opt {
      Source.fromFile(file).mkString
    }
}
