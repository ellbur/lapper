
package com.github.ellbur.lapper

sealed trait Continuation[+T]
case class Return[+T](x: T) extends Continuation[T]
case class Bounce[+T](f: () => Continuation[T]) extends Continuation[T]
