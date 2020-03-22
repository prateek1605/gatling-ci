package src.test.scala
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class MyFirstTest extends Simulation{

  //1 Http Conf

  val httpConf=http.baseUrl("http://localhost:8888/app/").header("Accept","application/json")

  //2 Scenario

  val scn=scenario("My First Scenario").exec(http("Get All Games").get("videogames"))

  //3 Load Scenario

  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))


}
