/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package component

import java.util.UUID

import org.scalatest.OptionValues._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import unit.util.XmlUtil.string2xml

class GetUnpulledNotificationSpec extends ComponentSpec with ExternalServices {


  override val clientId: String = UUID.randomUUID().toString

  override def beforeAll(): Unit = {
    startMockServer()
  }

  override protected def beforeEach() {
    resetMockServer()
  }

  override def afterAll(): Unit = {
    stopMockServer()
  }


  val notificationId = "notification-id"
  val validRequest = FakeRequest("GET", s"/unpulled/$notificationId").
    withHeaders(ACCEPT -> "application/vnd.hmrc.1.0+xml", xClientIdHeader -> clientId)

  val validListRequest = FakeRequest("GET", s"/unpulled").
    withHeaders(ACCEPT -> "application/vnd.hmrc.1.0+xml", xClientIdHeader -> clientId)

  feature("GET a list of unpulled notifications") {
    scenario("I want to successfully retrieve a list of unpulled notifications") {

      Given("There is list of unpulled notification in the API Notification Queue")

      val body = """{ "notifications": ["notification1", "notification2"] }""".stripMargin

      stubForExistingNotificationsList("/notifications/unpulled", body,
        Seq(ACCEPT -> "application/vnd.hmrc.1.0+xml", xClientIdHeader -> clientId))

      When("I call the GET unpulled notifications endpoint")
      val result = route(app, validListRequest).value

      Then("I receive the list of notifications")
      status(result) shouldBe OK

      val expectedXml = scala.xml.Utility.trim(
        <resource href="/notifications/unpulled/">
          <link rel="self" href="/notifications/unpulled/"/>
          <link rel="notification" href="/notifications/unpulled/notification1"/>
          <link rel="notification" href="/notifications/unpulled/notification2"/>
        </resource>
      )

      string2xml(contentAsString(result)) shouldBe expectedXml
    }

    scenario("I want to successfully retrieve an empty list of notifications") {

      Given("There are no notification in the API Notification Queue")

      val body = """{ "notifications": [] }""".stripMargin

      stubForExistingNotificationsList("/notifications/unpulled", body,
        Seq(ACCEPT -> "application/vnd.hmrc.1.0+xml", xClientIdHeader -> clientId))

      When("I call the GET unpulled notifications endpoint")
      val result = route(app, validListRequest).value

      Then("I receive the list of notifications")
      status(result) shouldBe OK

      val expectedXml = scala.xml.Utility.trim(
        <resource href="/notifications/unpulled/">
          <link rel="self" href="/notifications/unpulled/"/>
        </resource>
      )

      string2xml(contentAsString(result)) shouldBe expectedXml
    }
  }

  feature("GET an unpulled notification by id") {

    scenario("I want to successfully retrieve a notification by notification id") {
      Given("There is an unpulled notification in the API Notification Queue")

      val body = """<notification>some-notification</notification>""".stripMargin

      stubForExistingNotification("/notifications/unpulled", notificationId, body,
        Seq(ACCEPT -> "application/vnd.hmrc.1.0+xml", xClientIdHeader -> clientId))

      When("I call the GET unpulled notification endpoint")
      val result = route(app, validRequest).value

      Then("I receive the notification")
      status(result) shouldBe OK

      contentAsString(result).stripMargin shouldBe "<notification>some-notification</notification>"

    }

    scenario("I try to GET an already pulled notification") {
      Given("A notification that has been previously pulled")

      When("I call the GET unpulled notification endpoint")
      val result = route(app, validRequest).value

      Then("I receive a NOT FOUND error")
      status(result) shouldBe NOT_FOUND

      val expectedXml = scala.xml.Utility.trim(<errorResponse>
        <code>NOT_FOUND</code>
        <message>Resource was not found</message>
        </errorResponse>
      )

      string2xml(contentAsString(result)) shouldBe expectedXml
    }
  }
}
