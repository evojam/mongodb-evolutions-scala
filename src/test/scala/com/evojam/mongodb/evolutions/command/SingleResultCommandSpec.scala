package com.evojam.mongodb.evolutions.command

import org.scalatest._

import com.evojam.mongodb.evolutions.model.command.SingleResultCommand

class SingleResultCommandSpec extends FlatSpec with Matchers {
  val collection = "a-collection"
  val cmd = SingleResultCommand("script/sample.js.template", collection)

  "SingleResultCommand" should "read command from resources" in {
    cmd.value should not be empty
  }

  it should "begin with expected prefix" in {
    cmd.value should startWith ("var result =")
  }

  it should "end with expected suffix" in {
    cmd.value should endWith ("printjson(result);")
  }

  it should "contain collection name" in {
    cmd.value.contains(collection) should be (true)
  }
}
