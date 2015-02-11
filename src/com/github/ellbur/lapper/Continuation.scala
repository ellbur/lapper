
package com.github.ellbur.lapper

sealed trait Continuation[+T]

object Continuation {
  def trampoline[T](f: Continuation[T]): T = f match {
    case Return(x) => x
    case Bounce(f) => trampoline(f())
  }
}

case class Return[+T](x: T) extends Continuation[T]
case class Bounce[+T](f: () => Continuation[T]) extends Continuation[T]
