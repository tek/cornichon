package com.github.agourlay.cornichon.examples

import com.github.agourlay.cornichon.ExampleServer
import com.github.agourlay.cornichon.core.CornichonFeature

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

class LowLevelScalaExamplesSpec extends CornichonFeature with ExampleServer {

  val baseUrl = s"http://localhost:$port"

  lazy implicit val requestTimeout: FiniteDuration = 2000 millis

  lazy val featureName = "Low level Scala Dsl test"

  lazy val scenarios = Seq(
    scenario("test scenario")(
      Given("A value") { s ⇒
        val x = 33 + 33
        val s2 = s.addValue("my-key", "crazy value")
        val s3 = s2.addValue("name-in-title", "String")
        (x, s3)
      }(66),
      When("I take a letter <name-in-title>") { s ⇒
        val x = 'A'
        (x, s)
      }('A'),
      When("I check the session") { s ⇒
        val x = s.getKey("my-key")
        (x, s)
      }(Some("crazy value"))
    )
  )
}