package com.odenzo.xrpl.signing.core.models

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.syntax.all.*
import cats.syntax.all.*
import com.odenzo.xrpl.signing.core.models.XrpPublicKey.Codecs.given
import com.tersesystems.blindsight.LoggerFactory
import io.circe.{ Codec, Decoder, Json }
import io.scalaland.chimney.dsl.*
import io.scalaland.chimney.internal.runtime.{ TransformerFlags, TransformerOverrides }
import io.scalaland.chimney.partial.Result
import io.scalaland.chimney.{ PartialTransformer, Transformer }
import scodec.bits.hex

/**
  * A Wallet holds credentials and addresses for an account. It can be used to
  * sign txn etc. Normally a wallet is (initially) populated from a
  * WalletPropse.Rs but this is a simplified version.
  * @param accountAddress
  *   In XRPL Base-58 form
  * @param keyType
  *   secp256k1 or ed25519
  */
case class Wallet(
    accountAddress: String,
    keyType: KeyType,
    masterSeed: XrpSeed,
    publicKey: XrpPublicKey,
) derives Codec.AsObject

object Wallet {
  val GENESIS_MASTER_PASSPHRASE: String = "masterpassphrase"

  private val log = LoggerFactory.getLogger

}
