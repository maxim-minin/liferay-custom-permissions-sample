package com.arcusys.customSecurityTest.repository

import com.arcusys.customSecurityTest.model.Message
import scala.collection.mutable.ArrayBuffer

object MessagesRepository {

  private val messages = new ArrayBuffer[Message]

  def create(body: String): Message = {
    val id = if (messages.isEmpty) 0 else messages.map(_.id).max
    val message = new Message(id + 1, body)
    messages += message
    message
  }

  def get(id: Int) = messages.find(_.id == id)

  def getAll = messages.toSeq

  def remove(id: Int) {
    for (message <- get(id)) messages -= message
  }
}
