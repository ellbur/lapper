
package com.github.ellbur.lapper
import Continuation.trampoline

object Lapper {
  type Next = Continuation[Unit]

  def then[T](x: => Continuation[T]) = Bounce(() => x)

  val done = Return(())

  def actor(ff: Reactable => Next): Actor = {
    val inbox = new Inbox

    new Thread {
      override def run(): Unit = {
        val f = ff(inbox)
        trampoline(f)
      }
    }.start()

    inbox: Receiver
  }

  type Actor = Receiver
}
