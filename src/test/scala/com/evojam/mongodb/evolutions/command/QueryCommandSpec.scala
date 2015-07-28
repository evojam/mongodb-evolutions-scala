package com.evojam.mongodb.evolutions.command

import org.scalatest._

import com.evojam.mongodb.evolutions.model.command.QueryCommand

class QueryCommandSpec extends FlatSpec with Matchers {
  val dbName = "db-name"
  val cmd = QueryCommand(dbName, "query/all.js.template")

  "QueryCommand" should "read command from resources" in {
    cmd.value should not be empty
  }

  it should "begin with expected prefix" in {
    cmd.value should startWith ("var cursor =")
  }

  it should "contain database name" in {
    cmd.value.contains(dbName) should be (true)
  }
}
