package com.evojam.mongodb.evolutions.validator.input

import scalaz._, Scalaz._

import com.evojam.mongodb.evolutions.model.evolution.Evolution

trait InputValidator {
  def validate(input: List[Evolution])
    : Validation[NonEmptyList[EvolutionsInputError], Unit]
}
