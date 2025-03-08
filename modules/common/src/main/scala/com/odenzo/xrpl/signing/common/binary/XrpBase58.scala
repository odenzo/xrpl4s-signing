package com.odenzo.xrpl.signing.common.binary

import scodec.bits.{ Bases, ByteVector }

import scala.annotation.tailrec

/**
  * to/from XRP Base 58 with fixes in broken Scodec library. These all return
  * ByteVectors so have to do a `.bits` for the from as needed.
  *
  * No other code should *ever* use the built-in fromBase58/toBase58 for
  * ByteBector or BitVector from scodec TODO: Example Code
  */
object XrpBase58 {
  private val alphabet       = XrplBase58Alphabet
  private val zeroChar: Char = alphabet.toChar(0) // r for the xrp alphabet 1 for default bitcoin
  val foo                    = ByteVector.fromValidBase58("sB", Bases.Alphabets.Base58)

  /**
    * Constructs a `ByteVector` from a base 58 string or returns an error
    * message if the string is not valid base 58. It is similar to Base64 but
    * has been modified to avoid both non-alphanumeric characters and letters
    * which might look ambiguous when printed. It is therefore designed for
    * human users who manually enter the data, copying from some visual source
    * Compared to Base64, the following similar-looking letters are omitted: 0
    * (zero), O (capital o), I (capital i) and l (lower case L) as well as the
    * non-alphanumeric characters + (plus) and / (slash). The actual order of
    * letters in the alphabet depends on the application, the default order is
    * the same used in Bitcoin An empty input string results in an empty
    * ByteVector. The string may contain whitespace characters which are
    * ignored.
    * @group base
    */
  def fromXrpBase58Descriptive(
      str: String
  ): Either[String, ByteVector] = {

    val zeroLength = str.takeWhile(_ == zeroChar).length
    val zeroes     = ByteVector.fill(zeroLength.toLong)(0)
    val trim       = str.splitAt(zeroLength)._2.toList
    val RADIX      = BigInt(58L)
    try {
      val decoded = trim.foldLeft(BigInt(0)) { (a, c) =>
        try a * RADIX + BigInt(alphabet.toIndex(c))
        catch {
          case _: IllegalArgumentException =>
            val idx = trim.takeWhile(_ != c).length
            throw new IllegalArgumentException(s"Invalid base 58 character '$c' at index $idx")
        }
      }
      if (trim.isEmpty) Right(zeroes)
      else
        Right(zeroes ++ ByteVector(decoded.toByteArray.dropWhile(_ == 0)))

    }
    catch {
      case e: IllegalArgumentException => Left(e.getMessage)
    }
  }

  /** @throws IllegalArgumentException if invalid. */
  final def fromValidXrpBase58(s: String): ByteVector = {
    // Yeah, should just have the base version be unsafe, and have this catch and convert to either. oh well
    fromXrpBase58Descriptive(s) match
      case Left(errMsg) => throw IllegalArgumentException(errMsg)
      case Right(value) => value
  }

  final def toXrpBase58(bv: ByteVector): String =
    if (bv.isEmpty)
      ""
    else {
      val ZERO      = BigInt(0)
      val RADIX     = BigInt(58L)
      val zeroChars = List.fill(bv.takeWhile(_ == 0).length.toInt)(zeroChar)

      @tailrec
      def go(value: BigInt, chars: List[Char]): String =
        value match {
          case ZERO => (zeroChars ++ chars).mkString
          case _    =>
            val (div, rem) = value /% RADIX
            go(div, alphabet.toChar(rem.toInt) +: chars)
        }

      go(value = BigInt(signum = 1, magnitude = bv.toArray), chars = List.empty)
    }

  // Add the toXrpBase58 as extension to Bit and ByteVector, maybe but in XrpBinOps instead of here
}

import scodec.bits.Bases.Alphabet

/**
  * Scodec Bits Alphabet to user for XRP Base58. used with Scodec Bits and all
  * the rest should be deleted. Not that we implement a new Alphabet
  * XrpBase58Fix
  */
object XrplBase58Alphabet extends Alphabet {
  val alphabet               = "rpshnaf39wBUDNEGHJKLM4PQRST7VWXYZ2bcdeCg65jkm8oFqi1tuvAxyz"
  val chars: Array[Char]     = alphabet.toArray
  val mapped: Map[Char, Int] = chars.zipWithIndex.toMap

  override def toChar(i: Int): Char = chars(i)

  override def toIndex(c: Char): Int = {
    mapped.get(c) match
      case Some(value) => value
      case None        => throw new IllegalArgumentException(s"Illegal XRP Base58 Char: $c")
  }

  override def ignore(c: Char): Boolean = c.isWhitespace
}
