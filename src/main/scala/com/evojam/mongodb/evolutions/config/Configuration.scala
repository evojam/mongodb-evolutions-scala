package com.evojam.mongodb.evolutions.config

import com.typesafe.config.Config

case class Configuration(
  mongoCmd: String,
  useLocks: Boolean,
  evolutionsCollection: String,
  lockCollection: String,
  evolutionsPath: String,
  isWindows: Boolean) {

  require(mongoCmd != null, "mongoCmd cannot be null")
  require(mongoCmd.nonEmpty, "mongoCmd cannot be empty")
  require(evolutionsCollection != null, "evolutionsCollection cannot be null")
  require(evolutionsCollection.nonEmpty, "evolutionsCollection cannot be empty")
  require(lockCollection != null, "lockColName cannot be null")
  require(lockCollection.nonEmpty, "lockColName cannot be empty")
  require(evolutionsPath != null, "evolutionsPath cannot be null")
}

object Configuration {
  val MongoCmd = Setting[String]("mongodb.evolution.mongoCmd", None)
  val UseLocks = Setting("mongodb.evolution.useLocks", Some(false))
  val EvolutionsColName = Setting("mongodb.evolution.colName", Some("mongodbevolutions"))
  val LockColName = Setting("mongodb.evolution.lockColName", Some("mongodb-evolutions-lock"))
  val EvolutionsPath = Setting("mongodb.evolution.evolutionsPath", Some("evolutions/"))

  def apply(implicit config: Config): Configuration =
    Configuration(
      MongoCmd.value,
      UseLocks.value,
      EvolutionsColName.value,
      LockColName.value,
      EvolutionsPath.value,
      System.getProperty("os.name").startsWith("Windows"))
}
