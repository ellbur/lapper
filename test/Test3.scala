
import com.github.ellbur.lapper.Lapper._

object Test3 extends App {
  {
    case object Go

    val bob =
      actor { r =>
        println("Begin")

        r.react {
          case Go =>
            println("Done")
            done
        }
      }

    println("Bob has started!")
    bob ! Go
  }
}
