//addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.9.7")

//
//addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")

// [[https://github.com/sbt/sbt-native-packager]]
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.0")

// Benchmarking https://github.com/ktoso/sbt-jmh
addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.3.7")

//addSbtPlugin("org.jmotor.sbt" % "sbt-dependency-updates" % "1.2.9")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.3") // or 0.6.4

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.10.4")
