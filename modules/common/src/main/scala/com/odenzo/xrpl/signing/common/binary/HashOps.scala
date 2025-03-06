package com.odenzo.xrpl.signing.common.binary

import java.security.MessageDigest

import scodec.bits.ByteVector

/**
  * Collection of Hashing Operations. Inputs need to go Java byte[], but outputs
  * are wrapped to IndexedSeq instead of Array[Byte] to get immutable ds. May
  * switch to immutable.ArraySeq.unsafeWrapArray to save the array copy since no
  * one else has a handle on the returned bytes from the digester. Note: Re
  * above, we are using ByteVector but still need to deal with the fact that the
  * Hashing op doesn't return an immutable array. We release the array into a
  * ByteVector that should be immutable
  */
trait HashOps {
  //  Security.addProvider(new BouncyCastleProvider)
  def xrpChecksum(body: ByteVector): ByteVector =
    sha256(sha256(body)).take(4)

  /**
    * This is equivalent to Ripple SHA512Half, it does SHA512 and returns first
    * 32 bytes
    */
  def sha512Half(bytes: ByteVector): ByteVector = sha512(bytes).take(32)

  /**
    * Default Java SHA256, should be the same as BouncyCastle sha512BC function.
    *
    * @param bytes
    *
    * @return
    *   64-byte SHA512 hash with no salt added.
    */
  def sha256(bytes: ByteVector): ByteVector = {
    val array: Array[Byte] = MessageDigest.getInstance("SHA-256").digest(bytes.toArray)
    ByteVector(array)
  }

  /**
    * Default Java SHA512, should be the same as BouncyCastle sha512BC function.
    *
    * @param bytes
    *
    * @return
    *   64-byte SHA512 hash with no salt added.
    */
  def sha512(bytes: ByteVector): ByteVector = {
    // toIndexedSeq will do a copy, enforcing immutable
    // unsafeArray  on  ArraySeq will not copy, and give the underlying mutable array
    // toArray also does a copy since Seq is immutable
    val array = MessageDigest.getInstance("SHA-512").digest(bytes.toArray)
    ByteVector(array)
  }

  /**
    * RipeMD160 digest/hash. Primarily used to convert PublicKey to AccountId
    *
    * @param bytes
    *   Is this supposed to be the Hash of original bytes or raw bytes?
    *
    * @return
    */
  def ripemd160(bytes: ByteVector): ByteVector = {
    val md = MessageDigest.getInstance("RIPEMD160")
    ByteVector(md.digest(bytes.toArray))
  }

  private val sha256Digest: MessageDigest = MessageDigest.getInstance("SHA-256")
}

object HashOps extends HashOps
