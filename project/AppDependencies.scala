import play.core.PlayVersion
import sbt._

object AppDependencies {

  private val customsApiCommonVersion = "1.27.0"
  private val bootStrapPlayVersion = "1.7.0"
  private val hmrcTestVersion = "3.0.0"
  private val scalaTestVersion = "3.0.5"
  private val pegDownVersion = "1.6.0"
  private val mockitoVersion = "2.18.3"
  private val scalaTestPlusPlayVersion = "2.0.1"
  private val wireMockVersion = "2.17.0"
  private val testScope = "test,it"

  val bootStrapPlay = "uk.gov.hmrc" %% "bootstrap-play-25" % bootStrapPlayVersion
  val customsApiCommon = "uk.gov.hmrc" %% "customs-api-common" % customsApiCommonVersion withSources()

  val hmrcTest = "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % testScope
  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % testScope
  val pegDown = "org.pegdown" % "pegdown" % pegDownVersion % testScope
  val mockito = "org.mockito" % "mockito-core" % mockitoVersion % testScope
  val playTest = "com.typesafe.play" %% "play-test" % PlayVersion.current % testScope
  val scalaTestPlusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion % testScope
  val wireMock = "com.github.tomakehurst" % "wiremock" % wireMockVersion exclude("org.apache.httpcomponents","httpclient") exclude("org.apache.httpcomponents","httpcore")
  val customsApiCommonTests = "uk.gov.hmrc" %% "customs-api-common" % customsApiCommonVersion % testScope classifier "tests"
}
