package com.evojam.mongodb.evolutions.model.command

import java.io.FileNotFoundException

import com.evojam.mongodb.evolutions.util.Resources

trait Command {
  def value: String

  protected def loadResource(resourceName: String, args: String*) =
    Resources.load(resourceName)
      .map(content => args.length match {
        case count if count > 0 => content.format(args: _*)
        case _ => content
      }).getOrElse(throw new FileNotFoundException(s"Cannot read resource $resourceName"))
}
