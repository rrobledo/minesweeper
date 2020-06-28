enablePlugins(JavaAppPackaging)
enablePlugins(UniversalPlugin)

name := "minesweeper"
version := "0.0.1"
organization := "Raul Robledo"
scalaVersion := "2.12.10"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases")

libraryDependencies ++= {
  val akkaVersion = "2.6.3"
  val akkaHttpVersion = "10.1.11"
  val akkaHttpJsonVersion = "1.31.0"
  val scalaLoggingVersion = "3.9.0"
  val json4sVersion = "3.6.7"
  val logbackVersion = "1.2.3"
  val nettyVersion = "4.1.47.Final"
  val mockitoVersion = "2.9.0"
  val scaldiAkkaVersion = "0.5.8"
  val scalatestVersion = "3.0.4"
  val MongodbVersion = "2.9.0"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "org.scalactic" %% "scalactic" % scalatestVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",

    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-jackson" % akkaHttpVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "io.netty" % "netty-all" % nettyVersion,
    "io.netty" % "netty-transport" % nettyVersion,
    "io.netty" % "netty-handler" % nettyVersion,
    "io.netty" % "netty-codec" % nettyVersion,
    "io.netty" % "netty-common" % nettyVersion,

    "ch.qos.logback" % "logback-core" % logbackVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "de.heikoseeberger" %% "akka-http-json4s" % akkaHttpJsonVersion,

    "org.json4s" %% "json4s-ext" % json4sVersion,
    "org.json4s" %% "json4s-native" % json4sVersion,
    "org.scaldi" %% "scaldi-akka" % scaldiAkkaVersion,
    "org.apache.httpcomponents" % "httpclient" % "4.5.3",

    "com.github.cb372" %% "scalacache-guava" % "0.28.0",
    "net.logstash.logback" % "logstash-logback-encoder" % "5.2",

    // Async/Away library
    "org.scala-lang.modules" %% "scala-async" % "0.10.0",

    // Mongodb library
    "org.mongodb.scala" %% "mongo-scala-driver" % MongodbVersion,

    "io.prometheus" % "simpleclient" % "0.8.0",
    "io.prometheus" % "simpleclient_common" % "0.8.0",
    "io.prometheus" % "simpleclient_hotspot" % "0.8.0",

    // test dependencies
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
    "org.mockito" % "mockito-core" % mockitoVersion % "test",
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
    "log4j" % "log4j" % "1.2.14" % "test",
    "nl.grons" %% "metrics4-scala" % "4.0.1" % "test",
    "nl.grons" %% "metrics4-akka_a25" % "4.0.1" % "test",
    "nl.grons" %% "metrics4-scala-hdr" % "4.0.1" % "test",
    "io.dropwizard.metrics" % "metrics-jmx" % "4.0.1" % "test",
    "com.outworkers"  %%  "util-testing" % "0.50.0" % "test"
  )
}

parallelExecution in Test := false
testGrouping <<= definedTests in Test map { tests =>
  tests.map { test =>
    import Tests._
    new Group(
      name = test.name,
      tests = Seq(test),
      runPolicy = InProcess)
  }.sortWith(_.name < _.name)
}

mappings in Universal ++= Seq(
  file("src/main/resources/application.conf") -> "conf/application.conf",
  file("src/main/resources/logback.xml") -> "conf/logback.xml")

javaOptions in Universal ++= Seq(
  s"-DAPP_VERSION=${version.value}",
  "-Dfile.encoding=UTF8",
  "-Djava.rmi.server.hostname=127.0.0.1") // password authentication for remote monitoring is disabled

bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/application.conf""""
bashScriptExtraDefines += """addJava "-Dlogback.configurationFile=${app_home}/../conf/logback.xml""""
dependencyCheckSuppressionFiles := Seq(new File("dependency-check-suppressions.xml"))
