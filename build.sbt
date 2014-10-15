
organization := "com.github.ellbur"

name := "lapper"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.2"

scalaSource in Compile <<= baseDirectory(_ / "src")

javaSource in Compile <<= baseDirectory(_ / "src")

scalaSource in Test <<= baseDirectory(_ / "test")

javaSource in Test <<= baseDirectory(_ / "test")

resourceDirectory in Compile <<= baseDirectory(_ / "resources")

resourceDirectory in Test <<= baseDirectory(_ / "test-resources")

libraryDependencies ++= Seq(
)

publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/.m2/repository")))

resolvers += "Local Maven Repository" at file(Path.userHome.absolutePath + "/.m2/repository").toURL.toString
