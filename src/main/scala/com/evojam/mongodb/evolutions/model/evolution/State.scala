package com.evojam.mongodb.evolutions.model.evolution

import scala.util.control.Exception.catching

import play.api.libs.json._

object State extends Enumeration {
  type State = Value

  val Applied, ApplyingUp, ApplyingDown = Value

  def unapply(in: String) =
    catching(classOf[NoSuchElementException])
      .opt(State.withName(in))

  implicit object Format extends Format[State] {
    override def writes(in: State) = JsString(in.toString)
    override def reads(in: JsValue) = in match {
      case JsString(value) =>
        unapply(value).map(JsSuccess(_, __))
          .getOrElse(JsError(__, "validate.error.expected.State"))
      case _ => JsError(__, "validate.error.expected.State")
    }
  }
}
