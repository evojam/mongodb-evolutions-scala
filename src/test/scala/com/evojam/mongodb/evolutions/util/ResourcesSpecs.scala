package com.evojam.mongodb.evolutions.util

import java.io.File

import org.scalatest._

class ResourcesSpecs extends FlatSpec with Matchers {
  "Resources" should "load embedded resource" in {
    val res = Resources.load("evolutions/1.js")
    res.isDefined should be (true)
  }

  it should "load from file" in {
    val res = Resources.load(new File("src/test/resources/evolutions/1.js"))
    res.isDefined should be (true)
  }
}
