package com.devinsideyou
package todo

import java.time.format.DateTimeFormatter

import cats._

object Program {
  def dsl[F[_]: effect.Sync]: F[Unit] = {
    val crudController: crud.Controller[F] =
      crud
        .DependencyGraph
        .dsl(Pattern, Console.dsl, Random.dsl)

    val program: F[Unit] =
      crudController.program

    println(
      s"[${scala.Console.YELLOW}warn${scala.Console.RESET}] Any output before this line is a bug!"
    )

    program
  }

  private val Pattern =
    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm")
}
