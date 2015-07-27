package com.evojam.mongodb.evolutions

import org.scalatest._

class MongoEvolutionsSpec extends FlatSpec with Matchers {
  "MongoEvolutions" should "be instantiatable" in {
    val ev = new MongoEvolutions()
    ev shouldNot be (null)
  }
}
