package com.devinsideyou

import java.time.LocalDateTime

sealed abstract class Todo extends Product with Serializable {
  protected type ThisType <: Todo

  def description: String

  def withUpdatedDescription(newDescription: String): ThisType

  def deadline: LocalDateTime

  def withUpdatedDeadline(newDeadline: LocalDateTime): ThisType
}

object Todo {
  final case class Existing(id: String, data: Data) extends Todo {
    override protected type ThisType = Existing

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
      extends Todo {
    override protected type ThisType = Data

    override def withUpdatedDescription(newDescription: String): ThisType =
      copy(description = newDescription)

    override def withUpdatedDeadline(newDeadline: LocalDateTime): ThisType =
      copy(deadline = newDeadline)
  }
}
