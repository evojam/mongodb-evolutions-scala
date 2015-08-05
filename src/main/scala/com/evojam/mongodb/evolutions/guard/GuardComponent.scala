package com.evojam.mongodb.evolutions.guard

import com.evojam.mongodb.evolutions.command.CommandsComponent
import com.evojam.mongodb.evolutions.config.ConfigurationComponent
import com.evojam.mongodb.evolutions.executor.ExecutorComponent
import com.evojam.mongodb.evolutions.util.LoggerComponent

trait GuardComponent {
  this: ConfigurationComponent
    with CommandsComponent
    with ExecutorComponent
    with LoggerComponent =>

  val guard: Guard

  class GuardImpl extends Guard {
    override def withLock(block: => Unit) {
      config.useLocks match {
        case true => acquireLockAndExecute(block)
        case false => block
      }
    }

    private def unlock() =
      executor.executeAndCollect[Lock](commands.releaseLock)

    private def isLocked() =
      executor.executeAndCollect[Lock](commands.getLock)
        .map(_.locked).getOrElse(false)

    private def acquireLockAndExecute(block: => Unit) {
      isLocked match {
        case true =>
          logger.error("The db is already locked by another process.")
        case false =>
          executor.executeAndCollect[Lock](commands.acquireLock) match {
            case Some(Lock(true)) =>
              try { block }
              finally {
                unlock()
                ()
              }
            case _ =>
              logger.error("Failed to acquire lock.")
          }
      }
    }
  }
}
