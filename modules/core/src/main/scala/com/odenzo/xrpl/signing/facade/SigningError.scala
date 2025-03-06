package com.odenzo.xrpl.signing.facade

/**
  * Contains a semi-deprecated list of all errors thrown by tge LocalSigningAPI
  * Apis.
  */
//noinspection IllegalNull
enum SigningError(val message: String, ex: Throwable) extends Throwable(message, ex):
  case UnexpectedError(e: Throwable) extends SigningError(e.getMessage, e)
  case ErrorMessage(m: String) extends SigningError(m, null)
  case NestedError(m: String, e: Throwable) extends SigningError(m, e)
