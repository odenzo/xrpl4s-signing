package com.odenzo.xrpl.signing.core.models

import io.circe.Codec
import io.circe.Codec.*
import AccountPublicKey.given

/**
  * When proposing a new wallet this returns all the values in text form.
  * TRypicall XRPL Base58 or Hex
  * @param account_id
  * @param key_type
  *   KeyType enum as String
  * @param master_key
  *   Explain master key vs master_seed
  * @param master_seed
  * @param master_seed_hex
  * @param public_key
  * @param public_key_hex
  */
case class WalletProposeResult(
    account_id: AccountAddress,
    key_type: KeyType,
    master_key: String,
    master_seed: String,
    master_seed_hex: String,
    public_key: AccountPublicKey,
    public_key_hex: String,
) derives Codec.AsObject {
  inline def isED25519: Boolean           = key_type == KeyType.ed25519
  inline def isSecp256k1: Boolean         = key_type == KeyType.secp256k1
  inline def isRFC1751Passphrase: Boolean = master_key.contains(" ")

}

/* ```{ "account_id": "rPucyPBNtLenrXunXSPvXHVwn8w8qVWn78", "key_type": "secp256k1", "master_key": "PAN MUTE JUTE EVER
 * HAP HAP CUBA MORN HOOF BUN MORE AD", "master_seed": "sp5izAs3LSKVXbQHmmQACdxoLfjGz", "master_seed_hex":
 * "01EEAB8469E2F76C329021BE921EF831", "public_key": "aBQHfQBnPUQXv6f6pyn3e82fqTMxQMgd2xb6QwqyDUv3eUCbVjAo",
 * "public_key_hex": "0331A63115CD9F325A30FC8179AAD89DFEF0EF136C27E49D5161BB4CF3B7736D75", "warning": "This wallet was
 * generated using a user-supplied passphrase. It may be vulnerable to brute-force attacks." }```, */
