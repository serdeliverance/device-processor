import Dependencies._

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.challenge"
ThisBuild / organizationName := "device-processor"


lazy val circeVersion = "0.14.1"

lazy val domain =
  project
    .in(file("domain"))

lazy val commons =
  project
    .in(file("commons"))
    .settings(commonDependencies)
    .dependsOn(domain)

lazy val producer =
  project
    .in(file("producer"))
    .dependsOn(domain, commons)

lazy val consumer =
  project
    .in(file("consumer"))
    .dependsOn(domain, commons)



lazy val commonDependencies =
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe"     %% "circe-generic-extras" % circeVersion,
    scalaTest % Test
  )