package src.test.scala.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

import io.gatling.core.scenario.Simulation

class AddPauseTime extends Simulation{

  val httpConf=http.baseUrl("http://localhost:8888/app/").header("Accept","application/json")

  val scn = scenario("VideoGameDB-3 calls")
    .exec(http("Get All Video Games- 1 call")
    .get("videogames")).pause(5)
    .exec(http("Get Specific games-2nd cakk")
      .get("videogames/1")).pause(5)
    .exec(http("Get all video games-3 call").get("videogames")).pause(3000.milliseconds)

  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))
}
