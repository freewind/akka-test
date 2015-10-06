import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}

object FailTest extends App {

  val system = ActorSystem("test-failure")

  val aaa = system.actorOf(Props(new AAA))

  aaa ! Start


}

class AAA extends Actor {

  val bbb = context.actorOf(Props(new BBB))

  override val supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10)({
    case _ => println("####### child has exception")
      Restart
  })

  override def receive: Receive = {
    case Start =>
      bbb ! Hello
  }
}

class BBB extends Actor {
  println("####### BBB is created: " + self.path.name)
  override def receive: Actor.Receive = {
    case Hello =>
      println("####### receive Hello message")
      throw new Exception("Hello exception")
  }
}


case object Start
case object Hello
