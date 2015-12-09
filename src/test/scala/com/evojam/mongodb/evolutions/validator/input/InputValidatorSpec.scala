package com.evojam.mongodb.evolutions.validator.input

import com.evojam.mongodb.evolutions.mock.Evolutions
import org.scalatest._

class InputValidatorSpec extends FlatSpec with Matchers
  with InputValidatorComponent
  with Evolutions {

  override val inputValidator = new InputValidatorImpl()

  "InputValidator" should
    "pass on correct input" in {
    inputValidator.validate(predefEvolutions)
      .toOption should be (Some(()))
  }

  it should "fail when evolutions starts with number different that one" in {
    inputValidator.validate(predefEvolutions.tail)
      .toOption should be (None)
  }

  it should "fail when revisions are not unique" in {
    inputValidator.validate(predefEvolutions ::: List(predefEvolution05))
      .toOption should be (None)
  }

  it should "fail when count of evolutions doesn't equal last revision" in {
    inputValidator.validate(predefEvolutions ::: List(predefEvolution07))
      .toOption should be (None)
  }
}
