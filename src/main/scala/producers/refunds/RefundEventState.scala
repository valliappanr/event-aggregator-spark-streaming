package producers.refunds

import producers.EventState

/**
  * Created by valli on 10/06/2016.
  */
case class CreateRefund(refundNo: Int) extends  EventState
case class StartRefundPayment(refundNo: Int) extends  EventState
case class CompleteRefundPayment(refundNo: Int) extends  EventState
case class CompleteRefund(refundNo: Int) extends  EventState
