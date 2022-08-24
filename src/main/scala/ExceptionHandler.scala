package com.tauro

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.ExceptionHandler


object ExceptionHandler {

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: IdentificationError =>
      complete(HttpResponse(StatusCodes.BadRequest, entity = e.msg))
    case e: BureauError =>
      complete(HttpResponse(StatusCodes.InternalServerError, entity = e.msg))
    case e: MissingIdError =>
      complete(HttpResponse(StatusCodes.BadRequest, entity = e.msg))
    case e: AccountNotFoundError =>
      complete(HttpResponse(StatusCodes.NotFound, entity = e.msg))
    case e: ObGatewayError =>
      complete(HttpResponse(StatusCodes.BadRequest, entity = e.msg))
  }
}
