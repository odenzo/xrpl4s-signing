package com.odenzo.xrpl.signing.core.passphases

import com.odenzo.xrpl.signing.common.binary.XrpBinaryOps
import com.odenzo.xrpl.signing.core.models.XrpSeed
import scodec.bits.ByteVector

/**
  * Supports converting from RFC1751, a random hex string and a passphrease to
  * an XrpSeed (aka MasterSeed in this context)
  */
object PassphraseOps:

  def randomPassphrase: String = ???

  /**
    * Given an arbitrary String sniffs to see if its
    *   1. RFC1751,
    *   2. valid Hex string
    *   3. Valid passphrease
    *
    *   - First one that is valie wins and is converted to XrpSeed
    */
  def seedFromString(sniffed: String): XrpSeed = ???

  def seedFromPassphrase(s: String): XrpSeed =
    val bv: ByteVector = XrpBinaryOps.stringToByteVector_UTF8(s)
    XrpSeed.fromBytesUnsafe(XrpBinaryOps.sha512(bv).take(16))

  def seedFromRFC1751(words: String): XrpSeed = {
    RFC1751Keys.twelveWordsAsBytes(words)
  }

  def seedFromBytes(hex: String): XrpSeed =
    val bx = ByteVector.fromValidHex(hex)
    XrpSeed.fromBytesUnsafe(XrpBinaryOps.sha512(bx).take(16))
