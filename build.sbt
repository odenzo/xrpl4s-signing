import BuildSettings.MyCompileOptions.scala3Options

ThisBuild / resolvers ++= Seq(Resolver.mavenLocal, "jitpack" at "https://jitpack.io")
ThisBuild / organization := "com.odenzo"
ThisBuild / scalaVersion := "3.6.3"
ThisBuild / semanticdbEnabled := true

Test / logBuffered := true
Test / parallelExecution := false

lazy val `xrpls-signing` =
  (project in file(".")).aggregate(common, signing).settings(publish / skip := true)

//lazy val macros = (project in file("modules/xrpl-macros")).settings(
//  name := "xrpl-macros",
//  scalacOptions := scala3Options,
//  libraryDependencies ++= Libs.stdlibs ++ Libs.echopraxia ++ Libs.bouncycastle,
//)

lazy val common = (project in file("modules/common")).settings(
  name := "common",
  scalacOptions := scala3Options,
  libraryDependencies ++= Libs.stdlibs ++ Libs.bouncycastle,
)

lazy val signing = (project in file("modules/core"))
  .dependsOn(common)
  .settings(name := "xrpl-signing-core",
            scalacOptions := scala3Options,
            libraryDependencies ++= Libs.stdlibs ++ Libs.bouncycastle,
           )

lazy val server = (project in file("modules/server"))
  .dependsOn(common, signing)
  .settings(name := "xrpl-signing-server",
            scalacOptions := scala3Options,
            libraryDependencies ++= Libs.stdlibs ++ Libs.bouncycastle,
           )
