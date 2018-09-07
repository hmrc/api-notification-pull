import play.core.PlayVersion
import sbt._

object AppDependencies {

  val bootStrapPlay = "uk.gov.hmrc" %% "bootstrap-play-25" % "1.7.0"

  val hmrcTest = "uk.gov.hmrc" %% "hmrctest" % "3.0.0"

  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4"

  val pegDown = "org.pegdown" % "pegdown" % "1.6.0"

  val mockito = "org.mockito" % "mockito-core" % "2.15.0"

  val playTest = "com.typesafe.play" %% "play-test" % PlayVersion.current

  val scalaTestPlusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1"

  val wireMock = "com.github.tomakehurst" % "wiremock" % "2.15.0" exclude("org.apache.httpcomponents","httpclient") exclude("org.apache.httpcomponents","httpcore")
}

/*




lazy val compile = Seq(
  ws,
  "uk.gov.hmrc" %% "bootstrap-play-25" % "1.7.0"
)

lazy val scope: String = "test, it"

lazy val test = Seq(
  "uk.gov.hmrc" %% "hmrctest" % "3.0.0" % scope,
  "org.scalatest" %% "scalatest" % "3.0.4" % scope,
  "org.pegdown" % "pegdown" % "1.6.0" % scope,
  "org.mockito" % "mockito-core" % "2.15.0" % scope,
  "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % scope,
  "com.github.tomakehurst" % "wiremock" % "2.15.0" % scope exclude("org.apache.httpcomponents","httpclient") exclude("org.apache.httpcomponents","httpcore")
)


 */