package com.odenzo.xrpl.signing.common.utils

import com.fasterxml.jackson.databind.JsonNode
import com.tersesystems.blindsight.{ *, given }
import io.circe.*
import io.circe.syntax.*
import net.logstash.logback.argument.*
import net.logstash.logback.argument.StructuredArguments.{ kv, raw }

import java.time.Instant
import java.util.UUID

trait BlindsightUtils {

  import com.tersesystems.blindsight.AST.{ *, given }
  import com.tersesystems.blindsight.DSL.{ *, given }
  import io.circe.Encoder

//  implicit val requestToResolver: LoggerResolver[UUID] = LoggerResolver { (instance: UUID) =>
//    org.slf4j.LoggerFactory.getLoggerFactory.getLogger("requests." + instance.toString)
//  }

  import com.tersesystems.blindsight.AST.{ *, given }
  import com.tersesystems.blindsight.DSL.{ *, given }

  given ToArgument[List[String]] = { v =>
    Argument(bobj("details" -> BArray(v.map(new BString(_)))))
  }

  /**
    * This allows any T that has a Circe Encoder to be converted to String.
    * There is no real use case for this in structured logging
    */
  extension [T: Encoder](x: T)
    def asBValue: BString = {
      BString(x.asJson.noSpacesSortKeys)
    }

  given ToArgument[Throwable] = { err =>
    // WOuld love to have a stack trace maybe four deep or something
    // B;indsight logback stuff has an exception formatter, hmmm...
    Argument(bobj("exception" -> bobj("message" -> err.getMessage)))

  }

  /**
    * TODO: Place this more ergonomically into a ToArgument. This is also slow
    * as..
    */
  def jlog(circeJson: Json): JsonNode = {
    import com.fasterxml.jackson.databind.*
    val res: JsonNode = new ObjectMapper().readTree(circeJson.spaces2)
    res
  }

  given ToArgument[(String, Json)] = { tj =>
    Argument(
      bobj(
        BField(tj._1, BString(tj._2.spaces4))
      )
    )
  }

// Make this a Valid AST Node I guess
  //  given ToArgument[circe:Json] = { circe =>
//    circeToJacksonJsonNode(circe)
//  }

  given ToArgument[(String, BValue)] = {
    case (k, v) =>
      Argument(bobj(k -> v))
  }

  /** Allows easy logging of a Circe JSON Decoding Failure */
  given ToArgument[DecodingFailure] = { err =>
    val bo = bobj(
      BField("circe", "decoding_error"),
      BField("reason", err.reason.toString),
      BField("error_message", err.message),
      BField("history", err.history.toString),
      BField("root_string", err.pathToRootString.getOrElse("Unknown Root")),
    )
    Argument(bo)
  }

  /** Instant to String */
  def instantToBString(i: Instant): BString = new BString(i.toString)

  /** Instant to epoch millies */
  def instantToBTimestamp(i: Instant): BValue = new BLong(i.toEpochMilli)

  def jarg(key: String, json: Json): Argument = {
    val jnode = jlog(json)
    new Argument(jnode)
  }

  def jsonToAST(json: Json): AST.BValue = {
    json.fold(
      jsonNull    = AST.BNull,
      jsonBoolean = (v: Boolean) => BBool(v),
      jsonNumber  = (v: JsonNumber) => BString(json.noSpaces),
      jsonString  = (x: String) => BString(x),
      jsonArray   = arr => BArray(arr.map(jsonToAST).toList),
      jsonObject  = (obj: JsonObject) => {
        val ast: List[(String, BValue)] =
          obj.toList.sortBy(_._1).map { case (field, v) => BField(field, jsonToAST(v)) }
        BObject(ast)
      },
    )
  }

  def logArg[T: Encoder](label: String, t: T): Argument = jsonToArgument(label, t.asJson)

  def jsonToArgument(label: String, json: Json): Argument = {
    Argument(bobj(label -> jsonToAST(json)))
  }
//  given jsonToArgument[T: Encoder]: ToArgument[(String, T)] = ToArgument {
//    import com.fasterxml.jackson.databind.JsonNode
//    case (k, instance) =>
//      val node = JsonNode
//      Argument(StructuredArguments.keyValue(k, node)) // or raw(k, node.toPrettyString)
//  }

// Some Marker helprs

  object markers {
    import net.logstash.logback.marker.Markers as LogstashMarkers
    import net.logstash.logback.marker.{ Markers => LogstashMarkers }
    private val tlog = LoggerFactory.getLogger

    /**
      * Safe to use as log.withMarker(processMarker("foo"))... will add to
      * Markers
      */
    def processMarker(processName: String): BObject = bobj("process" -> BString(processName))
    def correlationId(id: String): BObject          = bobj("traceId" -> BString(id))

    val xx: Markers   = Markers(processMarker("foo"))
    val m1log: Logger = tlog.withMarker(processMarker("foo")).withMarker(correlationId("1"))
    m1log.markers.size
    /* import net.logstash.logback.marker.{Markers => LogstashMarkers} val loggerWithTwoMarkers =
     * entryLogger.withMarker(LogstashMarkers.append("user", "will")) */
    // def addMarker(markers: Markers, marker:Ma) =
  }

}
