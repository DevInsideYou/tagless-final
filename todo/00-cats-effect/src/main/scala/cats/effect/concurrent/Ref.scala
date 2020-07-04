package cats
package effect
package concurrent

import java.util.concurrent.atomic.AtomicReference

import cats.implicits._

trait Ref[F[_], A] {
  def get: F[A]
  def set(a: A): F[Unit]

  def update(aa: A => A): F[Unit]
  def updateAndGet(aa: A => A): F[A]
  def modify[B](aab: A => (A, B)): F[B]
}

object Ref {
  def of[F[_]: Sync, A](a: A): F[Ref[F, A]] =
    F.delay {
      new Ref[F, A] {
        private[this] var state: AtomicReference[A] =
          new AtomicReference(a)

        override def get: F[A] =
          F.delay(state.get)

        override def set(a: A): F[Unit] =
          F.delay(state.set(a))

        override def update(aa: A => A): F[Unit] =
          updateAndGet(aa).void

        override def updateAndGet(aa: A => A): F[A] =
          modify { a =>
            val desiredState = aa(a)
            val result = desiredState

            desiredState -> result
          }

        override def modify[B](aab: A => (A, B)): F[B] = {
          @scala.annotation.tailrec
          def setOrDieTrying: B = {
            val currentState = state.get
            val (desiredState, result) = aab(currentState)

            if (state.compareAndSet(currentState, desiredState))
              result
            else
              setOrDieTrying
          }

          F.delay(setOrDieTrying)
        }
      }
    }
}
