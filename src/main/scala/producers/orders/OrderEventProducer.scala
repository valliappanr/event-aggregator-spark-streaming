package producers.orders

import akka.actor.{ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

/**
  * Created by valli on 10/06/2016.
  */
object OrderEventProducer extends App
{
  val r = Random
  val system = ActorSystem("MySystem")
  val orderEventActor = system.actorOf(Props[OrderEventStateActor], name="orderevent")
  system.scheduler.schedule(0 seconds, 5 seconds)(initiateOrderRequest())

  def initiateOrderRequest(): Unit = {
    val orderNo = r.nextInt(25000)
    orderEventActor ! CreateOrder(orderNo)
  }
}

