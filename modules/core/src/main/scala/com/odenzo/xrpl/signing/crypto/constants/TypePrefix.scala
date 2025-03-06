package com.odenzo.xrpl.signing.crypto.constants

import cats.Eq
import cats.data.Validated
import cats.implicits.catsSyntaxEq
import scodec.bits.ByteVector

/**
  * https://xrpl.org/docs/references/protocol/data-types/base58-encodings The
  * table will all the encodings the XRP Ledger Uses
  *
  * @param byte
  */
enum TypePrefix(val prefix: Byte):
  case AccountAddress extends TypePrefix(0x00)
  case AccountPublicKey extends TypePrefix(0x23)
  case SeedValue extends TypePrefix(0x21)
  case ValidationPublicKey extends TypePrefix(0x1c)

  def bv: ByteVector = ByteVector(prefix)

object TypePrefix {
  def typePrefixIs(target: Byte, typePrefix: TypePrefix): Validated[String, Byte] =
    Validated.cond(
      typePrefix.prefix === target,
      target,
      s"TypePrefix $typePrefix didn't match given $target",
    )
}
