package com.odenzo.xrpl.signing.core.ed25519

import cats.syntax.HashOps
import com.odenzo.xrpl.signing.common.binary.XrpBinaryOps
import com.odenzo.xrpl.signing.core.models.*
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
object Ed25519KeyGenerators {
  val secureRandom: SecureRandom = new SecureRandom()

  /**
    * Generates a totally random keypair for ED25519 I guess we could down-cast
    * to ED speficic public and private keys
    *
    * Alot of redundancy here. What to we really need to do, derive a
    * KeyPair/Wallet from Seed or Private Key
    *
    * ANd want to have a passphrase too, which kind of restricts that.
    */
  def createRandomKeyPair(): AsymmetricCipherKeyPair = {
    val keygen: Ed25519KeyPairGenerator = new Ed25519KeyPairGenerator()
    keygen.init(new Ed25519KeyGenerationParameters(secureRandom))
    val keyPair                         = keygen.generateKeyPair()
    keyPair
  }

  def createXrpKeyPair(xrpSeed: XrpSeed): XrpKeyPair =
    val (bcPublic, bcPrivate)       = createBcKeyPairFromXrpSeed(xrpSeed)
    val publicModel: XrpPublicKey   = convertBcPublicKeyToModel(bcPublic)
    val privateModel: XrpPrivateKey = convertBcPrivateKeyToModel(bcPrivate)
    XrpKeyPair(publicModel, privateModel)

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
  def createBcKeyPairFromXrpSeed(seed: XrpSeed): (Ed25519PublicKeyParameters, Ed25519PrivateKeyParameters) = {
    val rawSeed: ByteVector                       = XrpSeed.unwrap(seed)
    val hex                                       = rawSeed.toHex
    println(s"SeedHex ${hex.size / 2} Bytes: $hex")
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

    /**
      * @return
      *   XrpPublicKey from the raw crpto (e.g. handles left padding 0xED
      */
  inline def convertBcPublicKeyToModel(publicKey: Ed25519PublicKeyParameters): XrpPublicKey                         =
    XrpPublicKey.fromBytesUnsafe(ByteVector(publicKey.getEncoded))

  inline def convertBcPrivateKeyToModel(privateKeyBC: Ed25519PrivateKeyParameters): XrpPrivateKey =
    XrpPrivateKey.fromBytesUnsafe(ByteVector(privateKeyBC.getEncoded))

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
