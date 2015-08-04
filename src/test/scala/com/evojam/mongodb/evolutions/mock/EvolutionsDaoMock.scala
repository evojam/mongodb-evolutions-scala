package com.evojam.mongodb.evolutions.mock

import com.evojam.mongodb.evolutions.dao.EvolutionsDao
import com.evojam.mongodb.evolutions.executor.ExecutorResult
import com.evojam.mongodb.evolutions.model.evolution.Evolution

class EvolutionsDaoMock(var evolutions: List[Evolution]) extends EvolutionsDao {
  override def get(revision: Int) =
    evolutions.find(_.revision == revision)

  override def removeAll() = {
    evolutions = Nil
    ExecutorResult.Success
  }

  override def insert(evolution: Evolution) = {
    evolutions = evolutions ::: List(evolution)
    ExecutorResult.Success
  }

  override def isProcessing() =
    false

  override def remove(revision: Int) = {
    evolutions = evolutions.filterNot(_.revision == revision)
    ExecutorResult.Success
  }

  override def remove(evolution: Evolution) =
    remove(evolution.revision)

  override def save(evolution: Evolution) =
    evolutions
      .find(_.revision == evolution.revision)
      .map(evo => {
        evolutions = evolutions.patch(
          evolutions.indexOf(evo),
          List(evolution), 1)
        ExecutorResult.Success
      })
      .getOrElse(ExecutorResult.Failure)

  override def getAll(): List[Evolution] =
    evolutions
}
