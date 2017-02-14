package aggregator

import java.io.Serializable

/**
  * Created by valli on 10/06/2016.
  */


trait Event extends Serializable{
  def name: String
}

trait Events extends Serializable{
  def values: Seq[Event]
  def exists(name: String): Boolean = values.exists(n => n.name.equals(name))
  def size:Int = values.size
}