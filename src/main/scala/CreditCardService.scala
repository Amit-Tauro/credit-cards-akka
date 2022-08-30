package com.tauro

import JsonFormats._

import akka.actor.typed.ActorSystem

import scala.concurrent.{ExecutionContextExecutor, Future}

trait CreditCardSlice {
  def creditCards(req: CreditCardRequest)(implicit ec: ExecutionContextExecutor, sys: ActorSystem[_]): Future[List[CreditCard]]
}

class CreditCardService(val gateway: CreditCardGateway) extends CreditCardSlice {
    def creditCards(req: CreditCardRequest)(implicit ec: ExecutionContextExecutor, sys: ActorSystem[_]): Future[List[CreditCard]] = {
      for {
        csCards <- gateway.fetchCsCards(req)
        scoredCards <- gateway.fetchScoredCards(req)
      } yield sortCreditCards(csCards, scoredCards)
    }

    private def sortCreditCards(cs: List[CsCardResponse], sc: List[ScoredCardsResponse]): List[CreditCard] = {
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
