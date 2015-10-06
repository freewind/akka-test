package sender_info

import akka.actor._

object ActorDemo extends App {
  val system = ActorSystem("ServerActorDemo")
  val server = system.actorOf(Props[ServerActor])
  (0 to 10).map(i => system.actorOf(Props.create(classOf[ClientActor], server, i.toString))).foreach(_ ! "Start")
}

class ServerActor extends Actor {
  override def receive: Receive = {
    case s: String if s.startsWith("OK") =>
      println("message from client: " + s)
      println(sender())
    case msg =>
      println("message from client: " + msg)
      println(sender())
      sender() ! "OK"
  }
}

class ClientActor(serverActor: ActorRef, index: String) extends Actor {
  override def receive: Actor.Receive = {
    case "Start" => serverActor ! "Hello from client " + index
    case "OK" => serverActor ! "OK " + index
  }
}
