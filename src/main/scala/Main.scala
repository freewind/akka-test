import akka.actor.{ActorRef, Actor, Props, ActorSystem}
import akka.actor.Actor.Receive
import akka.routing.{RoundRobinPool, RoundRobinGroup, RoundRobinRouter}

object Pi extends App {
  private val number = 100000

  println((0 to number).sum)


  val system = ActorSystem("PiSystem")

  val listener = system.actorOf(Props(new Listener))
  val master = system.actorOf(Props(new Master(workerCount = 4, number = number, listener = listener)), "master")

  master ! StartCalc
}

class Master(workerCount: Int, number: Int, listener: ActorRef) extends Actor {
  val worker = context.actorOf(Props(new Worker).withRouter(RoundRobinPool(workerCount)))
  val numbersList = (0 to number).grouped(10).toList
  private var result = 0
  private var messageCount = 0

  override def receive: Receive = {
    case StartCalc =>
      println("Start!")
      numbersList.foreach(worker ! Work(_))
    case Result(sum) =>
      result += sum
      messageCount += 1
      if (messageCount == numbersList.length) {
        listener ! FinalResult(result)
      }
  }

}

class Listener extends Actor {
  override def receive: Actor.Receive = {
    case FinalResult(result) =>
      println("####### total sum : " + result)
      context.system.shutdown()
  }
}

class Worker extends Actor {
  override def receive: Actor.Receive = {
    case Work(nums) =>
      sender ! Result(nums.sum)
  }
}

case object StartCalc
case class Work(nums: Seq[Int])
case class Result(sum: Int)
case class FinalResult(total: Int)
