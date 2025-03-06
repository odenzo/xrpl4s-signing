import sbt.*

//noinspection TypeAnnotation
object Libs {
  object V {
    val circe       = "0.14.10"
    val circeOptics = "0.15.0"
    val cats        = "2.13.0"
    val catsEffect  = "3.5.7"

    val spire           = "0.18.0"
    val scribe          = "3.15.2"
    val monocle         = "3.3.0"
    val pprint          = "0.9.0"
    val munit           = "1.1.0"
    val munitCheck      = "1.1.0"
    val munitCatsEffect = "2.0.0"
    val scodecBits      = "1.2.1"
    val scodec          = "2.3.2"
    val bouncyCastle    = "1.80"
    val blindsight      = "2.0.0"
    val terseLogback    = "1.2.0"
    val logback         = "1.5.16"
    val http4s          = "0.23.30"
    val chimney         = "1.7.3"
    val fs2             = "3.11.0"
    val fs2Data         = "1.11.2"
    val catsActors      = "2.0.1"
  }

  val chimney = Seq(
    "io.scalaland" %% "chimney" % V.chimney
  )

  // This needs: resolvers += "jitpack" at "https://jitpack.io"
  val catsActors = Seq("com.github.suprnation.cats-actors" %% "cats-actors" % V.catsActors)

  val fs2 = Seq(
    "co.fs2" %% "fs2-core" % V.fs2,
    "co.fs2" %% "fs2-io" % V.fs2,
    "org.gnieh" %% "fs2-data-json" % V.fs2Data,
    "org.gnieh" %% "fs2-data-json-circe" % V.fs2Data,
  )

  val http4s = Seq(
    "org.http4s" %% "http4s-ember-client" % V.http4s,
    "org.http4s" %% "http4s-ember-server" % V.http4s,
    "org.http4s" %% "http4s-dsl" % V.http4s,
    "org.http4s" %% "http4s-circe" % V.http4s,
    "org.http4s" %% "http4s-netty-client" % "0.5.22",
    "org.http4s" %% "http4s-jdk-http-client" % "0.10.0",
  )
  val munit  = Seq(
    "org.scalameta" %% "munit" % V.munit % Test,
    "org.scalameta" %% "munit-scalacheck" % V.munit % Test,
    "org.typelevel" %% "munit-cats-effect" % V.munitCatsEffect % Test,
  )

  val pprint = Seq("com.lihaoyi" %% "pprint" % V.pprint)

  /** JSON Libs == Circe and Associated Support Libs */
  val circe =
    Seq(
      "io.circe" %% "circe-core" % V.circe,
      "io.circe" %% "circe-generic" % V.circe,
      "io.circe" %% "circe-parser" % V.circe,
      // "io.circe" %% "circe-scodec" % V.circe,
      "io.circe" %% "circe-pointer" % V.circe,
      "io.circe" %% "circe-pointer-literal" % V.circe,
      "io.circe" %% "circe-testing" % V.circe % Test,
      "io.circe" %% "circe-optics" % V.circeOptics,
      "io.circe" %% "circe-literal" % V.circe,
      // "io.circe" %% "circe-derivation" % V.circeVersion
      // "io.circe" %% "circe-refined"    % V.circeVersion,
      // "io.circe" %% "circe-extras"     % V.circeVersion,
      //   "io.circe" %% "circe-generic-extras" % circeGenericExtraVersion,
      //   "io.circe" %% "circe-yaml"    % circeVersion, // Version 0.14 only
    )

  val monocle = Seq(
    "dev.optics" %% "monocle-core" % V.monocle,
    "dev.optics" %% "monocle-macro" % V.monocle,
    "dev.optics" %% "monocle-law" % V.monocle % Test,
  )

  /** Currently this is only for the binary serialization */
  val spire      = Seq("org.typelevel" %% "spire" % V.spire, "org.typelevel" %% "spire-extras" % V.spire)
  val cats       = Seq(
    "org.typelevel" %% "cats-core" % V.cats,
    "org.typelevel" %% "cats-effect" % V.catsEffect,
    "org.typelevel" %% "cats-effect-testkit" % V.catsEffect % Test,
  )
  val scodecBits = Seq("org.scodec" %% "scodec-bits" % V.scodecBits)

  val scodec = Seq(
    "org.scodec" %% "scodec-core" % V.scodec
    // "org.scodec" %% "scodec-spire" % scodecV
  )

  // Java Only Crypto Library
  val bouncycastle = {
    Seq("org.bouncycastle" % "bcprov-jdk18on" % V.bouncyCastle)
  }

  lazy val blindsight = Seq(
    "com.tersesystems.blindsight" %% "blindsight-api" % V.blindsight,
    "com.tersesystems.blindsight" %% "blindsight-logstash" % V.blindsight,
    "com.tersesystems.blindsight" %% "blindsight-inspection" % V.blindsight,
    // "com.tersesystems.blindsight" %% "blindsight-ringbuffer" % V.blindsight,
    "com.tersesystems.logback" % "logback-uniqueid-appender" % V.terseLogback,
    "com.tersesystems.logback" % "logback-exception-mapping" % V.terseLogback,
    "com.tersesystems.logback" % "logback-exception-mapping-providers" % V.terseLogback,
    "ch.qos.logback" % "logback-classic" % V.logback,
    // "com.tersesystems.logback" % "logstash-logback-context" % "0.1.7",
    // "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.11.0"
    //  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.3",
  )

  val stdlibs =
    cats ++ scodec ++ scodecBits ++ monocle ++ munit ++ pprint ++ circe ++ chimney ++ catsActors ++ blindsight

}
