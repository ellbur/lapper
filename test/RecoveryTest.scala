
import com.github.ellbur.lapper.Lapper._

import scala.collection.mutable.ArrayBuffer

object RecoveryTest extends App {
  case object Start
  case object Proceed
  case object Abort

  case class Aborted() extends Exception

  lazy val runner = actor { r =>
    r.react {
      case Start =>
        var acquired = ArrayBuffer[Int]()
        r.attempt {
          def iter(i: Int): Next = {
            if (i < 5) {
              acquired += i

              r.react {
                case Proceed =>
                  iter(i + 1)

                case Abort =>
                  throw Aborted()
              }
            }
            else {
              println("Acquired all the data: " + acquired.toSeq)
              done
            }
          }
          iter(0)
        } handling {
          case Aborted() =>
            println("Failed to acquire all the data. Only got " + acquired.toSeq)
            done
        }
    }
  }

  runner ! Start
  runner ! Proceed
  runner ! Proceed
  runner ! Abort
}
