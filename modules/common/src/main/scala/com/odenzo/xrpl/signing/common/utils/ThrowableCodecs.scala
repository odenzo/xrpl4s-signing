package com.odenzo.xrpl.signing.common.utils

import io.circe.{ Decoder, Encoder }

/**
  * This is just use for developer debugging. When we have a Throwable as a
  * member name. Bad coding.
  */
object ThrowableCodecs:
  given Encoder[Throwable] = Encoder.encodeString.contramap(_.getLocalizedMessage)
  given Decoder[Throwable] = Decoder.decodeString.map(s => Throwable(s))
