
organization := "com.github.ellbur"

name := "lapper"

version := "1.2-SNAPSHOT"

scalaVersion := "2.11.4"

scalaSource in Compile <<= baseDirectory(_ / "src")

javaSource in Compile <<= baseDirectory(_ / "src")

scalaSource in Test <<= baseDirectory(_ / "test")

javaSource in Test <<= baseDirectory(_ / "test")

resourceDirectory in Compile <<= baseDirectory(_ / "resources")

resourceDirectory in Test <<= baseDirectory(_ / "test-resources")

libraryDependencies ++= Seq(
)

resolvers += "Local Maven Repository" at file(Path.userHome.absolutePath + "/.m2/repository").toURL.toString

credentials += Credentials(Path.userHome / ".ivy2" / ".local-archive-internal-credentials.txt")

publishMavenStyle := true

