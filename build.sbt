import play.sbt.PlayImport.PlayKeys.playDefaultPort
import uk.gov.hmrc.DefaultBuildSettings

ThisBuild / majorVersion                                         := 0
ThisBuild / scalaVersion                                         := "2.13.16"
ThisBuild / semanticdbEnabled                                    := true
ThisBuild / semanticdbVersion                                    := scalafixSemanticdb.revision
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

lazy val microservice = Project("tax-free-childcare-payments-nsi-stub", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions ++= Seq(
      "-Wconf:src=routes/.*:s",
      "-Wconf:cat=unused&src=views/.*\\.scala:s",
      "-Wconf:cat=unused&src=.*RoutesPrefix\\.scala:s",
      "-Wconf:cat=unused&src=.*Routes\\.scala:s",
      "-Wconf:cat=unused&src=.*ReverseRoutes\\.scala:s"
    ),
    resolvers += Resolver.jcenterRepo,
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    playDefaultPort := 10501,
    routesImport ++= Seq("models.request._")
  )
  .settings(CodeCoverageSettings.settings *)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.it)

commands ++= Seq(
  Command.command("run-all-tests")(state => "test" :: "it/test" :: state),
  Command.command("pre-commit") { state =>
    "clean" :: "scalafmtAll" :: "scalafixAll" :: "coverage" :: "run-all-tests" :: "coverageReport" :: state
  }
)
