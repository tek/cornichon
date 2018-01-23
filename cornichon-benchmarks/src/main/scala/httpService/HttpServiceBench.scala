package httpService

import java.util.concurrent.{ ExecutorService, Executors }

import cats.instances.string._
import com.github.agourlay.cornichon.core.{ Config, Session }
import com.github.agourlay.cornichon.http.{ HttpMethods, HttpRequest, HttpService }
import com.github.agourlay.cornichon.resolver.PlaceholderResolver
import org.openjdk.jmh.annotations._
import HttpServiceBench._
import com.github.agourlay.cornichon.http.client.NoOpHttpClient
import monix.execution.Scheduler

import scala.concurrent.Await
import scala.concurrent.duration._

@State(Scope.Benchmark)
@BenchmarkMode(Array(Mode.Throughput))
@Warmup(iterations = 20)
@Measurement(iterations = 20)
@Fork(value = 1, jvmArgsAppend = Array(
  "-XX:+UnlockCommercialFeatures",
  "-XX:+FlightRecorder",
  "-XX:StartFlightRecording=duration=60s,filename=./profiling-data.jfr,name=profile,settings=profile",
  "-XX:FlightRecorderOptions=settings=/Library/Java/JavaVirtualMachines/jdk1.8.0_162.jdk/Contents/Home/jre/lib/jfr/profile.jfc,samplethreads=true",
  "-Xmx1G"))
class HttpServiceBench {

  //sbt:benchmarks> jmh:run .*HttpService.*

  var es: ExecutorService = _
  val client = new NoOpHttpClient
  var httpService: HttpService = _

  @Setup(Level.Trial)
  final def beforeAll(): Unit = {
    es = Executors.newFixedThreadPool(1)
    val scheduler = Scheduler(es)
    httpService = new HttpService("", 2000.millis, client, PlaceholderResolver.withoutExtractor(), Config())(scheduler)
  }

  @TearDown(Level.Trial)
  final def afterAll(): Unit = {
    es.shutdown()
  }

  //  [info] Benchmark                                    Mode  Cnt      Score      Error  Units
  //  [info] httpService.HttpServiceBench.singleRequest  thrpt   20  63073.377 ± 8119.694  ops/s
  @Benchmark
  def singleRequest() = {
    val f = httpService.requestEffect(request)
    val res = Await.result(f(session), Duration.Inf)
    assert(res.isRight)
  }
}

object HttpServiceBench {
  val session = Session.newEmpty
  val request = HttpRequest[String](
    method = HttpMethods.GET,
    url = "https://myUrl/my/segment",
    body = Some(""" { "k1":"v1", "k2":"v2","k3":"v3","k4":"v4" } """),
    params = ("q1", "v1") :: ("q2", "v2") :: ("q3", "v3") :: Nil,
    headers = ("h1", "v1") :: ("h2", "v2") :: ("h3", "v3") :: Nil)
}