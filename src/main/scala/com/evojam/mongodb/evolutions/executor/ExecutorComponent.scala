package com.evojam.mongodb.evolutions.executor

import java.io.{PrintWriter, File}

import scala.language.postfixOps
import scala.sys.process.{ProcessLogger, Process}
import scala.util.control.Exception.catching

import com.fasterxml.jackson.core.JsonParseException
import play.api.libs.json.{Json, Reads}
import com.evojam.mongodb.evolutions.config.ConfigurationComponent
import com.evojam.mongodb.evolutions.model.command.Command
import com.evojam.mongodb.evolutions.util.LoggerComponent

case class InvalidDatabaseEvolutionScript(msg: String) extends Exception

trait ExecutorComponent {
  this: ConfigurationComponent
    with LoggerComponent =>

  val executor: Executor

  class ExecutorImpl extends Executor {
    override def executeAndCollect[T: Reads](cmd: Command) = {
      logger.info(s"execute: ${cmd.value}")

      val input = inputFile(cmd)
      try {
        val processLogger = ListProcessLogger()
        processResult(
          runScript(input, processLogger),
          processLogger.msgs.reverse.mkString("\n"))
      } finally {
        input.delete()
        ()
      }
    }

    override def execute(cmd: Command) = {
      logger.info(s"execute: ${cmd.value}")

      val input = inputFile(cmd)
      try {
        runScript(input) match {
          case 0 => ExecutorResult.Success
          case _ => ExecutorResult.Failure
        }
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
        case _ =>
          throw InvalidDatabaseEvolutionScript(s"Failed to execute command: $result, $output")
      }

    private def runScript(script: File, processLogger: ProcessLogger = ListProcessLogger()): Int =
      runProcess(config.mongoCmd, s"--quiet ${script.getAbsolutePath}") ! processLogger

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
        pw.print(cmd.value)
      }
      input
    }
  }
}
