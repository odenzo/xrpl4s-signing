package com.odenzo.xrpl.signing.core.ed25519

import com.odenzo.xrpl.signing.common.binary.{ HashOps, XrpBinaryOps }
import com.odenzo.xrpl.signing.core.models.{ AccountPublicKey, XrpSeed }
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

/**
  * Alot of redundancy here. What to we really need to do, derive a
  * KeyPair/Wallet from Seed or Private Key. And we should be able to generate a
  * random seed i guess for WalletPropose functionlity.
  */
object KeyGenerators {

  /**
    * Generates a totally random keypair for ED25519 I guess we could down-cast
    * to ED speficic public and private keys
    *
    * Alot of redundancy here. What to we really need to do, derive a
    * KeyPair/Wallet from Seed or Private Key
    */
  def createRandomKeyPair(): AsymmetricCipherKeyPair = {
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
    * @return
    *   Bouncv Castle public and private key pair.
    */
  def createKeyPairFromXrpSeed(seed: XrpSeed): (Ed25519PublicKeyParameters, Ed25519PrivateKeyParameters) = {
    val privateKey                                = derivePrivateKeyFromSeed(seed)
    val privateKeyBC: Ed25519PrivateKeyParameters = privateKeyToBC(privateKey)
    val publicKeyBC: Ed25519PublicKeyParameters   = privateKeyBC.generatePublicKey()
    (publicKeyBC, privateKeyBC)
  }

  /** Given a 16 byte seed transform to 32 byte private key as bytevector */
  inline def derivePrivateKeyFromSeed(seed: XrpSeed): ByteVector = XrpBinaryOps.sha512Half(seed.asRawSeed)

  inline def privateKeyToBC(privateKey: ByteVector): Ed25519PrivateKeyParameters =
    new Ed25519PrivateKeyParameters(privateKey.toArray, 0)

  inline def derivePublicKeyFromPrivateKeyBC(privateKeyBC: Ed25519PrivateKeyParameters): Ed25519PublicKeyParameters =
    privateKeyBC.generatePublicKey()

    /** @return  32 byte public key without the 0xED in front. */

  inline def convertBcPublicKeyToModel(publicKey: Ed25519PublicKeyParameters): AccountPublicKey =
    AccountPublicKey.fromBytesUnsafe(ByteVector(publicKey.getEncoded))

  inline def convertBcPrivateKeyToModel(privateKeyBC: Ed25519PrivateKeyParameters): ByteVector =
    ByteVector.apply(privateKeyBC.getEncoded)

}

// TODO: Where is the create account address for ED, or is it the same as for sepk

/**
  * Given a full public key started with ed in hex then drop the ED header bytes
  * and convert to BouncyCastle's ED25519 Public Key Parameters. The ED header
  * bytes are XRPL specific.
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
