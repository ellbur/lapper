
package com.github.ellbur.lapper

sealed trait Continuation[+T] {
  def map[U](f: T => U): Continuation[U]
  def flatMap[U](f: T => Continuation[U]): Continuation[U]
}

object Continuation {
  def trampoline[T](f: Continuation[T]): T = f match {
    case Return(x) => x
    case Bounce(f) => trampoline(f())
  }
}

case class Return[+T](x: T) extends Continuation[T] {
  override def map[U](f: (T) => U): Continuation[U] = Return(f(x))
  override def flatMap[U](f: (T) => Continuation[U]): Continuation[U] = f(x)
}

case class Bounce[+T](f: () => Continuation[T]) extends Continuation[T] {
  override def map[U](g: (T) => U): Continuation[U] = Bounce(() => f() map g)
  override def flatMap[U](g: (T) => Continuation[U]): Continuation[U] = Bounce(() => f() flatMap g)
}
