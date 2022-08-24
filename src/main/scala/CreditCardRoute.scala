package com.tauro


import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import JsonFormats._

import spray.json.DefaultJsonProtocol._
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import ExceptionHandler.exceptionHandler

trait CreditCardRoute extends CreditCardSlice {

  implicit val system = ActorSystem(Behaviors.empty, "my-system")
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.executionContext

  val route = handleExceptions(exceptionHandler) {
    path("creditcards") {
      post {
        entity(as[CreditCardRequest]) { req =>
          onSuccess(creditCardService.creditCards(req)) { res =>
            complete((StatusCodes.OK, res))
          }
        }
      }
    }
  }
}
