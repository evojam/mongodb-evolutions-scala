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
      executor.execute[Lock](commands.releaseLock)

    private def acquireLockAndExecute(block: => Unit) {
      executor.execute[Lock](commands.acquireLock) match {
        case Some(lock) =>
          lock.locked match {
            case true =>
              try { block }
              finally {
                unlock()
                ()
              }
            case false =>
              logger.error(s"The db is already locked by another process.")
          }
        case _ =>
          logger.error(s"Failed to acquire lock.")
      }
    }
  }
}
