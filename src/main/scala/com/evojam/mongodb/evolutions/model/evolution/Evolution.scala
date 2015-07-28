package com.evojam.mongodb.evolutions.model.evolution

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

import com.evojam.mongodb.evolutions.model.evolution.State.State

case class Evolution(
  revision: Int,
  up: Option[Script],
  down: Option[Script],
  state: State,
  timestamp: DateTime,
  lastProblem: Option[String]) {

  require(revision > 0, "revision must be greater than 0")
  require(up != null, "up cannot be null")
  require(down != null, "down cannot be null")
  require(state != null, "state cannot be null")
  require(timestamp != null, "timestamp cannot be null")
  require(lastProblem != null, "lastProblem cannot be null")

  def hash(): String =
    up.map(_.md5).getOrElse("") +
      down.map(_.md5).getOrElse("")
}

object Evolution {
  implicit val writes = new Writes[Evolution] {
    override def writes(evo: Evolution) = Json.obj(
      "revision" -> evo.revision,
      "up" -> evo.up,
      "down" -> evo.down,
      "state" -> evo.state,
      "timestamp" -> evo.timestamp,
      "lastProblem" -> evo.lastProblem,
      "hash" -> evo.hash)
  }

  implicit val reads = (
    (__ \ 'revision).read[Int] ~
    (__ \ 'up).read[Option[Script]] ~
    (__ \ 'down).read[Option[Script]] ~
    (__ \ 'state).read[State] ~
    (__ \ 'timestamp).read[DateTime] ~
    (__ \ 'lastProblem).read[Option[String]])(
      Evolution.apply _)
}
