package producers.orders

import producers.EventState

/**
  * Created by valli on 10/06/2016.
  */
case class CreateOrder(orderNo: Int) extends  EventState
case class StartPayment(orderNo: Int) extends  EventState
case class CompletePayment(orderNo: Int) extends  EventState
case class CompleteOrder(orderNo: Int) extends  EventState
