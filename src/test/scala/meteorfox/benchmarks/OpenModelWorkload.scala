package meteorfox.benchmarks

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class OpenModelWorkload extends Simulation {

  val HTTP_URL = System.getProperty("openmodelworkload.url")

  val HTTP_METHOD = System.getProperty("openmodelworkload.method", "GET")

  val HTTP_STATUS_CODE = Integer.getInteger("openmodelworkload.status.code", 200).toInt

  val HTTP_ACCEPT_HEADER = System.getProperty("openmodelworkload.acceptHeader",
    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

  val HTTP_ACCEPT_ENCODING_HEADER = System.getProperty(
    "openmodelworkload.acceptEncodingHeader", "gzip, deflate")

  val SCENARIO_NAME = System.getProperty("openmodelworkload.scenarioName")

  val PAGE_NAME = System.getProperty("openmodelworkload.pageName")

  val RAMP_UP_START : Double =
    System.getProperty("openmodelworkload.rampUpUsersPerSecStart").toDouble

  val RAMP_UP_END : Double =
    System.getProperty("openmodelworkload.rampUpUsersPerSecEnd").toDouble

  val RAMP_UP_TIME_SECS: Int =
    Integer.getInteger("openmodelworkload.rampUpDurationSecs")

  val STEADY_STATE: Double =
    System.getProperty("openmodelworkload.steadyStateUsersPerSec").toDouble

  val STEADY_TIME_SECS: Int =
    Integer.getInteger("openmodelworkload.steadyStateDurationSecs")

  val RAMP_DOWN_START : Double =
    System.getProperty("openmodelworkload.rampDownUsersPerSecStart").toDouble

  val RAMP_DOWN_END : Double =
    System.getProperty("openmodelworkload.rampDownUsersPerSecEnd").toDouble

  val RAMP_DOWN_TIME_SECS: Int =
    Integer.getInteger("openmodelworkload.rampDownDurationSecs")



  val httpProtocol = http
    .baseURL(HTTP_URL)
    .acceptHeader(HTTP_ACCEPT_HEADER)
    .acceptEncodingHeader(HTTP_ACCEPT_ENCODING_HEADER)
    .connection("keep-alive")
    .shareConnections


  val scn = scenario(SCENARIO_NAME)
    .group(PAGE_NAME)(
      exec(
        http(PAGE_NAME)
          .httpRequest(HTTP_METHOD, HTTP_URL)
          .check(status.is(_ => HTTP_STATUS_CODE))))


  setUp(scn.inject(
    rampUsersPerSec(RAMP_UP_START).to(RAMP_UP_END)
      .during(RAMP_UP_TIME_SECS seconds),
    constantUsersPerSec(STEADY_STATE).during(STEADY_TIME_SECS seconds),
    rampUsersPerSec(RAMP_DOWN_START).to(RAMP_DOWN_END)
      .during(RAMP_DOWN_TIME_SECS seconds)
  ).protocols(httpProtocol))

}
