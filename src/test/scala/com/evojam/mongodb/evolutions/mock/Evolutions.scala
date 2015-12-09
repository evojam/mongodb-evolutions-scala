package com.evojam.mongodb.evolutions.mock

import com.evojam.mongodb.evolutions.model.evolution.{Script, Evolution}

trait Evolutions {
  val findScript = Script(
    """db.collection.find({
      |  'name': 'aname'
      |});""".stripMargin)

  val setSthSctipt = Script(
    "db.collection.update({}, { '$set': { 'sth': 'AName' } }, { 'multi': true });")

  val setSthModifiedSctipt = Script(
    "db.collection.update({}, { '$set': { 'sth': 'Name' } }, { 'multi': true });")

  val unsetSthScript = Script(
    "db.collection.update({}, { '$unset': { 'sth': true } }, { 'multi': true });")

  val unsetSthModifiedScript = Script(
    "db.collection.update({}, { '$unset': { 'sthelse': true } }, { 'multi': true });")

  val predefEvolution01 = Evolution(1, findScript, None, None, None, None)
  val predefEvolution02 = Evolution(2, findScript, Some(findScript), None, None, None)
  val predefEvolution03 = Evolution(3, setSthSctipt, Some(unsetSthScript), None, None, None)
  val predefEvolution03ModifiedUp = Evolution(3, setSthModifiedSctipt, Some(unsetSthScript), None, None, None)
  val predefEvolution03ModifiedDown = Evolution(3, setSthSctipt, Some(unsetSthModifiedScript), None, None, None)
  val predefEvolution04 = Evolution(4, setSthSctipt, None, None, None, None)
  val predefEvolution05 = Evolution(5, setSthSctipt, Some(unsetSthScript), None, None, None)
  val predefEvolution05ModifiedUp = Evolution(5, setSthModifiedSctipt, Some(unsetSthScript), None, None, None)
  val predefEvolution05ModifiedDown = Evolution(5, setSthSctipt, Some(unsetSthModifiedScript), None, None, None)
  val predefEvolution06 = Evolution(6, findScript, Some(unsetSthScript), None, None, None)
  val predefEvolution07 = Evolution(7, findScript, Some(unsetSthModifiedScript), None, None, None)

  val predefEvolutions = List(
    predefEvolution01,
    predefEvolution02,
    predefEvolution03,
    predefEvolution04,
    predefEvolution05)
}
