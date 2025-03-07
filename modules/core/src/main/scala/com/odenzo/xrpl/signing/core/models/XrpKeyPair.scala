package com.odenzo.xrpl.signing.core.models

case class XrpKeyPair[PRIVATE, PUBLIC](privateKey: PRIVATE, publicKey: PUBLIC)
