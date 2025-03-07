package com.odenzo.xrpl.signing.testkit

import cats.effect.{ IO, Resource }
import com.odenzo.xrpl.signing.core.models.WalletProposeResult
import io.circe.JsonObject
import munit.CatsEffectSuite
import munit.catseffect.IOFixture

import java.io.InputStream
import scala.io.Source
trait WalletTestIOSpec extends CatsEffectSuite {

  /** A list of WalletPropose responses generated from stand-alone XRPL server */
  val walletDataResource: Resource[IO, List[WalletProposeResult]] =
    TestUtils.loadListOfJsonResource[WalletProposeResult]("test/myTestData/wallets/walletRs.json")

  val walletDataFixture: IOFixture[List[WalletProposeResult]] = ResourceSuiteLocalFixture(
    "wallet-data",
    walletDataResource,
  )

  override def munitFixtures = List(walletDataFixture)

}
