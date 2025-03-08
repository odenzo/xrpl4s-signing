package com.odenzo.xrpl.signing.common.binary

import com.odenzo.xrpl.signing.common.utils.*
import com.odenzo.xrpl.signing.common.utils.MyLogging
import com.tersesystems.blindsight.LoggerFactory
import io.circe.{ Decoder, Encoder }
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.BigIntegers
import scodec.bits.Bases.Alphabet
import scodec.bits.Bases.Alphabets.*
import scodec.bits.{ BitVector, ByteVector, hex }

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.{ MessageDigest, Provider, SecureRandom, Security }
import scala.annotation.tailrec
import scala.quoted.ToExpr.ArrayOfByteToExpr
import scala.util.Try

/** XrpSpecific and General Binary Ops, including some Bouncy Castle ops */
trait XrpBinaryOps extends MyLogging {
  private val log                         = LoggerFactory.getLogger
  private val sha256Digest: MessageDigest = MessageDigest.getInstance("SHA-256")
  Security.addProvider(new BouncyCastleProvider)
  val provider: Provider                  = Security.getProvider("BC")

  // These are defined elsewhere, and should be removed
  val accountPrefix          = hex"00"
  val publicKeyPrefix        = hex"23"
  val seedValuePrefix        = hex"21"
  val validationPubKeyPrefix = hex"1C"

  export ByteVector.given
  val secureRandom: SecureRandom = SecureRandom.getInstanceStrong

  /**
    * Given an unsigned (?) integer in as bytes convert to a BigInt for use in
    * Bouncy Castle/JCA. I am unsure exactly what it is excepting in terms of
    * bits, typically we have 32 or 33 bytes so its a large number. Way to bit
    * to fit in a long.
    */
  inline def convertByteVectorToBigInt(bv: ByteVector): BigInt = {
    BigIntegers.fromUnsignedByteArray(bv.toArray)
  }

  /**
    * Currently this does 2-complement to decimal represesentation but it DOES
    * NOT pad the bytes out to any fixed length
    */
  inline def convertBigIntToUnsignedByteVector(v: BigInt): ByteVector = {
    val bytes: Array[Byte] = BigIntegers.asUnsignedByteArray(v.bigInteger) // Can pass in a len to get zero left padding
    ByteVector(bytes)
  }

  def randomBits(len: Int): BitVector =
    val bytes = new Array[Byte](len)
    secureRandom.nextBytes(bytes)
    BitVector(bytes)

  /** Arbitrary but we will *always* use UTF-8 bytes to convert Strings to Bytes */
  def stringToByteVector_UTF8(s: String): ByteVector =
    ByteVector(s.getBytes(StandardCharsets.UTF_8))

  //  Security.addProvider(new BouncyCastleProvider)
  def xrpChecksum(body: ByteVector): ByteVector =
    sha256(sha256(body)).take(4)

  /**
    * This is equivalent to Ripple SHA512Half, it does SHA512 and returns first
    * 32 bytes
    */
  def sha512Half(bytes: ByteVector): ByteVector = sha512(bytes).take(32)

  /**
    * Default Java SHA256, should be the same as BouncyCastle sha512BC function.
    *
    * @param bytes
    * @return
    *   64-byte SHA512 hash with no salt added.
    */
  def sha256(bytes: ByteVector): ByteVector = {
    val array: Array[Byte] = MessageDigest.getInstance("SHA-256").digest(bytes.toArray)
    ByteVector(array)
  }

  /**
    * Default Java SHA512, should be the same as BouncyCastle sha512BC function.
    *
    * @param bytes
    * @return
    *   64-byte SHA512 hash with no salt added.
    */
  def sha512(bytes: ByteVector): ByteVector = {
    // toIndexedSeq will do a copy, enforcing immutable
    // unsafeArray  on  ArraySeq will not copy, and give the underlying mutable array
    // toArray also does a copy since Seq is immutable
    val array = MessageDigest.getInstance("SHA-512").digest(bytes.toArray)
    ByteVector(array)
  }

  /**
    * RipeMD160 digest/hash. Primarily used to convert PublicKey to AccountId
    *
    * @param bytes
    *   Is this supposed to be the Hash of original bytes or raw bytes?
    * @return
    */
  def ripemd160(bytes: ByteVector): ByteVector = {
    val md = MessageDigest.getInstance("RIPEMD160")
    ByteVector(md.digest(bytes.toArray))
  }

  /**
    * Limits on size to 64 bits, throws. ByteVector is considered as unsigned
    * bytes *
    */
  def unsignedBytesToBigInt(bv: ByteVector): BigInt =
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
    val checksum = xrpChecksum(chunk)
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
    XrpBase58.fromXrpBase58Descriptive(b58)

  def fromXrpBase58Unsafe(b58: String): ByteVector =
    fromXrpBase58(b58) match
      case Left(err)    => throw IllegalArgumentException(s"Invalid Base58: $err")
      case Right(value) => value

  /** These might beed fixing for leading 'r' (0 decimal) values */
  inline def toXrpBase58(input: IterableOnce[Byte]): String = toXrpBase58(ByteVector(input))
  inline def toXrpBase58(bv: ByteVector): String            = XrpBase58.toXrpBase58(bv)

  import io.circe.*
  import cats.syntax.all.*

  given Conversion[BitVector, ByteVector] = _.bytes
  given Conversion[ByteVector, BitVector] = _.bits

  extension (a: ByteVector)
    def stripPrefix(u: ByteVector): ByteVector             = u.drop(1)
    def stripChecksum(u: ByteVector): ByteVector           = u.dropRight(4)
    def unwrappedPropertyHandler: ByteVector => ByteVector = stripPrefix.andThen(stripChecksum)

    def base58Check(headerAndBody: ByteVector): ByteVector = headerAndBody ++ sha256(headerAndBody).take(4)

  extension (a: BitVector)
    def stripPrefix(u: BitVector): BitVector             = u.drop(1 * 8)
    def stripChecksum(u: BitVector): BitVector           = u.dropRight(4 * 8)
    def unwrappedPropertyHandler: Any                    = stripPrefix.andThen(stripChecksum)
    def base58Check(headerAndBody: BitVector): BitVector = headerAndBody ++ sha256(headerAndBody).take(4).bits

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
