package com.odenzo.xrpl.signing.crypto.seeds

import com.odenzo.xrpl.signing.common.utils.MyLogging
import com.odenzo.xrpl.signing.crypto.seeds.{ RFC1751Keys, SeedOps }
import scodec.bits.{ ByteVector, hex }

/**
  * This is really just a test of RFC1751 works converting to a seed, compared
  * by hex. RFC functions just throw Exceptions now, most not wrapped even,
  * These are really SeedOps tests, not detailed unit tests into RFC1751 yet
  */
class RFC1751KeysTest extends munit.FunSuite with MyLogging {
  val words: String = "UTAH HONK SAT BLEW SHY RAP ELLA CHOU OSLO MAKE NUT RIG"
  val utahHex       = hex"6FD822DB25C72C7B6CBE93ACEA44D3F2"

  val fourwords: List[String] = words.split(' ').take(6).toList

  test("Twelve Words") {
    val computed = RFC1751Keys.twelveWordsAsBytes(words)
    val raw      = computed.asRawSeed
    assertEquals(raw, utahHex)
  }

  test("Genesis Passphrase") {
    val passphrase = "masterpassphrase"
    val words      = "I IRE BOND BOW TRIO LAID SEAT GOAL HEN IBIS IBIS DARE"
    val seed       = hex"DEDCE9CE67B451D852FD4E846FCDE31C"
    assertEquals(SeedOps.seedFromPassphrase(passphrase).asRawSeed, seed)
    assertEquals(SeedOps.seedFromRFC1751(words).asRawSeed, seed)

  }

//  test("Scala 4") {
//    val bytes = RFC1751Keys.etob(fourwords))
//    val cHex  = ByteUtils.bytes2hex(bytes)
//    cHex shouldEqual "F2D344EAAC93BE6C"
//  }

  test("Ripple ED Wallet Ex") {
    val master_key: String          = "HICK LAUD TONY FORM SCOT DOES ORGY BUOY JUKE GLOB HUGE POE"
    val master_seed_hex: ByteVector = hex"69C269468F0E4CC9E97C9DC2B667B597"
    assertEquals(SeedOps.seedFromRFC1751(master_key).asRawSeed, master_seed_hex)

  }

}
