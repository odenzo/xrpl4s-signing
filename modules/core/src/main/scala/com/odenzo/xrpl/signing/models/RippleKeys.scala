package com.odenzo.xrpl.signing.models





//package com.odenzo.xrpl.models.keys
//
//import cats.implicits.*
//import com.odenzo.xrpl.common.XRPBase58
//import com.odenzo.xrpl.models.atoms.*
//import io.circe.*
//import io.circe.generic.semiauto.*
//import io.circe.syntax.*
//
///**
//  * FIXME: Turn this into enumerated types. Maybe a subpackage for all the
//  * Crypto stuff
//  */
//
///* "account_id" : "ra8iBMyU6JrEVLxBBG98sWnmai3fKdTZvd", AKA: AccountAddress "key_type" : "secp256k1", "master_key" :
// * "FOLD SAT ORGY PRO LAID FACT TWO UNIT MARY SHOD BID BIND", "master_seed" : "sn9tYCjBpqXgHKwJeMT1LC4fdC17d",
// * "master_seed_hex" : "B07650EDDF46DE42F9968A1A2557E783", "public_key" :
// * "aBPUAJbNXvxP7uiTxmCcCpVgrGjsbJ8f7hQaYPRrdajXNWXuCNLX", "public_key_hex" :
// * "02A479B04EDF3DC29EECC89D5F795E32F851C23B402D637F5552E411C354747EB5"
// *
// * Base58 -- Like Ripple Address , Public Key, Validation Seed RFC-1751 -- like master_key and validation_key
// *
// * TODO: Define base types like these in seperate file. */
//
///**
//  * I use signature to normalize the different types of keys and associated
//  * information required in requests Also to mask the values in toString here,
//  * if the secret if > 4 since I have hacky tests . But for now, only support
//  * the RippleMasterSeed from AccountKeys.
//  */
//sealed trait RippleSignature
//
//object RippleSignature {
//
//  def mask(s: String): String = s.zipWithIndex.map((c, i) => if (i > 4 & i % 2 === 1) '*' else c).mkString
//
//  implicit val encoder: Encoder[RippleSignature] = Encoder.instance[RippleSignature] {
//    case d: RippleSeed => d.asJson
//    case d: RippleKey  => d.asJson
//  }
//}
//
//
//
//
///**
//  * Represents the RFC-1751 work format of master seeds,
//  *
//  * @param v
//  *   RFC-1751 form , e.g. "FOLD SAT ORGY PRO LAID FACT TWO UNIT MARY SHOD BID
//  *   BIND"
//  */
//case class RippleKey(v: RFC1751) extends RippleSignature
//
//object RippleKey {
//  implicit val decode: Decoder[RippleKey] = Decoder.decodeString.map(s => RippleKey(RFC1751(s)))
//  implicit val encode: Encoder[RippleKey] = Encoder.encodeString.contramap(_.v.v)
//}
//
///** Not used much now, as default KeyType is only non-experimental key */
//case class RippleKeyType(v: String) extends AnyVal
//
//object RippleKeyType {
//
//  val ED25519: RippleKeyType   = RippleKeyType("ed25519")
//  val SECP256K1: RippleKeyType = RippleKeyType("secp256k1")
//
//  implicit val decode: Decoder[RippleKeyType] = Decoder.decodeString.map(RippleKeyType(_))
//  implicit val encode: Encoder[RippleKeyType] = Encoder.encodeString.contramap(_.v)
//}
//
///** Always use Hex format of Ripple public key */
//case class SigningPublicKey(v: String)
//
//object SigningPublicKey {
//
//  def fromPublicKey(ripplePublicKey: RipplePublicKey): SigningPublicKey = {
//    val hex: String = XRPBase58.bytesFromBase58(ripplePublicKey.base58.v).toHex
//    SigningPublicKey(hex)
//  }
//
//  given Decoder[SigningPublicKey] = Decoder.decodeString.map(SigningPublicKey(_))
//  given Encoder[SigningPublicKey] = Encoder.encodeString.contramap[SigningPublicKey](_.v)
//
//}
//
///**
//  * @param validation_key
//  * @param validation_public_key
//  * @param validation_seed
//  */
//case class ValidationKeys(
//    validation_key: RippleKey,
//    validation_public_key: RipplePublicKey,
//    validation_seed: RippleSeed,
//) derives Codec.AsObject
//
///**
//  * Account Keys created by propose_wallet, removing redundant HEX but keeping
//  * masterKey and masterseed
//  *
//  * "result" : { "account_id" : "r99mP5QSjNdsEtng26uCnrieTZQe1wNYkf", "key_type"
//  * : "secp256k1", "master_key" : "MEND TIED IT NINA AVID SHE ROTH ANTE JUDO
//  * CHOU THE OWLY", "master_seed" : "shm5hC3ZoiWUy6GALxajhF5ddXqvC",
//  * "master_seed_hex" : "950314B38DAAC9D277F844627B6C3DBA", "public_key" :
//  * "aBQk4H5STdAr68s5nY371NRp4VfAwdoF3zsvh3CKLVezPQ64XyZJ", "public_key_hex" :
//  * "036F89F2B2E5DC47E4F72B7C33169F071E9F476DAD3D20EF39CA3778BC4508F102" }
//  *
//  * This just gets a subset of the keys -- forget why.
//  *
//  * @param account_id
//  *   The id of the account, always in account address format.
//  * @param key_type
//  * @param master_key
//  * @param master_seed
//  * @param public_key
//  */
//case class AccountKeys(
//    account_id: AccountAddr,
//    key_type: RippleKeyType,
//    master_key: RippleKey,
//    master_seed: RippleSeed,
//    public_key: RipplePublicKey,
//    public_key_hex: String,
//) derives Codec.AsObject {
//
//  def address: AccountAddr    = account_id
//  def secret: RippleSignature = master_seed
//
//}
//
///**
//  * Once a tx_json is signed there is a TxnSignature which is what this
//  * represents. Not used much so far. TODO: Opaque type
//  * @param v
//  */
//case class TxSignature(v: String) extends AnyVal
//
//object TxSignature {
//  given Decoder[TxSignature] = Decoder.decodeString.map(TxSignature(_))
//  given Encoder[TxSignature] = Encoder.encodeString.contramap[TxSignature](_.v)
//}
