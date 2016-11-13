package com.github.agourlay.cornichon.steps.regular

import java.util.Timer

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }
import com.github.agourlay.cornichon.core._
import com.github.agourlay.cornichon.core.Done._
import com.github.agourlay.cornichon.core.Engine._

case class DebugStep(message: Session ⇒ String, title: String = "Debug step") extends Step {

  def setTitle(newTitle: String) = copy(title = newTitle)

  override def run(engine: Engine)(initialRunState: RunState)(implicit ec: ExecutionContext, timer: Timer) = {
    val (fullLogs, xor) = Try {
      message(initialRunState.session)
    } match {
      case Success(debugMessage) ⇒
        val runLogs = Vector(DebugLogInstruction(debugMessage, initialRunState.depth))
        (runLogs, rightDone)
      case Failure(e) ⇒
        val debugErrorLogs = errorLogs(title, e, initialRunState.depth)
        val failedStep = FailedStep.fromThrowable(this, e)
        (debugErrorLogs, Left(failedStep))
    }
    Future.successful(initialRunState.appendLogs(fullLogs), xor)
  }
}