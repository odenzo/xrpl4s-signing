# Seeds to generate keys from.

All Keys boil down to 16 bytes, there are just various ways to get there.

The MasterSeed type is used to represent the master seed in binary form.

This directory extracts out all the non-binary seed based operations, the seeds can then 
be used for both Secp256k1 and ED25519 style crypto keys.

The seeds styles Supported:

+ From RFC1751 words, each word interpreted as ASCII (?)
+ Seeds from Passphrases, which interpret the Java/Scale String as UTF-8 bytes
+ Seed from underlying bytes
