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
object SignTransaction extends BouncyCastleOps with XrpBinaryOps {

  private val curve: X9ECParameters = CustomNamedCurves.getByName("curve25519")
  private val order: BigInteger     = curve.getCurve.getOrder

  //  private val domainParams: ECDomainParameters =
  //    new ECDomainParameters(curve.getCurve, curve.getG, curve.getN, curve.getH)

  /** Generate signature using Bouncy Castle Directly using ED25519 Private Key */
  def sign(payload: ByteVector, edPrivateKey: Ed25519PrivateKeyParameters): ByteVector = {
    assert(payload.size <= Int.MaxValue)
    val edSigner: Ed25519Signer = new Ed25519Signer()
    edSigner.init(true, edPrivateKey)
    edSigner.update(payload.toArray, 0, payload.size.toInt)
    val signature: Array[Byte]  = edSigner.generateSignature()
    ByteVector(signature)
  }

}
