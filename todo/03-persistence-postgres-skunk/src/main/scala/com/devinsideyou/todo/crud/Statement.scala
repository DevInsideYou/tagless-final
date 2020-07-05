package com.devinsideyou
package todo
package crud

import java.time.LocalDateTime
import java.util.UUID

import skunk._
import skunk.codec.all._
import skunk.implicits._

object Statement {
  final implicit private class UUIDCodecOps(
      private val uuid: Codec[UUID]
    ) extends AnyVal {
    def string: Codec[String] =
      uuid.imap(_.toString)(UUID.fromString)
  }

  final implicit private class TodoDataCompanionOps(
      private val data: Todo.Data.type
    ) {
    val codec: Codec[Todo.Data] =
      (text ~ timestamp).gimap[Todo.Data]
  }

  final implicit private class TodoExistingCompanionOps(
      private val existing: Todo.Existing.type
    ) {
    val codec: Codec[Todo.Existing] =
      (uuid.string ~ Todo.Data.codec).gimap[Todo.Existing]
  }

  object Insert {
    val one: Query[Todo.Data, Todo.Existing] =
      sql"""
               INSERT INTO todo (description, deadline)
               VALUES (${Todo.Data.codec})
            RETURNING *
         """.query(Todo.Existing.codec)

    def many(size: Int): Query[List[Todo.Data], Todo.Existing] =
      sql"""
               INSERT INTO todo (description, deadline)
               VALUES (${Todo.Data.codec.list(size)})
            RETURNING *
         """.query(Todo.Existing.codec)

    object WithUUID {
      val one: Command[Todo.Existing] =
        sql"""
              INSERT INTO todo
              VALUES (${Todo.Existing.codec})
           """.command

      def many(size: Int): Command[List[Todo.Existing]] =
        sql"""
              INSERT INTO todo
              VALUES (${Todo.Existing.codec.list(size)})
           """.command
    }
  }

  object Update {
    val one: Query[Todo.Existing, Todo.Existing] =
      sql"""
               UPDATE todo
                  SET description = $text, deadline = $timestamp
                WHERE id = ${uuid.string}
            RETURNING *
         """.query(Todo.Existing.codec).contramap(toTwiddle)

    object Command {
      val one: Command[Todo.Existing] =
        sql"""
              UPDATE todo
                 SET description = $text, deadline = $timestamp
               WHERE id = ${uuid.string}
           """.command.contramap(toTwiddle)
    }

    private def toTwiddle(e: Todo.Existing): String ~ LocalDateTime ~ String =
      e.data.description ~ e.data.deadline ~ e.id
  }

  object Select {
    val all: Query[Void, Todo.Existing] =
      sql"""
            SELECT *
              FROM todo
         """.query(Todo.Existing.codec)

    def many(size: Int): Query[List[String], Todo.Existing] =
      sql"""
            SELECT *
              FROM todo
             WHERE id IN (${uuid.string.list(size)})
         """.query(Todo.Existing.codec)

    val byDescription: Query[String, Todo.Existing] =
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

    def many(size: Int): Command[List[String]] =
      sql"""
            DELETE
              FROM todo
             WHERE id IN (${uuid.string.list(size)})
         """.command
  }
}
