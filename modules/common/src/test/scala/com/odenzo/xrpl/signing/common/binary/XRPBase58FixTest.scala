package com.odenzo.xrpl.signing.common.binary

import com.odenzo.xrpl.signing.common.utils.MyLogging
import com.tersesystems.blindsight.LoggerFactory
import munit.*
import scodec.bits.BitVector

class XRPBase58FixTest extends munit.FunSuite with MyLogging {
  private val log    = LoggerFactory.getLogger
  // These all have checksum attached, Base64Checksum style
  val ACCOUNT_ZERO   = "rrrrrrrrrrrrrrrrrrrrrhoLvTp"
  val ACCOUNT_ONE    = "rrrrrrrrrrrrrrrrrrrrBZbvji"
  val ACCOUNT_SAMPLE = "rDTXLQ7ZKZVKz33zJbHjgVShjsBnqMBhmN"

  /* - https://xrpl.org/docs/references/protocol/data-types/base58-encodings
   * - Data Type Starts With Type Prefix Content size¹ Maximum characters
   * -Account address: r 0x00 20 bytes 35
   * - Account public key: a 0x23 33 bytes 53
   * - Seed value (for secret keys): s 0x21 16 bytes 29
   * - Validation public key or node public key: n 0x1C 33 bytes 53 */
  test("Encode ") {
    val src       = ACCOUNT_SAMPLE
    val bv        = XrpBinaryOps.fromXrpBase58Unsafe(src)
    val backToB58 = XrpBinaryOps.toXrpBase58(bv)
    log.debug(s"$src => ( 1 byte + ${bv.size} bytes + 4 bytes) ${bv.toHex}")

  }

  test("r -zeros") {
    val bv = BitVector.fromBase58Descriptive("r", XrplBase58Alphabet)
    log.info(s"r => $bv")
  }
}
