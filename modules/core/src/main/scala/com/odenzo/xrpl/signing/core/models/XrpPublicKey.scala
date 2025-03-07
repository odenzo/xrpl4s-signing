package com.odenzo.xrpl.signing.core.models

import cats.data.Validated
import com.odenzo.xrpl.signing.common.binary.{ XrpBase58Fix, XrpBinaryOps }
import com.odenzo.xrpl.signing.common.utils.CirceCodecUtils
import io.circe.{ Codec, Decoder, Encoder }
import scodec.bits.ByteVector.*
import scodec.bits.{ ByteVector, hex }
import scodec.given

/**
  * Supports secp or ed25519 key types. secp is 33 bytes, and ed25519 is 32
  * bytes normalized to 33 by prepended 0xED
  */
opaque type XrpPublicKey = ByteVector

object XrpPublicKey extends XrpBinaryOps:

  /**
    * Expect a 33 byte secp256k1 or prefixed 0xED ed25519 key. 32 byte keys are
    * assumed to be ed25519 and 0xED prefix is added
    */
  def fromBytesUnsafe(bv: ByteVector): XrpPublicKey = {
    bv.size match
      case 33       => bv
      case 32       => hex"ED" ++ bv
      case otherLen => throw IllegalArgumentException(s"Invalid Size: $otherLen not 32 or 33")
  }
  def fromHexUnsafe(hex: String): XrpPublicKey      = fromBytesUnsafe(ByteVector.fromValidHex(hex))

  /** This doesn't seem to be picked automatically and needs import? */
  extension (ms: XrpPublicKey)
    def bv: ByteVector       = ms
    def asHex: String        = ms.toHex
    def base58: String       = XrpBase58Fix.toXrpBase58(ms: ByteVector)
    def isEd25519: Boolean   = ms.head == 0xed
    def isSecp256k1: Boolean = !isEd25519

  def validated(ms: XrpPublicKey): Validated[String, XrpPublicKey] = {
    cats.data.Validated.cond(ms.size > 0, ms, s"XrpPublicKey cannot be zero length")
  }

  object Codecs:
    given base58Codec: Codec[XrpPublicKey] = CirceCodecUtils.xrpBase58Codec

end XrpPublicKey
