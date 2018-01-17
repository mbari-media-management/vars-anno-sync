lazy val configVersion = "1.3.2"
lazy val gsonVersion = "2.8.2"
lazy val guiceVersion = "4.1.0"
lazy val jerseyVersion = "2.25.1"
lazy val junitVersion = "4.12"
lazy val logbackVersion = "1.2.1"
lazy val rabbitmqVersion = "5.1.2"
lazy val scalatestVersion = "3.0.1"
lazy val slf4jVersion = "1.7.24"

lazy val buildSettings = Seq(
  //ensimeIgnoreScalaMismatch in ThisBuild := true,
  organization := "org.mbari.m3",
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.12.4")
)

lazy val consoleSettings = Seq(
  shellPrompt := { state =>
    val user = System.getProperty("user.name")
    user + "@" + Project.extract(state).currentRef.project + ":sbt> "
  },
  initialCommands in console :=
    """
      |import java.time.Instant
      |import java.util.UUID
    """.stripMargin
)

lazy val dependencySettings = Seq(
  libraryDependencies ++= {
    Seq(
      "ch.qos.logback" % "logback-classic"  % logbackVersion,
      "ch.qos.logback" % "logback-core"     % logbackVersion,
      "com.typesafe"   % "config"           % configVersion,
      "junit"          % "junit"            % junitVersion % "test",
      "org.scalatest"  %% "scalatest"       % scalatestVersion % "test",
      "org.slf4j"      % "log4j-over-slf4j" % slf4jVersion,
      "org.slf4j"      % "slf4j-api"        % slf4jVersion
    )
  },
  resolvers ++= Seq(
    Resolver.mavenLocal,
    Resolver.sonatypeRepo("releases"),
    "hohonuuli-bintray" at "http://dl.bintray.com/hohonuuli/maven")
)

lazy val optionSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "UTF-8", // yes, this is 2 args
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture"
  ),
  javacOptions ++= Seq("-target", "1.8", "-source", "1.8"),
  updateOptions := updateOptions.value.withCachedResolution(true)
)

lazy val appSettings = buildSettings ++ consoleSettings ++ dependencySettings ++
  optionSettings

lazy val `vars-anno-sync` = (project in file("."))
  .enablePlugins(PackPlugin)
  .settings(appSettings)
  .settings(
    name := "vars-anno-sync",
    version := "0.1-SNAPSHOT",
    fork := true,
    libraryDependencies ++= Seq(
      "com.rabbitmq" % "amqp-client" % rabbitmqVersion,
      "org.glassfish.jersey.core" % "jersey-client" % jerseyVersion
    )
  )
  .settings(
    scalafmtOnCompile := true
  )


