package com.odenzo.xrpl.signing.crypto.secp256k1

import com.odenzo.xrpl.signing.common.utils.MyLogging
import com.tersesystems.blindsight.LoggerFactory
import scodec.bits.{ ByteVector, hex }

class DERTest extends munit.FunSuite with MyLogging {
  private val log = LoggerFactory.getLogger

  test("Decoding from Byte") {
    // This is an example from Tx
    val sig: ByteVector =
      hex"304502210085974FA1E3192621D4481EC635A15D8955842E4C551D2C6907CCE457D3B4E9EE022068B31369259CC08F25BB452A6D7CCE3BF0231D877B5C789EA8F79D0B79A3CF8C"
    println(s"Hex Dump:\n ${sig.toHexDumpColorized}")
    val prefix: Byte    = sig.head
    val len: Int        = sig.get(1).toInt

    log.info(s"Prefix: $prefix")
    log.info(s"Total Length: ${len}")
    // This is an example from Tx
    // 304502210085974FA1E3192621D4481EC635A15D8955842E4C551D2C6907CCE457D3B4E9EE022068B31369259CC08F25BB452A6D7CCE3BF0231D877B5C789EA8F79D0B79A3CF8C

  }
}
