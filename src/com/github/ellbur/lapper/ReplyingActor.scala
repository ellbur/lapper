
package com.github.ellbur.lapper

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}
import Lapper._
import com.github.ellbur.lapper.Continuation._
import scala.concurrent.duration.Duration
import scala.collection.JavaConversions._

class ReplyingActor(ff: ReplyingReactable => Next) extends ReplyingReactable with ReplyingReceiver { self =>
  private lazy val queue = new LinkedBlockingQueue[RepliableMessage]
  private var running: Boolean = true

  override protected def !(msg: RepliableMessage): Unit = {
    synchronized {
      if (!running) {
        msg.fail(new ActorNotRunningException)
      }
      queue.put(msg)
    }
  }

  override def react[T](f: ReplyingFunction[T]): Continuation[T] = then {
    val message = queue.take()
    try {
      val responseNext: (Either[Throwable,message.Response], Continuation[T]) = f(message.message)

      responseNext._1 match {
        case Left(t) => message.fail(t)
        case Right(r) => message.reply(r)
      }
      responseNext._2
    }
    catch {
      case t: Throwable =>
        message.fail(t)
        throw t
    }
  }

  override def react[T](f: DelayedReplyingFunction[T]): Continuation[T] = then {
    val message = queue.take()
    try {
      val responseNext: Continuation[(Either[Throwable,message.Response], Continuation[T])] = f(message.message)

      responseNext flatMap { responseNext =>
        responseNext._1 match {
          case Left(t) => message.fail(t)
          case Right(r) => message.reply(r)
        }
        responseNext._2
      }
    }
    catch {
      case t: Throwable =>
        message.fail(t)
        throw t
    }
  }

  override def reactWithin[T](duration: Duration)(f: OptionReplyingFunction[T]): Continuation[T] = then {
    val message = Option(queue.poll(duration.length, duration.unit))
    message match {
      case Some(message) =>
        try {
          val responseNext: (Either[Throwable,message.Response], Continuation[T]) = f(message.message)
          responseNext._1 match {
            case Left(t) => message.fail(t)
            case Right(r) => message.reply(r)
          }
          responseNext._2
        }
        catch {
          case t: Throwable =>
            message.fail(t)
            throw t
        }
      case None =>
        f.none()
    }
  }

  override def check[T](f: OptionReplyingFunction[T]): Continuation[T] = then {
    val message = Option(queue.poll())
    message match {
      case Some(message) =>
        try {
          val responseNext: (Either[Throwable,message.Response], Continuation[T]) = f(message.message)
          responseNext._1 match {
            case Left(t) => message.fail(t)
            case Right(r) => message.reply(r)
          }
          responseNext._2
        }
        catch {
          case t: Throwable =>
            message.fail(t)
            throw t
        }
      case None =>
        f.none()
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

  val thread: Thread = new Thread {
    override def run(): Unit = {
      try {
        val f = ff(self)
        trampoline(f)
      }
      finally {
        synchronized {
          running = false
          queue.iterator foreach (_.fail(new ActorNotRunningException))
          queue.clear()
        }
      }
    }
  }

  thread.start()

  def join(): Unit = {
    thread.join()
  }
}
