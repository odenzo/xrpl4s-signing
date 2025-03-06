package com.odenzo.xrpl.signing.crypto.ed25519

import com.odenzo.xrpl.signing.models.XrpSeed
import com.tersesystems.blindsight.LoggerFactory
import io.circe.Json
import io.circe.literal.json
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.params.AsymmetricKeyParameter
import scodec.bits.{ByteVector, hex}

class GeneratorsTest extends munit.FunSuite {
  private val log          = LoggerFactory.getLogger
  private val wallet: Json =
    json"""
    {
              "account_id": "rn4gsh2qp8842mTA5HfwGT3L1XepQCpqiu",
              "key_type": "ed25519",
              "master_key": "ANTE TUFT MEG CHEN CRAB DUMB COW OWNS ROOF FRED EDDY FORD",
              "master_seed": "spqnjaMMxPSvtaD4nevqqdjj4kzie",
              "master_seed_hex": "09A117434757F90BF0BED6B29F185E4D",
              "public_key": "aKGGHoqb2C2Xj6qtzikTTdsQdPcnYS8ue4XzXvT2T6fuofFP4zrA",
              "public_key_hex": "EDC5349AD8114DCDA07A355AA850FABE710CEE8FCBD891F1B919A6F6713C7BABA0"
            }
    """

  val secretkey  = "ANTE TUFT MEG CHEN CRAB DUMB COW OWNS ROOF FRED EDDY FORD"
  val seedB58    = "spqnjaMMxPSvtaD4nevqqdjj4kzie"
  val seedHex    = hex"09A117434757F90BF0BED6B29F185E4D"
  val signPubKey = "EDC5349AD8114DCDA07A355AA850FABE710CEE8FCBD891F1B919A6F6713C7BABA0"
  val sender     = "rn4gsh2qp8842mTA5HfwGT3L1XepQCpqiu"
  val pubKeyHex  = hex"C5349AD8114DCDA07A355AA850FABE710CEE8FCBD891F1B919A6F6713C7BABA0" // ED removed

  val newKeyPar: AsymmetricCipherKeyPair = Generators.keyPairFromBouncyCastleRandom()

  test("Generate KeyPair") {
    val masterSeed                 = XrpSeed.fromBytesUnsafe(seedHex)
    val derivedPrivate: ByteVector = Generators.derivePrivateKeyFromSeed(masterSeed)
    val derivedPublic: ByteVector  = Generators.derivePublicKeyFromPrivateKey(derivedPrivate)
    log.info(s"Derived Public Key: $derivedPublic")
    log.info(s"Derived Public Key: $pubKeyHex")
    assert(derivedPublic.equals(pubKeyHex))
  }
}
