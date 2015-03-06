
import com.github.ellbur.lapper.SingleReceiver

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object SingleReceiverTest extends App {
  {
    val r = new SingleReceiver

    Await.result(r.future, 1 second)
  }
}
