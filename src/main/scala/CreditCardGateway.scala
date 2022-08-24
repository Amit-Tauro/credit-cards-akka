package com.tauro

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import spray.json._
import JsonFormats._
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.{ExecutionContextExecutor, Future}

final case class CreditCardGatewayError(msg: String) extends RuntimeException(msg)

class CreditCardGateway {

    def fetchCsCards(req: CreditCardRequest)(implicit ec: ExecutionContextExecutor, sys: ActorSystem[_]): Future[List[CsCardResponse]] = {
      implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
        EntityStreamingSupport.json()

      val entityJson: JsValue = CsCardRequest(req.name, req.creditScore).toJson
      val clientReq = HttpRequest(method = HttpMethods.POST,
        uri = "https://app.clearscore.com/api/global/backend-tech-test/v1/cards",
        entity = HttpEntity(ContentTypes.`application/json`, entityJson.prettyPrint))
      Http().singleRequest(clientReq).flatMap(r => Unmarshal(r).to[List[CsCardResponse]]).recover {
        case e: Exception => throw CreditCardGatewayError(s"${e}")
      }
    }

  def fetchScoredCards(req: CreditCardRequest)(implicit ec: ExecutionContextExecutor, sys: ActorSystem[_]): Future[List[ScoredCardsResponse]] = {
      val entityJsonSc: JsValue = ScoredCardsRequest(req.name, req.creditScore, req.salary).toJson
      val clientReqSc = HttpRequest(method = HttpMethods.POST,
        uri = "https://app.clearscore.com/api/global/backend-tech-test/v2/creditcards",
        entity = HttpEntity(ContentTypes.`application/json`, entityJsonSc.prettyPrint))
      Http().singleRequest(clientReqSc).flatMap(r => Unmarshal(r).to[List[ScoredCardsResponse]]).recover {
        case e: Exception => throw CreditCardGatewayError(s"${e}")
      }
    }
}
