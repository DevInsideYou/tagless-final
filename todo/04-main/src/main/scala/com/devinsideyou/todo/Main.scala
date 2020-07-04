package com.devinsideyou
package todo

object Main extends App {
  Program.dsl[cats.effect.IO].unsafeRunSync()
}
