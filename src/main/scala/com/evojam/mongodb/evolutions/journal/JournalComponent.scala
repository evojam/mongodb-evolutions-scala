package com.evojam.mongodb.evolutions.journal

import com.evojam.mongodb.evolutions.command.CommandsComponent
import com.evojam.mongodb.evolutions.executor.ExecutorComponent
import com.evojam.mongodb.evolutions.model.journal.Entry

trait JournalComponent {
  this:  ExecutorComponent
    with CommandsComponent =>

  val journal: Journal

  class JournalImpl extends Journal {
    override def push(entry: Entry) {
      executor.execute(commands.addToJournal(entry))
      ()
    }
  }
}
