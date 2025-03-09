package com.odenzo.xrpl.signing.core.secp256k1

import cats.*
import cats.data.*
import cats.implicits.*
import com.odenzo.xrpl.signing.common.binary.XrpBinaryOps
import com.odenzo.xrpl.signing.common.utils.MyLogging
import com.tersesystems.blindsight.LoggerFactory
import org.bouncycastle.asn1.{ASN1Integer, DERSequenceGenerator}
import org.bouncycastle.util.BigIntegers
import scodec.bits.{BitVector, ByteVector, hex}

import java.io.ByteArrayOutputStream
import java.math.BigInteger
import scala.util.Try

/**
  * This is used just for DER encoding of ECDSA Signature for Secp256k1 Ripple
  * stuff.
  *   - r and s from Signature are used as Java BigInteger for downstream Bouncy
  *     Castle crypto calls.
  */
object DER extends MyLogging with XrpBinaryOps {
  private val log = LoggerFactory.getLogger

  val header = hex"30"

  /**
    * s should be less then CurveOrder - s, this returns true if it is else
    * false using secp256k1 curve
    */
  def checkIfSignatureCanonical(r: BigInteger, s: BigInteger): Boolean = {
    val order: BigInteger = SecpOps.Constants.N_Order // This is N, is it always positive
    val invS: BigInteger  = order.subtract(s)
    // S should be less than invS
    (s.compareTo(invS) <= 0) // S > invS  then false

  }

  /**
    * Call out to Bouncy Castle to convert `R` amd `S` into bytes
    * @return
    *   Dynamic length array byte (70-72 normally)
    */
  protected def derBytes(r: BigInteger, s: BigInteger): Array[Byte] = { // Usually 70-72 bytes.
    val bos = new ByteArrayOutputStream(72)
    val seq = new DERSequenceGenerator(bos)
    seq.addObject(new ASN1Integer(r))
    seq.addObject(new ASN1Integer(s))
    seq.close()
    bos.toByteArray
  }

  /**
    * This is guaranteed to have a 0x30 as first byte and second byte unsigned
    * int of remaining length the an R and S field.
    */
  case class Signature(txnSignature: ByteVector, r: Field, s: Field)

  case class Field(prefix: Byte, lenBin: BitVector, value: ByteVector) {
    lazy val toByteVector: ByteVector = ByteVector.fromByte(prefix) ++ lenBin.toByteVector ++ value
    def asBigInteger: BigInteger      = convertByteVectorToBigInt(value).bigInteger
    def len: Short                    = lenBin.toShort(false)

  }

  object Signature extends MyLogging {

    /**
      * Construct a DER.Signature from r and s values assumed to be like (64
      * unsigned int?)
      *
      * How long/big are we expecting r and s?
      */
    def packFromRandS(r: BigInteger, s: BigInteger): ByteVector = {
      // https://bitcoin.stackexchange.com/questions/2376/ecdsa-r-s-encoding-as-a-signature
      // Try and use a lib
      log.debug(s"r = $r   s = $s")

      // JCA does it, but sometimes we need to pack zero in front of r and s
      // It appears zlen is calculated not including ht
      // And S has an extra zero byte in from
      // I think we may just want to trim to get them to size?

      // TODO: Pay attention to possible 2-complement encoding
      // TODO: Investigate 0x02 vs 0x03
      // TODO: Check on the dt at end, think bitcoin only
      val rBytes = convertBigIntToUnsignedByteVector(r)
      val rLen   = ByteVector.fromLong(rBytes.size, 1) // Convert length to a unsigned byte (unsigned?)
      val rField = hex"02" ++ rLen ++ rBytes

      val sBytes = convertBigIntToUnsignedByteVector(s)
      val sLen   = ByteVector.fromLong(sBytes.size, 1)
      val sField = hex"02" ++ sLen ++ sBytes

      val sigLen = ByteVector.fromLong(rField.size + sField.size, 1)

      val ht = hex"6D"

      // Wrap in XRPL format
      hex"30" ++ sigLen ++ rField ++ sField

    }

  }
}
