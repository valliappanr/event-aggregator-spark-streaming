package producers.refunds

import java.util

import aggregator.refunds.RefundEvents.{RefundCompleted, RefundInitiated, RefundPaymentCompleted, RefundPaymentRequested}
import akka.actor.Actor
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import producers.orders.{CompleteOrder, CompletePayment, StartPayment}

import scala.util.Random

/**
  * Created by valli on 10/06/2016.
  */
class RefundEventStateActor extends Actor {

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

  def sendMessage(refundNo: Int, state: String): Unit = {
    val refundNoString = refundNo.toString
    val message=new ProducerRecord[String, String]("refund-events",refundNoString,state)
    kafkaProducer.send(message)

  }
  override def receive: Receive = {
    case CreateRefund(refundNo) => {
      sendMessage(refundNo, RefundInitiated.name)
      self ! StartRefundPayment(refundNo)
    }
    case StartRefundPayment(refundNo) => {
      sendMessage(refundNo, RefundPaymentRequested.name)
      self ! CompleteRefundPayment(refundNo)
    }
    case CompleteRefundPayment(refundNo) => {
      sendMessage(refundNo, RefundPaymentCompleted.name)
      val randomNumber = r.nextInt(4)
      if (randomNumber != 2) {
        self ! CompleteRefund(refundNo)
      }
    }
    case CompleteRefund(orderNo) => {
      sendMessage(orderNo, RefundCompleted.name)
    }
  }
}