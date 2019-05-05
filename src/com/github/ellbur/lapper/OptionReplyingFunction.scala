
package com.github.ellbur.lapper

trait OptionReplyingFunction[+T] {
  def none(): Continuation[T]
  def apply[R](message: ReplyWorthyMessage[R]): (Either[Throwable,R], Continuation[T])
}
