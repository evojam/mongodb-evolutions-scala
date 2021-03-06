package com.evojam.mongodb.evolutions

import com.evojam.mongodb.evolutions.model.evolution.{Evolution, State, Action}

case class MongoEvolutionsException(msg: String) extends Exception(msg)

class MongoEvolutions extends MongoEvolutionsComponent {
  def applyUp(evolution: Evolution) {
    logger.info(s"Applying up: ${evolution.revision}")

    dao.insert(evolution.copy(state = Some(State.ApplyingUp)))
    executor.execute(commands.applyScript(evolution.up))
    dao.save(evolution.copy(state = Some(State.Applied)))
    ()
  }

  def applyDown(evolution: Evolution) {
    logger.info(s"Applying down: ${evolution.revision}")

    evolution.down
      .foreach { downScript =>
        dao.save(evolution.copy(state = Some(State.ApplyingDown)))
        executor.execute(commands.applyScript(downScript))
        dao.remove(evolution)
      }
  }

  def update(evolution: Evolution) {
    logger.info(s"Updating ${evolution.revision}")
    dao.save(evolution.copy(state = Some(State.Applied)))
    ()
  }

  def applyEvolutions() {
    logger.info("Apply evolutions")

    guard.withLock {
      if (noPendingEvolution) {
        evolutionsManager.getActions().foreach {
          case (Action.ApplyUp, evolution) =>
            applyUp(evolution)
          case (Action.ApplyDown, evolution) =>
            applyDown(evolution)
          case (Action.Update, evolution) =>
            update(evolution)
        }
      } else {
        throw MongoEvolutionsException("There are some pending evolutions.")
      }
    }
  }

  private def noPendingEvolution(): Boolean =
    dao.getAll
      .filterNot(_.state == Some(State.Applied))
      .isEmpty
}
