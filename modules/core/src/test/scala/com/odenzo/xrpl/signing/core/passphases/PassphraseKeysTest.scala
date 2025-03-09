package com.odenzo.xrpl.signing.core.passphases

import cats.effect.IO
import com.odenzo.xrpl.signing.common.utils.MyLogging
import com.odenzo.xrpl.signing.core.models.{ WalletProposeResult, XrpSeed }
import com.odenzo.xrpl.signing.core.passphases.{ RFC1751Keys, PassphraseOps }
import com.odenzo.xrpl.signing.testkit.WalletTestIOSpec
import com.tersesystems.blindsight.LoggerFactory
import scodec.bits.Bases.Alphabets
import scodec.bits.{ ByteVector, hex }

/**
  * This is really just a test of RFC1751 works converting to a seed, compared
  * by hex. RFC functions just throw Exceptions now, most not wrapped even,
  * These are really SeedOps tests, not detailed unit tests into RFC1751 yet
  * Test data contains a mix of RFC and regular passphrases
  */
class PassphraseKeysTest extends WalletTestIOSpec {

  private val log                                                              = LoggerFactory.getLogger
  import XrpSeed.asRawSeed // Extension method
  def checkRFC(walletRs: WalletProposeResult)(using loc: munit.Location): Unit = {
    test(s"${walletRs.account_id} - ${walletRs.key_type}") {
      val rfcPassphrase: String = walletRs.master_key
      log.debug(s"MasterKey: [$rfcPassphrase]")
      val masterSeedHex: String = walletRs.master_seed_hex
      val computed: ByteVector  = RFC1751Keys.twelveWordsAsBytes(rfcPassphrase).asRawSeed
      val hex: String           = computed.toHex(Alphabets.HexUppercase)
      assertEquals(hex, masterSeedHex)

    }
  }

  walletDataResource
    .use { (wallets: List[WalletProposeResult]) =>
      wallets.foreach { (rs: WalletProposeResult) => checkRFC(rs) }
      IO.unit
    }.unsafeRunSync()
}
