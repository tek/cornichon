package com.github.agourlay.cornichon.server

import org.scalatest.{ BeforeAndAfterAll, Suite }

import scala.concurrent.Await
import scala.concurrent.duration._

trait ExampleServer extends BeforeAndAfterAll {
  self: Suite ⇒

  lazy val port = 8080
  lazy val baseUrl = s"http://localhost:$port"
  val server = Await.result(new RestAPI().start(port), 5 second)

  override def afterAll() = {
    server.unbind()
  }
}