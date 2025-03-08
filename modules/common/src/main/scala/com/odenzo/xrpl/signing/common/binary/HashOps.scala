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
 
}

object HashOps extends HashOps
