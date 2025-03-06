package com.odenzo.xrpl.signing.models

case class XrpKeyPair[PRIVATE, PUBLIC](privateKey: PRIVATE, publicKey: PUBLIC)
