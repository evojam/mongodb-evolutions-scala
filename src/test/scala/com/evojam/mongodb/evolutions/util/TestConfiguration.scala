package com.evojam.mongodb.evolutions.util

import com.typesafe.config.Config

import com.evojam.mongodb.evolutions.config.Setting

case class TestConfiguration(embeddedHost: String, embeddedPort: Int) {
  require(embeddedHost != null, "embeddedHost cannot be null")
  require((0 to 65535).contains(embeddedPort), "embeddedPort must be a valid port number")

}

object TestConfiguration {
  private val embeddedHost = Setting("mongodb.embedded.host", Some("localhost"))
  private val embeddedPort = Setting[Int]("mongodb.embedded.port", Some(27017))  // scalastyle:ignore


  def apply(implicit config: Config): TestConfiguration =
    apply(
      embeddedHost = embeddedHost.value,
      embeddedPort = embeddedPort.value)

}
