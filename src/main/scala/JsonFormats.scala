package com.tauro

import QuickstartApp.{CreditCard, CreditCardRequest, CsCardRequest, CsCardResponse, ScoredCardsRequest, ScoredCardsResponse}

import spray.json.{DefaultJsonProtocol, jsonWriter}

object JsonFormats {

  import DefaultJsonProtocol._

  implicit val creditCardRequestJsonFormat = jsonFormat3(CreditCardRequest)
  implicit val csCardResponseJsonFormat = jsonFormat3(CsCardResponse)
  implicit val csCardRequestJsonFormat = jsonFormat2(CsCardRequest)
  implicit val scoredCardsRequestJsonFormat = jsonFormat3(ScoredCardsRequest)
  implicit val scoredCardsResponseJsonFormat = jsonFormat3(ScoredCardsResponse)
  implicit val creditCardJsonFormat = jsonFormat4(CreditCard)

}
