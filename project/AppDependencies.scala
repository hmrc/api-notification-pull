import sbt.*

object AppDependencies {

  private val testScope = "test,component"
  val playVersion = "play-30"
  val bootstrap = "8.5.0"

  val compile = Seq(
    "uk.gov.hmrc"                   %% s"bootstrap-backend-$playVersion"   % bootstrap
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                   %% "bootstrap-test-play-30"    % bootstrap    % testScope,
    "org.mockito"                   %% "mockito-scala-scalatest"   % "1.17.31"    % testScope,
    "org.wiremock"                   % "wiremock-standalone"       % "3.5.3"      % testScope,
    "com.vladsch.flexmark"           % "flexmark-all"              % "0.64.8"     % testScope,
    "org.scalatestplus"             %% "scalatestplus-mockito"     % "1.0.0-M2"   % testScope,
    "org.scalatestplus.play"        %% "scalatestplus-play"        % "7.0.1"      % testScope,
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"      % "2.17.0"     % testScope
  )
}
