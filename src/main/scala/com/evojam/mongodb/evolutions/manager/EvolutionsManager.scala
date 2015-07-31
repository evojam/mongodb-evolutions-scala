package com.evojam.mongodb.evolutions.manager

import com.evojam.mongodb.evolutions.model.evolution.Action.Action
import com.evojam.mongodb.evolutions.model.evolution.Evolution

trait EvolutionsManager {
  def getAll(): List[Evolution]
  def getActions(): List[(Action, Evolution)]
}
