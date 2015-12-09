package com.evojam.mongodb.evolutions.validator.input

import scalaz._, Scalaz._

import com.evojam.mongodb.evolutions.model.evolution.Evolution

case class EvolutionsInputError(msg: String) {
  require(msg != null, "msg cannot be null")
}

case class EvolutionsInputException(errors: Iterable[EvolutionsInputError])
  extends Exception(errors.map(_.msg).mkString(";"))

trait InputValidatorComponent {
  val inputValidator: InputValidator

  class InputValidatorImpl extends InputValidator {
    override def validate(input: List[Evolution]) =
      (validateCount(input) |@| validateUnique(input) |@| validateFirst(input))(
        (_, _, _) => ())

    private def validateCount =
      validate("Count of evolutions must be same as last revision")(
        evolutions =>
          evolutions.length === evolutions.maxBy(_.revision).revision)

    private def validateUnique =
      validate("Revisions must be unique")(
        evolutions =>
          evolutions.foldLeft(Set.empty[Int])(
            (acc, evolution) => acc + evolution.revision)
            .length === evolutions.length)

    private def validateFirst =
      validate("First revision must be 1")(
        _.minBy(_.revision).revision === 1)

    private def validate(failureMsg: String)(cond: List[Evolution] => Boolean) =
      (evolutions: List[Evolution]) =>
        cond(evolutions)
          .option(().successNel[EvolutionsInputError])
          .getOrElse(EvolutionsInputError(failureMsg).failureNel[Unit])
  }
}
