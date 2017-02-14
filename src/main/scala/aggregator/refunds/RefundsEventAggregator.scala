package aggregator.refunds

import aggregator.EventAggregator

import aggregator.refunds.RefundEventInstances._
/**
  * Created by valli on 10/06/2016.
  */
object RefundsEventAggregator {
  def main(args: Array[String]): Unit = {
    val aggregator = EventAggregator("refund-events")
    aggregator.startStreamingAndStoreFailedEvents()
  }
}
