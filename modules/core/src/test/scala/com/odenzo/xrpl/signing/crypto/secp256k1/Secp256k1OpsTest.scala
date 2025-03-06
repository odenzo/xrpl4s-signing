package com.odenzo.xrpl.signing.crypto.secp256k1

class Secp256k1OpsTest extends munit.FunSuite {}
//  test("Account Public Key to Address") {
//    val pub                  = "0330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD020"
//    val acct                 = "rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"
//    val sepBytes: List[Byte] = getOrLog(hex2bytes(pub))
//    val csepAddr             = accountpubkey2address(sepBytes)
//
//    getOrLog(csepAddr).v shouldEqual acct
//
//    val edPubKey: String   = "ED9434799226374926EDA3B54B1B461B4ABF7237962EAE18528FEA67595397FA32"
//    val edAddr: String     = "rDTXLQ7ZKZVKz33zJbHjgVShjsBnqMBhmN"
//    val eBytes: List[Byte] = getOrLog(hex2bytes(edPubKey))
//
//    val cedAddr = accountpubkey2address(eBytes)
//
//    getOrLog(cedAddr).v shouldEqual edAddr
//  }

//package com.odenzo.ripple.localops.impl.crypto
//
//import java.math.BigInteger
//import java.security.{ KeyFactory, Security }
//
//import org.bouncycastle.jce.provider.BouncyCastleProvider
//
//import com.odenzo.ripple.localops.impl.crypto.core.Secp256K1CryptoBC
//import com.odenzo.ripple.localops.impl.utils.ByteUtils
//import com.odenzo.ripple.localops.testkit.OTestSpec
//
//class Secp256k1CryptoBCTest extends munit.FunSuite {
//  Security.addProvider(new BouncyCastleProvider)
//
//  val fact: KeyFactory = KeyFactory.getInstance("EC", "BC")
//

//}
