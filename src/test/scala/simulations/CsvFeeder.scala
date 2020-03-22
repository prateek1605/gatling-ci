package src.test.scala.simulations

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class CsvFeeder extends Simulation{

  val httpConf=http.baseUrl("http://localhost:8888/app/").header("Accept","application/json")

  val csvFile=csv("data/csvFeederData.csv").circular

  def getAllVideoGames(): ChainBuilder ={
    repeat(10) {
      feed(csvFile).exec(http("Get Specific VideoGames").get("videogames/${gameid}").check(status.in(200 to 210))
      .check(jsonPath("$.name").is("${gamename}")))
        .pause(5)
    }
  }

  val scn =scenario("CsvFeeder")
    .exec(getAllVideoGames())

  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))

}
