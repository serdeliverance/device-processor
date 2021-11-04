import Dependencies._

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.challenge"
ThisBuild / organizationName := "device-processor"


lazy val circeVersion = "0.14.1"
lazy val akkaVersion = "2.6.17"
lazy val akkaStreamKafkaVersion = "2.0.5"
lazy val alpakkaKafkaVersion = "2.1.1"
lazy val alpakkaSlickVersion = "3.0.3"
lazy val logbackVersion = "1.2.5"
lazy val jacksonVersion = "2.11.4"
lazy val postgresVersion = "42.2.2"

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
    .settings(consumerDependencies)
    .dependsOn(domain, commons)



lazy val commonDependencies =
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe"     %% "circe-generic-extras" % circeVersion,
    "com.typesafe.akka"  %% "akka-stream"               % akkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-typed" % akkaVersion,
    "com.typesafe.akka"  %% "akka-stream-kafka"         % akkaStreamKafkaVersion,
    "com.typesafe.slick" %% "slick"          % "3.3.3",
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    scalaTest % Test
  )

lazy val consumerDependencies =
  libraryDependencies ++= Seq(

    "com.typesafe.akka" %% "akka-stream-kafka" % alpakkaKafkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-slick" % alpakkaSlickVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
    "org.postgresql"     % "postgresql"      % postgresVersion,
  )