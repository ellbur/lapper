
package com.github.ellbur.lapper

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

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
