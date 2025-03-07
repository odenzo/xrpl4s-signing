package com.odenzo.xrpl.signing.core.secp256k1

import com.tersesystems.blindsight.LoggerFactory
import scodec.bits.{ ByteVector, hex }
import cats.effect.IO
import com.odenzo.xrpl.signing.common.utils.MyLogging
import com.odenzo.xrpl.signing.core.models.{ AccountPublicKey, WalletProposeResult, XrpKeyPair, XrpSeed }
import com.odenzo.xrpl.signing.core.seeds.{ RFC1751Keys, SeedOps }
import com.odenzo.xrpl.signing.testkit.WalletTestIOSpec
import com.tersesystems.blindsight.LoggerFactory
import scodec.bits.Bases.Alphabets
import scodec.bits.{ ByteVector, hex }

/**
  * Check to see the Secp Wallet Generator is accurate for MasterSeedHex
  *   - MasterSeed and MasterSeedHex isomorphism tested elsewhere.
  *   - Passphrase to MasterSeedHex tested elsewhere.
  */
class GeneratorSecpTest extends WalletTestIOSpec {

  private val log = LoggerFactory.getLogger

  import XrpSeed.asRawSeed // Extension method

  def check(walletRs: WalletProposeResult)(using loc: munit.Location): Unit = {
    test(s"${walletRs.account_id} - ${walletRs.key_type}") {

      val seed: XrpSeed = XrpSeed.fromBase58Unsafe(walletRs.master_seed)
      log.debug(s"Seed: ${seed.toHex}")
      /** Private and Public key */
      val keys: XrpKeyPair[ByteVector, AccountPublicKey] = Generators.createKeyPairFromMasterSeed(seed)
      // import AccountPublicKey.asRawKey
      val publicKey: ByteVector                          = keys.publicKey.asRawKey

      val expectedPrivateKey = walletRs.master_key

      assertEquals(publicKey.toHex(Alphabets.HexUppercase), walletRs.public_key_hex)

    }
  }

  walletDataResource
    .use { (wallets: List[WalletProposeResult]) =>
      wallets.foreach { (rs: WalletProposeResult) => check(rs) }
      IO.unit
    }.unsafeRunSync()
}

//  test("Wallet Shuffle") {
//    val account_id                                            = "r49pwNZibgeK83BeEuHYFKBpJE5Tt4USsQ"
//    val key_type                                              = "secp256k1"
//    val master_key                                            = "TILE TAKE WELD CASK NEWT TIRE WIND SOFA SHED HELL TOOK FAR"
//    val master_seed                                           = "ssDtFWc75geBLkzYcSYJ3nFbpRkaX"
//    val master_seed_hex                                       = hex"25DC4E4B6933FCFBD93F1CB2E6E3BCEB"
//    val public_key                                            = "aBPHrChJfFe7MtwyPtpf82CsseoW2X22M8dS4eAjWdrWGBX48gk5"
//    val public_key_hex                                        = "02ADBA6E42BCC1CEF0DA5CF2AC82A374C72ED7A78527976225D8AF49B82137934B"
//    log.info(s"Source Seed: ${master_seed_hex}")
//    log.info(s"Master Seed Prefix: ${TypePrefix.SeedValue.bv}")
//    val firstMasterSeed                                       = XrpSeed.fromBytesUnsafe(master_seed_hex)
//    log.info(s"FirstSeed RAW: ${firstMasterSeed.asRawSeed}")
//    log.info(s"FirstSeed : ${firstMasterSeed.bv}")
//    val accountKeys: XrpKeyPair[ByteVector, AccountPublicKey] = Generators.generateAllFromMasterSeed(firstMasterSeed)
//    val accountPublic: AccountPublicKey                       = accountKeys.publicKey
//    val computedRawPublicKey: ByteVector                      = accountPublic.asRawKey
//
//  }

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
