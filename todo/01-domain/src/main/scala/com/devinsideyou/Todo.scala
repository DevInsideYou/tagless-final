package com.devinsideyou

import java.time.LocalDateTime

sealed abstract class Todo[+TodoId] extends Product with Serializable {
  protected type ThisType <: Todo[TodoId]

  import Todo._

  final def fold[B](
      ifExisting: (TodoId, Data) => B,
      ifData: (String, LocalDateTime) => B
    ): B =
    this match {
      case Existing(id, data)          => ifExisting(id, data)
      case Data(description, deadline) => ifData(description, deadline)
    }

  def description: String

  def withUpdatedDescription(newDescription: String): ThisType

  def deadline: LocalDateTime

  def withUpdatedDeadline(newDeadline: LocalDateTime): ThisType
}

object Todo {
  final case class Existing[TodoId](id: TodoId, data: Data)
      extends Todo[TodoId] {
    override protected type ThisType = Existing[TodoId]

    override def description: String =
      data.description

    override def withUpdatedDescription(newDescription: String): ThisType =
      copy(data = data.withUpdatedDescription(newDescription))

    override def deadline: LocalDateTime =
      data.deadline

    override def withUpdatedDeadline(newDeadline: LocalDateTime): ThisType =
      copy(data = data.withUpdatedDeadline(newDeadline))
  }

  final case class Data(description: String, deadline: LocalDateTime)
      extends Todo[Nothing] {
    override protected type ThisType = Data

    override def withUpdatedDescription(newDescription: String): ThisType =
      copy(description = newDescription)

    override def withUpdatedDeadline(newDeadline: LocalDateTime): ThisType =
      copy(deadline = newDeadline)
  }
}
