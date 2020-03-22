package src.test.scala.simulations

import io.gatling.core.scenario.Simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._


class JsonPath extends Simulation{

  val httpConf=http.baseUrl("http://localhost:8888/app/").header("Accept","application/json")


  val scn=scenario("JsonPath Check")
      .exec(http("Get Video Games with id").get("videogames/1")
        .check(jsonPath("$.name").is("Resident Evil 4")))

    .exec(http("Get all video games").get("videogames")
        .check(jsonPath("$[1].id").saveAs("gameId")))
    .exec { session => println (session); session}

      .exec(http("Get Video Games with id")
        .get("videogames/${gameId}")
        .check(jsonPath("$.name").is("Gran Turismo 3"))
      .check(bodyString.saveAs("responseBody")))
      .exec {session => println(session("responseBody").as[String]); session}


  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))


}
