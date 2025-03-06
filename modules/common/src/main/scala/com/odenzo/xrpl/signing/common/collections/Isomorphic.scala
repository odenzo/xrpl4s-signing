package com.odenzo.xrpl.signing.common.collections

trait Isomorphic[A, B] {
  def to(a: A): B
  def from(b: B): A
}
