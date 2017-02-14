package aggregator.orders

import aggregator.EventAggregator

import aggregator.orders.OrderEvents._
import aggregator.orders.OrderEventInstances._
/**
  * Created by valli on 10/06/2016.
  */
object OrderEventsAggregator {
  def main(args: Array[String]): Unit = {
    val aggregator = EventAggregator("order-events")
    aggregator.startStreamingAndStoreFailedEvents()
  }
}
