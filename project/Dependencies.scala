import sbt._

object Dependencies {

  val fs2Version = "1.0.0"
  val catsVersion = "1.4.0"
  val catsEffectVersion = "1.0.0"

  // Dependencies for JVM part of code
  val backendDeps = Def.setting(
    Seq(
      "com.lihaoyi" %% "sourcecode" % "0.1.4",               // expert println debugging
      "com.lihaoyi" %% "pprint" % "0.5.3",                   // pretty print for types and case classes
      "org.typelevel" %% "cats-core" % catsVersion,          // abstract category dork stuff

      "com.chuusai" %% "shapeless" % "2.3.2",                // Abstract level category dork stuff

      "joda-time" % "joda-time" % "2.9.9",
      "org.joda" % "joda-convert" % "2.0.1",

      "org.typelevel" %% "cats-effect" % catsEffectVersion,  // IO monad category wank

      "co.fs2" %% "fs2-core" % fs2Version,                   // The best library
      "co.fs2" %% "fs2-io"   % fs2Version,                   // The best library

      "com.beachape" %% "enumeratum" % "1.5.13",
      "com.github.nscala-time" %% "nscala-time" % "2.16.0",     // Time

      "org.tpolecat" %% "atto-core"    % "0.6.3",
      "org.tpolecat" %% "atto-refined" % "0.6.3",

      "org.typelevel" %% "spire" % "0.14.1",

      "io.estatico" %% "newtype" % "0.4.2",

      "com.github.pathikrit" %% "better-files" % "3.7.0",

      "org.atnos" %% "eff" % "5.2.0",


      "org.scala-lang.modules" %% "scala-swing" % "2.1.1",
      "org.apache.xmlgraphics" % "batik-swing" % "1.11"
      ))
}
