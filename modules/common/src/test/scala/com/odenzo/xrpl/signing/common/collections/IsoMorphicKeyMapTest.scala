package com.odenzo.xrpl.signing.common.collections

import com.tersesystems.blindsight.LoggerFactory

class IsoMorphicKeyMapTest extends munit.FunSuite {
  private val log = LoggerFactory.getLogger

  test("basic") {
    val data: Seq[(String, Int)] = List("one" -> 1, "two" -> 2, "three" -> 3, "four" -> 4, "five" -> 5)
    val iso                      = IsoMorphicKeyMap.from(data)
    val n = iso.getTo("one")
    log.debug(s"N = $n")
    val l = iso.getFrom(n.get)
    log.debug(s"Back = $l")
    val depends = iso.get("one")
    log.debug(s"Depends = $depends")
    val depends2 = iso.get(2)
    log.debug(s"Depends2 = $depends2")
    
    val setA = iso.getSetFromA("one")
    log.debug(s"SetA = $setA")
    val setB = iso.getSetFromB(3)
    log.debug(s"SetB = $setB")
    
    
    
  }
}
