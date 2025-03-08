package com.odenzo.xrpl.signing.core.models

import com.odenzo.xrpl.signing.common.utils.CirceCodecUtils
import io.circe.Codec
import scodec.bits.{ ByteVector, hex }

/**
  * We just store the raw public key here, and serialize with the type prefix
  * The raw key is prefixed with 0xED if its ed25519 or nothing if its secp The
  * raw key has no field prefix or checksum (until its encoded). But hex"ED" is
  * added. For some core operations this needs to be stripped, but Address is
  * calculated with it.
  */
opaque type XrpPublicKey = ByteVector

object XrpPublicKey {
  val typePrefix = hex"23"

  given Codec[XrpPublicKey] = CirceCodecUtils.xrpBase58Codec

  /**
    * Package a raw public key in 33 bytes compressed form into wrapped,
    * serializable with the type prefix. If its 32 bytes its assumed to be an
    * ed25519 key and 0xED is added
    */
  def fromBytesUnsafe(b: ByteVector): XrpPublicKey =
    if b.size == 33 then b
    else if b.size == 32 then hex"ED" ++ b
    else throw IllegalArgumentException(s"AccountPublicKey size ${b.size} not in (32,33)")

  extension (apk: XrpPublicKey)
    /** @returns Full 33 byte key, including the 0xED for ED25519 */
    def asFullBytes: ByteVector = apk

    /**
      * @return
      *   33 bytes of raw secp public key or 32 bytes of raw ed25519 key
      *   (stripping away 0xED first byte)
      */
    def asRawKey: ByteVector =
      apk.size match
        case 33 if apk.head == 0xed => apk.drop(1)
        case 33                     => apk
        case other                  => throw IllegalStateException(s"Malformed AccountPublicKey: $apk")
}
