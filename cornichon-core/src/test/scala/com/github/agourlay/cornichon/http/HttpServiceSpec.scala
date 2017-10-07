package com.github.agourlay.cornichon.http

import cats.scalatest.{ EitherMatchers, EitherValues }
import cats.instances.string._
import com.github.agourlay.cornichon.core.{ Config, Session }
import com.github.agourlay.cornichon.http.HttpMethods.GET
import com.github.agourlay.cornichon.http.client.Http4sClient
import com.github.agourlay.cornichon.resolver.PlaceholderResolver
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class HttpServiceSpec extends WordSpec
  with Matchers
  with BeforeAndAfterAll
  with EitherValues
  with EitherMatchers {

  val client = new Http4sClient()
  val service = new HttpService("", 2000 millis, client, PlaceholderResolver.withoutExtractor(), Config())

  override def afterAll() = {
    client.shutdown()
  }

  "HttpService" when {
    "fillInSessionWithResponse" must {
      "extract content with NoOpExtraction" in {
        val resp = CornichonHttpResponse(200, Nil, "hello world")
        val filledSession = service.fillInSessionWithResponse(Session.newEmpty, resp, NoOpExtraction)
        filledSession.value.get("last-response-status") should beRight("200")
        filledSession.value.get("last-response-body") should beRight("hello world")
      }

      "extract content with RootResponseExtraction" in {
        val resp = CornichonHttpResponse(200, Nil, "hello world")
        val filledSession = service.fillInSessionWithResponse(Session.newEmpty, resp, RootExtractor("copy-body"))
        filledSession.value.get("last-response-status") should beRight("200")
        filledSession.value.get("last-response-body") should beRight("hello world")
        filledSession.value.get("copy-body") should beRight("hello world")
      }

      "extract content with PathResponseExtraction" in {
        val resp = CornichonHttpResponse(200, Nil,
          """
            {
              "name" : "batman"
            }
          """)
        val filledSession = service.fillInSessionWithResponse(Session.newEmpty, resp, PathExtractor("name", "part-of-body"))
        filledSession.value.get("last-response-status") should beRight("200")
        filledSession.value.get("last-response-body") should beRight(
          """
            {
              "name" : "batman"
            }
          """
        )
        filledSession.value.get("part-of-body") should beRight("batman")
      }
    }

    "decodeSessionHeaders" must {
      "fail if wrong format" in {
        HttpService.decodeSessionHeaders("headerkey-headervalue") should be(left)
      }
    }

    "configureRequest" must {
      "add accept gzip according to config - true" in {
        val service = new HttpService("", 2000 millis, client, PlaceholderResolver.withoutExtractor(), Config(addAcceptGzipByDefault = true))
        service.configureRequest(HttpRequest[String](GET, "", None, Nil, Nil)).headers should be(("Accept-Encoding" -> "gzip") :: Nil)
      }

      "add accept gzip according to config - false" in {
        val service = new HttpService("", 2000 millis, client, PlaceholderResolver.withoutExtractor(), Config(addAcceptGzipByDefault = false))
        service.configureRequest(HttpRequest[String](GET, "", None, Nil, Nil)).headers should be(Nil)
      }

    }
  }
}
