package producers.refunds

import akka.actor.{ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

/**
  * Created by valli on 10/06/2016.
  */
object RefundEventProducer extends App{
  val r = Random
  val system = ActorSystem("MySystem")
  val refundEventActor = system.actorOf(Props[RefundEventStateActor], name="refundevent")
  system.scheduler.schedule(0 seconds, 5 seconds)(initiateOrderRequest())

  def initiateOrderRequest(): Unit = {
    val refundNo = r.nextInt(25000)
    refundEventActor ! CreateRefund(refundNo)
  }
}
