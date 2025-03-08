package com.odenzo.xrpl.signing.core

import cats.effect.IO
import com.odenzo.xrpl.signing.common.utils.MyLogging
import com.odenzo.xrpl.signing.core.DeriveAccountAddress
import com.odenzo.xrpl.signing.core.models.{ AccountAddress, WalletProposeResult, XrpPublicKey, XrpSeed }
import com.odenzo.xrpl.signing.core.seeds.{ RFC1751Keys, SeedOps }
import com.odenzo.xrpl.signing.testkit.WalletTestIOSpec
import com.tersesystems.blindsight.LoggerFactory
import scodec.bits.Bases.Alphabets
import scodec.bits.{ ByteVector, hex }

/** Tests using the pubic key to derive the Account Address */
class DeriveAccountAddressTest extends WalletTestIOSpec {

  private val log                                                           = LoggerFactory.getLogger
  import XrpSeed.asRawSeed // Extension method
  def check(walletRs: WalletProposeResult)(using loc: munit.Location): Unit = {
    test(s"${walletRs.account_id} - ${walletRs.key_type}") {
      val publicKey: XrpPublicKey      = walletRs.public_key
      val computed: IO[AccountAddress] = DeriveAccountAddress.accountPublicKey2address(publicKey)
      assertIO(computed, walletRs.account_id) // This is really AccountAddress (prefix and checksummed)

    }
  }

  walletDataResource
    .use { (wallets: List[WalletProposeResult]) =>
      wallets.foreach { (rs: WalletProposeResult) => check(rs) }
      IO.unit
    }.unsafeRunSync()
}
