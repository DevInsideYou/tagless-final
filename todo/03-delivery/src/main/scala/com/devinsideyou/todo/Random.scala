package com.devinsideyou
package todo

trait Random {
  def nextInt(n: Int): Int
}

object Random {
  implicit val dsl: Random =
    new Random {
      override def nextInt(n: Int): Int =
        scala.util.Random.nextInt(n)
    }
}
