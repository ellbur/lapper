
package com.github.ellbur.lapper

import java.lang.reflect.InvocationTargetException

import scala.annotation.tailrec

trait ReplyingReceiver {
  def !?~[A](msg: ReplyWorthyMessage[A]): A = this !? new SomeReplyWorthyMessage {
    override type Response = A
    override val message: ReplyWorthyMessage[Response] = msg
  }

  def !?(msg: SomeReplyWorthyMessage): msg.Response = {
    object message extends RepliableMessage {
      override val message: ReplyWorthyMessage[Response] = msg.message
      type Response = msg.Response
      private var thing: Option[Either[Throwable,Response]] = None
      override def reply(response: Response): Unit = {
        synchronized {
          thing = Some(Right(response))
          notifyAll()
        }
      }
      override def fail(t: Throwable): Unit = {
        synchronized {
          thing = Some(Left(t))
          notifyAll()
        }
      }
      def receive(): Response = {
        synchronized {
          @tailrec def step(): Response = {
            thing match {
              case Some(x) => x match {
                case Left(t) => throw new InvocationTargetException(t)
                case Right(r) => r
              }
              case None =>
                wait()
                step()
            }
          }
          step()
        }
      }
    }
    this ! message
    message.receive()
  }

  protected def !(msg: RepliableMessage): Unit
}
