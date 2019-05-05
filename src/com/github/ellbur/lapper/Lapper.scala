
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

  def actorWithOptions(priority: Int = Thread.NORM_PRIORITY)(ff: Reactable => Next): Actor = {
    val inbox = new UnlimitedInbox

    val thread = new Thread {
      override def run(): Unit = {
        val f = ff(inbox)
        trampoline(f)
      }
    }
    thread.setPriority(priority)
    thread.start()

    inbox: Receiver
  }

  def throttledActor(size: Int, priority: Int = Thread.NORM_PRIORITY)(ff: Reactable => Next): Actor = {
    val inbox = new ThrottledInbox(size)

    val thread = new Thread {
      override def run(): Unit = {
        val f = ff(inbox)
        trampoline(f)
      }
    }
    thread.setPriority(priority)
    thread.start()

    inbox: Receiver
  }

  def replyingActor(ff: ReplyingReactable => Next): ReplyingActor = new ReplyingActor(ff)

  type Actor = Receiver
  type ReplyingActor = com.github.ellbur.lapper.ReplyingActor
}
