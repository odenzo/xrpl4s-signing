package com.odenzo.xrpl.signing.common.utils

import cats.*
import cats.data.*
import cats.syntax.all.*
import com.tersesystems.blindsight.LoggerFactory
import io.circe
import io.circe.Decoder.Result
import io.circe.jawn.JawnParser
import io.circe.syntax.*
import io.circe.*
import io.circe.derivation.Configuration

import java.io.File
import scala.io.Source
import scala.util.Try

trait CirceUtils extends MyLogging {
  private val log = LoggerFactory.getLogger(getClass)

  def loadResourceAsString(resourcePath: String): Either[RuntimeException, String] = {
    Try {
      val stream = getClass.getResourceAsStream(resourcePath)
      val source = Source.fromResource(resourcePath)
      val text   = source.mkString
      source.close()
      text

    }.toEither.left.map(t => new RuntimeException(s"Error loading resource $resourcePath: ${t.getMessage}", t))
  }

  // Parsing Files
  // Parsing Resources
  def parseResource(resourcePath: String): Either[Exception, Json] = {
    loadResourceAsString(resourcePath).flatMap { src =>
      log.info(s"Got the Src from $resourcePath => $src")
      val t = src.mkString
      parseJson(t)
    }
  }

  inline def parseJson(src: String): Either[ParsingFailure, Json] = io.circe.parser.parse(src)

  inline def decode[T: Decoder](src: String): Either[circe.Error, T] = io.circe.parser.decode(src)

  inline def decodeJson[T: Decoder](src: Json): Either[circe.Error, T] = src.as[T]

  def decodeUnsafe[T](src: Json)(using Decoder[T]): T = {
    src.as[T] match
      case Left(err)    =>
        log.warn(s"Error Decoding Json: ${err.getMessage} for \n ${src.spaces2}")
        throw err
      case Right(value) => value

  }
  // Wrapping Errors with full JSON source
  // Specific JSON Errors (to embed additional info)

  // To Clean:

  /** Ripled doesn't like objects like { x=null } */
  val droppingNullsPrinter: Printer = Printer.spaces2.copy(dropNullValues = true)

  /**
    * Converts json to formatted text dropping null JsonObject fields.
    *
    * @param json
    * @return
    */
  def print(json: Json): String = json.deepDropNullValues.noSpaces

  def printObj(jsonObject: JsonObject): String = print(jsonObject.asJson)

  /** Monoid/Semigroup for Circe Json Object so we can add them togeher. */
  given jsonObjectMonoid: Monoid[JsonObject] = new Monoid[JsonObject] {
    def empty: JsonObject = JsonObject.empty

    def combine(x: JsonObject, y: JsonObject): JsonObject = JsonObject.fromIterable(x.toVector |+| y.toVector)
  }

  /** Adds a command field to the given Request */
  def deriveRqEncoder[T: Encoder.AsObject](command: String): Encoder.AsObject[T] = {
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
  //  def renameLedgerField(autoLedger: JsonObject, fieldName: String = "ledger"): JsonObject = {
  //    val oldKey = fieldName
  //
  //    // Looks like Json.fold is reasonable way but not now.
  //    // Also, case Json.JString(v)  not working really. Are there unaaply somewhere.
  //    val ledgerVal: Option[(String, Json)] = autoLedger(oldKey).map {
  //      case json if json.isNumber => ("ledger_index", json)
  //      case json if json.isString =>
  //        val hashOrName: (String, Json) = json.asString match {
  //          case Some(ledger) if Hash256.isValidHash(ledger) => ("ledger_hash", json)
  //          case Some(assume_named_ledger)                   => ("ledger_index", json)
  //        }
  //        hashOrName
  //
  //      case other => ("INVALID_LEDGER", Json.Null) // Not sure how to signal error yet
  //    }
  //
  //    ledgerVal.map(field => field +: autoLedger.remove(oldKey)).getOrElse(autoLedger)
  //
  //
  //  }

}
object CirceUtils extends CirceUtils
