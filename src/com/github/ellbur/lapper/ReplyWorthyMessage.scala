
package com.github.ellbur.lapper

trait ReplyWorthyMessage[Response]

trait SomeReplyWorthyMessage {
  type Response
  val message: ReplyWorthyMessage[Response]
}
