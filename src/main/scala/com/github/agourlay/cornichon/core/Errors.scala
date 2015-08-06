package com.github.agourlay.cornichon.core

import spray.json.JsValue

trait CornichonError extends Exception {
  val msg: String
}

case class StepExecutionError[A](title: String, exception: Throwable) extends CornichonError {
  val msg = s"step '$title' failed by throwing exception ${exception.printStackTrace()}"
}

case class StepAssertionError[A](title: String, expected: A, actual: A) extends CornichonError {
  private val baseMsg = s"step '$title' did not pass assertion, expected was '$expected' but actual result is '$actual'"

  // TODO offer better diff
  val msg = actual match {
    case s: String  ⇒ s"$baseMsg - diff is '${s.diff(expected.asInstanceOf[String])}'"
    case j: JsValue ⇒ s"$baseMsg - diff is '${j.prettyPrint.diff(expected.asInstanceOf[JsValue].prettyPrint)}'"
    case _          ⇒ baseMsg
  }

}

case class ResolverError(key: String) extends CornichonError {
  val msg = s"key '<$key>' can not be resolved"
}

case class SessionError(title: String, key: String) extends CornichonError {
  val msg = s"key '$key' can not be found session for step '$title'"
}

case class KeyNotFoundInSession(key: String) extends Exception