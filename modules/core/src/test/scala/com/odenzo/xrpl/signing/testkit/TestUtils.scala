package com.odenzo.xrpl.signing.testkit

import cats.effect.{ IO, Resource }
import io.circe.Decoder
import io.circe.parser.decode

import java.io.InputStream
import scala.io.Source

trait TestUtils {

  /** Load a Resource from ClassPath as a Cats Resource */
  def loadResourceFile(fileName: String): Resource[IO, String] = {
    Resource.make {
      IO.blocking {
        val stream: InputStream = getClass.getClassLoader.getResourceAsStream(fileName)
        Source.fromInputStream(stream).mkString
      }
    } { content =>
      IO.blocking(println("Resource released")).void
    }
  }

  def loadListOfJsonResource[T: Decoder](fileName: String): Resource[IO, List[T]] = {
    loadResourceFile(fileName).evalMap(str => IO.fromEither(decode[List[T]](str)))
  }
}

object TestUtils extends TestUtils
