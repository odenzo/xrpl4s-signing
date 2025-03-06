package com.odenzo.xrpl.signing.common.utils

import cats.data.{ NonEmptyChain, Validated, ValidatedNec }
import cats.effect.*
import cats.effect.syntax.all.*

import cats.*
import cats.data.*
import cats.syntax.all.*

/**
  * Some generic Cats Validation Helpers. Add some Echopraxia structured logging
  * in here too
  */
trait ValidationUtils:

  /**
    * Converts Validation Errors to message in IllegalArgument Exception and
    * throws
    */
  def throwValidationErrors[A](v: ValidatedNec[String, A]): A =
    v match
      case Validated.Valid(a)                          => a
      case Validated.Invalid(e: NonEmptyChain[String]) =>
        val msg = e.foldSmash("Validation Errors:", ";", ".")
        throw IllegalArgumentException(msg)

  def throwValidationErrors[A](named: String, v: ValidatedNec[String, A]): A =
    v match
      case Validated.Valid(a)                          => a
      case Validated.Invalid(e: NonEmptyChain[String]) =>
        val msg = e.foldSmash(s"$named Validation Errors:", ";", ".")
        throw IllegalArgumentException(msg)

object ValidationUtils extends ValidationUtils
