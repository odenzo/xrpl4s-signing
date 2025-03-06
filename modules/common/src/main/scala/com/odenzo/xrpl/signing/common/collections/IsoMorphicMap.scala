package com.odenzo.xrpl.signing.common.collections

import cats.*
import cats.data.*
import cats.implicits.*
import cats.syntax.all.{ given, * }

/**
  * Immutable IsoMorphic map of two data models, each with unique keys. KeyA =>
  * (DataA,DataB), KeyB =>(DataA, DataB) KeyA =:= KeyB should be false but not
  * enforced. For simple isomorphic mapping between K1 <-> K2 see
  * IsomorphicKeyMap.
  */
class KeyedMap[K, D](val map: Map[K, D]):
  def get(key: K): Option[D] = map.get(key)
  def apply(key: K)          = map(key)

object KeyedMap:
  /**
    * @param data
    * @param keyFn
    *
    * @tparam K
    *   The key used for the map based on S => K
    * @tparam D
    *   The actually data stored
    */
  def from[K, D](data: List[D], keyFn: D => K): KeyedMap[K, D] = {
    // Damn, what happened to `productMap`  f(b:B => c:C) : (B,C), now just mproduct ?
    val entries: Seq[(K, D)] = data.map(d => keyFn(d) -> d)
    KeyedMap(entries.toMap)
  }

  /** In case we have a large data object and we just need to trim it down. */
  def from[S, K, D](data: List[D], keyFn: D => K, compactFn: D => S = identity): KeyedMap[K, S] = {
    val mapped: Map[K, S] = data.map(d => keyFn(d) -> compactFn(d)).toMap
    KeyedMap(mapped)
  }
