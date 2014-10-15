
package com.github.ellbur.lapper

object Lapper {
  private def trampoline[T](f: Continuation[T]): T = f match {
    case Return(x) => x
    case Bounce(f) => trampoline(f())
  }

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
