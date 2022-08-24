package com.tauro

import spray.json.{DefaultJsonProtocol, jsonWriter}

import DefaultJsonProtocol._

object JsonFormats {

  final case class CreditCardRequest(name: String, creditScore: Int, salary: Int)
  final case class CsCardRequest(name: String, creditScore: Int)
  final case class ScoredCardsRequest(name: String, score: Int, salary: Int)
  final case class CreditCard(provider: String, name: String, apr: Double, cardScore: Double)
  final case class CsCardResponse(cardName: String, apr: Double, eligibility: Double)
  final case class ScoredCardsResponse(card: String, apr: Double, approvalRating: Double)


  implicit val creditCardRequestJsonFormat = jsonFormat3(CreditCardRequest)
  implicit val csCardResponseJsonFormat = jsonFormat3(CsCardResponse)
  implicit val csCardRequestJsonFormat = jsonFormat2(CsCardRequest)
  implicit val scoredCardsRequestJsonFormat = jsonFormat3(ScoredCardsRequest)
  implicit val scoredCardsResponseJsonFormat = jsonFormat3(ScoredCardsResponse)
  implicit val creditCardJsonFormat = jsonFormat4(CreditCard)

}
