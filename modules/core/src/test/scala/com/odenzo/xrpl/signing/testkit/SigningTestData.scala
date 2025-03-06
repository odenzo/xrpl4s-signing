package com.odenzo.xrpl.signing.testkit

import io.circe.literal.json

object SigningTestData {

  val proposedWallet = json""" {
                                 "result" : {
                                     "account_id" : "rNuShN4nDnCmj92iLx27RhZAhL5AqSEiT1",
                                     "key_type" : "secp256k1",
                                     "master_key" : "FUND HANS SOLD DRUM IRE MITT ROCK KICK FEET HURL NAIR LACY",
                                     "master_seed" : "ssqeXB5uoqinxvu8wABiZVYBuDWzF",
                                     "master_seed_hex" : "521B9CCEFBCD14D179E7A1BB87775287",
                                     "public_key" : "aB42Pejquv6rCSVv4krV51kW1oUUzWm9pJ1cibCdCS1J3f16pvWu",
                                     "public_key_hex" : "02517E68B3391BCF576F840D092DE3796BC9385AF206D077DE4A31D99FD1FAE7DD",
                                     "status" : "success",
                                     "warning" : "This wallet was generated using a user-supplied passphrase that has low entropy and is vulnerable to brute-force attacks."
                                 }
                             } """
}
