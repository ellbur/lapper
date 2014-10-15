
package com.github.ellbur.lapper

import java.util.concurrent.LinkedBlockingQueue
import Lapper._
import scala.concurrent.duration.Duration

class Inbox extends Reactable with Receiver {
  private[this] val queue = new LinkedBlockingQueue[Any]

  override def !(msg: Any): Unit = {
    queue.put(msg)
  }

  override def react[T](f: PartialFunction[Any, Continuation[T]]) = then(f(queue.take()))

  override def reactWithin[T](duration: Duration)(f: PartialFunction[Option[Any], Continuation[T]]): Continuation[T] =
    then {
      f(Option(queue.poll(duration.length, duration.unit)))
    }
}
