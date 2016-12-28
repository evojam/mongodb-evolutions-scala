package com.evojam.mongodb.evolutions.config

import scala.language.existentials
import scala.reflect.runtime.universe._
import scala.util.control.Exception.catching

import com.typesafe.config.{ConfigException, Config}

case class Setting[T: TypeTag](entry: String, default: Option[T]) {
  require(entry != null, "entry cannot be null")
  require(entry.nonEmpty, "entry cannot be empty")
  require(default != null, "default cannot be null")

  def value()(implicit config: Config): T = this match {
    case Setting(entry, default) if typeOf[T] <:< typeOf[Boolean] =>
      getOrDefault(config.getBoolean(entry))
    case Setting(entry, default) if typeOf[T] <:< typeOf[Int] =>
      getOrDefault(config.getInt(entry))
    case Setting(entry, default) if typeOf[T] <:< typeOf[Double] =>
      getOrDefault(config.getDouble(entry))
    case Setting(entry, default) if typeOf[T] <:< typeOf[String] =>
      getOrDefault(config.getString(entry))
    case _ => defaultValue
  }

  private def defaultValue: T =
    default.getOrElse(throw new Exception(s"There is no $entry configuration available!"))

  private def getOrDefault(getter: => R forSome { type R }): T = // scalastyle:ignore
    catching(classOf[ConfigException])
      .opt(getter).map(_.asInstanceOf[T])
      .getOrElse(defaultValue)
}
