# event-aggregator-spark-streaming

* A generic real time event aggregator to aggregate events within the time window, using which to see all the
expected events have arrived, if not raise an issue by storing the failed events to elastic search and display
it in Kibana

* This can be used to highlight any events across the system failed to satisfy the agreed SLAs.
 

![alt tag](https://github.com/valliappanr/spark-streaming/blob/master/find-fraudulent-ids.png)

# Pre-requisite
* Kafka as message broker
* Zookeeper to manage kafka
* Elastic search / Kibana to store the output and render the output in dashboard


# Quick Start - Guide
* Start Zookeeper and Kafka
* Start Elastic search / Kibana
* Run OrderEventProducer from scala app from the codebase, which will generate events for Order processing
  (the orders can be in one of 4 states ( OrderInitiated,PaymentStarted, PaymentCompleted, OrderCompleted).
  Randomly it will skip to generate events for PaymentCompleted state, so that this can be viewed as failed
  events in the Kibana dashboard.
  
* Run OrderEventsAggregator from scala app from the codebase, which aggregates the order events for the key
order number within the specified time window. If all the order events are not received for a particular order
then that order is marked as failed by storing the event with order number to elastic search and display it in
Kibana dashboard.
* The app could quiet easily support new events by just defining the events and start the application with that
  events, and the aggregation works straight away as long as the events are being pushed onto the kafka topics.



![alt tag](https://github.com/valliappanr/spark-streaming/blob/master/failed-events.png)
