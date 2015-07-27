package com.evojam.mongodb.evolutions.executor

import scala.sys.process.ProcessLogger

private[executor] case class ListProcessLogger(
  var msgs: List[String] = Nil,
  var errors: List[String] = Nil) extends ProcessLogger {

  require(msgs != null, "msgs cannot be null")
  require(errors != null, "errors cannot be null")

  override def out(s: => String) = msgs ::= s

  override def err(s: => String) = errors ::= s

  override def buffer[T](f: => T): T = f
}
