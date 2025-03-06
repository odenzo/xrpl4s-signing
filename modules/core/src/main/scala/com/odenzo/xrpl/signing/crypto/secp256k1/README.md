# SECP256k1 Crypto Notes

https://xrpl.org/docs/concepts/accounts/cryptographic-keys#secp256k1-key-derivation


This all starts with a 16 byte seed, just like ed25519


## Upper Path Intermediate + Root Public Key to get MasterPublic Key (alternative route, not the A+B % MAX_KEY route)
Convert the root public key to its 33-byte compressed form.
The uncompressed form of any ECDSA public key consists of a pair of 32-byte integers: an X coordinate, and a Y coordinate. The compressed form is the X coordinate and a one-byte prefix: 0x02 if the Y coordinate is even, or 0x03 if the Y coordinate is odd.
You can convert an uncompressed public key to the compressed form with the openssl commandline tool. For example, if the uncompressed public key is in the file ec-pub.pem, you can output the compressed form like this:

Derive the master public key pair by adding the intermediate public key to the root public key. Similarly, derive the secret key by adding the intermediate secret key to the root secret key.
An ECDSA secret key is a very large integer, so you can calculate the sum of two secret keys by summing them modulo the secp256k1 group order.
An ECDSA public key is a point on the elliptic curve, so you should use elliptic curve math to sum the points.


## Validators
Validators use this root key pair. If you are calculating a validator's key pair, you can stop here. To distinguish between these two different types of public keys, the base58 serialization for validator public keys uses the prefix 0x1c.
