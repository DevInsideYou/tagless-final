package com.devinsideyou
package todo
package crud

import java.time.LocalDateTime
import java.util.UUID

import skunk._
import skunk.codec.all._
import skunk.implicits._

object Statement {
  final implicit private class TodoDataCompanionOps(
      private val data: Todo.Data.type
    ) {
    val codec: Codec[Todo.Data] =
      (text ~ timestamp).gimap[Todo.Data]
  }

  final implicit private class TodoExistingCompanionOps(
      private val existing: Todo.Existing.type
    ) {
    val codec: Codec[Todo.Existing[UUID]] =
      (uuid ~ Todo.Data.codec).gimap[Todo.Existing[UUID]]
  }

  object Insert {
    val one: Query[Todo.Data, Todo.Existing[UUID]] =
      sql"""
               INSERT INTO todo (description, deadline)
               VALUES (${Todo.Data.codec})
            RETURNING *
         """.query(Todo.Existing.codec)

    def many(size: Int): Query[List[Todo.Data], Todo.Existing[UUID]] =
      sql"""
               INSERT INTO todo (description, deadline)
               VALUES (${Todo.Data.codec.list(size)})
            RETURNING *
         """.query(Todo.Existing.codec)

    object WithUUID {
      val one: Command[Todo.Existing[UUID]] =
        sql"""
              INSERT INTO todo
              VALUES (${Todo.Existing.codec})
           """.command

      def many(size: Int): Command[List[Todo.Existing[UUID]]] =
        sql"""
              INSERT INTO todo
              VALUES (${Todo.Existing.codec.list(size)})
           """.command
    }
  }

  object Update {
    val one: Query[Todo.Existing[UUID], Todo.Existing[UUID]] =
      sql"""
               UPDATE todo
                  SET description = $text, deadline = $timestamp
                WHERE id = ${uuid}
            RETURNING *
         """.query(Todo.Existing.codec).contramap(toTwiddle)

    object Command {
      val one: Command[Todo.Existing[UUID]] =
        sql"""
              UPDATE todo
                 SET description = $text, deadline = $timestamp
               WHERE id = ${uuid}
           """.command.contramap(toTwiddle)
    }

    private def toTwiddle(
        e: Todo.Existing[UUID]
      ): String ~ LocalDateTime ~ UUID =
      e.data.description ~ e.data.deadline ~ e.id
  }

  object Select {
    val all: Query[Void, Todo.Existing[UUID]] =
      sql"""
            SELECT *
              FROM todo
         """.query(Todo.Existing.codec)

    def many(size: Int): Query[List[UUID], Todo.Existing[UUID]] =
      sql"""
            SELECT *
              FROM todo
             WHERE id IN (${uuid.list(size)})
         """.query(Todo.Existing.codec)

    val byDescription: Query[String, Todo.Existing[UUID]] =
      sql"""
            SELECT *
              FROM todo
             WHERE description ~ $text
         """.query(Todo.Existing.codec)
  }

  object Delete {
    val all: Command[Void] =
      sql"""
            DELETE
              FROM todo
         """.command

    def many(size: Int): Command[List[UUID]] =
      sql"""
            DELETE
              FROM todo
             WHERE id IN (${uuid.list(size)})
         """.command
  }
}
