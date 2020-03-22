package src.test.scala.simulations

import com.fasterxml.jackson.databind.PropertyName
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class RunTimeParameters extends Simulation {

  private def getProperty(propertyName: String,defaultValue: String): String ={

    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  def userCount=getProperty("USERS", "5").toInt
  def rampDuration=getProperty("RampDuration","5").toInt
  def Url = getProperty("Url","http://localhost:8888/app/")

  before{
    println(s"Running test with ${userCount} users")
    println(s"Running test with ${rampDuration} rampUp")
    println(s"Running test with ${Url} url")
  }
  val httpConf=http.baseUrl(Url).header("Accept","application/json")

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
    .exec(getAllVideoGames())


  setUp(scn.inject(nothingFor(5 seconds),rampUsers(userCount) during(rampDuration seconds)).protocols(httpConf.inferHtmlResources()))

}
