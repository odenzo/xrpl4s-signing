package com.odenzo.xrpl.signing.core.secp256k1

import cats.*
import cats.data.*
import cats.implicits.*
import com.odenzo.xrpl.signing.common.binary.XrpBinaryOps
import com.odenzo.xrpl.signing.common.utils.MyLogging
import SecpOps.Constants.params
import cats.syntax.HashOps
import com.odenzo.xrpl.signing.core.models.{ XrpKeyPair, XrpPrivateKey, XrpPublicKey, XrpSeed }
import com.tersesystems.blindsight.LoggerFactory
import org.bouncycastle.crypto.params.{ ECDomainParameters, ECPublicKeyParameters }
import org.bouncycastle.math.ec.ECPoint
import scodec.bits.{ ByteVector, hex }

import java.math.BigInteger
import java.security.KeyPair
import scala.annotation.tailrec
import scala.collection.immutable

/**
  * For secp the basic idea if that a AccountFamilyGenerator has a public and
  * private key f(seed) => AccountFamilyGenerator (FGPK FamilyGeneratorKeyPair)
  * f(FGPK) =>
  *
  * This is only applicable to secp256k1 key types.
  */
object SecpKeyGenerators extends MyLogging  {
  private val log = LoggerFactory.getLogger
  import SecpOps.Constants

  /** The order of secp256k1 is the max value */
  protected val ZERO_KEY: ByteVector = Constants.zero32
  protected val MAX_KEY: BigInteger  = Constants.N_Order

//  /**
//    * Create a Generator from Master Seed, then create a Keypair from that for
//    * the account.
//    */
//  def rebuildAccountKeyPairFromSeed(seed: XrpSeed): KeyPair = {
//    val generator: AccountFamilyGenerator = createFamilyGenerator(seed)
//    val d                                 = generator.generateKeyPair
//    Secp256K1CryptoBC.dToKeyPair(d)
//
//  }

  /**
    * MasterKeyPair private, public. Private Key ever used directly or always
    * seed?
    */
  def createXrpKeyPair(seed: XrpSeed): XrpKeyPair = {
    val rootPrivateKey: ByteVector   = deriveRootPrivateKeyFromSeed(seed)
    val rootPublicKey                = deriveRootPublicKeyFromRootPrivateKey(rootPrivateKey)
    val intPrivateKey                = deriveIntermediatePrivateKey(rootPublicKey)
    val intPublicKey                 = deriveIntermediatePublicKey(intPrivateKey)
    val masterPrivateKey: ByteVector = deriveMasterPrivateKeyModuloStyle(rootPrivateKey, intPrivateKey)
    val masterPublicKeyB: ByteVector = deriveMasterPublicKeyFromMasterPrivateKey(masterPrivateKey)
    log.info(s"MasterPublicKeyB: $masterPublicKeyB")
    // assert(masterPublicKeyA.equals(masterPublicKeyB), "SECP MasterPublicKeys Didn't match")
    val privateKey                   = XrpPrivateKey.fromBytesUnsafe(masterPrivateKey)
    val publicKey                    = XrpPublicKey.fromBytesUnsafe(masterPublicKeyB)
    XrpKeyPair(publicKey, privateKey)
  }

  /**
    * Iteration to create a AccountFamilyGenerator and can make actual KeyPairs
    * for the seed.
    *
    * @param seed
    * @param rootKeyIndex
    *   4 byte unsigned counter, starts at zero and is incremented as a signed
    *   int, but its always >= 0. Not that I am not sure how big, but I use a
    *   long. I am never sure how far this actually iterates, can't image more
    *   than MAX_INT (signed)
    */

  @tailrec
  def generatorFromSeed(seed: ByteVector, keySequence: Long): ByteVector = {
    val keySequenceBytes: ByteVector    = ByteVector.fromLong(keySequence, 4)
    val generatedPrivateKey: ByteVector = sha512Half(seed ++ keySequenceBytes)

    if isValidPrivateKey(generatedPrivateKey)
    then generatedPrivateKey
    else generatorFromSeed(seed, keySequence + 1)

  }

  def deriveRootPrivateKeyFromSeed(seed: XrpSeed): ByteVector = {
    val paddedSeed           = seed.asRawSeed /// .padLeft(8) // seed is always 16, not sure why padding here
    val fullHash: ByteVector = generatorFromSeed(paddedSeed, 0L)
    log.debug(s"Full Hash/Seed: len ${fullHash.length} => ${fullHash.toHex}")
    fullHash.take(32)
  }

  /**
    * @return
    *   RootPublicKey derived from RootPrivateKey as 33 byte compressed format
    */
  def deriveRootPublicKeyFromRootPrivateKey(privateKey: ByteVector): ByteVector = {
    publicKeyFromPrivateKey(privateKey)
  }

  /**
    * Not understanding the Family Number. This used to be in terms of
    * AccountFamily and FamilyGenerator. Basically its the same recursive loop
    * incrementing Int(ermidiate) Key
    */
  def deriveIntermediatePrivateKey(
      rootPublicKey: ByteVector,
      familyName: ByteVector = ByteVector.low(4),
  ): ByteVector = {
    val seed                               = rootPublicKey ++ familyName
    val intermediatePrivateKey: ByteVector = generatorFromSeed(seed, 0L)
    intermediatePrivateKey
  }

  def deriveIntermediatePublicKey(intermediatePrivateKey: ByteVector): ByteVector = {
    publicKeyFromPrivateKey(intermediatePrivateKey)
  }

  /**
    * Takes root private key and intermediate private key, adding their unit
    * values Modulo Group Order to generate the 32 bytes MasaterPrivate Key.
    * @param rootPrivateKey
    * @param intermediatePrivateKey
    */
  def deriveMasterPrivateKeyModuloStyle(rootPrivateKey: ByteVector, intermediatePrivateKey: ByteVector): ByteVector = {
    // Both should be 32 bytes
    val root                   = XrpBinaryOps.unsignedBytesToBigInt(rootPrivateKey)
    val intermediate           = XrpBinaryOps.unsignedBytesToBigInt(intermediatePrivateKey)
    val MAX_KEY: BigInteger    = Constants.N_Order // Group Order
    val masterPrivateKeyBigInt = (root + intermediate) % MAX_KEY
    ByteVector(masterPrivateKeyBigInt.toByteArray)
  }

  def deriveMasterPublicKeyFromMasterPrivateKey(masterPrivateKey: ByteVector): ByteVector = {
    publicKeyFromPrivateKey(masterPrivateKey)
  }

  /**
    * This is used for the special AccountFamilyGenerator way of doing things.
    * Given a private key is created the corresponding public key.
    *
    * @param privateKey
    *   32 bytes that corresponds to D value (magnitude)
    * @param compress
    *   How the public key is encoded
    * @return
    *   Compressed bytes for the public key, this is from Bouncy Castle directly
    */
  inline def publicKeyFromPrivateKey(privateKey: ByteVector): ByteVector = {
    val compress                   = true
    val domain: ECDomainParameters = new ECDomainParameters(params.getCurve, params.getG, params.getN, params.getH)

    // This is a positive number, so 1, and then a bigendian binary array of the number
    val bd: BigInteger         = BigInt(1, privateKey.toArray).bigInteger
    val q: ECPoint             = domain.getG.multiply(bd)
    val publicParams           = new ECPublicKeyParameters(q, domain)
    val publicKey: Array[Byte] = publicParams.getQ.getEncoded(compress)
    ByteVector(publicKey)
  }

}
