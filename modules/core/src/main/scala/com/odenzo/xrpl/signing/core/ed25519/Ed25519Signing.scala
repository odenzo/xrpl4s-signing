package com.odenzo.xrpl.signing.core.ed25519

import cats.*
import cats.data.*
import com.odenzo.xrpl.signing.common.binary.XrpBinaryOps
import io.circe.literal.json
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.ec.CustomNamedCurves
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.*
import org.bouncycastle.crypto.signers.Ed25519Signer
import org.bouncycastle.jce.provider.BouncyCastleProvider
import scodec.bits.{ ByteVector, hex }

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.{ Provider, SecureRandom }

/**
  * See: https://xrpl.org/docs/references/protocol/binary-format for concepts.
  * So, signing normally involves sending the tx_json in Json format (basically
  * a Txn serialized). The Server then selects a subset of the fields, orders
  * then and creates a Binary txn which is then signed. This is inserted into
  * the SignRq and then a hash is made of the whole txn.
  *
  * Various ways can be used to provider the private key to sign, including the
  * seed (master or regular I guess).
  *
  * Since the current bincodec is in the xrpls-core (via bin-codec and models
  * modules) we prefer to do it differently. To make this more generic. Maybe
  * that is a mistake though. Anyway, we need to return a txBlob (TxnSignature)
  * Lets see the bare minimum we can get an input to do that in addition to the
  * private key. Why do I want to do this? Because I want NO server to be used
  * to sign a txn. I want to embedd that in iPhone app with Seed in secure
  * enclave. I don't want to rewrite all the bin-codec stuff, or convert to
  * ScalaJS and run as Javascript. (Although not a bad option!) So, we need some
  * scala code to do stuff the deal with, for example, the sample transaction
  * data listed below
  *
  * @return
  *   tx_blob that can be submitted to add to ledger
  */
object Exploratory {
  val txjson = json"""{
                       "Account": "rMBzp8CgpE441cp5PVyA9rpVV7oT8hP3ys",
                       "Expiration": 595640108,
                       "Fee": "10",
                       "Flags": 524288,
                       "OfferSequence": 1752791,
                       "Sequence": 1752792,
                       "SigningPubKey": "03EE83BB432547885C219634A1BC407A9DB0474145D69737D09CCDC63E1DEE7FE3",
                       "TakerGets": "15000000000",
                       "TakerPays": {
                         "currency": "USD",
                         "issuer": "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B",
                         "value": "7072.8"
                       },
                       "TransactionType": "OfferCreate",
                       "TxnSignature": "30440220143759437C04F7B61F012563AFE90D8DAFC46E86035E1D965A9CED282C97D4CE02204CFD241E86F17E011298FC1A39B63386C74306A5DE047E213B0F29EFA4571C2C",
                       "hash": "73734B611DDA23D3F5F62E20A173B78AB8406AC5015094DA53F53D39B9EDB06C"
                     """

  val binaryRep =
    hex"120007220008000024001ABED82A2380BF2C2019001ABED764D55920AC9391400000000000000000000000000055534400000000000A20B3C85F482532A9578DBB3950B85CA06594D165400000037E11D60068400000000000000A732103EE83BB432547885C219634A1BC407A9DB0474145D69737D09CCDC63E1DEE7FE3744630440220143759437C04F7B61F012563AFE90D8DAFC46E86035E1D965A9CED282C97D4CE02204CFD241E86F17E011298FC1A39B63386C74306A5DE047E213B0F29EFA4571C2C8114DD76483FACDEE26E60D8A586BB58D09F27045C46"
}

object Ed25519Signing extends XrpBinaryOps {
  "120007220008000024001ABED82A2380BF2C2019001ABED764D55920AC9391400000000000000000000000000055534400000000000A20B3C85F482532A9578DBB3950B85CA06594D165400000037E11D60068400000000000000A732103EE83BB432547885C219634A1BC407A9DB0474145D69737D09CCDC63E1DEE7FE3744630440220143759437C04F7B61F012563AFE90D8DAFC46E86035E1D965A9CED282C97D4CE02204CFD241E86F17E011298FC1A39B63386C74306A5DE047E213B0F29EFA4571C2C8114DD76483FACDEE26E60D8A586BB58D09F27045C46"
  private val curve: X9ECParameters = CustomNamedCurves.getByName("curve25519")
  private val order: BigInteger     = curve.getCurve.getOrder

  //  private val domainParams: ECDomainParameters =
  //    new ECDomainParameters(curve.getCurve, curve.getG, curve.getN, curve.getH)

  /** Generate signature using Bouncy Castle Directly using ED25519 Private Key */
  def sign(payload: ByteVector, edPrivateKey: Ed25519PrivateKeyParameters): ByteVector = {
    assert(payload.size <= Int.MaxValue)
    val edSigner: Ed25519Signer = new Ed25519Signer()
    edSigner.init(true, edPrivateKey)
    edSigner.update(payload.toArray, 0, payload.size.toInt)
    val signature: Array[Byte]  = edSigner.generateSignature()
    ByteVector(signature)
  }

  /**
    * Signed a signed message (or whatever) and a public key, verify valid
    * signature and trust it. 64 byte signatures are compressed versions, 64
    * bytes are output
    *
    * @param payload
    *   Bytes to verify that signature matches
    * @param txnSignature
    *   The TxnSignature as bytes in Ripple context, big-endian hex more or
    *   less.
    * @param pubKey
    *   The SigningPubKey in native Ed25519 Format. This is the public key
    *   stored in the Wallet.
    * @return
    */
  def verify(
      payload: ByteVector,
      txnSignature: ByteVector,
      pubKey: Ed25519PublicKeyParameters,
  ): Boolean = {
    assert(payload.size <= Int.MaxValue)
    val edSigner: Ed25519Signer = new Ed25519Signer()
    edSigner.init(false, pubKey)
    edSigner.update(payload.toArray, 0, payload.length.toInt)
    edSigner.verifySignature(txnSignature.toArray)

  }

}
