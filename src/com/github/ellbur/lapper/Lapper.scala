
package com.github.ellbur.lapper

import Continuation.trampoline

object Lapper {
  type Next = Continuation[Unit]

  def then[T](x: => Continuation[T]) = Bounce(() => x)

  val done = Return(())

  def actor(ff: Reactable => Next): Actor = {
    val inbox = new UnlimitedInbox

    new Thread {
      override def run(): Unit = {
        val f = ff(inbox)
        trampoline(f)
      }
    }.start()

    inbox: Receiver
  }

  def throttledActor(size: Int)(ff: Reactable => Next): Actor = {
    val inbox = new ThrottledInbox(size)

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
