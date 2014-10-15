
package com.github.ellbur.lapper

object Lapper {
  private def trampoline[T](f: Continuation[T]): T = f match {
    case Return(x) => x
    case Bounce(f) => trampoline(f())
  }

  type Next = Continuation[Unit]

  def then[T](x: => Continuation[T]) = Bounce(() => x)

  val done = Return(())

  def actor(ff: Reactable => Next) = {
    val inbox = new Inbox

    new Thread {
      override def run(): Unit = {
        val f = ff(inbox)
        trampoline(f)
      }
    }.start()

    inbox: Receiver
  }
}
