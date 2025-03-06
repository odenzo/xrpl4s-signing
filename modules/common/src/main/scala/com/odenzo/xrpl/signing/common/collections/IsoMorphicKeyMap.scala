package com.odenzo.xrpl.signing.common.collections

import cats.*
import cats.data.*
import cats.implicits.*
import cats.syntax.all.{ *, given }
import com.tersesystems.blindsight.LoggerFactory

import scala.reflect.ClassTag
import scala.util.NotGiven

/**
  * Immutable IsoMorphic map of simple keys: KeyA => KeyB and KeyB=>KeyA
  * supported. This is implemented by two underlying maps since typically low
  * memory usage. These are immutable maps. Constructor is open, but builders in
  * companian class should be used. Tempted to simplify by ensuirinmg A =%= B is
  * false.
  *
  * Possible issue for opaque types where A would match the underlying opaque
  * type?
  */
class IsoMorphicKeyMap[A: ClassTag, B: ClassTag](foward: Map[A, B], backward: Map[B, A]) {
  private val log = LoggerFactory.getLogger

  def get(key: A | B)(using NotGiven[A <:< B], NotGiven[B <:< A]): Option[A | B] = {
    key match
      case x: B =>
        log.debug("Matched Key as B")
        backward.get(x)
      case x: A =>
        log.debug("Matched Key as A")
        foward.get(x)

  }

  val x = Map().ap

  /** Given K1 get D1, given K2 get D2 */
  def getTo(key: A): Option[B] = foward.get(key)

  def getToUnsafe(key: A): B = getTo(key).getOrElse(
    throw new NoSuchElementException(
      s"Trouble Looking up data type ${ClassTag[A]}:: $key for ${key.getClass.getSimpleName}"
    )
  )

  def getFrom(key: B): Option[A] = backward.get(key)

  def getFromUnsafe(key: B): A = getFrom(key).getOrElse(
    throw new NoSuchElementException(s"Contragetting ${key.getClass}::${key} was not found in map")
  )

  def getSetFromA(key: A): Option[(A, B)] = getTo(key).tupleLeft(key)

  def getSetFromB(key: B): Option[(A, B)] = getFrom(key).tupleRight(key)
}

object IsoMorphicKeyMap:
  def from[A: ClassTag, B: ClassTag](data: Seq[(A, B)]): IsoMorphicKeyMap[A, B] =
    IsoMorphicKeyMap(data.toMap, data.map { case (a, b) => (b, a) }.toMap)
