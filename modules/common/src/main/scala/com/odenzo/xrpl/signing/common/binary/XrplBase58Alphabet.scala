package com.odenzo.xrpl.signing.common.binary

import scodec.bits.Bases.Alphabet

/**
  * Scodec Bits Alphabet to user for XRP Base58. used with Scodec Bits and all
  * the rest should be deleted.
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
