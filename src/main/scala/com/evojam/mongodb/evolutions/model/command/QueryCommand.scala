package com.evojam.mongodb.evolutions.model.command

case class QueryCommand(db: String, queryResource: String, queryArgs: String*) extends Command {
  require(db != null, "db cannot be null")
  require(db.nonEmpty, "db cannot be empty")
  require(queryResource != null, "queryResource cannot be null")
  require(queryResource.nonEmpty, "queryResource cannot be empty")

  override lazy val value =
    loadResource("command/queryCommand.js.template")
      .format(db, query)

  private lazy val query =
    loadResource(queryResource, queryArgs: _*)
}
