
package com.github.ellbur.lapper2

object Lapper {
  def actor(ff: Reactable => Unit, priority: Int = Thread.NORM_PRIORITY): Actor = {
    val inbox = new UnlimitedInbox

    val thread =
      new Thread {
        override def run(): Unit = {
          ff(inbox)
        }
      }
    thread.setPriority(priority)
    thread.start()

    new Actor {
      override def join(): Unit = thread.join()
      override def !(msg: Any): Unit = inbox ! msg
    }
  }

  def throttledActor(size: Int, priority: Int = Thread.NORM_PRIORITY)(ff: Reactable => Unit): Actor = {
    val inbox = new ThrottledInbox(size)

    val thread =
      new Thread {
        override def run(): Unit = {
          ff(inbox)
        }
      }
    thread.setPriority(priority)
    thread.start()

    new Actor {
      override def join(): Unit = thread.join()
      override def !(msg: Any): Unit = inbox ! msg
    }
  }
}
