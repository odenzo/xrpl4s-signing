package com.odenzo.xrpl.signing.core.models

import cats.*
import cats.data.*
import cats.implicits.catsSyntaxEq
import cats.syntax.all.*
import com.odenzo.xrpl.signing.common.binary.XrpBinaryOps
import com.odenzo.xrpl.signing.common.utils.CirceCodecUtils
import com.odenzo.xrpl.signing.core.constants.TypePrefix
import com.tersesystems.blindsight.LoggerFactory
import io.circe.Codec
import scodec.bits.ByteVector

import scala.util.Random

/** This is the TypePrefix plus the body, without the Checksum */
opaque type XrpSeed <: ByteVector = ByteVector

object XrpSeed:
  private val log = LoggerFactory.getLogger

  val bodyLengthInBytes: Int = 21 // Including Prefix Byte and 4 byte checksum

  /** Integers to avoid Unicode which mucks with logging */
  def randomPassphrase: String = Random.nextLong().toString

  /** Validate the wrapped bytevector, including any prefixes etc */
  def validated(bv: ByteVector): ValidatedNec[String, XrpSeed] =
    (
      TypePrefix.typePrefixIs(bv.head, TypePrefix.SeedValue).toValidatedNec,
      Validated.condNec(
        bv.size === bodyLengthInBytes,
        bv,
        s"""| MasterSeed/XrpSeed should be ${bodyLengthInBytes}   but was ${bv.size}
            | The ByteVector was ${bv}
            | The typePrefix (not FieldId) was ${bv.head} ${TypePrefix.SeedValue}
            |""".stripMargin,
      ),
    ).mapN((_, _) => bv: XrpSeed)

  /**
    * Bytes given `b` get typePrefix pre-pended, but the checksum is not
    * appended. Essentially the given `b`
    */
  def fromBytesUnsafe(b: ByteVector): XrpSeed =
    assert(b.size == 16)
    TypePrefix.SeedValue.bv ++ b

  /** TODO: Add Validation THis will bve tge full typeCode + body + checksum */
  def fromBase58Unsafe(b58: String): XrpSeed =
    val full: ByteVector = XrpBinaryOps.fromXrpBase58Unsafe(b58) // Will have field and and checkum
    log.trace(s"Decoded [$b58] to $full")
    full: XrpSeed

  /**
    * Strips away a 21 byte down to 16 bytes after stipping checking and
    * typeCode
    */
  def unwrap(checksum58: ByteVector): XrpSeed =
    require(checksum58.size == 21, "XrpSeed/XrpSeed must be 21 bytes with typeCode and CheckSum")
    checksum58.drop(1).dropRight(4)

  // leftMap Function worthy of a utility
  def attemptFrom(bv: ByteVector): Either[String, XrpSeed] =
    log.trace(s"Attempting to decode XrpSeed attemptFrom Validation Phase: $bv")
    validated(bv).toEither.leftMap(errs => errs.foldSmash("Errors -> ", ";", "<-"))

  given base58: Codec[XrpSeed] =
    CirceCodecUtils.xrpBase58Codec.iemap((in: ByteVector) => attemptFrom(in))((seed: XrpSeed) => seed.bv)

  extension (ms: XrpSeed)
    /** Byte Vector including any prefix or checksum */
    def bv: ByteVector = ms

    /**
      * The raw 16 byte seed for crypto work. currently not storing the
      * checksum, only prefix
      */
    def asRawSeed: ByteVector = ms.drop(1)

end XrpSeed
