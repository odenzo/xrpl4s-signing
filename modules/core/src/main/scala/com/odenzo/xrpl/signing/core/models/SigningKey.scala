package com.odenzo.xrpl.signing.core.models

import cats.syntax.all.*
import org.bouncycastle.crypto.AsymmetricCipherKeyPair

import java.security.KeyPair

/**
  * Allows for precalculation of Signing Key in implementation dependant format
  * This should be treated as an Opaque Type For SECP keys this saves time
  * deriving signing key from master_seed each time.
  */
sealed trait SigningKey {
  def signPubKey: String
}

case class SigningKeyEd25519(kp: AsymmetricCipherKeyPair, signPubKey: String) extends SigningKey
case class SigningKeySecp256(kp: KeyPair, signPubKey: String) extends SigningKey
