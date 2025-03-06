package com.odenzo.xrpl.signing.common.utils

import cats.implicits.catsSyntaxEitherId

opaque type ErrorOr[E <: Throwable, A] <: Either[E, A] = Either[E, A]

object ErrorOr:
  def success[E <: Throwable, A](a: A): ErrorOr[E, A] = a.asRight
  def failure[E <: Throwable, A](e: E): ErrorOr[E, A] = e.asLeft

  extension [E <: Throwable, A](a: ErrorOr[E, A])
    def asEither: Either[E, A] = a
    def getOrThrow: A          = a match
      case Left(e)      => throw e
      case Right(value) => value

  extension [E <: Throwable, A](ae: Either[E, A])
    // def toErrorOr: ErrorOr[E, A] = ae.fold[ErrorOr[E, A]](Left(_), Right(_))
    def getOrThrowEither: A = ae.fold(err => throw err, identity)
