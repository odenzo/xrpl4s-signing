package com.odenzo.xrpl.signing.common.utils

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*
import io.circe.*
import io.circe.Decoder.{ Result, withReattempt }

trait ErrorUtils {

  // Trying to minimize my previous goofiness

  def raiseError[E <: Throwable, T](attempt: Either[E, T]): T =
    attempt match
      case Left(value)  => throw value
      case Right(value) => value
}

object ErrorUtils extends ErrorUtils
