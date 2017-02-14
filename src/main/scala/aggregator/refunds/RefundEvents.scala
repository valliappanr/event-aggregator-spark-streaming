package aggregator.refunds

import aggregator.{Event, Events}

/**
  * Created by valli on 10/06/2016.
  */
case object RefundEvents extends Events {
  case object RefundInitiated extends Event {
    val name = "RefundInitiated"
  }
  case object RefundPaymentRequested extends Event {
    val name = "RefundPaymentRequested"
  }
  case object RefundPaymentCompleted extends Event {
    val name = "RefundPaymentCompleted"
  }
  case object RefundCompleted extends Event {
    val name = "RefundCompleted"
  }

  val values = Seq(RefundInitiated, RefundPaymentRequested, RefundPaymentCompleted, RefundCompleted)
}

object RefundEventInstances {
  implicit val events = RefundEvents
}