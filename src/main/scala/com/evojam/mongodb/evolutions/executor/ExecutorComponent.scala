package com.evojam.mongodb.evolutions.executor

import java.io.{PrintWriter, File}

import scala.language.postfixOps
import scala.sys.process.Process
import scala.util.control.Exception.catching

import com.fasterxml.jackson.core.JsonParseException
import play.api.libs.json.{Json, Reads}

import com.evojam.mongodb.evolutions.command.Command
import com.evojam.mongodb.evolutions.config.ConfigurationComponent
import com.evojam.mongodb.evolutions.util.LoggerComponent

case class InvalidDatabaseEvolutionScript(msg: String) extends Exception

trait ExecutorComponent {
  this: ConfigurationComponent
    with LoggerComponent =>

  val executor: Executor

  class ExecutorImpl extends Executor {
    override def execute[T: Reads](cmd: Command) = {
      logger.info(s"execute: ${cmd.command}")

      val input = inputFile(cmd)
      try {
        val processLogger = ListProcessLogger()
        processResult(
          runProcess(config.mongoCmd, s"--quiet ${input.getAbsolutePath}") ! processLogger,
          processLogger.msgs.reverse.mkString("\n"))
      } finally {
        input.delete()
        ()
      }
    }

    private def processResult[T: Reads](result: Int, output: String): Option[T] =
      result match {
        case 0 if output.nonEmpty =>
          catching(classOf[JsonParseException])
            .opt(Json.parse(cleanUpResult(output)))
            .map(_.asOpt[T])
            .getOrElse(throw InvalidDatabaseEvolutionScript(s"Failed to parse result: $output"))
      }

    private def runProcess(app: String, param: String) = {
      val cmd = app + " " + param
      config.isWindows match {
        case true => Process("cmd" :: "/c" :: cmd :: Nil)
        case false => Process(cmd)
      }
    }

    private def cleanUpResult(input: String) =
      ("ObjectId\\(([\"a-zA-Z0-9]*)\\)"r)
        .replaceAllIn(input, _.group(1))

    private def printToFile(file: File)(op: PrintWriter => Unit) {
      val pw = new PrintWriter(file)
      try { op(pw) }
      finally { pw.close() }
    }

    private def inputFile(cmd: Command): File = {
      val input = File.createTempFile("mongodb-script", ".js")
      printToFile(input) { pw =>
        pw.print(cmd.command)
      }
      input
    }
  }
}
