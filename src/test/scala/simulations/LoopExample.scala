package src.test.scala.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class LoopExample extends Simulation{

  val httpConf=http.baseUrl("http://localhost:8888/app/").header("Accept","application/json")


  def getAllVideoGames(): ChainBuilder ={
    repeat(3){
      exec(http("Get All Video Games- 1 call")
        .get("videogames"))
    }
  }

  def getSpecificVideoGame(): ChainBuilder ={
    repeat(3) {
      exec(http("Get Specific games-2nd call")
        .get("videogames/1").check(status.in(200 to 210)))
    }
  }

  val scn1 = scenario("Code Reuse").exec(getAllVideoGames())
    .pause(5)
    .exec(getSpecificVideoGame())
    .pause(10)
    .exec(getAllVideoGames())

  setUp(scn1.inject(atOnceUsers(1)).protocols(httpConf))
}
