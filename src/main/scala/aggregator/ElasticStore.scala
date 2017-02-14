package aggregator

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.rdd.RDD
import org.elasticsearch.spark._

/**
  * Created by valli on 10/06/2016.
  */

case object ElasticStore {
  val dateFormat  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
  def storeInES(rdd: RDD[(FailedEvent)], indexName: String): Unit = {
    rdd.map{ record => {
      val timestamp = dateFormat.format(new Date())
      Map("@timestamp" -> timestamp, "eventRef" -> record.eventRef, "states" -> record.totalStates)
    }
    }.saveToEs(indexName)
  }
}