package com.odenzo.xrpl.signing.crypto.ed25519

import com.odenzo.xrpl.signing.common.binary.{ HashOps, XrpBinaryOps }
import com.odenzo.xrpl.signing.models.{ AccountPublicKey, XrpSeed }
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.{
  AsymmetricKeyParameter,
  Ed25519KeyGenerationParameters,
  Ed25519PrivateKeyParameters,
  Ed25519PublicKeyParameters,
}
import scodec.bits.{ ByteVector, hex }

import java.security.SecureRandom

object Generators {

  /**
    * Generates a totally random keypair fir ED25519 I guess we could down-cast
    * to ED spefici public and private keys
    */
  def keyPairFromBouncyCastleRandom(): AsymmetricCipherKeyPair = {
    val RANDOM: SecureRandom            = new SecureRandom()
    val keygen: Ed25519KeyPairGenerator = new Ed25519KeyPairGenerator()
    keygen.init(new Ed25519KeyGenerationParameters(RANDOM))
    val keyPair                         = keygen.generateKeyPair()
    keyPair
  }

  /**
    * Then generates the public key directly from PrivateKey, no AccountFamily
    * mumbo jumbo. SHA512 so the length is priv is somewhat arbitrary, usually
    * 128-bits
    *
    * @param priv
    *   Array is needed to pass into Hash function, not modified
    */
  def keyPairFromBouncyCastlePrivateKeyBytes(seed: XrpSeed): AsymmetricCipherKeyPair = {
    val rawPrivateKey: ByteVector               = HashOps.sha512Half(seed.asRawSeed)
    val privateKey: Ed25519PrivateKeyParameters = new Ed25519PrivateKeyParameters(rawPrivateKey.toArray, 0)
    val publicKey: Ed25519PublicKeyParameters   = privateKey.generatePublicKey()
    new AsymmetricCipherKeyPair(publicKey, privateKey)
  }

  /** Given a 16 byte seed transform to 32 byte private key as bytevector */
  def derivePrivateKeyFromSeed(seed: XrpSeed): ByteVector =
    XrpBinaryOps.sha512Half(seed.asRawSeed)

  /** Returns  32 byte public key without the 0xED in front. */
  def derivePublicKeyFromPrivateKey(rawPrivateKey: ByteVector): ByteVector = {
    val privateKey: Ed25519PrivateKeyParameters = new Ed25519PrivateKeyParameters(rawPrivateKey.toArray, 0)
    val publicKey: Ed25519PublicKeyParameters   = privateKey.generatePublicKey()
    ByteVector(publicKey.getEncoded)
  }

  def publicKeyToAccountPublicKey(bv: ByteVector): AccountPublicKey = {
    AccountPublicKey.fromBytesUnsafe(bv)
  }
}

/**
  * @param publicKeyParams
  *   This must be the public key, to save external casting take more generic
  *   type Ed25519PublicKeyParameters < AsymmetricKeyParameter Lets see if we
  *   can make invariant and use Ed...KeyParameters
  * @return
  *   THe public key, 33 bytes with ED prefix like Ripple does it
  */
def publicKey2Bytes(publicKeyParams: AsymmetricKeyParameter): ByteVector = {
  val key = publicKeyParams.asInstanceOf[Ed25519PublicKeyParameters]
  if key.isPrivate then throw IllegalArgumentException("JCA Expected PublicKey but was Private")
  else hex"ED" ++ ByteVector(key.getEncoded)

}

/**
  * Given a full public key started with ed in hex then drop the ED header bytes
  * and convert to BouncyCastle's ED25519 Public Key Parameters.
  *
  * @param edPublicKey
  *   ED25519 Public Key *with* the 0xED header. Will take without too. 33 or 32
  *   bytes
  */
def signingPubKey2KeyParameter(edPublicKey: ByteVector): Ed25519PublicKeyParameters = {
  val withoutHeader = if edPublicKey.head == 0xed then edPublicKey.drop(1) else edPublicKey
  assert(withoutHeader.size == 32)
  new Ed25519PublicKeyParameters(edPublicKey.toArray, 0)
}
