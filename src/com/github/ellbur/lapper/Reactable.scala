
package com.github.ellbur.lapper
import scala.concurrent.duration.Duration
import Lapper._
import Continuation.trampoline

trait Reactable {
  def react[T](f: PartialFunction[Any, Continuation[T]]): Continuation[T]
  def reactWithin[T](timeout: Duration)(f: PartialFunction[Option[Any], Continuation[T]]): Continuation[T]

  def check[T](f: PartialFunction[Option[Any], Continuation[T]]): Continuation[T]

  def whileNoMessages(f: => Unit): Next

  def attempt[A](action: => Continuation[A]) = new {
    def handling[A1>:A](recovery: PartialFunction[Throwable,Continuation[A1]]): Continuation[A1] = then {
      try
        Return(trampoline(action))
      catch
        recovery
    }
  }

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
        restartOnError(f)
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
