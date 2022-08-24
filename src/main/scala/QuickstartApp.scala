package com.tauro

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http

import scala.io.StdIn

object QuickstartApp {

  implicit val system = ActorSystem(Behaviors.empty, "my-system")
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.executionContext

  val gateway = new CreditCardGateway
  val service = new CreditCardService(gateway)
  val creditCardRoute = new CreditCardRoute(service)

  def main(args: Array[String]): Unit = {
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(creditCardRoute.route)

    println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
