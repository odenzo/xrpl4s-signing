package com.odenzo.xrpl.signing.common.binary

import com.odenzo.xrpl.signing.common.utils.MyLogging
import com.tersesystems.blindsight.LoggerFactory
import io.circe.Encoder
import munit.*
import scodec.bits.{ BitVector, hex }

/**
  * Well, now things are broken and I had no decent tests. The inbuild
  * ByteVector functionality should work for cases that don't start with `r`s.
  * XrpSeed is broken, and AccountAddress isn't happy either. Lets work with
  * some known data (Genesis account) to verify whats happening.
  */
class XrplBase58Test extends munit.FunSuite with MyLogging {
  private val log = LoggerFactory.getLogger

  def roundTrip(base58Str: String) = {
    val fixed = XrpBase58.fromValidXrpBase58(base58Str)
    val back  = XrpBase58.toXrpBase58(fixed)
    log.info(s"""NIn:\t $base58Str
                |NFixed\t$fixed
                |NBack:\t $back""".stripMargin)

    val fixedO = BitVector.fromValidBase58(base58Str, XrplBase58Alphabet)
    val backO  = fixedO.toBase58(XrplBase58Alphabet)
    log.info(s"""OIn:\t $base58Str
                |OFixed\t$fixed
                |OBack:\t $back""".stripMargin)

  }

  test("Roundtrips") {
    roundTrip("r")
    roundTrip("rr")
    roundTrip("sno")
  }

  test("Seed") {
    roundTrip("snoPBrXtMeMyMHUVTgbuqAfg1SUTb")
  }

  test("ZERO") {
    val zero   = XrplBase58Alphabet.toChar(0)
    assertEquals(zero, 'r')
    val zindex = XrplBase58Alphabet.toIndex('r')
    assertEquals(zindex, 0)
  }

  test("Alphabet") {
    val alphabet                       = XrplBase58Alphabet.alphabet
    val mapped                         = XrplBase58Alphabet.mapped
    log.info(s"Mapped $mapped")
    val orderedChars: IndexedSeq[Char] = Range(0, 58).map { (i: Int) =>
      val char       = XrplBase58Alphabet.toChar(i)
      val index: Int = XrplBase58Alphabet.toIndex(char)
      assertEquals(index, i)
      char
    }
    val alphaback                      = orderedChars.foldLeft("")((acc, char) => acc + char)
    log.info(alphaback)
    assertEquals(alphaback, alphabet)
  }
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
