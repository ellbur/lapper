
package com.github.ellbur.lapper2

class Locked[T](_init: T) {
  private var it = _init

  def apply(): T = synchronized(it)
  def update(next: T) = synchronized { it = next; notifyAll() }
  def transform(f: T => T) = synchronized { it = f(it); notifyAll() }
  def use(f: T => Unit) = synchronized(f(it))
  def loop[A](f: T => Option[A]) = synchronized {
    def step(): A = {
      f(it) match {
        case Some(result) => result
        case None =>
          wait()
          step()
      }
    }
    step()
  }
}

object Locked {
  def apply[T](x: T): Locked[T] = new Locked[T](x)
}
