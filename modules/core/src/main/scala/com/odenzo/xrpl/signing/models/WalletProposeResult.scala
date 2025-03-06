package com.odenzo.xrpl.signing.models

//package com.odenzo.ripple.signing.models
//
//import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
//import io.circe.{ Decoder, Encoder }
//import io.circe.*
//
///**
//  * When proposing a new wallet this returns all the values in text form.
//  * TRypicall XRPL Base58 or Hex
//  * @param account_id
//  * @param key_type
//  *   KeyType enum as String
//  * @param master_key
//  *   Explain master key vs master_seed
//  * @param master_seed
//  * @param master_seed_hex
//  * @param public_key
//  * @param public_key_hex
//  */
//private[signing] class WalletProposeResult(
//    account_id: String,
//    key_type: String,
//    master_key: String,
//    master_seed: String,
//    master_seed_hex: String,
//    public_key: String,
//    public_key_hex: String,
//) derives Codec.AsObject
