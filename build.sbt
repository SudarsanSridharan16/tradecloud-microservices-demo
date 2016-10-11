name := """akkaDocker"""

version := "0.11"

scalaVersion := "2.11.8"

enablePlugins(DockerPlugin)
enablePlugins(JavaAppPackaging)

dockerExposedPorts := Seq(2552, 8080)
dockerBaseImage := "java:8"
dockerRepository := Some("benniekrijger")

libraryDependencies ++= Dependencies.common
