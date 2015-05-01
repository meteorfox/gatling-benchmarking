package meteorfox.benchmarks

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._


class LoadBalancedOpenModelWorkload extends Simulation {
  val HTTP_URLS = System.getProperty("loadbalancedworkload.baseURLs")

  val HTTP_RESOURCE_PATH = System.getProperty(
    "loadbalancedworkload.resourcePath","")

  val HTTP_METHOD = System.getProperty("loadbalancedworkload.method", "GET")

  val HTTP_STATUS_CODE = Integer.getInteger("loadbalancedworkload.status.code", 200).toInt

  val HTTP_ACCEPT_HEADER = System.getProperty("loadbalancedworkload.acceptHeader",
    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

  val HTTP_ACCEPT_ENCODING_HEADER = System.getProperty(
    "loadbalancedworkload.acceptEncodingHeader", "gzip, deflate")

  val SCENARIO_NAME = System.getProperty("loadbalancedworkload.scenarioName")

  val PAGE_NAME = System.getProperty("loadbalancedworkload.pageName")

  val RAMP_UP_START : Double =
    System.getProperty("loadbalancedworkload.rampUpUsersPerSecStart").toDouble

  val RAMP_UP_END : Double =
    System.getProperty("loadbalancedworkload.rampUpUsersPerSecEnd").toDouble

  val RAMP_UP_TIME_SECS: Int =
    Integer.getInteger("loadbalancedworkload.rampUpDurationSecs")

  val STEADY_STATE: Double =
    System.getProperty("loadbalancedworkload.steadyStateUsersPerSec").toDouble

  val STEADY_TIME_SECS: Int =
    Integer.getInteger("loadbalancedworkload.steadyStateDurationSecs")

  val RAMP_DOWN_START : Double =
    System.getProperty("loadbalancedworkload.rampDownUsersPerSecStart").toDouble

  val RAMP_DOWN_END : Double =
    System.getProperty("loadbalancedworkload.rampDownUsersPerSecEnd").toDouble

  val RAMP_DOWN_TIME_SECS: Int =
    Integer.getInteger("loadbalancedworkload.rampDownDurationSecs")



  val httpProtocol = http
    .baseURLs(HTTP_URLS.split(',').toList)
    .acceptHeader(HTTP_ACCEPT_HEADER)
    .acceptEncodingHeader(HTTP_ACCEPT_ENCODING_HEADER)
    .connection("keep-alive")
    .shareConnections


  val scn = scenario(SCENARIO_NAME)
    .group(PAGE_NAME)(
      exec(
        http(PAGE_NAME)
          .httpRequest(HTTP_METHOD, HTTP_RESOURCE_PATH)
          .check(status.is(_ => HTTP_STATUS_CODE))))


  setUp(scn.inject(
    rampUsersPerSec(RAMP_UP_START).to(RAMP_UP_END)
      .during(RAMP_UP_TIME_SECS seconds),
    constantUsersPerSec(STEADY_STATE).during(STEADY_TIME_SECS seconds),
    rampUsersPerSec(RAMP_DOWN_START).to(RAMP_DOWN_END)
      .during(RAMP_DOWN_TIME_SECS seconds)
  ).protocols(httpProtocol))
}
