package com.evojam.mongodb.evolutions.model.evolution

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

import com.kifi.macros.json

@json case class Script(value: String) {
  require(value != null, "value cannot be null")
  require(value.nonEmpty, "value cannot be empty")

  def md5(): String =
    Base64.getEncoder().encodeToString(
      MessageDigest.getInstance("MD5")
        .digest(value.trim.getBytes(StandardCharsets.UTF_8)))
}
