import scalariform.formatter.preferences._

import bintray.Plugin._

name := "cornichon"

organization := "com.github.agourlay"

version := "0.1.SNAPSHOT"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalaVersion := "2.11.7"

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-encoding", "UTF-8",
  "-Ywarn-dead-code",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-feature",
  "-Ywarn-unused-import"
)

fork in Test := true

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(AlignParameters, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)
  .setPreference(RewriteArrowSymbols, true)

libraryDependencies ++= {
  val scalaTestV = "2.2.5"
  val jsonLensesV = "0.6.0"
  val akkaHttpV = "1.0"
  val akkaV = "2.3.12"
  val catsV = "0.1.2"
  val parboiledV = "2.1.0"
  val logbackV = "1.1.3"
  Seq(
     "com.typesafe.akka" %% "akka-http-experimental"            % akkaHttpV
    ,"com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaHttpV
    ,"com.typesafe.akka" %% "akka-slf4j"                        % akkaV
    ,"net.virtual-void"  %% "json-lenses"                       % jsonLensesV
    ,"org.spire-math"    %% "cats-macros"                       % catsV
    ,"org.spire-math"    %% "cats-core"                         % catsV
    ,"org.spire-math"    %% "cats-std"                          % catsV
    ,"org.parboiled"     %% "parboiled"                         % parboiledV
    ,"org.scalatest"     %% "scalatest"                         % scalaTestV
    ,"ch.qos.logback"    %  "logback-classic"                   % logbackV
  )
}

Seq(bintraySettings:_*)