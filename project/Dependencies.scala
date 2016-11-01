import sbt._

object  Dependencies {
  object Version {
    val akka = "2.4.11"
    val constructr = "0.13.2"
  }

  lazy val common = Seq(
    "org.json4s"                          %% "json4s-jackson"                 % "3.4.1",
    "org.json4s"                          %% "json4s-ext"                     % "3.4.1",
    "joda-time"                           %  "joda-time"                      % "2.9.4",
    "org.joda"                            %  "joda-convert"                   % "1.8.1",
    "ch.qos.logback"                      %  "logback-classic"                % "1.1.6",
    "com.iheart"                          %% "ficus"                          % "1.2.3",
    "com.typesafe.akka"                   %% "akka-actor"                     % Version.akka,
    "com.typesafe.akka"                   %% "akka-persistence"               % Version.akka,
    "com.typesafe.akka"                   %% "akka-cluster-sharding"          % Version.akka,
    "com.typesafe.akka"                   %% "akka-cluster"                   % Version.akka,
    "com.typesafe.akka"                   %% "akka-http-core"                 % Version.akka,
    "com.typesafe.akka"                   %% "akka-http-experimental"         % Version.akka,
    "com.pauldijou"                       %% "jwt-core"                       % "0.9.0"
  )

  lazy val service = common ++ tests ++ Seq(
    "com.typesafe.akka"                   %% "akka-slf4j"                     % Version.akka,
    "com.typesafe.akka"                   %% "akka-persistence-cassandra"     % "0.19",
    "de.heikoseeberger"                   %% "constructr-akka"                % Version.constructr,
    "nl.tradecloud"                       %% "kafka-akka-extension"           % "0.7",
    "com.tecsisa"                         %% "constructr-coordination-consul" % "0.3.0",
    "org.iq80.leveldb"                    %  "leveldb"                        % "0.7",
    "org.fusesource.leveldbjni"           %  "leveldbjni-all"                 % "1.8",
    "de.heikoseeberger"                   %% "akka-http-json4s"               % "1.10.0"
  )

  lazy val serviceIdentity = service ++ Seq(
    "org.mindrot"                         %  "jbcrypt"                        % "0.3m"
  )

  lazy val serviceUser = service
  lazy val serviceItem = service

  lazy val tests = Seq(
    "org.scalatest"                       %% "scalatest"                      % "2.2.4"         % "test",
    "com.typesafe.akka"                   %% "akka-testkit"                   % Version.akka    % "test"
  )
}