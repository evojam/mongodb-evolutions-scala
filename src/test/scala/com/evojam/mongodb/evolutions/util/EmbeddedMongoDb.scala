package com.evojam.mongodb.evolutions.util

import org.scalatest.{BeforeAndAfterAll, Suite}

import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net, RuntimeConfigBuilder}
import de.flapdoodle.embed.mongo.distribution.{IFeatureAwareVersion, Version}
import de.flapdoodle.embed.mongo.{Command, MongodExecutable, MongodProcess, MongodStarter}
import de.flapdoodle.embed.process.config.IRuntimeConfig
import de.flapdoodle.embed.process.config.io.ProcessOutput
import de.flapdoodle.embed.process.runtime.Network
import org.slf4j.LoggerFactory

import com.typesafe.config.ConfigFactory

sealed case class MongodProps(mongodProcess: MongodProcess, mongodExe: MongodExecutable)

trait EmbeddedMongoDb extends BeforeAndAfterAll { this: Suite =>

  private lazy val testConfig = TestConfiguration(ConfigFactory.load())

  private var mongoProces: Option[MongodProps] = None // scalastyle:ignore

  private lazy val logger = LoggerFactory.getLogger(getClass)

  private val mongoVersion = Version.Main.V3_2

  private lazy val runtimeConfig = new RuntimeConfigBuilder()
    .defaultsWithLogger(Command.MongoD, logger)
    .processOutput(ProcessOutput.getDefaultInstanceSilent)
    .build()

  override def beforeAll() = {
    logger.debug(s"Starting embedded MongoDB ${testConfig.embeddedHost}:${testConfig.embeddedPort}")
    mongoProces = Some(mongoStart(testConfig.embeddedHost, testConfig.embeddedPort))
  }

  override def afterAll() = {
    mongoProces.foreach { process =>
      logger.debug(s"Stop embedded MongoDB ${testConfig.embeddedHost}:${testConfig.embeddedPort}")
      mongoStop(process)
    }
  }

  private def mongoStart(bindIp: String, port: Int): MongodProps = {
    val mongodExe: MongodExecutable = mongodExec(bindIp, port, mongoVersion, runtimeConfig)
    MongodProps(mongodExe.start(), mongodExe)
  }

  private def mongoStop(mongodProps: MongodProps) = {
    Option(mongodProps).foreach(_.mongodProcess.stop())
    Option(mongodProps).foreach(_.mongodExe.stop())
  }

  private def runtime(config: IRuntimeConfig): MongodStarter =
    MongodStarter.getInstance(config)

  private def mongodExec(bindIp: String, port: Int, version: IFeatureAwareVersion,
                         runtimeConfig: IRuntimeConfig): MongodExecutable =
    runtime(runtimeConfig).prepare(
      new MongodConfigBuilder()
        .version(version)
        .net(new Net(bindIp, port, Network.localhostIsIPv6()))
        .build())
}
