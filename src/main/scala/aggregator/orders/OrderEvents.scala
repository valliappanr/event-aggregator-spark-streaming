package aggregator.orders

import aggregator.{Event, Events}

/**
  * Created by valli on 10/06/2016.
  */
object OrderEvents extends Events {
  case object OrderInitiated extends Event {
    val name = "OrderInitiated"
  }
  case object PaymentStarted extends Event {
    val name = "OrderInitiated"
  }
  case object PaymentCompleted extends Event {
    val name = "OrderInitiated"
  }
  case object OrderCompleted extends Event {
    val name = "OrderInitiated"
  }

  val values = Seq(OrderInitiated, PaymentStarted, PaymentCompleted, OrderCompleted)
}

object OrderEventInstances {
  implicit val events = OrderEvents
}