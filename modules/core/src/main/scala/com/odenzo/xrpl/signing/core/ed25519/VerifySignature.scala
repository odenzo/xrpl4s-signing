package com.odenzo.xrpl.signing.core.ed25519

import cats.*
import cats.data.*
import com.odenzo.xrpl.signing.common.binary.{ BouncyCastleOps, XrpBinaryOps }
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.ec.CustomNamedCurves
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.*
import org.bouncycastle.crypto.signers.Ed25519Signer
import org.bouncycastle.jce.provider.BouncyCastleProvider
import scodec.bits.{ ByteVector, hex }

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.{ Provider, SecureRandom }

/**
 * There is no account family for ed25519 This is all a bit messy when I
 * combine with our object representations and the actual BouncyCastle stuff.
 * Need to trim down to actual use cases
 * https://tools.ietf.org/html/draft-josefsson-eddsa-ed25519-03#section-5.2
 * https://xrpl.org/docs/concepts/accounts/cryptographic-keys#key-derivation 16
 * byte seed ->
 */
object VerifySignature extends BouncyCastleOps with XrpBinaryOps {

  /**
   * Signed a signed message (or whatever) and a public key, verify valid
   * signature and trust it. 64 byte signatures are compressed versions, 64
   * bytes are output
   *
   * @param payload
   * Bytes to verify that signature matches
   * @param txnSignature
   * The TxnSignature as bytes in Ripple context, big-endian hex more or
   * less.
   * @param pubKey
   * The SigningPubKey in native Ed25519 Format. This is the public key
   * stored in the Wallet.
   * @return
   */
  def verify(
              payload: ByteVector,
              txnSignature: ByteVector,
              pubKey: Ed25519PublicKeyParameters,
            ): Boolean = {
    assert(payload.size <= Int.MaxValue)
    val edSigner: Ed25519Signer = new Ed25519Signer()
    edSigner.init(false, pubKey)
    edSigner.update(payload.toArray, 0, payload.length.toInt)
    edSigner.verifySignature(txnSignature.toArray)

  }

}
