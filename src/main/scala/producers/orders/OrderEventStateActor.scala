package producers.orders

import java.util

import aggregator.orders.OrderEvents.{OrderCompleted, OrderInitiated, PaymentCompleted, PaymentStarted}
import akka.actor.Actor
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import scala.util.Random

/**
  * Created by valli on 10/06/2016.
  */
class OrderEventStateActor extends Actor {

  val kafkaProducer = createKafkaProducer()
  val r = Random

  def createKafkaProducer(): KafkaProducer[String, String] = {
    val props = new util.HashMap[String, Object]()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put("partitioner.class", "org.apache.kafka.clients.producer.internals.DefaultPartitioner")
    new KafkaProducer[String,String](props)
  }

  def sendMessage(orderNo: Int, state: String): Unit = {
    val orderNoString = orderNo.toString
    val message=new ProducerRecord[String, String]("order-events",orderNoString,state)
    kafkaProducer.send(message)

  }
  override def receive: Receive = {
    case CreateOrder(orderNo) => {
      sendMessage(orderNo, OrderInitiated.name)
      self ! StartPayment(orderNo)
    }
    case StartPayment(orderNo) => {
      sendMessage(orderNo, PaymentStarted.name)
      self ! CompletePayment(orderNo)
    }
    case CompletePayment(orderNo) => {
      sendMessage(orderNo, PaymentCompleted.name)
      val randomNumber = r.nextInt(4)
      if (randomNumber != 2) {
        self ! CompleteOrder(orderNo)
      }
    }
    case CompleteOrder(orderNo) => {
      sendMessage(orderNo, OrderCompleted.name)
    }
  }
}