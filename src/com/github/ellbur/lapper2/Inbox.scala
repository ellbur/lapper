
package com.github.ellbur.lapper2

import java.util.concurrent.BlockingQueue

import scala.annotation.tailrec
import scala.concurrent.duration.Duration

trait Inbox extends Reactable with Receiver {
  protected val queue: BlockingQueue[Any]

  override def !(msg: Any): Unit = {
    queue.put(msg)
  }

  override def react: Any = queue.take()

  override def reactWithin(duration: Duration) = Option(queue.poll(duration.length, duration.unit))

  override def check: Option[Any] = Option(queue.poll())

  override def whileNoMessages(f: => Unit): Unit = {
    @tailrec def step(): Unit = {
      if (queue.isEmpty) {
        f
        step()
      }
    }
    step()
  }
}
