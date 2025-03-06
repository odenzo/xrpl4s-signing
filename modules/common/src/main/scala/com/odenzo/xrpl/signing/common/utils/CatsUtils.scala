package com.odenzo.xrpl.signing.common.utils

import cats.Monoid
import scodec.bits.{ BitVector, ByteVector }

/** Some cats utils, not just some instances */
object CatsUtils {

  given Monoid[BitVector] with {
    def empty: BitVector                               = BitVector.empty
    def combine(x: BitVector, y: BitVector): BitVector = x ++ y
  }

  given Monoid[ByteVector] with {
    def empty: ByteVector                                 = ByteVector.empty
    def combine(x: ByteVector, y: ByteVector): ByteVector = x ++ y
  }
}
