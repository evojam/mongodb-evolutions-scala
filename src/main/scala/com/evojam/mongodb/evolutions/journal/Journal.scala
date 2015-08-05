package com.evojam.mongodb.evolutions.journal

import com.evojam.mongodb.evolutions.model.journal.Entry

trait Journal {
  def push(entry: Entry): Unit
}
