package com.evojam.mongodb.evolutions.journal

import com.evojam.mongodb.evolutions.clock.ClockComponent
import com.evojam.mongodb.evolutions.command.CommandsComponent
import com.evojam.mongodb.evolutions.executor.ExecutorComponent
import com.evojam.mongodb.evolutions.model.evolution.Evolution
import com.evojam.mongodb.evolutions.model.journal.Entry

trait JournalComponent {
  this:  ExecutorComponent
    with CommandsComponent
    with ClockComponent =>

  val journal: Journal

  class JournalImpl extends Journal {
    override def push(operation: String, evolution: Evolution) {
      executor.execute(
        commands.addToJournal(Entry(operation, clock.now, Some(evolution))))
      ()
    }

    override def push(operation: String) {
      executor.execute(
        commands.addToJournal(Entry(operation, clock.now, None)))
      ()
    }
  }
}
