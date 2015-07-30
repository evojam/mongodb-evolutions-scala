package com.evojam.mongodb.evolutions.dao

import com.evojam.mongodb.evolutions.executor.ExecutorResult.ExecutorResult
import com.evojam.mongodb.evolutions.model.evolution.Evolution

trait EvolutionsDao {
  def get(revision: Int): Option[Evolution]
  def getAll(): List[Evolution]
  def insert(evolution: Evolution): ExecutorResult
  def save(evolution: Evolution): ExecutorResult
  def remove(revision: Int): ExecutorResult
  def removeAll(): ExecutorResult
  def isProcessing(): Boolean
}
