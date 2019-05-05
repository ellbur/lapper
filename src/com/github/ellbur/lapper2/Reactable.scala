
package com.github.ellbur.lapper2

import scala.annotation.tailrec
import scala.concurrent.duration.Duration

trait Reactable {
  def react: Any
  def reactWithin(timeout: Duration): Option[Any]

  def check: Option[Any]

  def whileNoMessages(f: => Unit): Unit

  def restartOnError(_f: => Unit): Unit = {
    @tailrec def step(): Unit = {
      (try {
        _f
        false
      }
      catch {
        case t: Throwable =>
          t.printStackTrace()
          true
      }) match {
        case true => step()
        case false =>
      }
    }

    step()
  }

  def loop(_f: => Unit): Unit = {
    @tailrec def step(): Unit = {
      try {
        _f
      }
      catch {
        case t: Throwable =>
          t.printStackTrace()
      }

      step()
    }

    step()
  }
}
