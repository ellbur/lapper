
package com.github.ellbur.lapper

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

class ThrottledInbox(size: Int) extends Inbox {
  override protected val queue: BlockingQueue[Any] = new EndDroppingArrayBlockingQueue[Any](size)
}
