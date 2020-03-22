package src.test.scala.simulations



  import io.gatling.core.scenario.Simulation
  import io.gatling.core.Predef._
  import io.gatling.core.structure.ChainBuilder
  import io.gatling.http.Predef._


  class CsvFeederToCustom extends Simulation {


    val httpConf=http.baseUrl("http://localhost:8888/app/").header("Accept","application/json")

    val gameid= (1 to 10).iterator

    val customFeed=Iterator.continually(Map("gameId" -> gameid.next()))

    val csvFile=csv("data/csvFeederData.csv").circular

    def getAllVideoGames(): ChainBuilder ={
      repeat(10) {
        feed(customFeed).exec(http("Get Specific VideoGames").get("videogames/${gameId}")
          .check(status.in(200 to 210)))
          .pause(2)
      }
    }

    val scn =scenario("CsvFeeder")
      .exec(getAllVideoGames())

    setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))


}
