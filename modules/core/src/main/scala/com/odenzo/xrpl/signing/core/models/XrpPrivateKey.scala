package com.odenzo.xrpl.signing.core.models

import cats.data.Validated
import com.odenzo.xrpl.signing.common.binary
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
opaque type XrpPrivateKey = ByteVector

object XrpPrivateKey:

  /**
    * Expect a 33 byte secp256k1 or prefixed 0xED ed25519 key. 32 byte keys are
    * assumed to be ed25519 and 0xED prefix is added
    */
  def fromBytesUnsafe(bv: ByteVector): XrpPrivateKey = bv
  def fromHexUnsafe(hex: String): XrpPrivateKey      = fromBytesUnsafe(ByteVector.fromValidHex(hex))
  def fromBase58Unsafe(v: String)                    = XrpBinaryOps.fromXrpBase58Unsafe(v)

  /** This doesn't seem to be picked automatically and needs import? */
  extension (ms: XrpPrivateKey)
    def bv: ByteVector = ms
    def asHex: String  = ms.toHex
    def base58: String = XrpBase58Fix.toXrpBase58(ms: ByteVector)

  object Codecs:
    given Codec[XrpPrivateKey] = CirceCodecUtils.xrpBase58Codec
