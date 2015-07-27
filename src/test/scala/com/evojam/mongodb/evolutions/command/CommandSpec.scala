package com.evojam.mongodb.evolutions.command

import org.scalatest._

class CommandSpec extends FlatSpec with Matchers {
  val collection = "a-collection"
  val cmd = Command("lock/acquireLock.js", collection)

  "Command" should "read command from resources" in {
    cmd.command should not be empty
  }

  it should "begin with expected prefix" in {
    cmd.command should startWith (cmd.CommandPrefix)
  }

  it should "end with expected suffix" in {
    cmd.command should endWith (cmd.CommandSuffix)
  }

  it should "contain collection name" in {
    cmd.command.contains(collection) should be (true)
  }
}
