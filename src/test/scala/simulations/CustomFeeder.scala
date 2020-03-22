package src.test.scala.simulations

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.util.Random

class CustomFeeder extends Simulation{

  val httpConf=http.baseUrl("http://localhost:8888/app/").header("Accept","application/json")


  var gameId=(21 to 30).iterator

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

      "id" -> gameId.next(),
      "name" -> ("MortalCombat-"+generateRandomString(5)),
      "releaseDate" -> generateRandomDate(now,rnd),
      "reviewScore" -> rnd.nextInt(30),
      "category" -> ("Category-"+generateRandomString(10)),
      "rating" -> ("Rating-"+generateRandomString(10)),

  ))

//  def postNewGame(): ChainBuilder ={
//
//    repeat(5){
//      feed(customfeed).exec(http("Post Game Call").post("videogames")
//          .body(StringBody(
//            "{"+
//              "\n\t\"id\": ${id},"+
//      "\n\t\"name\": \"${name}\","+
//      "\n\t\"releaseDate\":\"${releaseDate}\","+
//      "\n\t\"reviewScore\": \"${reviewScore}\","+
//      "\n\t\"category\": \"${category}\","+
//      "\n\t\"rating\": \"${rating}\"\n}")
//          ).asJson.check(status.is(200))).pause(5)
//    }
//  }

  def postNewGame(): ChainBuilder ={

    repeat(5){
      feed(customfeed).exec(http("Post Game Call").post("videogames")
        .body(ElFileBody("data/NewPostGameTemplate.json")
        ).asJson.check(status.is(200))).pause(5)
    }
  }

  val scn=scenario("My First Scenario").exec(postNewGame())

  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))


}
