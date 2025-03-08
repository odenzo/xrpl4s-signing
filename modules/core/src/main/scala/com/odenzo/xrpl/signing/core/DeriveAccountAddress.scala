package com.odenzo.xrpl.signing.core

import com.odenzo.xrpl.signing.common.binary.XrpBinaryOps
import com.odenzo.xrpl.signing.core.models.{ AccountAddress, AccountPublicKey }
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
    *   secp265k or ed25519 public keys, 32 or 33 bytes. Handled Ed25519 and
    *   secpk1 keys, if ed25519 padded with 0xED
    * @return
    *   Ripple Account Address Base58 encoded with leading r and checksummed.
    */
  def accountPublicKey2address(publicKey: AccountPublicKey): IO[AccountAddress] = {
    import AccountPublicKey.* // Needed to get the extensions unfortunaltey, and its not named.
    val publicKeyBytes        = publicKey.coreBytes
    assert(publicKeyBytes.size == 33L, "Public Key must be 33 bytes")
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
