
package com.github.ellbur.lapper

trait ReplyingFunction[+T] {
  def apply[R](message: ReplyWorthyMessage[R]): (Either[Throwable,R], Continuation[T])
}
