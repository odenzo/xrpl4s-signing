package com.odenzo.xrpl.signing.crypto.secp256k1

import com.odenzo.xrpl.signing.crypto.constants.TypePrefix
import com.odenzo.xrpl.signing.models.{ AccountPublicKey, XrpKeyPair, XrpSeed }
import com.tersesystems.blindsight.LoggerFactory
import scodec.bits.{ ByteVector, hex }

class GeneratorsTest extends munit.FunSuite {

  private val log = LoggerFactory.getLogger

  test("Genesis") { // masterpassphrase
    val json =
      """
        |{
        |	 "Request":{
        |    "command" : "wallet_propose",
        |    "seed" : null,
        |    "passphrase" : "masterpassphrase",
        |    "key_type" : "secp256k1",
        |    "id" : "6ebbdd79-8ea5-4b4e-aa2a-1464e323a219"
        |  },
        |	 "Response":{
        |    "id" : "6ebbdd79-8ea5-4b4e-aa2a-1464e323a219",
        |    "result" : {
        |        "account_id" : "rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh",
        |        "key_type" : "secp256k1",
        |        "master_key" : "I IRE BOND BOW TRIO LAID SEAT GOAL HEN IBIS IBIS DARE",
        |        "master_seed" : "snoPBrXtMeMyMHUVTgbuqAfg1SUTb",
        |        "master_seed_hex" : "DEDCE9CE67B451D852FD4E846FCDE31C",
        |        "public_key" : "aBQG8RQAzjs1eTKFEAQXr2gS4utcDiEC9wmi7pfUPTi27VCahwgw",
        |        "public_key_hex" : "0330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD020",
        |        "warning" : "This wallet was generated using a user-supplied passphrase that has low entropy and is vulnerable to brute-force attacks."
        |    },
        |    "status" : "success",
        |    "type" : "response"
        |  }
        |}
    """.stripMargin

  }

  test("Making a KeyPair") {
    // ANd also if I can just use a private key!
    val masterSeedHex = "559EDD35041D3C11F9BBCED912F4DE6A" // Ripple master seed is different than Private Key?

    val addr   = "r4kBGj4QWm1ord3fjd6KcGdENtzB4Fh4JS"
    val secret = "sh1aUhGupHaJG7gMeaxC6Nnde3Lnc"

    // Master seed is 32 bytes, need to find an ed255219 key from Ripple

  }

  test("Wallet Shuffle") {
    val account_id                                            = "r49pwNZibgeK83BeEuHYFKBpJE5Tt4USsQ"
    val key_type                                              = "secp256k1"
    val master_key                                            = "TILE TAKE WELD CASK NEWT TIRE WIND SOFA SHED HELL TOOK FAR"
    val master_seed                                           = "ssDtFWc75geBLkzYcSYJ3nFbpRkaX"
    val master_seed_hex                                       = hex"25DC4E4B6933FCFBD93F1CB2E6E3BCEB"
    val public_key                                            = "aBPHrChJfFe7MtwyPtpf82CsseoW2X22M8dS4eAjWdrWGBX48gk5"
    val public_key_hex                                        = "02ADBA6E42BCC1CEF0DA5CF2AC82A374C72ED7A78527976225D8AF49B82137934B"
    log.info(s"Source Seed: ${master_seed_hex}")
    log.info(s"Master Seed Prefix: ${TypePrefix.SeedValue.bv}")
    val firstMasterSeed                                       = XrpSeed.fromBytesUnsafe(master_seed_hex)
    log.info(s"FirstSeed RAW: ${firstMasterSeed.asRawSeed}")
    log.info(s"FirstSeed : ${firstMasterSeed.bv}")
    val accountKeys: XrpKeyPair[ByteVector, AccountPublicKey] = Generators.generateAllFromMasterSeed(firstMasterSeed)
    val accountPublic: AccountPublicKey                       = accountKeys.publicKey
    val computedRawPublicKey: ByteVector                      = accountPublic.asRawKey

  }
}
// test("About Ripple") {
//    val masterSeed       = "F5933E5F60ED0F7A940E995B26F9191E"
//    val accountD         = "35778234256876764691539509132472247051158811702107230030471922537290092532408"
//    val accountPublicKey = "03AC6788F14F95D87AFF0236A3F671DBF774F24B9E9E94C2B188E9E82DD2F36C21"
//
//    val rippleD       = new BigInteger(accountD)
//    val rippleDHex    = ByteUtils.bytes2hex(rippleD.toByteArray)
//    logger.info(s"RippleD Hex:\n $rippleDHex")
//    val rippleKeyPair = Secp256K1CryptoBC.dToKeyPair(rippleD)
//    logger.info("Ripple KeyPair: " + rippleKeyPair.getPrivate)
//    val pub           = Secp256K1CryptoBC.compressPublicKey(rippleKeyPair.getPublic)
//    bytes2hex(pub) shouldEqual accountPublicKey
//  }
