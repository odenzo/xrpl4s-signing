package com.odenzo.xrpl.signing.core.secp256k1

import Secp256k1Ops.Constants
import org.bouncycastle.jce.spec.{ ECPrivateKeySpec, ECPublicKeySpec }
import org.bouncycastle.math.ec.ECPoint

import java.math.BigInteger
import java.security.{ KeyFactory, KeyPair, PrivateKey, PublicKey }

object MiscOps {

  /**
    * This is used now because AccountFamilyGenerator creates d value.
    *
    * @param d
    *   The SECP356k ECDSA Key as BigInteger It is the random value of private
    *   key really.
    *
    * @return
    *   d converted to public and private keypair. Make compressed public key.
    */
  def dToKeyPair(d: BigInteger): KeyPair = {
    val eckf: KeyFactory = KeyFactory.getInstance("EC", "BC")

    val privateKeySpec: ECPrivateKeySpec = new ECPrivateKeySpec(d, Constants.ecSpec)
    val exPrivateKey: PrivateKey         = eckf.generatePrivate(privateKeySpec)

    val q: ECPoint                     = Constants.domainParams.getG.multiply(d)
    val publicKeySpec: ECPublicKeySpec = new ECPublicKeySpec(q, Constants.ecSpec)
    val exPublicKey: PublicKey         = eckf.generatePublic(publicKeySpec)

    new KeyPair(exPublicKey, exPrivateKey)
  }

  //  /**
  //    * Takes a JCA Public Key and compresses it in XRPL format and returns as
  //    * Hex. TODO: Remove
  //    * @param pubKey
  //    *   Java JCA Public Key, many kinds
  //    * @return
  //    */
  //  private def publicKeyCompressed(pub: PublicKey): ByteVector = ByteVector(compressPublicKey(pub).toArray)

}
