/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.apinotificationpull.acceptance

import java.util.UUID

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{status => wmStatus, _}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.OptionValues._
import org.scalatest._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

class RetrieveAndDeleteNotificationSpec extends FeatureSpec with GivenWhenThen with Matchers with GuiceOneAppPerTest
  with BeforeAndAfterEach with BeforeAndAfterAll {

  private val clientId = "client-id"
  private val xClientIdHeader = "X-Client-ID"

  private val notificationId1 = UUID.randomUUID.toString
  private val notificationId2 = UUID.randomUUID.toString

  private val externalServicesHost = "localhost"
  private val externalServicesPort = 11111

  override def newAppForTest(testData: TestData): Application = new GuiceApplicationBuilder().configure(Map(
    "microservice.services.api-notification-queue.host" -> externalServicesHost,
    "microservice.services.api-notification-queue.port" -> externalServicesPort
  )).build()

  val externalServices: WireMockServer = new WireMockServer(wireMockConfig.port(externalServicesPort))

  override def beforeAll(): Unit = {
    super.beforeAll()
    if(!externalServices.isRunning) externalServices.start()
    WireMock.configureFor(externalServicesHost, externalServicesPort)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    externalServices.stop()
  }

  feature("GET all notifications from the API Notification Pull service") {
    info("As a 3rd Party")
    info("I want to successfully retrieve all notification locations by client id")

    val validRequest = FakeRequest("GET", "/").
      withHeaders(ACCEPT -> "application/vnd.hmrc.1.0+xml", xClientIdHeader -> clientId)

    scenario("Successful GET and 3rd party receives the notifications locations") {
      Given("There are notifications in the API Notification Queue")
      stubForAllNotifications()

      When("You call making the 'GET' action to the api-notification-pull service")
      val result = route(app, validRequest).value

      Then("You will receive all notifications for your client id")
      status(result) shouldBe OK

      // TODO: fix it once we have the XML body formatted in HAL
      val expectedBody = s"""<notifications><notification>/notification/$notificationId1</notification><notification>/notification/$notificationId2</notification></notifications>"""
      contentAsString(result).stripMargin shouldBe expectedBody

      And("The notifications will be retrieved")
      verify(getRequestedFor(urlMatching("/notifications")))
    }

    scenario("Missing Accept Header") {
      Given("You do not provide the Accept Header")
      val request = validRequest.copyFakeRequest(headers = validRequest.headers.remove(ACCEPT))

      When("You call make the 'GET' call to the api-notification-pull service")
      val result = route(app, request).value

      Then("You will be returned a 406 error response")
      status(result) shouldBe NOT_ACCEPTABLE
      contentAsString(result) shouldBe ""
    }

    scenario("Missing X-Client-Id Header") {
      Given("The platform does not inject a X-Client-Id Header")
      val request = validRequest.copyFakeRequest(headers = validRequest.headers.remove(xClientIdHeader))

      When("You call make the 'GET' call to the api-notification-pull service ")
      val result = route(app, request).value

      Then("You will be returned a 500 error response")
      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsString(result) shouldBe ""
    }
  }

  feature("Retrieve(DELETE) a single notification from the API Notification Pull service") {
    info("As a 3rd Party")
    info("I want to successfully retrieve a notification waiting for me")
    info("So that I can progress my original declaration submission")

    val validRequest = FakeRequest("DELETE", s"/$notificationId1").
      withHeaders(ACCEPT -> "application/vnd.hmrc.1.0+xml", xClientIdHeader -> clientId)

    scenario("Successful DELETE and 3rd party receives the notification") {
      Given("There is a notification waiting in the API Notification Queue and you have the correct notification Id")
      val notificationBody = "<notification>notification</notification>"
      stubForExistingNotification(notificationId1, notificationBody)

      When("You call making the 'DELETE' action to the api-notification-pull service")
      val result = route(app, validRequest).value

      Then("You will receive the notification")
      status(result) shouldBe OK
      contentAsString(result).stripMargin shouldBe notificationBody

      And("The notification will be DELETED")
      verify(deleteRequestedFor(urlMatching(s"/notifications/$notificationId1")))
    }

    scenario("3rd party provides notification Id but there are no notifications available or matching the Notification Id") {
      Given("A notification has already been retrieved using the correct notification Id")

      stubFor(get(urlMatching(s"/notifications/$notificationId1"))
        .willReturn(aResponse().withStatus(NOT_FOUND)))

      When("You make another call using the same notification Id")
      val result = route(app = app, validRequest).value

      Then("You will receive a 404 error response")
      status(result) shouldBe NOT_FOUND
      contentAsString(result) shouldBe "NOT FOUND"
    }

    scenario("Invalid Accept Header") {
      Given("You do not provide the Accept Header")
      val request = validRequest.copyFakeRequest(headers = validRequest.headers.remove(ACCEPT))

      When("You call make the 'DELETE' call, with a notification Id, to the api-notification-pull service")
      val result = route(app, request).value

      Then("You will be returned a 406 error response")
      status(result) shouldBe NOT_ACCEPTABLE
      contentAsString(result) shouldBe ""
    }

    scenario("Missing X-Client-Id Header") {
      Given("You do not provide the X-Client-Id Header")
      val request = validRequest.copyFakeRequest(headers = validRequest.headers.remove(xClientIdHeader))

      When("You call make the 'DELETE' call, with a notification Id, to the api-notification-pull service ")
      val result = route(app, request).value

      Then("You will be returned a 500 error response")
      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsString(result) shouldBe ""
    }
  }

  private def stubForExistingNotification(notificationId: String, notificationBody: String) = {
    stubFor(get(urlMatching(s"/notifications/$notificationId"))
      .willReturn(aResponse()
        .withStatus(OK)
        .withBody(notificationBody)))

    stubFor(delete(urlMatching(s"/notifications/$notificationId"))
      .willReturn(aResponse().withStatus(OK)))
  }

  private def stubForAllNotifications() = {
    stubFor(get(urlMatching("/notifications"))
      .willReturn(aResponse()
        .withStatus(OK)
        .withBody(s"""{"notifications":["/notification/$notificationId1","/notification/$notificationId2"]}""")
      ))
  }

}
