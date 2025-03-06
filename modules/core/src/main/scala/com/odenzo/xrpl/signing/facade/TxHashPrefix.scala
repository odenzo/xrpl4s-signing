package com.odenzo.xrpl.signing.facade

import cats.*
import cats.data.*
import scodec.bits.{ ByteVector, hex }

/**
  * These are all Four Bytes Long with bottom byte 00 // Unsigned Transaction is
  * "53545800 + TxBlob sha512half // Unsigned Multisigned // Signed "54584E00
  * (both kinds I think)
  */
enum TxHashPrefix(bytes: ByteVector) {

  /** Prefix to txblob to make transaction hash */
  case transactionID extends TxHashPrefix(hex"54584E00")

  /** Unsigned Single Signer Txn * */
  case transaction extends TxHashPrefix(hex"534E4400")

  /** Account State * */
  case accountStateEntry extends TxHashPrefix(hex"4D4C4E00")

  /** inner node in tree * */
  case innerNode extends TxHashPrefix(hex"4D494E00")
  // ledger master data for signing
  case ledgerHeader extends TxHashPrefix(hex"4C575200")

  /** inner transaction to single signed, before signing */
  case transactionSig extends TxHashPrefix(hex"53545800")

  /** Transaction for MultiSigning */
  case transactionMultiSig extends TxHashPrefix(hex"534D5400")

  /** Validation for signing */
  case validation extends TxHashPrefix(hex"56414C00")

  /** proposal for signing, not sure what kind of proposal */
  case proposal extends TxHashPrefix(hex"50525000")

  // payment channel claim
  case paymentChannelClaim extends TxHashPrefix(hex"434C4D00")

}

// The human was of understanding. Three char prefix and 4 char is 0 byte
//object RippledHashPrefix {
//
//  val transactionID: UInt       = HashPrefix('T', 'X', 'N')
//  val txNode: UInt              = HashPrefix('S', 'N', 'D')
//  val leafNode: UInt            = HashPrefix('M', 'L', 'N')
//  val innerNode: UInt           = HashPrefix('M', 'I', 'N')
//  val innerNodeV2: UInt         = HashPrefix('I', 'N', 'R')
//  val ledgerMaster: UInt        = HashPrefix('L', 'W', 'R')
//  val txSign: UInt              = HashPrefix('S', 'T', 'X')
//  val txMultiSign: UInt         = HashPrefix('S', 'M', 'T')
//  val validation: UInt          = HashPrefix('V', 'A', 'L')
//  val proposal: UInt            = HashPrefix('P', 'R', 'P')
//  val manifest: UInt            = HashPrefix('M', 'A', 'N')
//  val paymentChannelClaim: UInt = HashPrefix('C', 'L', 'M')
//
//
//}
