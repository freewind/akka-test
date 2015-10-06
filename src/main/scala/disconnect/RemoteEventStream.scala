package disconnect

import akka.actor._
import akka.remote.{DisassociatedEvent, AssociatedEvent, RemotingLifecycleEvent}
import com.typesafe.config.ConfigFactory
import java.io.IOException

class RemoteEventStreamActor extends Actor{

  context.system.eventStream.subscribe(self, classOf[RemotingLifecycleEvent])

  def receive = {
    case AssociatedEvent(localAddress,remoteAddress,inbound) =>
      println(s"### AssociatedEvent info : local address is $localAddress, remote address is $remoteAddress," +
        s"inbound is $inbound")

    case DisassociatedEvent(localAddress,remoteAddress,inbound) =>
      println(s"### DisassociatedEvent info : local address is $localAddress, remote address is $remoteAddress," +
        s"inbound is $inbound")

    case MyException => {
//      throw new IOException("Exception occurs")
      sender ! "Exception"
      sender ! PoisonPill
      sender ! "Exception"
    }
  }
}

object RemoteEventStream {
  def main(args : Array[String]){
    val system = ActorSystem("EventStreamSystem" ,
      ConfigFactory.parseString(
        """
          |akka {
          |  actor {
          |    provider = "akka.remote.RemoteActorRefProvider"
          |  }
          |  remote {
          |    enabled-transports = ["akka.remote.netty.tcp"]
          |    netty.tcp {
          |      hostname = "127.0.0.1"
          |      port = 2050
          |    }
          | }
          |}
        """.stripMargin)
    )
    val actor = system.actorOf(Props[RemoteEventStreamActor],"RemoteEventStreamActor")
  }
}
