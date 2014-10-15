
import com.github.ellbur.lapper.Lapper._
import com.github.ellbur.lapper.Locked

object Test4 extends App {
  {
    case object Go

    val keepGoing = Locked[Boolean](true)

    val bob =
      actor { r =>
        println("Begin")

        def countingLoop(n: Int): Next = {
          if (n < 1000000) {
            r.react {
              case Go =>
                countingLoop(n + 1)
            }
          }
          else {
            println("Done")
            keepGoing() = false
            done
          }
        }
        countingLoop(0)
      }

    def spamLoop(): Unit = {
      if (keepGoing()) {
        bob ! Go
        spamLoop()
      }
    }
    spamLoop()
  }
}
