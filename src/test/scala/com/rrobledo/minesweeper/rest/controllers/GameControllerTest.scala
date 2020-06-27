package com.rrobledo.minesweeper.rest.controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpHeader, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.rrobledo.minesweeper.repositories.RepoModule
import com.rrobledo.minesweeper.rest.RestInterface
import com.rrobledo.minesweeper.services.ServicesModule
import com.rrobledo.minesweeper.utils.UtilsModule
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Inside, Matchers}
import scaldi.{Module, MutableInjectorAggregation, TypesafeConfigInjector}

import scala.concurrent.ExecutionContextExecutor

class GameControllerTest
  extends FunSuiteLike
    with Matchers
    with Inside
    with ScalaFutures
    with ScalatestRouteTest
    with BeforeAndAfterAll  {

  val httpEntity: (String) => HttpEntity.Strict = (str: String) => HttpEntity(ContentTypes.`application/json`, str)
  implicit val config: Config = ConfigFactory.load()
  private implicit val testModule: MutableInjectorAggregation =
    new Module {
    } ::
      new UtilsModule ::
      new ServicesModule ::
      new RepoModule ::
      TypesafeConfigInjector(config)

  val routes = Route.seal(new RestInterface() {
    implicit val system: ActorSystem = inject[ActorSystem]
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  }.routes)

  test("POST /api/v1/games") {
      val validGame =
        """
        {
          "options" : {
            "rows": 5,
            "cols": 5,
            "mines": 4,
            "limitTime": 60
          }
        }
        """

      val header = RawHeader("X-User-Id", "raul.osvaldo.robledo@gmail.com")
      Post(s"/api/v1/games", httpEntity(validGame)).withHeaders(header) ~> routes ~> check {
        status shouldBe StatusCodes.Created
      }
  }
}