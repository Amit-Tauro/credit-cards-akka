package com.tauro


import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import JsonFormats._

import spray.json.DefaultJsonProtocol._
import akka.actor.typed.ActorSystem

import scala.concurrent.ExecutionContextExecutor

class CreditCardRoute(val service: CreditCardService)(implicit ec: ExecutionContextExecutor, sys: ActorSystem[_]) {
  val route = handleExceptions(MyExceptionHandler.myExceptionHandler) {
    path("creditcards") {
      post {
        entity(as[CreditCardRequest]) { req =>
          onSuccess(service.creditCards(req)) { res =>
            complete((StatusCodes.OK, res))
          }
        }
      }
    }
  }
}
