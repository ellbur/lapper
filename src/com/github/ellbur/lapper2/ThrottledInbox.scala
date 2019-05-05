
package com.github.ellbur.lapper2

import java.util.concurrent.BlockingQueue

class ThrottledInbox(size: Int) extends Inbox {
  override protected val queue: BlockingQueue[Any] = new EndDroppingArrayBlockingQueue[Any](size)
}
