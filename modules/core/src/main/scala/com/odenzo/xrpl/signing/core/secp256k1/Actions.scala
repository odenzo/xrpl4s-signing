package com.odenzo.xrpl.signing.core.secp256k1

import cats.*
import cats.data.*
import cats.implicits.*
import com.odenzo.xrpl.signing.common.utils.MyLogging
import Secp256k1Ops.privateKey2D
import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.params.{ ECDomainParameters, ECPrivateKeyParameters, ECPublicKeyParameters }
import org.bouncycastle.crypto.signers.{ ECDSASigner, HMacDSAKCalculator }
import org.bouncycastle.jcajce.provider.asymmetric.ec.{ BCECPrivateKey, BCECPublicKey }
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.{ ECNamedCurveParameterSpec, ECPrivateKeySpec, ECPublicKeySpec }
import org.bouncycastle.math.ec
import org.bouncycastle.math.ec.ECPoint
import scodec.bits.{ ByteVector, hex }

import java.math.BigInteger
import java.security.*

/**
  * This is focussed just on getting Secp256k1 txnscenarios and Verification
  * Working. TODO: Trim this down to used functions
  */
object Actions extends MyLogging {
  import Secp256k1Ops.Constants

  /**
    * Canonical Public Interface. For now it takes a KeyPair for internal
    * validation
    *
    * @param message
    *   This is the Hash value to sign, for Ripple the (HashPrefix ++
    *   SerializedForSigning(tx_json)) applied to SHA512Half (I am pretty sure)
    * @param sig
    *   Signature for XXX
    * @pubKey
    *   Public key of the creator of the Signature of the message. We verify
    *   message was signed by corresponding private key.
    */
  def verify(message: ByteVector, sig: DER.Signature, pubKey: PublicKey): Boolean = {
    {
      pubKey match {
        case bcecKey: BCECPublicKey =>
          val signer                        = new ECDSASigner
          val pubPoint                      = bcecKey.getQ
          val params: ECPublicKeyParameters = new ECPublicKeyParameters(pubPoint, Constants.domainParams)
          signer.init(false, params)
          signer.verifySignature(message.toArray, sig.r.asBigInteger, sig.s.asBigInteger)

        case other => throw IllegalArgumentException(s"Illegal Public Key Type: ${other.getClass}")
      }
    }
  }

  /**
    * Given a hash (of txn normally) and Public/Private Keypair create a DER
    * Signature using SHA256Digest with ECDSA Signer. Secp only?
    * @param hash
    *   The has of the txn to sign
    * @param secret
    *   The public/private keypair
    */
  def sign(hash: ByteVector, secret: KeyPair) = {

    val kCalc: HMacDSAKCalculator = new HMacDSAKCalculator(new SHA256Digest)
    val signer                    = new ECDSASigner(kCalc)
    val d                         = privateKey2D(secret.getPrivate)
    val privKeyParam              = new ECPrivateKeyParameters(d, Constants.domainParams)
    signer.init(true, privKeyParam)

    val sigs               = signer.generateSignature(hash.toArray)
    val r: BigInteger      = sigs(0)
    val s: BigInteger      = sigs(1)
    val otherS: BigInteger = Constants.N_Order.subtract(s)
    val finalS             = if (s.compareTo(otherS) === 1) otherS else s // s > otherS
    DER.Signature.packFromRandS(r, finalS)

  }

}
