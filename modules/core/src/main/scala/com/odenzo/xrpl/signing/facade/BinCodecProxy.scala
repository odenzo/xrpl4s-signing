package com.odenzo.xrpl.signing.facade





//package com.odenzo.ripple.signing.impl
//
//import io.circe.Json
//import cats.*
//import cats.data.*
//import cats.implicits.*
//import com.odenzo.xrpl.common.ELogging
//import com.odenzo.xrpl.common.utils.ELogging
//import scodec.bits.ByteVector
//
///**
//  * Production level proxying to ripple-binary-codec routines just to add a
//  * layer and error conversion. Error conversion done because we want
//  * ripple-binary-codec to be usable without any binding into this lib. This
//  * whole thing could be an implementation details, but maybe some use exposing
//  * it?
//  *
//  * TODO: Move/Copy the BinCodec stuff for tx_json parsing into something I cann
//  * pull into signing-local without a lot of overhead.
//  */
//trait BinCodecProxy extends ELogging {
//
//  /**
//    * Serializes a transaction payload in Ripple binary format for constructing
//    * a TxBlob from. All isSerializable fields serialized.
//    *
//    * @param jsonObject
//    *   tx_json with PubSigningKey and TxnSignature added
//    *
//    * @return
//    *   Hex string representing the serialization in total.
//    */
//  @inline
//  final def binarySerialize(jsonObject: Json): Either[LocalOpsError, EncodedSTObject] = {
//    RippleCodecDebugAPI.binarySerialize(jsonObject)
//  }
//
//  /**
//    * Serializes a transaction payload in Ripple binary format for constructing
//    * a TxnSignature from.
//    *
//    * @param tx_json
//    *   JsonObject representing a transaction tx_json, no auto-filling of fields
//    *   is done.
//    *
//    * @return
//    *   Encoded/Serialized structure that can be inspected or converted to
//    *   bytes.
//    */
//  @inline
//  final def binarySerializeForSigning(tx_json: Json): Either[LocalOpsError, EncodedSTObject] = {
//    RippleCodecDebugAPI.binarySerializeForSigning(tx_json)
//  }
//
//  /**
//    * Serializes a transaction payload in Ripple binary format for constructing
//    * a TxnSignature from.
//    *
//    * @param tx_json
//    *
//    * @return
//    *   Serialized form for signing, but without the HashPrefix.
//    */
//  final def serialize(tx_json: Json): ByteVector = {
//    RippleCodecAPI.serializedTxBlob(tx_json)
//  }
//
//  /**
//    * @param tx_json
//    *   Fully formed tx_json with all auto-fillable fields etc.
//    *
//    * @return
//    *   tx_json in binary format, which corresponds to TxBlob in the main code.
//    *   TxBlob
//    */
//  final def serializeForSigning(tx_json: Json): ByteVector = {
//    RippleCodecAPI.signingTxBlob(tx_json)
//  }
//
//  /**
//    * Details here. Use case and function. Used for sorting Signer fields,
//    * does/should it strip prefix and suffix or not
//    */
//  def serializeAddress(signAddrB58Check: String): Any = {
//    RippleCodecAPI.serializedAddress(signAddrB58Check)
//  }
//
//  /** Debugging Routine mostly... but in Test context */
//  def decodeBlob(blob: String): Any = {
//    TxBlobBuster.bust(blob)
//  }
//}
//
//object BinCodecProxy extends BinCodecProxy
