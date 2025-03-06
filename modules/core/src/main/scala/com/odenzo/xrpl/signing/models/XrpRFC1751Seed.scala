package com.odenzo.xrpl.signing.models

import io.circe.{ Decoder, Encoder }

opaque type XrpRFC1751Seed = String

object XrpRFC1751Seed:
  def fromStringUnsafe(s: String): XrpRFC1751Seed = s

  object Codecs:
    given Encoder[XrpRFC1751Seed] = Encoder.encodeString
    given Decoder[XrpRFC1751Seed] = Decoder.decodeString
