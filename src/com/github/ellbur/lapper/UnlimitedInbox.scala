
package com.github.ellbur.lapper

import java.util.concurrent.LinkedBlockingQueue
import Lapper._
import scala.concurrent.duration.Duration

class UnlimitedInbox extends Inbox {
  protected val queue = new LinkedBlockingQueue[Any]
}
