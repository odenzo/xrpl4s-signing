package com.odenzo.xrpl.signing.common.binary

import cats.data.{ Validated, ValidatedNec }
import io.circe
import io.circe.*
import _root_.scodec.bits.{ BitVector, ByteVector, hex }

import java.security.MessageDigest
import scala.util.Try

/** Generic (mostly) Exensions and implicits for ByteVector and BitVector */
object XrpBinOps {

  given Conversion[BitVector, ByteVector] = _.bytes
  given Conversion[ByteVector, BitVector] = _.bits

  extension (a: ByteVector)
    def stripPrefix(u: ByteVector): ByteVector             = u.drop(1)
    def stripChecksum(u: ByteVector): ByteVector           = u.dropRight(4)
    def unwrappedPropertyHandler: ByteVector => ByteVector = stripPrefix.andThen(stripChecksum)

    def base58Check(headerAndBody: ByteVector): ByteVector = headerAndBody ++ HashOps.sha256(headerAndBody).take(4)

  extension (a: BitVector)
    def stripPrefix(u: BitVector): BitVector             = u.drop(1 * 8)
    def stripChecksum(u: BitVector): BitVector           = u.dropRight(4 * 8)
    def unwrappedPropertyHandler: Any                    = stripPrefix.andThen(stripChecksum)
    def base58Check(headerAndBody: BitVector): BitVector = headerAndBody ++ HashOps.sha256(headerAndBody).take(4).bits

}
