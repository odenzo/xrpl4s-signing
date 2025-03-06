package com.odenzo.xrpl.signing.common.binary

import com.tersesystems.blindsight.LoggerFactory
import io.circe.{ Codec, Decoder, Encoder }
import scodec.bits.BitVector
/* case class Residue[M <: Int](n: Int) extends AnyVal { def +(rhs: Residue[M])(implicit m: ValueOf[M]):
 * Residue[M] = Residue((this.n + rhs. n) % valueOf[M]) } val fiveModTen = Residue[10](5) val nineModTen =
 * Residue[10](9) fiveModTen + nineModTen // OK == Residue[10](4) val fourModEleven = Residue[11](4) fiveModTen +
 * fourModEleven // compiler error: type mismatch; // found : Residue[11] */
/**
  * A Fixed Size Binary expects/demands a BitVector of the required length
  * exactly. Here a will normally an Opaque type (opaque type XXX = BitVector)
  * Example try FixedSizeBinary[A,S<:Int] and valueOf on the functions. Maybe
  * then we can write Circe Encoder and Decoder for A that are opaque type A =
  * BitVector. Should have a base typeclass Isomorphism, that is Cats already?
  */
trait FixedSizeBinary[A](fixedSizeInBits: Long) {

  private val log = LoggerFactory.getLogger
  def fromBits(bits: BitVector): A

  def toBits(a: A): BitVector

  def validated(bits: BitVector): A = (fromBits andThen ensureLen)(bits)

  def ensureLen(b: A): A = {

    val bin = toBits(b)
    bin.size.compareTo(fixedSizeInBits) match
      case 0  => b
      case -1 => fromBits(bin.padLeft(fixedSizeInBits))
      case 1  =>
        log.warn(s"${bin.size} > $fixedSizeInBits fixed size bits. Truncating.")
        fromBits(bin.drop(bin.size - fixedSizeInBits))
  }
  def xor(a: A, b: A): A = fromBits(toBits(a).xor(toBits(b)))

  def and(a: A, b: A): A = fromBits(toBits(a).and(toBits(b)))

  def convertToUnsignedLong(a: A): Long = toBits(a).toLong(signed = false)

  def convertToBigInt(a: A): BigInt = BigInt(toBits(a).toByteArray)

  def convertToHex(a: A): String    = toBits(a).toHex
  def convertToBase58(a: A): String = toBits(a).toBase58(XrplBase58Alphabet)

  def convertFromHex(s: String): Either[String, BitVector]    = BitVector.fromHexDescriptive(s)
  def convertFromBase58(a: String): Either[String, BitVector] = BitVector.fromBase58Descriptive(a, XrplBase58Alphabet)

  val decoderHex: Decoder[A]             = Decoder.decodeString.emap(convertFromHex).map(validated)
  val encoderHex: Encoder[A]             = Encoder.encodeString.contramap[A](convertToHex)
  val encoderB58: Encoder[A]             = Encoder.encodeString.contramap[A](convertToBase58)
  val decodeB58: Decoder[A]              = Decoder.decodeString.emap(convertFromBase58).map(validated)
  val hexCodec: Codec[A]                 = Codec.from(decoderHex, encoderHex)
  extension (x: A) def asBits: BitVector = toBits(x)

}
