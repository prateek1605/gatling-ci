package src.test.scala.simulations

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class VideoGameApis extends Simulation{

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

  var gameId=35

  val rnd= new Random()

  val now=LocalDate.now()

  def generateRandomString(length: Int): Any ={
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  var pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def generateRandomDate(startDate:LocalDate, random: Random):String={
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }

  val customfeed=Iterator.continually(Map(

    "id" -> gameId,
    "name" -> ("MortalCombat-"+generateRandomString(5)),
    "releaseDate" -> generateRandomDate(now,rnd),
    "reviewScore" -> rnd.nextInt(30),
    "category" -> ("Category-"+generateRandomString(10)),
    "rating" -> ("Rating-"+generateRandomString(10)),

  ))

  def getVideoGameApi(): ChainBuilder ={
    exec(http("Get Video Game Api").get("videgames").check(status.in(200)).check(bodyString.saveAs("responseBody")))
    .exec {session => println(session("responseBody").as[String]); session}
  }


    def getSpecificVideoGame(): ChainBuilder ={
      exec(http("Get Specific Video Game")
        .get(s"videogames/${gameId}").check(status.in(200 to 210)).check(bodyString.saveAs("responseBody")))
        .exec {session => println(session("responseBody").as[String]); session}
    }

  def postNewGame(): ChainBuilder ={

      feed(customfeed).exec(http("Post Game Call").post("videogames")
        .body(ElFileBody("data/NewPostGameTemplate.json")
        ).asJson.check(status.is(200)).check(bodyString.saveAs("responseBody")))
        .exec {session => println(session("responseBody").as[String]); session}
    }


  def deleteGameId(): ChainBuilder ={
    exec(http("Delete game id").delete(s"videogames/${gameId}")
      .check(status.in(200)).check(jsonPath("$.status").is("Record Deleted Successfully")))
  }

  val scn = scenario("Video Game Apis")
    .exec(postNewGame()).pause(10)
    .exec(getSpecificVideoGame().pause(10))
    .exec(getVideoGameApi()).pause(5)
    .exec(deleteGameId())

  setUp(scn.inject(atOnceUsers(userCount)).protocols(httpConf))

  after{
    println("Video Game Apis successfully executed")
  }
}
