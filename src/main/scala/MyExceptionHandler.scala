package com.tauro

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import Directives._
import com.tauro.CreditCardGatewayError

object MyExceptionHandler {

  val myExceptionHandler = ExceptionHandler {
    case e: CreditCardGatewayError => complete(HttpResponse(StatusCodes.BadRequest, entity = e.msg))
  }

}


