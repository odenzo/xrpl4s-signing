//package com.odenzo.xrpl.signing.core.seeds
//
//import com.odenzo.xrpl.signing.crypto.seeds.{ RFC1751Keys, SeedOps }
//
//import scodec.bits.{ Bases, ByteVector, hex }
//
///**
//  * This is really just a test of RFC1751 works converting to a seed, compared
//  * by hex. RFC functions just throw Exceptions now, most not wrapped even,
//  * These are really SeedOps tests, not detailed unit tests into RFC1751 yet
//  */
//class SeedOpsTest extends munit.FunSuite with MyLogging {
//
//  private val log = LoggerFactory.getLogger
//
//  val words: String = "UTAH HONK SAT BLEW SHY RAP ELLA CHOU OSLO MAKE NUT RIG"
//  val utahHex       = hex"6FD822DB25C72C7B6CBE93ACEA44D3F2"
//
//  val fourwords: List[String] = words.split(' ').take(6).toList
//
//  val keyType          = "secp256k1"
//  val address: String  = "rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"
//  val passphrase       = "masterpassphrase"
//  val masterSeedBase58 = "snoPBrXtMeMyMHUVTgbuqAfg1SUTb"
//  val masterSeedHex    = "DEDCE9CE67B451D852FD4E846FCDE31C"
//  val RFC1751          = "I IRE BOND BOW TRIO LAID SEAT GOAL HEN IBIS IBIS DARE"
//  val publicKeyBase58  = "aBQG8RQAzjs1eTKFEAQXr2gS4utcDiEC9wmi7pfUPTi27VCahwgw"
//  val publicKeyHex     = "0330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD020"
//
//  test("Twelve Words") {
//    val computed = RFC1751Keys.twelveWordsAsBytes(words)
//    val raw      = computed.asRawSeed
//    assertEquals(raw, utahHex)
//  }
//
//  /** Conclusion Fix58 is broken. Buildin one works. */
//  test("Genesis Seed") {
//    val bytesFromHex = ByteVector.fromValidHex(masterSeedHex)
//    val withTypeCode = hex"21" ++ bytesFromHex
//    val withChecksum = withTypeCode ++ HashOps.xrpChecksum(withTypeCode)
//    val toTest       = withChecksum
//    log.info(s"Base From Hex: $bytesFromHex")
//    log.info(s"Genesis Seed $withTypeCode")
//    log.info(s"WithChecksum: $withChecksum")
//    val seedB58N     = XrpBase58Fix.toXrpBase58(toTest)
//    val seedB58O     = toTest.toBase58(Bases.Alphabets.Base58)
//    log.debug(s"$seedB58O <->$seedB58N")
//
//    assertEquals(seedB58N, seedB58O)
//  }
//
//  test("AccountAddress") {
//    val a54BytesO = ByteVector.fromValidBase58(address)
//    val a54BytesN = XrpBase58Fix.fromValidXrpBase58(address)
//    log.info(s"Parse B58: $a54BytesO <-> $a54BytesN ") // Len 25 = 1+20+4 (type+accountId+checksum)
//    val hexO      = a54BytesO.toHex
//    val hexN      = a54BytesN.toHex
//
//    assertEquals(hexO, hexN)
//  }
////  test("Scala 4") {
////    val bytes = RFC1751Keys.etob(fourwords))
////    val cHex  = ByteUtils.bytes2hex(bytes)
////    cHex shouldEqual "F2D344EAAC93BE6C"
////  }
//
//  test("Ripple ED Wallet Ex") {
//    val master_key: String          = "HICK LAUD TONY FORM SCOT DOES ORGY BUOY JUKE GLOB HUGE POE"
//    val master_seed_hex: ByteVector = hex"69C269468F0E4CC9E97C9DC2B667B597"
//    assertEquals(SeedOps.seedFromRFC1751(master_key).asRawSeed, master_seed_hex)
//
//  }
//
//}
