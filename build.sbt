import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.trueaccord.scalapb.{ScalaPbPlugin => PB}

val commonSettings = Seq(
  name := """tradecloud-microservices""",
  version := "0.1",
  scalaVersion := "2.11.8",
  resolvers ++= Seq(
    Resolver.jcenterRepo
  ),
  dockerRepository := Some("tradecloud"),
  dockerBaseImage := "java:8"
) ++ PB.protobufSettings

lazy val root = (project in file("."))
  .settings(commonSettings:_*)
  .aggregate(serviceIdentity, serviceItem)

lazy val common = (project in file("common"))
  .settings(commonSettings:_*)
  .settings(
    name := "common",
    libraryDependencies ++= Dependencies.common
  )

lazy val serviceIdentity = (project in file("serviceIdentity"))
  .enablePlugins(JavaAppPackaging)
  .settings(commonSettings:_*)
  .settings(
    name := "service-identity",
    libraryDependencies ++= Dependencies.serviceIdentity,
    dockerExposedPorts := Seq(2552, 8080)
  )
  .dependsOn(common)

lazy val serviceUser = (project in file("serviceUser"))
  .enablePlugins(JavaAppPackaging)
  .settings(commonSettings:_*)
  .settings(
    name := "service-user",
    libraryDependencies ++= Dependencies.serviceUser,
    dockerExposedPorts := Seq(2552, 8080)
  )
  .dependsOn(common)

lazy val serviceItem = (project in file("serviceItem"))
  .enablePlugins(JavaAppPackaging)
  .settings(commonSettings:_*)
  .settings(
    name := "service-item",
    libraryDependencies ++= Dependencies.serviceItem,
    dockerExposedPorts := Seq(2552, 8080)
  )
  .dependsOn(common)
