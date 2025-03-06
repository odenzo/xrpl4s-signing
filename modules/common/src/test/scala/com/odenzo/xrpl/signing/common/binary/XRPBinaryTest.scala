package com.odenzo.xrpl.signing.common.binary

import com.odenzo.xrpl.signing.common.utils.MyLogging
import com.tersesystems.blindsight.LoggerFactory
import io.circe.Encoder
import munit.*
import scodec.bits.{ BitVector, hex }

class XRPBinaryTest extends munit.FunSuite with MyLogging {
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

  test("Simple Usage ") {

    trait BinaryClass[A] {
      def bits: BitVector
      def instance(bv: BitVector): A
    }
    object Foo           {
      case class BinChunk(bits: BitVector)
      object BinChunk {}
    }

    given ops: FixedSizeBinary[Foo.BinChunk](32) with {
      def fromBits(bits: BitVector): Foo.BinChunk = Foo.BinChunk(bits)
      def toBits(a: Foo.BinChunk): BitVector      = a.bits

    }
  }

  test("r -zeros") {
    val s = "rBB"
    val bv = BitVector.fromBase58Descriptive(s, XrplBase58Alphabet).map(_.padLeft(26))
    log.info(s" $s => $bv")
    bv.foreach(v => log.info(s"${v.toBin}"))
  }
}
