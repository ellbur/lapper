
import java.util.concurrent.LinkedBlockingQueue

object Test2 extends App {
  trait Reactable {
    def react[T](f: PartialFunction[Any, T]): T
  }

  trait Receiver {
    def !(msg: Any): Unit
  }

  class Inbox extends Reactable with Receiver {
    private[this] val queue = new LinkedBlockingQueue[Any]

    def !(msg: Any): Unit = {
      queue.put(msg)
    }

    def react[T](f: PartialFunction[Any, T]) =  f(queue.take())
  }

  sealed trait Continuation[+T]
  case class Return[+T](x: T) extends Continuation[T]
  case class Bounce[+T](f: () => Continuation[T]) extends Continuation[T]

  def trampoline[T](f: Continuation[T]): T = f match {
    case Return(x) => x
    case Bounce(f) => trampoline(f())
  }

  def then[T](x: => T) = () => x

  def actor(ff: Reactable => Continuation[Unit]) = {
    val inbox = new Inbox

    new Thread {
      override def run(): Unit = {
        val f = ff(inbox)
        trampoline(f)
      }
    }.start()

    inbox: Receiver
  }

  case object Go

  val bob =
    actor { r =>
      println("Begin")

      r.react {
        case Go =>
          println("Done")
          Return(())
      }
    }

  println("Bob has started!")
  bob ! Go
}
