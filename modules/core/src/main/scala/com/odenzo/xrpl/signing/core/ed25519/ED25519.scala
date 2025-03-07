package com.odenzo.xrpl.signing.core.ed25519

import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.ec.CustomNamedCurves

import java.math.BigInteger

trait ED25519 {

  private val curve: X9ECParameters = CustomNamedCurves.getByName("curve25519")
  private val order: BigInteger     = curve.getCurve.getOrder

}
