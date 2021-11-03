import Dependencies._

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.challenge"
ThisBuild / organizationName := "device-processor"


lazy val circeVersion = "0.14.1"
lazy val akkaVersion = "2.6.17"
lazy val akkaStreamKafkaVersion = "2.0.5"
lazy val logbackVersion = "1.2.5"

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
    .settings(producerDependencies)
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
    "com.typesafe.akka"  %% "akka-actor"                % akkaVersion,
    "com.typesafe.akka"  %% "akka-stream"               % akkaVersion,
    "com.typesafe.akka"  %% "akka-stream-kafka"         % akkaStreamKafkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    scalaTest % Test
  )

lazy val producerDependencies =
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test
  )