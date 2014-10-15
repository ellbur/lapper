
object Test1 extends App {
  def react[T](f: PartialFunction[Any, T]): T = f(())

  def then[T](x: => T) = () => x

  sealed trait Continuation[+T]
  case class Return[+T](x: T) extends Continuation[T]
  case class Bounce[+T](f: () => Continuation[T]) extends Continuation[T]

  def trampoline[T](f: Continuation[T]): T = f match {
    case Return(x) => x
    case Bounce(f) => trampoline(f())
  }

  {
    trampoline {
      def go(n: Int): Continuation[Unit] = {
        react {
          case () =>
            if (n < 100000)
              Bounce(then(go(n + 1)))
            else
              Return(println("Done"))
        }
      }

      go(0)
    }
  }
}
