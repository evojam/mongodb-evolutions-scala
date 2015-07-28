package com.evojam.mongodb.evolutions.model.evolution

import java.io.{IOException, File}

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

import com.evojam.mongodb.evolutions.model.evolution.State.State
import com.evojam.mongodb.evolutions.util.Resources

case class EvolutionException(msg: String) extends Exception(msg)

case class Evolution(
  revision: Int,
  up: Option[Script],
  down: Option[Script],
  state: State,
  timestamp: Option[DateTime],
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
    (__ \ 'timestamp).read[Option[DateTime]] ~
    (__ \ 'lastProblem).read[Option[String]])(
      Evolution.apply _)

  def fromFile(file: File): Evolution =
    Resources.load(file)
      .map(content => {
        val (down, up) = scripts(content)
        Evolution(
          revision(file.getName),
          up, down, State.Ready, None, None)
      }).getOrElse(throw new IOException(s"Cannot read from ${file.getAbsolutePath}"))

  private def revision(name: String): Int =
    name match {
      case EvolutionRevision(rev) =>
        rev.toInt
      case _ =>
        throw EvolutionException(s"Cannot parse the revision: $name")
    }

  private def scripts(content: String): (Option[Script], Option[Script]) =
    content.split('\n')
      .filter(_.nonEmpty)
      .foldLeft((List.empty[String], List.empty[String], Marker.Empty))(reduceLines) match {
        case (downs, ups, _) =>
          (script(downs), script(ups))
      }

  private type ReductionStep = (List[String], List[String], Marker.Value)

  private def reduceLines(step: ReductionStep, line: String): ReductionStep =
    step match {
      case (downs, ups, marker) => line match {
        case UpsMarker() => (downs, ups, Marker.Up)
        case DownsMarker() => (downs, ups, Marker.Down)
        case _ => marker match {
          case Marker.Up => (downs, line :: ups, marker)
          case Marker.Down => (line :: downs, ups, marker)
          case _ => (downs, ups, marker)
        }
      }
    }

  private def script(content: List[String]) =
    content.filter(_.nonEmpty) match {
      case lines if lines.nonEmpty =>
        Some(Script(lines.reverse.mkString("\n")))
      case _ => None
    }

  private val UpsMarker = "^//.*!Ups.*$".r
  private val DownsMarker = "^//.*!Downs.*$".r
  private val EvolutionRevision = "^(\\d+).js$".r
}
