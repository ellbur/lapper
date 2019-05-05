
package com.github.ellbur.lapper

trait DelayedReplyingFunction[+T] {
  def apply[R](message: ReplyWorthyMessage[R]): Continuation[(Either[Throwable,R], Continuation[T])]
}
