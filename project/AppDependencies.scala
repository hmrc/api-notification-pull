import sbt.*

object AppDependencies {

  val playVersion = "play-30"
  val bootstrap = "9.16.0"

  val compile = Seq(
    "uk.gov.hmrc"                   %% s"bootstrap-backend-$playVersion"   % bootstrap
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                   %% s"bootstrap-test-$playVersion"    % bootstrap    % Test,
    "org.mockito"                   %% "mockito-scala-scalatest"         % "1.17.37"    % Test,
    "org.wiremock"                   % "wiremock-standalone"             % "3.10.0"     % Test,
    "com.vladsch.flexmark"           % "flexmark-all"                    % "0.64.8"     % Test,
    "org.scalatestplus"             %% "scalatestplus-mockito"           % "1.0.0-M2"   % Test,
    "org.scalatestplus.play"        %% "scalatestplus-play"              % "7.0.1"      % Test,
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"            % "2.18.2"     % Test
  )
}
