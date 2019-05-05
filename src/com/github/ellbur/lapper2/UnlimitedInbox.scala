
package com.github.ellbur.lapper2

import java.util.concurrent.LinkedBlockingQueue

class UnlimitedInbox extends Inbox {
  protected val queue = new LinkedBlockingQueue[Any]
}
