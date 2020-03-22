package src.test.scala.simulations

import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import scala.concurrent.duration._

class RampUsersLoadSimulation extends Simulation{

  val httpConf=http.baseUrl("http://localhost:8888/app/").header("Accept","application/json")

  def getAllVideoGames(): ChainBuilder ={
    exec(http("Get All Video Games").get("videogames").check(status.in(200 to 210)))
  }

  def getSpecificVideoGame(): ChainBuilder ={

    exec(http("Get Specific Video Games").get("videogames/1").check(status.in(200)))
  }

  val scn=scenario("Basic Load Simulation")
    .exec(getAllVideoGames())
    .pause(5)
    .exec(getSpecificVideoGame())
    .pause(10)
    //.exec(getAllVideoGames())


  setUp(scn.inject(nothingFor(5 seconds),
   // constantUsersPerSec(5) during(10 seconds))
    rampUsersPerSec(1) to (5) during(10))
    .protocols(httpConf.inferHtmlResources()))

}
