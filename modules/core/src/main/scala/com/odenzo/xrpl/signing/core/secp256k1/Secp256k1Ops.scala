package com.odenzo.xrpl.signing.core.secp256k1

import com.odenzo.xrpl.signing.common.binary.XrpBinaryOps
import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import org.bouncycastle.math.ec.ECPoint
import scodec.bits.{ ByteVector, hex }

import java.math.BigInteger
import java.security.spec.ECPublicKeySpec
import java.security.*

trait Secp256k1Ops extends XrpBinaryOps {
  import Secp256k1Ops.Constants

  /** Assumes key is 32 bytes. Return true is all zero bits. */
  def isKeyAllZeroes(key: ByteVector): Boolean = key == Constants.zero32

  /** Ensure Private Key is strictly less than groupOrder */
  def isKeyLessThanGroupOrder(key: ByteVector): Boolean = {
    key < Constants.groupOrder // From Ordered[ByteVector]
  }

  def isValidPrivateKey(key: ByteVector): Boolean = isKeyLessThanGroupOrder(key) && !isKeyAllZeroes(key)

  /** @return Generates a JCA KeyPair for secp256k1 in its own packaging */
  def generateNewKeyPair(): KeyPair = {
    import Secp256k1Ops.Constants
    val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(Constants.keyType, Constants.providerName)
    kpg.initialize(Constants.ecSpec, new SecureRandom())
    val pair: KeyPair         = kpg.generateKeyPair
    pair
  }

  /**
    * Extract D value from the private key if it is a BCECPrivateKey else throw
    * error.
    *
    * @param priv
    *   instance of BCECPrivateKey
    * @return
    *   The D value of the private key
    */
  def privateKey2D(priv: PrivateKey): BigInteger = {
    priv match {
      case k: BCECPrivateKey => k.getD
      case other             => throw IllegalArgumentException(s"PrivateKey ${other.getClass} was not a BCECPrivateKey")
    }

  }

  /**
    * Public Keys with X only are compressed with added 0x02 or 0x03 as first
    * byte and 32 byte X Uncompressed Public Keys have 0x04 and 32 byte X and 32
    * byte Y
    *
    * @param compressedKey
    *   The public key, how these bytes are am not sure. uncompressed with 02 or
    *   03?
    * @return
    */
//  def decompressPublicKey(compressedKey: ByteVector): PublicKey = {
//    val point: ec.ECPoint              = Constants.ecSpec.getCurve.decodePoint(compressedKey.toArray)
//    val Q                              = point
//    val eckf: KeyFactory               = KeyFactory.getInstance("EC", Constants.providerName)
//    val publicKeySpec: ECPublicKeySpec = new ECPublicKeySpec(Q, Constants.ecSpec)
//    val exPublicKey: PublicKey         = eckf.generatePublic(publicKeySpec)
//    exPublicKey
//  }

  /**
    * @return
    *   Gives the raw bytes of the public key, which are preceeded by 02 or 03
    *   in encoded form. This is for sepck key only now.
    */
  def compressPublicKey(pub: PublicKey): ByteVector = {
    pub match {
      case p: ECPublicKey => ByteVector(p.getQ.getEncoded(true))
    }
  }
}

object Secp256k1Ops extends Secp256k1Ops {
  object Constants {
    val groupOrder                        = hex"FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141"
    val zero32: ByteVector                = ByteVector.low(32)
    val providerName                      = "BC"
    val curveName                         = "secp256k1"
    val keyType                           = "ECDSA"
    val params: X9ECParameters            = SECNamedCurves.getByName(curveName)
    val ecSpec: ECNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec(curveName)

    /** The parameters for secp256k1 curve */
    val domainParams: ECDomainParameters =
      new ECDomainParameters(params.getCurve, params.getG, params.getN, params.getH)

    /**
      * G is generator for secp256k1 curve.
      *
      * @return
      */
    val G_Generator: ECPoint = params.getG

    // TODO: Facade  org.bouncycastle.util.BigIntegers as needed

    /**
      * N is the order of the curve. Since we only use one curve here, it is a
      * constant
      */
    val N_Order: BigInteger       = params.getN
    val N_Order_Bytes: ByteVector = hex"FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141"
    val N_Order_Fixed             = BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16)

  }
}
