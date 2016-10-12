import sbt._

object  Dependencies {
  object Version {
    val akka = "2.4.10"
    val constructr = "0.13.2"
  }

  val common = Seq(
    "com.typesafe.akka"         %% "akka-actor"                     % Version.akka,
    "com.typesafe.akka"         %% "akka-cluster"                   % Version.akka,
    "com.typesafe.akka"         %% "akka-testkit"                   % Version.akka % "test",
    "com.typesafe.akka"         %% "akka-slf4j"                     % Version.akka,
    "com.typesafe.akka"         %% "akka-http-core"                 % Version.akka,
    "com.typesafe.akka"         %% "akka-persistence"               % Version.akka,
    "com.typesafe.akka"         %% "akka-cluster-sharding"          % Version.akka,
    "de.heikoseeberger"         %% "constructr-akka"                % Version.constructr,
    "com.tecsisa"               %% "constructr-coordination-consul" % "0.3.0",
    "org.iq80.leveldb"          %  "leveldb"                        % "0.7",
    "org.fusesource.leveldbjni" %  "leveldbjni-all"                 % "1.8",
    "de.heikoseeberger"         %% "akka-http-json4s"               % "1.10.0",
    "org.json4s"                %% "json4s-native"                  % "3.4.1",
    "org.json4s"                %% "json4s-ext"                     % "3.4.1",
    "joda-time"                 %  "joda-time"                      % "2.9.4",
    "org.joda"                  %  "joda-convert"                   % "1.8.1",
    "org.scalatest"             %% "scalatest"                      % "2.2.4" % "test",
    "ch.qos.logback"            %  "logback-classic"                % "1.1.6",
    "com.iheart"                %% "ficus"                          % "1.2.3"
  )
}