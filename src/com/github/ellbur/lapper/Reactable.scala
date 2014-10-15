
package com.github.ellbur.lapper
import scala.concurrent.duration.Duration
import Lapper._

trait Reactable {
  def react[T](f: PartialFunction[Any, Continuation[T]]): Continuation[T]
  def reactWithin[T](timeout: Duration)(f: PartialFunction[Option[Any], Continuation[T]]): Continuation[T]

  def restartOnError(f: Next): Next = Lapper.restartOnError(f)
}
