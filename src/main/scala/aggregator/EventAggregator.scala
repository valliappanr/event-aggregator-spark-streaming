package aggregator


import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._

import aggregator.ElasticStore._
/**
  * Created by valli on 10/06/2016.
  */
case class AggregatedEvents(events: Vector[String], timedOut: Boolean)

case class FailedEvent(eventRef: String, totalStates: Int)

case class EventAggregator(topicName: String) {
  def createKafkaParams(): Map[String, Object] = {

    Map[String, Object](
      "bootstrap.servers" -> "localhost:9092,anotherhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
  }

  def createStreamingContext(kafkaParams: Map[String, Object], topics: Array[String], ssc: StreamingContext): InputDStream[ConsumerRecord[String, String]] = {

    KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
  }

  def startStreamingAndStoreFailedEvents()(implicit events: Events): Unit = {
    val spark = SparkSession.builder().appName("event-aggregator-"+ topicName).master("local[2]")
      .config("es.index.auto.create", "true").getOrCreate()
    val ssc = new StreamingContext(spark.sparkContext, Seconds(10))
    val stream = createStreamingContext(createKafkaParams(), Array(topicName), ssc)
    ssc.checkpoint("/checkpoint"+topicName)

    val stateSpec = StateSpec.function(groupEventsByKey _)
      .numPartitions(2)
      .timeout(Seconds(40))
    val rdds = stream.map(line => (line.key, line.value)).mapWithState(stateSpec).foreachRDD(rdd=> {
      val mappedRdds = rdd.mapPartitions(iter => {
        for {keyValue <- iter; if keyValue._2.events.size != events.size}yield(FailedEvent(keyValue._1, keyValue._2.events.size))
      })
      storeInES(mappedRdds, Array("failed-events-"+topicName, topicName).mkString("/"))
    })
    ssc.start
    ssc.awaitTermination()

  }

  def groupEventsByKey(batchTime: Time, key: String, value: Option[String], state: State[AggregatedEvents])(implicit events: Events): Option[(String, AggregatedEvents)] = {
    value match {
      case Some(eventName) => {
        if(events.exists(eventName)) {
          val newState = state.getOption().getOrElse(AggregatedEvents(Vector.empty[String], false))
          val newSequence: Vector[String] = newState.events :+ eventName

          val aggregatedEvents = AggregatedEvents(newSequence, false)
          val output = (key, aggregatedEvents)
          state.update(aggregatedEvents)
        }
        None
      }
      case _ if state.isTimingOut() => {
        val newState = state.getOption().getOrElse(AggregatedEvents(Vector.empty[String], true))
        Some(key, AggregatedEvents(state.getOption().get.events, true))
      }
    }
  }
}