package com.odenzo.xrpl.signing.crypto.seeds

import cats.syntax.all.*
import org.bouncycastle.crypto.AsymmetricCipherKeyPair

import java.security.KeyPair

/// Private below here, or not exported as part of standard interoperable stuff.
/// Deprecating this if I can
case class Base58Check(v: String)
case class TxnSignature(hex: String)

// "error" : "invalidParams", "error_code" : 31, "error_message" : "Missing field 'secret'.",
case class ResponseError(error: String, error_code: Option[Int], error_message: Option[String])

object ResponseError {
  val kNoAccount: ResponseError     = invalid("Missing field 'account'")
  val kNoTxJson: ResponseError      = invalid("Missing field 'tx_json'")
  val kNoSecret: ResponseError      = invalid("Missing field 'secret'.")
  val kNoCommand: ResponseError     = ResponseError("missingCommand", None, None)
  val kBadCommand: ResponseError    = ResponseError("unknownCommand", None, Some("'sign' is the only command supporteds"))
  val kTooMany: ResponseError       = invalid(
    "Exactly one of the following must be specified: passphrase, secret, seed or seed_hex"
  )
  val kSecretAndType: ResponseError = invalid("The secret field is not allowed if key_type is used.")
  val kBadSecret: ResponseError     = ResponseError("badSecret", 41, "Secret does not match account.")

  def apply(err: String, code: Int, msg: String): ResponseError = ResponseError(err, Some(code), Some(msg))
  def invalid(msg: String): ResponseError                       = ResponseError("invalidParams", 31, msg)
  def internalErr(msg: String): ResponseError                   = ResponseError("Internal Client System Error", 666.some, msg.some)
}
