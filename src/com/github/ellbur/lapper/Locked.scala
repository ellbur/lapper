
package com.github.ellbur.lapper

class Locked[T](_init: T) {
  private var it = _init

  def apply(): T = synchronized(it)
  def update(next: T) = synchronized(it = next)
  def transform(f: T => T) = synchronized(it = f(it))
}

object Locked {
  def apply[T](x: T): Locked[T] = new Locked[T](x)
}
