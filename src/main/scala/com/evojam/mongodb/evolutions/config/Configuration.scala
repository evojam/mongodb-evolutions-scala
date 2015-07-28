package com.evojam.mongodb.evolutions.config

import com.typesafe.config.Config

case class Configuration(
  mongoCmd: String,
  useLocks: Boolean,
  lockDBName: String,
  evolutionsPath: String,
  isWindows: Boolean) {

  require(mongoCmd != null, "mongoCmd cannot be null")
  require(mongoCmd.nonEmpty, "mongoCmd cannot be empty")
  require(lockDBName != null, "lockDBName cannot be null")
  require(lockDBName.nonEmpty, "lockDBName cannot be empty")
  require(evolutionsPath != null, "evolutionsPath cannot be null")
}

object Configuration {
  val MongoCmd = Setting[String]("mongodb.evolution.mongoCmd", None)
  val UseLocks = Setting("mongodb.evolution.useLocks", Some(false))
  val LockDBName = Setting("mongodb.evolution.lockDBName", Some("mongodb-evolutions-lock"))
  val EvolutionsPath = Setting("mongodb.evolution.evolutionsPath", Some("evolutions/"))

  def apply(implicit config: Config): Configuration =
    Configuration(
      MongoCmd.value,
      UseLocks.value,
      LockDBName.value,
      EvolutionsPath.value,
      System.getProperty("os.name").startsWith("Windows"))
}
