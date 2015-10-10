package pass_functions

import akka.actor.{Props, ActorSystem, Actor}

object MyActorDemo extends App {

  def getVersionAndContent(): VersionAndContent = {
    new VersionAndContent(111, "hello")
  }

  val context = ActorSystem("MyActorSystem")
  val actor = context.actorOf(Props[MyActor])
  actor ! ContentChange("/aaa", () => getVersionAndContent())
}
class MyActor extends Actor {
  override def receive: Receive = {
    case ContentChange(path: String, f) =>
      println("### path: " + path)
      println("### version and content: " + f())
  }
}

case class VersionAndContent(version: Int, content: String)
case class ContentChange(path: String, f: () => VersionAndContent)
