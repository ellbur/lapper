
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

  def restartOnError(f: Next): Next =
    try {
      trampoline(f)
      done
    }
    catch {
      case t: Throwable =>
        System.err.println("Actor caught exception:")
        t.printStackTrace()
        System.err.println("Inside restartOnError -- Actor will now restart.")
        restartOnError(f)
    }
}
