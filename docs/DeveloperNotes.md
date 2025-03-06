# Developer Notes



This requires the ability to serialize to bytes tx_json and TxBlob, which includes address and TxnSignature

Thinking about how to minimize this, but some things we *have* to create and extract in pure binary form.

This is being done with Scala SCODEC (and we use scodec.bits too)

## Checksums

Many times we have the `body` of a XRPL value, but we need to encode it. The general formula
is  ```PrefixByte :: body :: rippleChecksum(PrefixByte::body)```

See ConversionOps for example code. 


## Customer Fields 

### 
