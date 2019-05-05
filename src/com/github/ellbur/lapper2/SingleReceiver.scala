
package com.github.ellbur.lapper2

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

class SingleReceiver extends Receiver {
  private var it: Option[Any] = None

  override def !(msg: Any): Unit = {
    synchronized {
      it = Some(msg)
      notifyAll()
    }
  }

  val future: Future[Any] = Future {
    blocking {
      synchronized {
        while (it.isEmpty)
          wait()
        it.get
      }
    }
  }
}
