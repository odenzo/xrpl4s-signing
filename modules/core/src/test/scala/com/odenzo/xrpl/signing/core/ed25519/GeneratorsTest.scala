package com.odenzo.xrpl.signing.crypto.ed25519

import com.odenzo.xrpl.signing.core.ed25519.Generators
import com.odenzo.xrpl.signing.core.models.XrpSeed
import com.tersesystems.blindsight.LoggerFactory
import io.circe.Json
import io.circe.literal.json
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.params.AsymmetricKeyParameter
import scodec.bits.{ ByteVector, hex }

class GeneratorsTest extends munit.FunSuite {
  private val log = LoggerFactory.getLogger
//
//  val newKeyPar: AsymmetricCipherKeyPair = Generators.createRandomKeypairkeyPairFromBouncyCastleRandom()
//
//  test("Generate KeyPair") {
//    val masterSeed                 = XrpSeed.fromBytesUnsafe(seedHex)
//    val derivedPrivate: ByteVector = Generators.derivePrivateKeyFromSeed(masterSeed)
//    val derivedPublic: ByteVector  = Generators.derivePublicKeyFromPrivateKey(derivedPrivate)
//    log.info(s"Derived Public Key: $derivedPublic")
//    log.info(s"Derived Public Key: $pubKeyHex")
//    assert(derivedPublic.equals(pubKeyHex))
//  }
}
