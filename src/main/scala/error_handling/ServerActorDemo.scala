package error_handling

import akka.actor.{Actor, ActorSystem, Props}

import scala.language.postfixOps

object ServerActorDemo extends App {
  val system = ActorSystem("demo")
  val actor = system.actorOf(Props[ServerActor])
  actor ! "Start"
  actor ! "Start"
  actor ! "other"

}


class ServerActor extends Actor {

  private var index = 0

  @throws[Exception](classOf[Exception])
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    println("### preRestart: " + reason)
    sender() ! "Server actor has something wrong"
  }

  override def receive: Receive = {
    case "Start" =>
      println("### actor started: " + index)
      index += 1
      throw new Exception("My exception when starting")
    case msg =>
      println("### actor get other message: " + msg + ", " + index)
      throw new Exception("another exception for other messages: " + msg)
  }
}
