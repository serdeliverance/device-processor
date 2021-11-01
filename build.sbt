import Dependencies._

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.challenge"
ThisBuild / organizationName := "device-processor"


lazy val circeVersion = "0.14.1"
    // libraryDependencies += scalaTest % Test

lazy val domain =
  project
    .in(file("domain"))

lazy val commons =
  project
    .in(file("commons"))
    .dependsOn(domain)

lazy val producer =
  project
    .in(file("producer"))
    .dependsOn(domain, commons)

lazy val consumer =
  project
    .in(file("consumer"))
    .dependsOn(domain, commons)

lazy val commonDependencies = Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe"     %% "circe-generic-extras" % circeVersion,
)