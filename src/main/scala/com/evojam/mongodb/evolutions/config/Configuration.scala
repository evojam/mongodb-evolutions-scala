package com.evojam.mongodb.evolutions.config

import com.typesafe.config.Config

case class Configuration(
  mongoCmd: String,
  useLocks: Boolean,
  evolutionsCollection: String,
  lockCollection: String,
  journalCollection: String,
  evolutionsPath: String,
  isWindows: Boolean) {

  require(mongoCmd != null, "mongoCmd cannot be null")
  require(mongoCmd.nonEmpty, "mongoCmd cannot be empty")
  require(evolutionsCollection != null, "evolutionsCollection cannot be null")
  require(evolutionsCollection.nonEmpty, "evolutionsCollection cannot be empty")
  require(lockCollection != null, "lockColName cannot be null")
  require(lockCollection.nonEmpty, "lockColName cannot be empty")
  require(journalCollection != null, "journalCollection cannot be null")
  require(journalCollection.nonEmpty, "journalCollection cannot be empty")
  require(evolutionsPath != null, "evolutionsPath cannot be null")
}

object Configuration {
  val MongoCmd = Setting[String]("mongodb.evolution.mongoCmd", None)
  val UseLocks = Setting("mongodb.evolution.useLocks", Some(false))
  val EvolutionsColName = Setting("mongodb.evolution.colName", Some("mongodbevolutions"))
  val LockColName = Setting("mongodb.evolution.lockColName", Some("mongodb_evolutions_lock"))
  val JournalColName = Setting("mongodb.evolution.journalColName", Some("mongo_evolutions_journal"))
  val EvolutionsPath = Setting("mongodb.evolution.evolutionsPath", Some("evolutions/"))

  def apply(implicit config: Config): Configuration =
    Configuration(
      mongoCmd = MongoCmd.value,
      useLocks = UseLocks.value,
      evolutionsCollection = EvolutionsColName.value,
      lockCollection = LockColName.value,
      journalCollection = JournalColName.value,
      evolutionsPath = EvolutionsPath.value,
      isWindows = System.getProperty("os.name").startsWith("Windows"))
}
