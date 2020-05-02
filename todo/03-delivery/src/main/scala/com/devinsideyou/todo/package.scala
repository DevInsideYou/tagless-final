package com.devinsideyou

package object todo {
  final implicit class PipeAndTap[A](private val a: A) extends AnyVal {
    @inline final def pipe[B](ab: A => B): B = ab(a)
    @inline final def tap[U](au: A => U): A = { au(a); a }
    @inline final def tapAs[U](u: => U): A = { u; a }
  }
}
