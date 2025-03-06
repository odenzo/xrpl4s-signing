package com.odenzo.xrpl.signing.crypto.seeds

import com.odenzo.xrpl.signing.common.binary.XrpBinaryOps
import com.odenzo.xrpl.signing.models.XrpSeed
import scodec.bits.ByteVector

trait SeedOps {

  def seedFromPassphrase(s: String): XrpSeed =
    val bytes = XrpBinaryOps.stringToByteVector_UTF8(s)
    seedFromBytes(bytes)

  def seedFromRFC1751(words: String): XrpSeed = {
    RFC1751Keys.twelveWordsAsBytes(words)
  }

  def seedFromBytes(bv: ByteVector): XrpSeed =
    XrpSeed.fromBytesUnsafe(XrpBinaryOps.sha512(bv).take(16))

}

object SeedOps extends SeedOps
