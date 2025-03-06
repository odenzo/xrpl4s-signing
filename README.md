
# xrpls-signing

Scala 3 stand-alone cryptography utilities to (primarily) enable Wallet Propose and
signing a XRPL Transaction without a dedicated XRPL Server with admin access.

The functionality is exposed both as an API, and a WebServer that can be run locally.


## Signing
This requires the txn hash and tx_json to already be produced. This functionality
is fairly heavyweight and lives in the xrpls-models module.
