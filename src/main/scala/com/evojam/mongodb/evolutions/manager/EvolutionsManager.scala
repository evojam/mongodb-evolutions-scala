package com.evojam.mongodb.evolutions.manager

import com.evojam.mongodb.evolutions.model.evolution.Evolution

trait EvolutionsManager {
  def getAll(): List[Evolution]
}
