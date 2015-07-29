package com.evojam.mongodb.evolutions.executor

object ExecutorResult extends Enumeration {
  type ExecutorResult = Value

  val Success, Failure = Value
}
