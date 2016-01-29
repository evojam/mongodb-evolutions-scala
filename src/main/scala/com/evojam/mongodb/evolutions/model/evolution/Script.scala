package com.evojam.mongodb.evolutions.model.evolution

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

import play.api.libs.json._

case class Script(value: String) {
  require(value != null, "value cannot be null")
  require(value.nonEmpty, "value cannot be empty")

  def md5(): String =
    Base64.getEncoder().encodeToString(
      MessageDigest.getInstance("MD5")
        .digest(value.trim.getBytes(StandardCharsets.UTF_8)))
}

object Script {
  implicit object ScriptFormat extends Format[Script] {
    override def reads(in: JsValue) =
      in match {
        case JsString(value) =>
          JsSuccess(Script(value), __)
        case _ => JsError(__, "validate.error.expected.Script")
      }

    override def writes(in: Script) =
      JsString(in.value)
  }
}
