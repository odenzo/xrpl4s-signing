package com.odenzo.xrpl.signing.common.binary

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.BigIntegers
import scodec.bits.ByteVector

import java.security.{ MessageDigest, Provider, Security }

trait BouncyCastleOps {

  Security.addProvider(new BouncyCastleProvider)
  val provider: Provider = Security.getProvider("BC")

//  /**
//    * RipeMD160 digest/hash. Note that XRPL does a SHA256 of original payloaed
//    * first
//    *
//    * @param bytes
//    * @return
//    */
//  def ripemd160(bytes: ByteVector): ByteVector = {
//    val md = MessageDigest.getInstance("RIPEMD160")
//    ByteVector(md.digest(bytes.toArray))
//  }

  /**
    * Given an unsigned (?) integer in as bytes convert to a BigInt for use in
    * Bouncy Castle/JCA. I am unsure exactly what it is excepting in terms of
    * bits, typically we have 32 or 33 bytes so its a large number. Way to bit
    * to fit in a long.
    */
  inline def convertByteVectorToBigInt(bv: ByteVector): BigInt = {
    BigIntegers.fromUnsignedByteArray(bv.toArray)
  }

  /**
    * Currently this does 2-complement to decimal represesentation but it DOES
    * NOT pad the bytes out to any fixed length
    */
  inline def convertBigIntToUnsignedByteVector(v: BigInt): ByteVector = {
    val bytes: Array[Byte] = BigIntegers.asUnsignedByteArray(v.bigInteger) // Can pass in a len to get zero left padding
    ByteVector(bytes)
  }

}
