package com.evojam.mongodb.evolutions.config

import com.typesafe.config.Config

case class Configuration(useLocks: Boolean, lockDBName: String) {
  require(lockDBName != null, "lockDBName cannot be null")
  require(lockDBName.nonEmpty, "lockDBName cannot be empty")
}

object Configuration {
  val UseLocks = Setting("mongodb.evolution.useLocks", Some(false))
  val LockDBName = Setting("mongodb.evolution.lockDBName", Some("mongodb-evolutions-lock"))

  def apply(implicit config: Config): Configuration =
    Configuration(
      UseLocks.value,
      LockDBName.value)
}
