package com.odenzo.xrpl.signing.common.binary

import com.odenzo.xrpl.signing.common.utils.*
import com.odenzo.xrpl.signing.common.utils.MyLogging
import com.tersesystems.blindsight.LoggerFactory
import io.circe.{ Decoder, Encoder }
import scodec.bits.Bases.Alphabet
import scodec.bits.Bases.Alphabets.*
import scodec.bits.{ BitVector, ByteVector, hex }

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.{ MessageDigest, SecureRandom }
import scala.annotation.tailrec
import scala.quoted.ToExpr.ArrayOfByteToExpr
import scala.util.Try

/** XrpSpecific (mostly) binary utilities */
trait XrpBinaryOps extends MyLogging with HashOps {
  private val log                         = LoggerFactory.getLogger
  private val sha256Digest: MessageDigest = MessageDigest.getInstance("SHA-256")

  val accountPrefix          = hex"00"
  val publicKeyPrefix        = hex"23"
  val seedValuePrefix        = hex"21"
  val validationPubKeyPrefix = hex"1C"

  export ByteVector.given
  val secureRandom = SecureRandom.getInstanceStrong

  def randomBits(len: Int): BitVector =
    val bytes = new Array[Byte](len)
    secureRandom.nextBytes(bytes)
    BitVector(bytes)

  /** Arbitrary but we will *always* use UTF-8 bytes to convert Strings to Bytes */
  def stringToByteVector_UTF8(s: String): ByteVector =
    ByteVector(s.getBytes(StandardCharsets.UTF_8))

    /**
      * Limits on size to 64 bits, throws. ByteVector is considered as unsigned
      * bytes *
      */
  def unsignedBytesToBigInt(bv: ByteVector): BigInt  =
    val fromBytes: BigInt = BigInt.apply(bv.toArray)
    val signedBig         = BigInt(1, bv.toArray)
    signedBig

  /**
    * TypeCode and FieldCode need sorting. This is one way to do it. There bit
    * vectors are unsigned and hopefully this sorts on unsigned (not 2-comp)
    * representation. Thinking about making a specific compare for FieldId,
    * which now just delegates to these via TypeCode and FieldCode compares.
    */
  def fieldBitsCompare(bitsA: BitVector, bitsB: BitVector): Int = bitsA.compare(bitsB)

  /**
    * Given as a Public Key adds prefix and XRP checksum.
    * @param publicKey
    *   secp265k or ed25519 keys key, if ed25519 padded with 0xED
    * @return
    *   Ripple Account Address Base58 encoded with leading r and checksummed.
    */
  def accountPublicKey2address(publicKey: ByteVector): ByteVector = {
    // Should start with ED if 32 byte  Ed25519
    val accountId: ByteVector = HashOps.ripemd160(HashOps.sha256(publicKey))
    val body                  = accountPrefix ++ accountId
    body ++ HashOps.xrpChecksum(body) // Assume we checksum the prefix too, forget

  }

  /**
    * This trims off the first *byte* and the last four checksum bytes from
    * Base58 Ripple encoded things. Suitable for master seed and public key
    *
    * @param rippleB58
    * @return
    */
  inline def trimHeaderAndChecksum(field: ByteVector): ByteVector = field.dropRight(4).drop(1)

  /**
    * You will have to add prefix per https://xrpl.org/base58-encodings.html.
    * @returns
    *   preffix ++ body ++ checsum
    */
  inline def checksumRippleStyle(prefix: ByteVector, body: ByteVector): ByteVector = {
    // This is pure conversion, no family stuff. Adds s and checksum
    val chunk    = prefix ++ body
    val checksum = HashOps.xrpChecksum(chunk)
    chunk ++ checksum
  }

  /**
    * Check if all bytes are zero, returning true if so. Isn't there an index of
    * first set. Throws on empty ByteVector.
    */
  @tailrec
  final def isZero(bv: ByteVector): Boolean =
    // bv.dropWhile(_ == 0).isEmpty
    if bv.head != 0x00 then false
    else isZero(bv.tail)

  /** Converts Master Seed Hex to XRPL Base58Check encoding */
  def checksumMasterSeed(seed: ByteVector): ByteVector = {
    checksumRippleStyle(seedValuePrefix, seed)
  }

  /** Converts Public Key Hex to Riopple Base58Check encoding */
  def checksumPublicKey(publicKey: ByteVector): ByteVector = {
    checksumRippleStyle(publicKeyPrefix, publicKey)
  }

  def asBigInt(u: ByteVector): BigInt = XrpBinaryOps.unsignedBytesToBigInt(u)

  def asLong(u: ByteVector): Option[Long] = Option.when(asBigInt(u).isValidLong)(u.toLong(false))

  def fromXrpBase58(b58: String): Either[String, ByteVector] =
    XrpBase58Fix.fromXrpBase58Descriptive(b58)

  def fromXrpBase58Unsafe(b58: String): ByteVector =
    fromXrpBase58(b58) match
      case Left(err)    => throw IllegalArgumentException(s"Invalid Base58: $err")
      case Right(value) => value

  /** These might beed fixing for leading 'r' (0 decimal) values */
  inline def toXrpBase58(input: IterableOnce[Byte]): String = toXrpBase58(ByteVector(input))
  inline def toXrpBase58(bv: ByteVector): String            = XrpBase58Fix.toXrpBase58(bv)

  import io.circe.*
  import cats.syntax.all.*

}

object XrpBinaryOps extends XrpBinaryOps

/* // By Guillaume Martres trait NewType[Wrapped] { opaque type Type = Wrapped
 *
 * def apply(w: Wrapped): Type = w
 *
 * extension (t: Type) { def unwrap: Wrapped = t } }
 *
 * // Which I presume is used like this:
 * object Username extends NewType[String] object Token extends NewType[String] object UserId extends NewType[String] */
