import sbt._

object AppDependencies {

  val playVersion = "play-30"
  val bootstrap = "8.5.0"

  val compile = Seq(
    "uk.gov.hmrc"                   %% s"bootstrap-backend-$playVersion"   % bootstrap
  )

  val test: Seq[ModuleID] = Seq(
//    "uk.gov.hmrc"                                %% "bootstrap-test-play-30"    % bootstrap % Test,
    "org.mockito"                   %% "mockito-scala-scalatest"      % "1.17.29"         % Test,
//    "org.mockito"                    % "mockito-core"                 % "5.11.0"          % Test,
    "org.wiremock"                   % "wiremock-standalone"          % "3.5.2"           % Test,
    "com.vladsch.flexmark"           % "flexmark-all"                 % "0.35.10"         % Test,
    "org.scalatestplus"                          %% "scalatestplus-mockito"     % "1.0.0-M2"               % Test,
    "org.scalatestplus.play"                     %% "scalatestplus-play"        % "5.1.0" % Test,
    "com.fasterxml.jackson.module"               %% "jackson-module-scala"      % "2.15.0"        % Test
  )
}
