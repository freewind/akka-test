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

  override def receive: Receive = {
    next(0, 0)
  }

  def next(result: Int, messageCount: Int): Receive = {
    case StartCalc =>
      println("Start!")
      numbersList.foreach(worker ! Work(_))
    case Result(sum) =>
      (messageCount + 1, result + sum) match {
        case (newMessageCount, newResult) =>
          context.become(next(newResult, newMessageCount))
          if (newMessageCount == numbersList.length) {
            listener ! FinalResult(newResult)
          }
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
