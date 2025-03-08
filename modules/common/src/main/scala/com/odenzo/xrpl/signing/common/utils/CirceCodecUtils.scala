package com.odenzo.xrpl.signing.common.utils

import cats.*
import cats.data.*
import cats.syntax.all.*
import com.odenzo.xrpl.signing.common.binary.{XrpBase58, XrplBase58Alphabet}
import com.odenzo.xrpl.signing.common.binary.XrpBinaryOps.toXrpBase58
import com.tersesystems.blindsight.LoggerFactory
import io.circe
import io.circe.Decoder.{ Result, decodeString }
import io.circe.Encoder
import io.circe.jawn.JawnParser
import io.circe.syntax.*
import io.circe.*
import io.circe.derivation.Configuration
import scodec.bits.{ BitVector, ByteVector }

import java.io.File
import scala.io.Source
import scala.util.Try

/** Some of this is duplicated all over the place. Centralize here. */
trait CirceCodecUtils extends MyLogging {
  private val log                                          = LoggerFactory.getLogger
  import XrpBase58.*
  val alphabet: XrplBase58Alphabet.type                    = XrplBase58Alphabet
  def fromXrpBase58Unsafe(s: String): ByteVector           = XrpBase58.fromValidXrpBase58(s)
  def fromXrpBase58(s: String): Either[String, ByteVector] = XrpBase58.fromXrpBase58Descriptive(s)
  def toXrpBase58(bits: BitVector): String                 = XrpBase58.toXrpBase58(bits.bytes)
  def toXrpBase58(bytes: ByteVector): String               = XrpBase58.toXrpBase58(bytes)

  def oldFrom(b58: String) = ByteVector.fromBase58Descriptive(b58, alphabet)

  /** Codecs for Base58 Strings */
  val xrpBase58Codec: Codec[ByteVector] = Codec.from[ByteVector](
    decodeString.emap(s => fromXrpBase58(s)),
    Encoder.encodeString.contramap((a: ByteVector) => toXrpBase58(a)),
  )

  val xrpBitsBase58Codec: Codec[BitVector] = Codec.from[BitVector](
    decodeString.emap(s => fromXrpBase58(s).map(_.bits)),
    Encoder.encodeString.contramap((a: BitVector) => toXrpBase58(a.bytes)),
  )

  val hexCodec: Codec[ByteVector] = Codec.from(
    decodeString.emap(s => ByteVector.fromHexDescriptive(s)),
    io.circe.Encoder.encodeString.contramap(a => a.toHex),
  )

  val hexBitsCodec: Codec[BitVector] = Codec.from(
    decodeString.emap(s => BitVector.fromHexDescriptive(s)),
    Encoder.encodeString.contramap(bv => bv.toHex),
  )

  /** this is kinda worthless */
  inline def opaqueHexBitsCodecFor[T](from: BitVector => Either[String, T], to: T => BitVector): Codec[T] =
    hexBitsCodec.iemap[T](from)(to)

  /** Adds a command field to the given Request for WebSocket format */
  def deriveRpcRqEncoder[T: Encoder.AsObject](command: String): Encoder.AsObject[T] = {
    summon[Encoder.AsObject[T]].mapJsonObject((jo: JsonObject) => jo.add("command", Json.fromString(command)))
  }

  /**
    * Utility to rename a field in a JsonObject, typically used in encoders
    * .mapJsonObject
    *
    * @param autoLedger
    * @param fieldName
    * @return
    */

  type KeyTransformer = String => String

  // -------------------- Turn Renaming of Keys into a Configuration Object ----------
  def customConfiguration(fn: KeyTransformer, skipping: List[String] = List.empty): Configuration = {
    def skip(name: String): String = if skipping.contains(name) then name else fn(name)

    Configuration.default.withTransformMemberNames(skip)
  }

  def customConfiguration(fn: KeyTransformer): Configuration = Configuration.default.withTransformMemberNames(fn)

  /** Capitalize a somewhat normal word */
  val capitalize: KeyTransformer = _.capitalize

  val typeName: KeyTransformer = (s: String) => if s.equals("xrpType") then "type" else s

  val unCapitalize: KeyTransformer = { (toString: String) =>
    if (toString == null) null
    else if (toString.isEmpty) toString
    else if (toString.charAt(0).isLower) toString
    else {
      val chars = toString.toCharArray
      chars(0) = chars(0).toLower
      new String(chars)
    }
  }

  def capitalizeConfig: Configuration = customConfiguration(capitalize)

  def capitalizeConfig(except: List[String]): Configuration =
    if except.isEmpty then customConfiguration(capitalize) else customConfiguration(capitalize, except)

  /**
    * Complicated for no reason, but these fields are never capitalized in V1.
    * Normally pass them in explicitly
    */
  val whitelist: Set[String] = Set("hash", "id", "command", "secret", "tx_json")

  /**
    * Typically used (slowly) for encoders. Possibly better to use derived or
    * generic-extras Note that it has a white-list of fields not to upcase (e.g.
    * hash)
    */
  def capitalizeFields(obj: JsonObject): JsonObject = {
    // Could optimize this a bit by checking if key.head.isUpper or something I bet.
    // And also case (key,json) if !whitelist.contains(key)
    // Also design these generic and maybe so we can compose on fields
    val upcasedFirst = obj.toList.map {
      case (key, json) if !whitelist.contains(key) => (capitalize(key), json)
      case other                                   => other
    }
    JsonObject.fromIterable(upcasedFirst)
  }

  /**
    * Idea here is to partially apply this with any (oldName,newName) entries
    * you want. If name not found then no changes to name. For instance, to
    * capitalize all and change some_oddball to SomeOddball
    * {{{
    *   val myMap = Map[String,String] ( "SomeOddball" -> "some_oddball")
    *   val customerOnly = customerNameTransformer(myMap)
    *   val composeStuff = capitalize.compose(customerOnly) // or x compose y style
    *   val moreReadbleFn = customerOnly.andThen(capitalize)
    * }}}
    *
    * @param map
    * @param name
    *
    * @return
    */
  def customNameTransformer(map: Map[String, String], name: String): String = map.getOrElse(name, name)

  def capitalizeExcept(these: List[String]): (String) => String = { (key: String) =>
    val newKey = if (!these.contains(key)) key.capitalize else key
    // logger.debug(s"Converted $key -> $newKey")
    newKey
  }

  /**
    * Caution that this must be done AFTER any general transaction of names for
    * top level
    */
  def liftJsonObject(obj: JsonObject, field: String): JsonObject = {
    // Relies RippleTransaction being encoded as JsonObject
    obj(field).flatMap(_.asObject) match {
      case None            => obj
      case Some(objToLift) => JsonObject.fromIterable(obj.remove(field).toList ::: objToLift.toList)
    }
  }

  def fieldNameChangeEx(name: String, newName: String)(in: JsonObject): JsonObject = {
    // If missing existing name return JsonObject unchanges.
    // If oldname == null then i guess will add newName : null
    val updated: Option[JsonObject] = in(name).map(oldVal => in.add(newName, oldVal)).map(jo => jo.remove(name))
    updated.getOrElse(in)
  }

  /**
    * *
    * {{{
    *     val changer = fieldNameChangeEx("oldName","newName")
    *     Decoder[A].prepare(prepareByMungingJsonObject(changer))
    * }}}
    *
    * @param fn
    * @param in
    *
    * @return
    */
  def prepareByMungingJsonObject(fn: JsonObject => JsonObject)(in: ACursor): ACursor = {
    in.withFocus(json => json.mapObject(jobj => fn(jobj)))
  }

}
object CirceCodecUtils extends CirceCodecUtils
