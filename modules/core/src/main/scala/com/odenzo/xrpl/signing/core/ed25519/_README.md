# ED25519 Cryptography for XRPL Usage

- TODO: Lets see if we can use standard Java Crypto Libs
- TODO: Regular Keys

Overall this has a few basic functions:

- Give a seed, generate public and private keypair
- Support construction of seeds from RFX1761 and raw passphrase to feed into other functions (see above)
- Given a public key, payload and signature—verify the signature is correct
- Sign a Txn given private keys and a txn hash, constructed from a Txn For Signing
