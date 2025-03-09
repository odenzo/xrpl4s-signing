package com.odenzo.xrpl.signing.core

import com.odenzo.xrpl.signing.common.binary.XrpBinaryOps
import com.odenzo.xrpl.signing.core.models.{ AccountAddress, XrpPublicKey }
import scodec.bits.ByteVector
import cats.effect.*
import cats.effect.syntax.all.*
import cats.*
import cats.data.*
import cats.syntax.all.*
import com.tersesystems.blindsight.LoggerFactory

object DeriveAccountAddress extends XrpBinaryOps {

  private val log = LoggerFactory.getLogger

  /**
    * Given as a Public Key adds prefix and XRP checksum.
    *
    * @param publicKey
    *   secp265k or ed25519 public keys, and operates on 33 bytes always, this
    *   includes the 0xED marker for Ed25519
    * @return
    *   Ripple Account Address Base58 encoded with leading r and checksum.
    */
  def accountPublicKey2address(publicKey: XrpPublicKey): IO[AccountAddress] = {
    import XrpPublicKey.*
    val publicKeyBytes        = publicKey.asRawKey
    val accountId: ByteVector = ripemd160(sha256(publicKeyBytes))
    val body: ByteVector      = accountPrefix ++ accountId
    val bytes: ByteVector     = body ++ xrpChecksum(body)
    IO.fromEither(
      AccountAddress
        .fromRawBytes(bytes)
        .leftMap((v: String) => IllegalArgumentException(s"Bad Bytes for AccountAddress"))
    )
  }

}
