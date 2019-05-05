
package com.github.ellbur.lapper

trait RepliableMessage extends SomeReplyWorthyMessage {
  def reply(response: Response)
  def fail(t: Throwable)
}
