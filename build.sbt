name := """akkaDocker"""

version := "0.34"

scalaVersion := "2.11.8"

enablePlugins(DockerPlugin)
enablePlugins(JavaAppPackaging)

dockerExposedPorts := Seq(2552, 8080)
dockerBaseImage := "java:8"
dockerRepository := Some("benniekrijger")

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Dependencies.common
