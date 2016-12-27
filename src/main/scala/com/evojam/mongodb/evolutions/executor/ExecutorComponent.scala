
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

case class ConnectionFailed(msg: String) extends Exception(msg)
case class InvalidDatabaseEvolutionScript(msg: String) extends Exception(msg)
case class UnknownErrors(errors: List[String]) extends Exception(errors.mkString(";"))

trait ExecutorComponent {
  this: ConfigurationComponent
    with LoggerComponent =>

  val executor: Executor

  class ExecutorImpl extends Executor {
    override def executeAndCollect[T: Reads](cmd: Command) = {
      logger.debug(s"execute: ${cmd.value}")

      val input = inputFile(cmd)
      try {
        val processLogger = ListProcessLogger()
        processResult(
          runScript(input, processLogger),
          processLogger)
      } finally {
        input.delete()
        ()
      }
    }

    override def execute(cmd: Command) = {
      logger.debug(s"execute: ${cmd.value}")

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

    private def processResult[T: Reads](result: Int, processLogger: ListProcessLogger): Option[T] = {
      val output = processLogger.msgs.reverse.mkString("\n")

      logger.debug(s"result: $result, output: $output")

      result match {
        case 0 if output.nonEmpty =>
          catching(classOf[JsonParseException])
            .opt(Json.parse(output))
            .map(_.asOpt[T])
            .getOrElse(throw InvalidDatabaseEvolutionScript(s"Failed to parse result: $output"))
        case _ if processLogger.errors.nonEmpty =>
          throw decodeProcessExceptions(result, output, processLogger.errors)
        case _ =>
          throw InvalidDatabaseEvolutionScript(s"Failed to execute command: $result, $output")
      }
    }

    private def decodeProcessExceptions(result: Int, output: String, errors: List[String]): Exception = {
      val connectionErrorMsg = "exception: connect failed"

      errors.find(_.equals(connectionErrorMsg))
        .map(_ => ConnectionFailed(s"Failed to connect: $result, $output"))
        .getOrElse(UnknownErrors(errors))
    }

    private def runScript(script: File, processLogger: ProcessLogger = ListProcessLogger()): Int =
      runProcess(config.mongoCmd, s"--quiet ${script.getAbsolutePath}") ! processLogger

    private def runProcess(app: String, param: String) = {
      val cmd = app + " " + param
      val isWindows = config.isWindows

      logger.debug(s"run process - command: `$cmd`, isWindows: `$isWindows`")

      isWindows match {
        case true => Process("cmd" :: "/c" :: cmd :: Nil)
        case false => Process(cmd)
      }
    }

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
