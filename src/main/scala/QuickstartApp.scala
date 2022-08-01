package com.tauro

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

import scala.io.StdIn
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import JsonFormats._

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.unmarshalling.Unmarshal
import spray.json.DefaultJsonProtocol.listFormat
import spray.json.{JsArray, JsString, JsValue, enrichAny}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.parsing.json.JSONObject
import akka.http.scaladsl.common.EntityStreamingSupport
import akka.http.scaladsl.common.JsonEntityStreamingSupport

object QuickstartApp {

  final case class CreditCardRequest(name: String, creditScore: Int, salary: Int)
  final case class CsCardRequest(name: String, creditScore: Int)
  final case class ScoredCardsRequest(name: String, score: Int, salary: Int)
  final case class CreditCard(provider: String, name: String, apr: Double, cardScore: Double)
  final case class CsCardResponse(cardName: String, apr: Double, eligibility: Double)
  final case class ScoredCardsResponse(card: String, apr: Double, approvalRating: Double)

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext


    val route =
      path("creditcards") {
        post {
          entity(as[CreditCardRequest]) { req =>
            onSuccess(fetchCs(req)) { res =>
              complete((StatusCodes.OK, res))
            }
          }
        }
      }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

  private def fetchCs(req: CreditCardRequest)(implicit ec: ExecutionContextExecutor, sys: ActorSystem[_]): Future[List[CreditCard]] = {
    implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
      EntityStreamingSupport.json()

    val entityJson: JsValue = CsCardRequest(req.name, req.creditScore).toJson
    val clientReq = HttpRequest(method = HttpMethods.POST,
      uri = "https://app.clearscore.com/api/global/backend-tech-test/v1/cards",
      entity = HttpEntity(ContentTypes.`application/json`, entityJson.prettyPrint))
    println(clientReq)
    val cs = Http().singleRequest(clientReq).flatMap(r => Unmarshal(r).to[List[CsCardResponse]])

    val entityJsonSc: JsValue = ScoredCardsRequest(req.name, req.creditScore, req.salary).toJson
    val clientReqSc = HttpRequest(method = HttpMethods.POST,
      uri = "https://app.clearscore.com/api/global/backend-tech-test/v2/creditcards",
      entity = HttpEntity(ContentTypes.`application/json`, entityJsonSc.prettyPrint))
    println(clientReqSc)
    println(entityJsonSc)
    val sc = Http().singleRequest(clientReqSc).flatMap(r => Unmarshal(r).to[List[ScoredCardsResponse]])

    println(s"sc request: ${sc}")

    for {
      csList <- cs
      scList <- sc
    } yield sortCreditCards(csList, scList)
  }

  private def sortCreditCards(cs: List[CsCardResponse], sc: List[ScoredCardsResponse]): List[CreditCard] = {
    println(s"sortCards: ${cs}")
    val csCards: List[CreditCard] = cs.map(r => CreditCard(provider = "CSCards",
      name = r.cardName, apr = r.apr, cardScore = CsScore(r)))
    val scCards: List[CreditCard] = sc.map(r => CreditCard(provider = "ScoredCards",
      name = r.card, apr = r.apr, cardScore = ScScore(r)))
    val cards: List[CreditCard] = csCards ::: scCards
    cards.sortWith(sortingScore)
  }

  private def CsScore(cs: CsCardResponse): Double = {
    cs.eligibility * (Math.pow(1 / cs.apr, 2))
  }

  private def ScScore(sc: ScoredCardsResponse): Double = {
    val eligibility: Double = sc.approvalRating*10
    eligibility * (Math.pow(1 / sc.apr, 2))
  }

  private def sortingScore(cs1: CreditCard, cs2: CreditCard): Boolean = {
    cs1.cardScore > cs2.cardScore
  }
}
