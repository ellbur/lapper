
package com.github.ellbur.lapper

import scala.concurrent.duration.Duration
import Lapper._
import Continuation.trampoline

trait ReplyingReactable {
  def react[T](f: ReplyingFunction[T]): Continuation[T]
  def react[T](f: DelayedReplyingFunction[T]): Continuation[T]
  def reactWithin[T](timeout: Duration)(f: OptionReplyingFunction[T]): Continuation[T]
  def check[T](f: OptionReplyingFunction[T]): Continuation[T]

  def whileNoMessages(f: => Unit): Next

  def restartOnError(_f: => Next): Next = {
    val f = then { _f }
    try {
      trampoline(f)
      done
    }
    catch {
      case t: Throwable =>
        System.err.println("Actor caught exception:")
        t.printStackTrace()
        System.err.println("Inside restartOnError -- Actor will now restart.")
        then(restartOnError(f))
    }
  }

  def loop(_f: => Next): Next = {
    restartOnError {
      val f = then { _f }
      def step(): Next = {
        trampoline(f)
        step()
      }
      step()
    }
  }
}
