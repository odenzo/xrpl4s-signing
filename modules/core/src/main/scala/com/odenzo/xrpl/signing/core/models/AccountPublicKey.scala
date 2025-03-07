package com.odenzo.xrpl.signing.core.models

import scodec.bits.{ ByteVector, hex }

/**
  * We just store the raw public key here, and serialize with the type prefix
  * The raw key is prefixed with 0xED if its ed25519 or nothing if its secp
  */
opaque type AccountPublicKey = ByteVector

object AccountPublicKey {
  val typePrefix = hex"23"

  /**
    * Package a raw public key in 33 bytes compressed form into wrapped,
    * serializable with the type prefix. If its 32 bytes its assumed to be an
    * ed25519 key and 0xED is added
    */
  def fromBytesUnsafe(b: ByteVector): AccountPublicKey =
    if b.size == 33 then b
    else if b.size == 32 then hex"ED" ++ b
    else throw IllegalArgumentException(s"AccountPublicKey size ${b.size} not in (32,33)")

  extension (apk: AccountPublicKey)
    /**
      * Returns 32 or 33 byte with no prefix. Strips 0xED from 33 bytes ti form
      * 32 byte
      */
    def asRawKey: ByteVector =
      apk.size match
        case 32                     => apk
        case 33 if apk.head == 0xed => apk.drop(1)
        case 33                     => apk
        case other                  => throw IllegalStateException(s"Malformed AccountPublicKey: $apk")
}
