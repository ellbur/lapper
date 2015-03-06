
import com.github.ellbur.lapper.Lapper._
import concurrent.duration._
import scala.language.postfixOps

object LoopTest extends App {
  {
    actor { r =>
      r.loop {
        r.reactWithin(1 second) {
          case _ =>
            println("1")
            assert(false)
            done
        }
      }
    }
  }
}
