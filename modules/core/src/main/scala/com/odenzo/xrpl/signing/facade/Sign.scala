package com.odenzo.xrpl.signing.facade





//package com.odenzo.ripple.signing.impl
//
//import cats.syntax.all.*
//import com.odenzo.ripple.signing.impl.crypto.AccountFamily
//import com.odenzo.ripple.signing.impl.crypto.core.{ED25519CryptoBC, KeyType, TxnSignature}
//import com.odenzo.ripple.signing.models.{SigningKey, SigningKeyEd25519}
//import com.odenzo.xrpl.common.keys.KeyType
//import com.odenzo.xrpl.common.utils.ELogging
//import io.circe.Json
//import scodec.bits.ByteVector
//
///** This takes a message and signs it. Returning the TxBlob */
//object Sign extends ELogging {
//
//  /** ToDo: Nice to put Address in this for use in MultiSigning future APIs */
//  def preCalcKeys(seed: ByteVector, keyType: KeyType) = {
//    keyType match {
//      case KeyType.ed25519   =>
//        for {
//          keys <- ED25519CryptoBC.generateKeyPairFromBytes(seed)
//          spub <- ED25519CryptoBC.publicKey2Hex(keys.getPublic)
//        } yield SigningKeyEd25519(keys, spub)
//      case KeyType.secp256k1 =>
//        for {
//          keys <- AccountFamily.rebuildAccountKeyPairFromSeedHex(seedhex)
//          spub  = Secp256K1CryptoBC.publicKey2hex(keys.getPublic)
//        } yield SigningKeySecp256(keys, spub)
//    }
//  }
//
//  /**
//    * This does the binary serialize for signing (only isSigning fields), adds
//    * TransactionSig Prefix then signs.
//    *
//    * @param tx_json
//    *   Filled tx_json, including SingingPubKey
//    *
//    * @return
//    *   TxnSignature
//    */
//  def signToTxnSignature(tx_json: ByteVector, key: SigningKey): Either[LocalOpsError, TxnSignature] = {
//
//    for {
//      encoded <- BinCodecProxy.binarySerializeForSigning(tx_json)
//      binBytes = encoded.toBytes
//      payload  = HashPrefix.transactionSig.asByteArray ++ binBytes
//      ans     <- signPayload(payload, key)
//    } yield ans
//
//  }
//
//  def signPayload(payload: Array[Byte], key: SigningKey): Either[LocalOpsError, TxnSignature] = {
//
//    val hex: Either[LocalOpsError, String] = key match {
//      case edKey: SigningKeyEd25519   => ED25519CryptoBC.sign(payload, edKey.kp).map(bytes2hex)
//      case secpKey: SigningKeySecp256 =>
//        val hashed = HashOps.sha512Half(payload)
//        Secp256K1CryptoBC.sign(hashed.toArray, secpKey.kp).map(b => b.toHex)
//    }
//    hex.map(TxnSignature)
//  }
//
//  /**
//    * This doesn't actually sign, just generates the TxBlob given TxnSignature.
//    * The SigningPubKey and everything is in the tx_json already.
//    *
//    * @param tx_json
//    *   Filled tx_json, including SingingPubKey
//    * @param txnSignature
//    *   In Hex format, Empty String when multisigning.
//    *
//    * @return
//    *   Updated tx_blob in hex form for use in Submit call.
//    */
//  def createSignedTxBlob(tx_json: Json, txnSignature: TxnSignature): Either[LocalOpsError, Array[Byte]] = {
//    // Could add the HashPrefix. and get the hash if needed, e.g. to recreate SignRs message
//    val withSig = tx_json.mapObject(_.add("TxnSignature", Json.fromString(txnSignature.hex)))
//    BinCodecProxy.serialize(withSig)
//  }
//
//  /**
//    * Has this been thoroughly tested? Should be 32 bytes TODO: Broken! And not
//    * always used as part of calc hash logic
//    *
//    * @param txblob
//    *   Is this a SigningTxBlob or all TxBlob
//    *
//    * @return
//    *   Calculates a response objects Hash (in hex) from tx_blob
//    */
//  def createResponseHashHex(txblob: Array[Byte]): String = {
//    // return new Hash256(sha512Half(HashPrefix.transactionID, serialized));
//    val payload: Seq[Byte]          = HashPrefix.transactionID.asBytes ++ txblob
//    val hashBytes: IndexedSeq[Byte] = HashOps.sha512Half(payload.toArray)
//    val hex                         = bytes2hex(hashBytes)
//    hex
//  }
//
//}
