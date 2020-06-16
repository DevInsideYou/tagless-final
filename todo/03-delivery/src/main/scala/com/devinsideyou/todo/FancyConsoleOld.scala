package com.devinsideyou
package todo

trait FancyConsoleOld {
  def getStrLnTrimmed: String
  def getStrLnTrimmedWithPrompt(prompt: String): String
  def putStrLn(line: String): Unit
  def putSuccess(line: String): Unit
  def putWarning(line: String): Unit
  def putError(line: String): Unit
  def putStrLnInColor(line: String)(color: String): Unit
}

object FancyConsoleOld {
  implicit def dsl(implicit console: ConsoleOld): FancyConsoleOld =
    new FancyConsoleOld {
      override def getStrLnTrimmed: String =
        console.getStrLn.pipe(_.trim)

      override def getStrLnTrimmedWithPrompt(prompt: String): String =
        console.getStrLnWithPrompt(prompt + " ").pipe(_.trim)

      override def putStrLn(line: String): Unit =
        console.putStrLn(line)

      override def putStrLnInColor(line: String)(color: String): Unit =
        console.putStrLn(inColor(line)(color))

      private def inColor(line: String)(color: String): String =
        color + line + scala.Console.RESET

      override def putSuccess(line: String): Unit =
        putStrLnInColor(line)(scala.Console.GREEN)

      override def putWarning(line: String): Unit =
        putStrLnInColor(line)(scala.Console.YELLOW)

      override def putError(line: String): Unit =
        putStrLnInColor(line)(scala.Console.RED)
    }
}
