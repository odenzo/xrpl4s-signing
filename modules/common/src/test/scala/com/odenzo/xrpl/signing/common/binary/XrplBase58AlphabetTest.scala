package com.odenzo.xrpl.signing.common.binary

import com.odenzo.xrpl.signing.common.utils.MyLogging
import com.tersesystems.blindsight.LoggerFactory
import io.circe.Encoder
import munit.*
import scodec.bits.{ BitVector, hex }

/**
  * This looks good and its simple. Ignores whitespace. Note I have trouble with
  * the base scodec using '1' as "zero" even using a different alphabet. e.g.
  * Default Base58 alphabets starts with `1` but Ripple starts with `r` THe
  * ByteVector and BitVector fromBase58Descriptive hard codes the `1` instead of
  * using alphabet.char(0)
  */
class XrplBase58AlphabetTest extends munit.FunSuite with MyLogging {
  private val log = LoggerFactory.getLogger

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
}
