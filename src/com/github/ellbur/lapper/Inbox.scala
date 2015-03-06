
package com.github.ellbur.lapper

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}
import Lapper._
import scala.concurrent.duration.Duration

trait Inbox extends Reactable with Receiver {
  protected val queue: BlockingQueue[Any]

  override def !(msg: Any): Unit = {
    queue.put(msg)
  }

  override def react[T](f: PartialFunction[Any, Continuation[T]]) = then(f(queue.take()))

  override def reactWithin[T](duration: Duration)(f: PartialFunction[Option[Any], Continuation[T]]): Continuation[T] = {
    then {
      f(Option(queue.poll(duration.length, duration.unit)))
    }
  }

  override def check[T](f: PartialFunction[Option[Any], Continuation[T]]): Continuation[T] = {
    then {
      f(Option(queue.peek()))
    }
  }

  override def whileNoMessages(f: => Unit): Next = {
    def step(): Next = {
      if (!queue.isEmpty)
        done
      else {
        f
        step()
      }
    }
    step()
  }
}
