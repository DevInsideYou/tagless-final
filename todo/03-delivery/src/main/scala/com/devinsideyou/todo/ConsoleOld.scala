package com.devinsideyou
package todo

trait ConsoleOld {
  def getStrLn: String
  def getStrLnWithPrompt(prompt: String): String
  def putStrLn(line: String): Unit
  def putErrLn(line: String): Unit
}

object ConsoleOld {
  implicit def dsl: ConsoleOld =
    new ConsoleOld {
      override def getStrLn: String =
        scala.io.StdIn.readLine()

      override def getStrLnWithPrompt(prompt: String): String =
        scala.io.StdIn.readLine(prompt)

      override def putStrLn(line: String): Unit =
        println(line)

      override def putErrLn(line: String): Unit =
        scala.Console.err.println(line)
    }
}
