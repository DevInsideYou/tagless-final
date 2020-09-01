package com.devinsideyou

import java.time.LocalDateTime

import org.scalacheck.ScalacheckShapeless

import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import org.typelevel.discipline.scalatest.FunSuiteDiscipline

trait TestSuite
    extends AnyFunSuite
       with should.Matchers
       with GivenWhenThen
       with BeforeAndAfterAll
       with BeforeAndAfterEach
       with ScalaCheckPropertyChecks
       with ScalacheckShapeless
       with FunSuiteDiscipline {
  final protected type Arbitrary[A] =
    org.scalacheck.Arbitrary[A]

  final protected val Arbitrary =
    org.scalacheck.Arbitrary

  final protected type Assertion =
    org.scalatest.compatible.Assertion

  final protected type Gen[+A] =
    org.scalacheck.Gen[A]

  final protected val Gen =
    org.scalacheck.Gen

  final implicit protected val arbitraryForLDT: Arbitrary[LocalDateTime] =
    Arbitrary {
      Gen.calendar.map { calendar =>
        LocalDateTime.ofInstant(
          calendar.toInstant,
          calendar.getTimeZone.toZoneId
        )
      }
    }
}
