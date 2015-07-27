package com.evojam.mongodb.evolutions.guard

import play.api.libs.json.Json

case class Lock(locked: Boolean)

object Lock {
  implicit val format = Json.format[Lock]
}
