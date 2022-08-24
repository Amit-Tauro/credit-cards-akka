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
//
//trait Cake extends CreditCardRoute with CreditCardSlice with CreditCardGatewaySlice


object QuickstartApp extends CreditCardRoute with CreditCardSlice with CreditCardGatewaySlice {


//  creditCardService.creditCards(req)

//  final case class CreditCardRequest(name: String, creditScore: Int, salary: Int)
//  final case class CsCardRequest(name: String, creditScore: Int)
//  final case class ScoredCardsRequest(name: String, score: Int, salary: Int)
//  final case class CreditCard(provider: String, name: String, apr: Double, cardScore: Double)
//  final case class CsCardResponse(cardName: String, apr: Double, eligibility: Double)
//  final case class ScoredCardsResponse(card: String, apr: Double, approvalRating: Double)



  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext


    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
